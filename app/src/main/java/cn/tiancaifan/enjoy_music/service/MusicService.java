package cn.tiancaifan.enjoy_music.service;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.LocalMusic;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.bean.SongUrl;
import cn.tiancaifan.enjoy_music.constant.CONSTANT;
import cn.tiancaifan.enjoy_music.utils.ByteObjectUtil;
import cn.tiancaifan.enjoy_music.utils.DataBaseUtil;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import cn.tiancaifan.enjoy_music.utils.VisualizerHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @ClassName: MusicService
 * @Description: TODO 音乐服务
 * @Date: 2022/4/22 13:51
 * @Author: fanxiaofan
 */
public class MusicService extends Service {

    //可视化数据的大小： getCaptureSizeRange()[0]为最小值，getCaptureSizeRange()[1]为最大值
    private int captureSize = Visualizer.getCaptureSizeRange()[1];
    private int captureRate = Visualizer.getMaxCaptureRate() * 3 / 4;
    private Visualizer visualizer;
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
    private List<Music> musics;//待播列表
    private List<Integer> shuffleMusics;//经过洗牌算法后的待播列表顺序
    private Boolean playerStatus = false;
    //当前播放位置
    private int position = -1;
    private int shufflePosition = -1;
    private int mode = 0;
    private Intent intent;
    //创建播放器对象
    private MediaPlayer player;
    private SharedPreferences.Editor editor;


    private DataBaseUtil dataBaseUtil;

