package cn.tiancaifan.enjoy_music.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.webkit.DownloadListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hmy.popwindow.PopItemAction;
import com.hmy.popwindow.PopWindow;

import com.zlm.hp.lyrics.LyricsReader;
import com.zlm.hp.lyrics.formats.lrcwy.WYLyricsFileReader;
import com.zlm.hp.lyrics.model.LyricsInfo;
import com.zlm.hp.lyrics.utils.ColorUtils;
import com.zlm.hp.lyrics.widget.ManyLyricsView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.PopWindowsListViewAdapter;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.bean.SongUrl;
import cn.tiancaifan.enjoy_music.constant.CONSTANT;
import cn.tiancaifan.enjoy_music.service.MusicService;
import cn.tiancaifan.enjoy_music.service.MusicServiceRemoteManager;
import cn.tiancaifan.enjoy_music.ui.widget.AttachmentRingView;
import cn.tiancaifan.enjoy_music.ui.widget.PlayPauseView;
import cn.tiancaifan.enjoy_music.utils.BlurTransformation;
import cn.tiancaifan.enjoy_music.utils.ByteObjectUtil;
import cn.tiancaifan.enjoy_music.utils.DataBaseUtil;
import cn.tiancaifan.enjoy_music.utils.FileUtil;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import cn.tiancaifan.enjoy_music.utils.SizeUtils;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;
import cn.tiancaifan.enjoy_music.utils.VisualizerHelper;
import cn.tiancaifan.enjoy_music.utils.download.DownLoadListener;
import cn.tiancaifan.enjoy_music.utils.download.DownLoadManager;
import cn.tiancaifan.enjoy_music.utils.download.DownLoadService;
import cn.tiancaifan.enjoy_music.utils.download.TaskInfo;
import cn.tiancaifan.enjoy_music.utils.download.dbcontrol.bean.SQLDownLoadInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaPlayer extends AppCompatActivity {

    private float DownX, DownY = 0;
    private float moveX, moveY = 0;
    private long currentMS, moveTime = 0;

    private ImageView albumImage;

    private LinearLayout bg;

    private TextView titleIv;
    private TextView subTitleTv, durationTv, progressTv, popWindowsCount;
    private SeekBar progressSb;

    private AttachmentRingView ring;

    private PlayPauseView playPauseIv;
    private ImageView backIv, prevPlayIv, nextPlayIv, playQueueIv, collectionMusicBtn, playModeIv,downloadBtn;
    private ManyLyricsView manyLyricsView;
    private RelativeLayout albumLayout, lycLayout;

    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
    private MusicServiceRemote mService;
    private ObjectAnimator rotation;
    private Thread sThread;
    private boolean flag, collectionFlag;
    private boolean status = true;
    private int[] modeResource = {R.drawable.ic_repeat, R.drawable.ic_repeat_one, R.drawable.ic_shuffle};
    private int modeFlag = 0;


    private MusicServiceReceiver receiver;

    private Music music;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        initInfo();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    int currentPosition = 0;
                    try {
                        if (mService.getPlayerStatus()) {
                            currentPosition = mService.getCurrentPosition() / 1000;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    int minutes = currentPosition / 60;
                    int seconds = currentPosition % 60;
                    String time = String.format(Locale.getDefault(), "%d:%02d",
                            minutes, seconds);
                    progressTv.setText(time);
                    progressSb.setProgress(currentPosition);
                    break;
            }
            return false;
        }
    });
    private DataBaseUtil dataBaseUtil;
    private DownLoadManager manager;


    public synchronized void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        manager = DownLoadService.getDownLoadManager();
        manager.setSupportBreakpoint(true);
        try {
            init();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void init() throws RemoteException {
        albumImage = findViewById(R.id.media_player_album);
        playPauseIv = findViewById(R.id.playPauseIv);
        backIv = findViewById(R.id.backIv);
        subTitleTv = findViewById(R.id.subTitleTv);
        bg = findViewById(R.id.media_player_bg);
        titleIv = findViewById(R.id.titleIv);
        ring = findViewById(R.id.ring);
        progressSb = findViewById(R.id.progressSb);
        durationTv = findViewById(R.id.durationTv);
        progressTv = findViewById(R.id.progressTv);
        manyLyricsView = findViewById(R.id.lyricsView);
        albumLayout = findViewById(R.id.media_player_album_layout);
        lycLayout = findViewById(R.id.media_player_lyrics_layout);
        prevPlayIv = findViewById(R.id.prevPlayIv);
        nextPlayIv = findViewById(R.id.nextPlayIv);
        playQueueIv = findViewById(R.id.playQueueIv);
        collectionMusicBtn = findViewById(R.id.collectionMusicBtn);
        playModeIv = findViewById(R.id.playModeIv);
        downloadBtn = findViewById(R.id.downloadBtn);

        playQueueIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showCustomSheet(mService.getListMusic());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        albumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lycLayout.setVisibility(View.VISIBLE);
                albumLayout.setVisibility(View.GONE);
            }
        });

        manyLyricsView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DownX = event.getX();
                        DownY = event.getY();
                        currentMS = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        DownX = event.getX();
                        DownY = event.getY();
                        moveX += Math.abs(event.getRawX() - DownX);//x轴移动距离
                        moveY += Math.abs(event.getRawY() - DownY);//y轴移动距离

                        break;
                    case MotionEvent.ACTION_UP:
                        moveTime = System.currentTimeMillis() - currentMS;

                        //判断是滑动还是点击操作、判断是否继续传递信号
                        if (moveTime < 300 && moveX < 20 && moveY < 20) {//点击事件
                            lycLayout.setVisibility(View.GONE);
                            albumLayout.setVisibility(View.VISIBLE);
                        }
                        moveX = moveY = 0;//归零
                    default:
                        break;
                }

                return false;
            }
        });

        ring.setScope(20);
        ring.setStart(20);
        ring.setRotate(true);
        ring.setWave(true);

        playPauseIv.setProgress(180f);

        StatusBarUtil.setTransparentForWindow(this);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //绑定Service
        MusicServiceRemoteManager manager = new MusicServiceRemoteManager();
        manager.bindToService(this, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = MusicServiceRemote.Stub.asInterface(service);
                Message msg = Message.obtain();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        });
        //默认颜色
        int[] paintColors = new int[]{
                ColorUtils.parserColor("#88ffffff"),
                ColorUtils.parserColor("#88ffffff")
        };
        manyLyricsView.setPaintColor(paintColors, false);

        //高亮颜色
        int[] paintHLColors = new int[]{
                ColorUtils.parserColor("#fada83"),
                ColorUtils.parserColor("#fada83")
        };
        manyLyricsView.setPaintHLColor(paintHLColors, false);
        manyLyricsView.setDefText("余音·听者，第二组");


        receiver = new MusicServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONSTANT.ACTION_STATUS);
        filter.addAction(CONSTANT.MUSIC_INFO);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VisualizerHelper.getInstance().addCallBack(ring);
    }


    private void initInfo() throws RemoteException {

        music = mService.getMusic();
        dataBaseUtil = new DataBaseUtil(new DataBaseUtil.DataBaseHelper(MediaPlayer.this, "MusicInfo.db", null, 1));

        //播放模式
        modeFlag = mService.getMode();
        playModeIv.setImageResource(modeResource[modeFlag]);
        //切换播放模式
        playModeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (++modeFlag == modeResource.length)
                    modeFlag = 0;
                playModeIv.setImageResource(modeResource[modeFlag]);
                try {
                    mService.setMode(modeFlag);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        checkCollection();
        collectionMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!collectionFlag) {
                    dataBaseUtil.addMusicToCollectionMusic(music);
                    collectionMusicBtn.setImageResource(R.drawable.ic_baseline_favorite_24);
                    collectionFlag = true;
                } else {
                    dataBaseUtil.deleteMusicFormCollectionMusicByMusicId(music);
                    collectionMusicBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    collectionFlag = false;
                }
            }
        });
        if (music == null) {
            return;
        }
        setMusicInfo(music);
        updateDuration();
        getLrc();
        manyLyricsView.setOnLrcClickListener(new ManyLyricsView.OnLrcClickListener() {
            @Override
            public void onLrcPlayClicked(int progress) {
                try {
                    mService.seekTo(progress);
                    manyLyricsView.seekto(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //旋转动画
        rotation = ObjectAnimator.ofFloat(albumImage, "rotation", 0f, 360.0f);
        rotation.setDuration(12000);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setRepeatCount(-1);

        boolean status = mService.getPlayerStatus();

        if (status) {
            rotation.start();
            playPauseIv.setPlaying(true);
            setSeekBarProcess();
            rotation.start();
        }

        playPauseIv.setPlayPauseListener(new PlayPauseView.PlayPauseListener() {
            @Override
            public void play() {
                setSeekBarProcess();
                try {
                    mService.pauseOrStartMusic();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void pause() {
                try {
                    mService.pauseOrStartMusic();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        progressSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (flag) {
                    try {
                        mService.seekTo(progress * 1000);
                        manyLyricsView.seekto(progress * 1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                flag = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                flag = false;
            }
        });

        prevPlayIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setStatus(false);
                    mService.preMusic();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        nextPlayIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mService.nextMusic();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //检查是否收藏
    private void checkCollection() {

        Music temp = dataBaseUtil.getMusicFormCollectionMusicByMusicId(music.getId());
        if (temp != null) {
            collectionMusicBtn.setImageResource(R.drawable.ic_baseline_favorite_24);
            collectionFlag = true;
        } else {
            collectionMusicBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            collectionFlag = false;
        }
    }

    //更新时间
    private void updateDuration() throws RemoteException {
        int duration = mService.getDuration() / 1000;
        int minutes = duration / 60;
        int seconds = duration % 60;
        String time = String.format(Locale.getDefault(), "%d:%02d",
                minutes, seconds);
        durationTv.setText(time);
        progressSb.setMax(duration);
    }

    //播放列表
    private void showCustomSheet(List<Music> musics) {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_windows_view, null, false);
        popWindowsCount = view.findViewById(R.id.popWindowsCount);
        popWindowsCount.setText("(" + musics.size() + ")");
        RecyclerView recyclerView = view.findViewById(R.id.popWindowsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        PopWindowsListViewAdapter adapter = new PopWindowsListViewAdapter(this, musics, mService);
        recyclerView.setAdapter(adapter);
        int index = adapter.getIndex();
        view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(this, 500)));
        adapter.smoothMoveToPosition(recyclerView, index);
        PopWindow popWindow = new PopWindow.Builder(this)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .addContentView(view)
                .addItemAction(new PopItemAction("关闭", new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                    }
                }))
                .create();
        popWindow.show();
    }

    //ui信息显示
    private void setMusicInfo(Music music) {
        titleIv.setText(music.getName());
        subTitleTv.setText(music.getAr().get(0).getName());
        //专辑
        Glide.with(MediaPlayer.this)
                .load(music.getAl().getPicUrl())
                .placeholder(R.drawable.changpian)
                .transition(new DrawableTransitionOptions().crossFade(1000))
                .apply(RequestOptions.bitmapTransform(new CircleCrop()).override(260, 260))
                .into(albumImage);
        //背景图片
        Glide.with(MediaPlayer.this)
                .load(music.getAl().getPicUrl())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(30, 20)))
                .placeholder(R.color.gray)
                .transition(new DrawableTransitionOptions().crossFade(2000))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        bg.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        if (music == null) {
            return;
        }

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadMusic(music);
            }
        });
    }

    private void downloadMusic(Music music) {
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

                String filename = music.getName() + "-" + music.getAr().get(0).getName() + ".mp3";
                int i = manager.addTask(music.getId() + "", songUrls.get(0).getUrl(), filename, FileUtil.getMusicPath() + File.separator + "download" + File.separator + filename);
                if (i == -1){
                    Toast.makeText(MediaPlayer.this,"文件已经下载",Toast.LENGTH_SHORT).show();
                }
                manager.setSingleTaskListener(music.getId()+"", new DownLoadListener(){
                    @Override
                    public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
                        //开始下载
                    }
                    @Override
                    public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
                        //更新进度
                        long downloadSize = sqlDownLoadInfo.getDownloadSize();
                        long fileSize = sqlDownLoadInfo.getFileSize();
                        String downSizeString = FileUtil.getFormatSize(downloadSize);
                        String fileSizeString = FileUtil.getFormatSize(fileSize);
                        Log.d("DOWNLOAD_MUSIC","总大小："+fileSizeString+"====已经下载："+downSizeString);
                    }
                    @Override
                    public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
                        //停止下载
                    }
                    @Override
                    public void onError(SQLDownLoadInfo sqlDownLoadInfo) {
                        //下载出错
                    }
                    @Override
                    public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
                        //下载成功
                        Toast.makeText(MediaPlayer.this,sqlDownLoadInfo.getFileName()+"下载完成",Toast.LENGTH_LONG).show();
                    }
                });
                ArrayList<TaskInfo> allTask = manager.getAllTask();
                for (TaskInfo taskInfo : allTask) {
                    Log.d("Music_Media",taskInfo.getFileName());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //更新SeekBar
    private void setSeekBarProcess() {
        sThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (status) {
                    Message message = Message.obtain();
                    message.what = 2;
                    mHandler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        setStatus(mService.getPlayerStatus());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        sThread.start();
    }

    //获取歌词
    private void getLrc() {
        manyLyricsView.initLrcData();
        Call<ResponseBody> call = retrofitAPI.lyric(music.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String lyric = JsonParser.parseString(string).getAsJsonObject().get("lrc").getAsJsonObject().get("lyric").getAsString();
                if (lyric.indexOf("[") == -1)
                    return;

                WYLyricsFileReader wyLyricsFileReader = new WYLyricsFileReader();
                boolean fileSupported = wyLyricsFileReader.isFileSupported(lyric);
                if (fileSupported) {
                    return;
                }
                LyricsInfo lyricsInfo = wyLyricsFileReader.readLrcText(null
                        , lyric
                        , null
                        , getExternalCacheDir().getPath() + File.separator + "lyric" + File.separator + music.getId() + ".lrcwy");

                LyricsReader lyricsReader = new LyricsReader();
                lyricsReader.setLyricsInfo(lyricsInfo);
                manyLyricsView.setLyricsReader(lyricsReader);
                manyLyricsView.setSize(50, 50);
                try {
                    manyLyricsView.play(mService.getCurrentPosition());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        setStatus(false);
        VisualizerHelper.getInstance().removeCallBack(ring);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    class MusicServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String action = intent.getAction();
            Log.d("MediaPlayer", "广播");
            switch (action) {
                case CONSTANT.ACTION_STATUS:
                    playStatusChange(bundle);
                    break;
                case CONSTANT.MUSIC_INFO:
                    setMainMusicInfo(bundle);
                    break;
            }

        }

        private void setMainMusicInfo(Bundle bundle) {
            byte[] temp = bundle.getByteArray("MusicInfo");
            music = (Music) ByteObjectUtil.byteArrayToObject(temp);
            getLrc();
            setMusicInfo(music);
            checkCollection();
        }

        private void playStatusChange(Bundle bundle) {
            boolean status = bundle.getBoolean("status");
            if (status) {
                manyLyricsView.resume();
                playPauseIv.setPlaying(true);
                if (!rotation.isStarted()) {
                    rotation.start();
                }
                rotation.resume();
                try {
                    updateDuration();
                    manyLyricsView.seekto(mService.getCurrentPosition());
                    setStatus(mService.getPlayerStatus());
                    setSeekBarProcess();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                manyLyricsView.pause();
                playPauseIv.setPlaying(false);
                rotation.pause();
            }
        }
    }

}