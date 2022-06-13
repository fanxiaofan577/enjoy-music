package cn.tiancaifan.enjoy_music.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.MusicSheetPlayListAdapter;
import cn.tiancaifan.enjoy_music.adapter.SearchInfoAlbumAdapter;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.Album;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.bean.SingerDetails;
import cn.tiancaifan.enjoy_music.service.MusicServiceRemoteManager;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingerDetailsFragment<T> extends Fragment {
    private RecyclerView musicView, albumView;
    private Long singerId;
    private SingerDetails singerDetails;
    private int resources;
    private List<Album> albumList;
    private List<Music> musicList;
    private TextView singerDetail;
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
    private static final String MUSIC = "cn.tiancaifan.enjoy_music.bean.Music";
    private static final String SINGERDETAILS = "cn.tiancaifan.enjoy_music.bean.SingerDetails";
    private static final String ALBUM = "cn.tiancaifan.enjoy_music.bean.Album";
    private MusicServiceRemote mService;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    init();
                    break;
            }
            return false;
        }
    });

    public SingerDetailsFragment(Long singerId, Class<T> cls) {
        this.singerId = singerId;
        String name = cls.getName();
        switch (name) {
            case MUSIC:
                resources = R.layout.singer_music;
                break;
            case SINGERDETAILS:
                resources = R.layout.singer_details;
                break;
            case ALBUM:
                resources = R.layout.singer_album;
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(resources, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicView = getView().findViewById(R.id.singer_music);
        albumView = getView().findViewById(R.id.singer_album);
        singerDetail = getView().findViewById(R.id.singer_details);
        MusicServiceRemoteManager manager = new MusicServiceRemoteManager();
        manager.bindToService(getContext(), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = MusicServiceRemote.Stub.asInterface(service);
                Message msg = Message.obtain();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });
    }

    private void init() {
        switch (resources) {
            case R.layout.singer_music://更换为单曲的布局
                getSingerMusic(singerId);
                break;
            case R.layout.singer_album://更换为专辑的布局
                getSingerAlbum(singerId);
                break;
            case R.layout.singer_details://更换为歌手详情的布局
                getSingerDetails(singerId);
                break;

        }
    }


    /**
     * 初始化歌手详情
     */
    private void getSingerDetails(Long singerId) {
        Log.d("getSingerDetails", singerId + "");
        Call<ResponseBody> call = retrofitAPI.singerDetails(singerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("singerDetails", "\n" + string);
                JsonObject data = JsonParser.parseString(string).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("artist");
                singerDetails = new Gson().fromJson(data, SingerDetails.class);
                String briefDesc = singerDetails.getBriefDesc();
                briefDesc = briefDesc.replace("\n","\n\u3000\u3000");
                briefDesc = "\u3000\u3000"+briefDesc;
                singerDetail.setText(briefDesc);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * 初始化歌手单曲*/
    private void getSingerMusic(Long singerId) {

        Call<ResponseBody> call = retrofitAPI.singerSong(singerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("singerDetails", "\n" + string);
                JsonArray hotSongs = JsonParser.parseString(string).getAsJsonObject().getAsJsonArray("hotSongs");
                musicList = new Gson().fromJson(hotSongs, new TypeToken<List<Music>>() {
                }.getType());
                MusicSheetPlayListAdapter musicSheetPlayListAdapter = new MusicSheetPlayListAdapter(getActivity(), musicList,mService);
                musicView.setAdapter(musicSheetPlayListAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                musicView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


            /**
     * 初始化歌手专辑*/
            private void getSingerAlbum(Long singerId) {

                Call<ResponseBody> call = retrofitAPI.singerAlbum(singerId);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String string = null;
                        try {
                            string = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("singerDetails", "\n" + string);
                        JsonArray hotAlbums = JsonParser.parseString(string).getAsJsonObject().getAsJsonArray("hotAlbums");
                        albumList = new Gson().fromJson(hotAlbums, new TypeToken<List<Album>>() {
                        }.getType());
                        SearchInfoAlbumAdapter searchInfoAlbumAdapter = new SearchInfoAlbumAdapter(albumList, getContext());
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),LinearLayoutManager.VERTICAL);
                        albumView.setLayoutManager(gridLayoutManager);
                        albumView.setAdapter(searchInfoAlbumAdapter);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
}