    private IBinder mBinder = new MusicServiceRemote.Stub() {

        @Override
        public void play(Music music) throws RemoteException {
            boolean flag = true;
            if (player != null) {
                player.release();
                player = null;
                playerStatus = false;
            }
            for (int i = 0; i < musics.size(); i++) {
                if (musics.get(i).getId() == music.getId()){
                   position = i;
                   flag = false;
                }
            }
            if (flag){
                musics.add(music);
                dataBaseUtil.addPlayList(musics);
                position = musics.size()-1;
            }
            shuffle(musics);
            shufflePosition = shuffleMusics.indexOf(position);
            getPlayer(musics.get(position));
        }

        @Override
        public void playByPosition(int index) throws RemoteException {
            if (player != null) {
                player.release();
                player = null;
                playerStatus = false;
            }
            position = index;
            shufflePosition = shuffleMusics.indexOf(position);
            getPlayer(musics.get(position));
        }

        @Override
        public void playList(List<Music> playlist) throws RemoteException {
            if (player != null) {
                player.reset();
                player.release();
                player = null;
                playerStatus = false;
            }
            musics = null;
            musics = playlist;
            dataBaseUtil.addPlayList(playlist);
            position = 0;
            Music music = musics.get(position);
            shuffle(musics);
            shufflePosition = shuffleMusics.indexOf(position);
            getPlayer(music);
        }

        @Override
        public void nextMusic() throws RemoteException {
            nextForMusics();
        }

        @Override
        public void preMusic() throws RemoteException {
            preForMusics();
        }

        @Override
        public void stop() throws RemoteException {
            player.stop();
        }

        @Override
        public void pauseOrStartMusic() throws RemoteException {
            pauseOrPlayMusic();
            sendBroadcastPlayerStatus();
        }

        @Override
        public void setMode(int mode) throws RemoteException {
            editor.putInt("mode",mode);
            editor.commit();
            MusicService.this.mode = mode;
        }

        @Override
        public int getMode() throws RemoteException {
            return mode;
        }

        @Override
        public void removeMuiscFromPlayList(int index) throws RemoteException {

        }

        @Override
        public boolean getPlayerStatus() throws RemoteException {
            if (player == null) {
                return false;
            }
            return playerStatus;
        }

        @Override
        public int getDuration() throws RemoteException {
            if (player == null || !playerStatus)
                return 0;
            return player.getDuration();
        }



        @Override
        public int getCurrentPosition() throws RemoteException {
            if (player == null)
                return 0;
            return  player.getCurrentPosition();
        }

        @Override
        public void clearPlayList() throws RemoteException {

        }

        @Override
        public void seekTo(int ms) throws RemoteException {
            player.seekTo(ms);
        }

        @Override
        public Music getMusic() throws RemoteException {
            if (musics.size() == 0) {
                return null;
            }
            return musics.get(position);
        }

        @Override
        public List<Music> getListMusic() throws RemoteException {
            return musics;
        }

        @Override
        public int getPosition() throws RemoteException {
            return position;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        musics = new ArrayList<>();
        dataBaseUtil = new DataBaseUtil(new DataBaseUtil.DataBaseHelper(MusicService.this, "MusicInfo.db", null, 1));
        musics = dataBaseUtil.getPlayList();

        SharedPreferences status = getSharedPreferences("PlayerList", MODE_PRIVATE);
        editor = getSharedPreferences("PlayerList", MODE_PRIVATE).edit();
        position = status.getInt("position", -1);
        shufflePosition = status.getInt("shufflePosition",-1);
        shuffle(musics);
        shufflePosition = shuffleMusics.indexOf(position);
        mode = status.getInt("mode",0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    /**
     * 暂停&播放
     */
    public void pauseOrPlayMusic() {
        if (player != null) {
            if (playerStatus) {
                player.pause();
                playerStatus = false;
            } else {
                player.start();
                playerStatus = true;
            }
        }else {
            getPlayer(musics.get(position));
        }
    }
    //下一首
    public void nextForMusics(){
        if (player != null) {
            player.release();
            player = null;
            playerStatus = false;
        }
        switch (mode){
            default:
                if (++position == musics.size()){
                    position = 0;
                }
                break;
            case 2:
                if (++shufflePosition == shuffleMusics.size()){
                    shufflePosition = 0;
                }
                position = shuffleMusics.get(shufflePosition);
                break;
        }

        getPlayer(musics.get(position));
    }
    //上一首
    public void preForMusics(){
        if (player != null) {
            player.release();
            player = null;

            playerStatus = false;
        }
        switch (mode){
            default:
                if (--position == -1){
                    position = musics.size()-1;
                }
                break;
            case 2:
                if (--shufflePosition == -1){
                    shufflePosition = 0;
                }
                position = shuffleMusics.get(shufflePosition);
                break;
        }
        getPlayer(musics.get(position));
    }

    /**
     * 停止播放
     */
    public void stopMusic() {
        if (player != null) {
            player.stop();
            player.release();//释放
            playerStatus = false;
            player = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在服务死亡之前停止音乐
        stopMusic();
    }

    /**
     * 发送播放音乐信息广播
     */
    public void sendBroadcastMusicInfo() {
        intent = new Intent();
        intent.setAction(CONSTANT.MUSIC_INFO);
        Bundle bundle = new Bundle();
        bundle.putByteArray("MusicInfo", ByteObjectUtil.objectToByteArray(musics.get(position)));
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * 发送player状态广播
     */
    private void sendBroadcastPlayerStatus() {
        intent = new Intent();
        intent.setAction(CONSTANT.ACTION_STATUS);
        Bundle bundle = new Bundle();
        bundle.putBoolean("status", playerStatus);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * 发送所有广播
     */
    private void sendBroadcastAll() {
        sendBroadcastMusicInfo();
        sendBroadcastPlayerStatus();
    }

    /**
     * 播放歌曲
     *
     * @param music
     */
    private synchronized void getPlayer(Music music) {
        try {
            LocalMusic localMusic = (LocalMusic) music;
            Log.d("Service","本地音乐");
            playerLocalMusic(localMusic);
        }catch (Exception e){
            playerRemoteMusic(music);
        }
    }

    private void playerLocalMusic(LocalMusic localMusic){
        player = new MediaPlayer();
        try {
            player.setDataSource(localMusic.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }


        player.prepareAsync();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playerStatus = true;
                mp.start();
                editor.putInt("position", position);
                editor.commit();
                sendBroadcastAll();
                if (visualizer == null) {
                    visualizer = new Visualizer(player.getAudioSessionId());
                } else {
                    visualizer.release();
                    visualizer = new Visualizer(player.getAudioSessionId());
                }
                visualizer.setCaptureSize(captureSize);
                visualizer.setDataCaptureListener(VisualizerHelper.getInstance().getDataCaptureListener(), captureRate, true, true);
                visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
                visualizer.setEnabled(true);
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mode == 1){
                    player.release();
                    player = null;
                    getPlayer(musics.get(position));
                    return;
                }
                playerStatus = false;
                sendBroadcastPlayerStatus();
                nextForMusics();
                Log.d("MusicService","end");
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("MusicService","error");
                Log.d("MusicService","what="+what);
                Log.d("MusicService","extra="+extra);
                return false;
            }
        });

    }

    private void playerRemoteMusic(Music music) {
        dataBaseUtil.addMusicToHistory(music);
        Call<ResponseBody> call = retrofitAPI.songUrl(music.getId());
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JsonArray data = JsonParser.parseString(string).getAsJsonObject().getAsJsonArray("data");
                List<SongUrl> songUrls = new Gson().fromJson(data, new TypeToken<List<SongUrl>>() {
                }.getType());

                try {
                    player = new MediaPlayer();
                    player.setDataSource(MusicService.this, Uri.parse(songUrls.get(0).getUrl()));
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                player.prepareAsync();
                Log.d("MusicService",songUrls.get(0).getUrl());
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        playerStatus = true;
                        mp.start();
                        editor.putInt("position", position);
                        editor.commit();
                        sendBroadcastAll();
                        if (visualizer == null) {
                            visualizer = new Visualizer(player.getAudioSessionId());
                        } else {
                            visualizer.release();
                            visualizer = new Visualizer(player.getAudioSessionId());
                        }
                        visualizer.setCaptureSize(captureSize);
                        visualizer.setDataCaptureListener(VisualizerHelper.getInstance().getDataCaptureListener(), captureRate, true, true);
                        visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
                        visualizer.setEnabled(true);
                    }
                });

                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mode == 1){
                            player.release();
                            player = null;
                            getPlayer(musics.get(position));
                            return;
                        }
                        playerStatus = false;
                        sendBroadcastPlayerStatus();
                        nextForMusics();
                        Log.d("MusicService","end");
                    }
                });

                player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.d("MusicService","error");
                        Log.d("MusicService","what="+what);
                        Log.d("MusicService","extra="+extra);
                        return false;
                    }
                });




            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private  void swap(List<Integer> temp,int i,int j){//交换 下标为i的牌和下标为j（随机生成数）的对象
        int tmp= temp.get(i);

        temp.set(i, temp.get(j));

        temp.set(j,tmp);

    }

    /**
     * 洗牌算法
     * @param musics
     */
    private void shuffle(List<Music> musics){
        shuffleMusics = createListInteger(musics);
        int size= shuffleMusics.size();//共多少张牌
        for(int i=size-1;i>0;i--){//保证每张牌都洗过，
            // i=size-1是牌下标最多到size-1，
            // i>0是因为下面的random.next(i);i是生成0-（i-1)的数字，i-1>=0
            Random random=new Random();
            int rand=random.nextInt(i);//生成0-9的下标，传n进去，生成0-（n-1）的数字(下标)，共n个数
            swap(shuffleMusics,i,rand);
        }
    }

    /**
     *
     * @param src
     * @param <T>
     * @return
     */
    private <T> List<Integer> createListInteger(List<T> src) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < src.size(); i++) {
            list.add(i);
        }
        return list;
    }

}