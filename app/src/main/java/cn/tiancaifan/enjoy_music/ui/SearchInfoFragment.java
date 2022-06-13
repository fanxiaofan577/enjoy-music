package cn.tiancaifan.enjoy_music.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.MusicSheetPlayListAdapter;
import cn.tiancaifan.enjoy_music.adapter.RecommendedSingerSheetAdapter;
import cn.tiancaifan.enjoy_music.adapter.RecommendedSongSheetAdapter;
import cn.tiancaifan.enjoy_music.adapter.SearchInfoAlbumAdapter;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.Album;
import cn.tiancaifan.enjoy_music.bean.Artist;
import cn.tiancaifan.enjoy_music.bean.HotSearch;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.bean.Singer;
import cn.tiancaifan.enjoy_music.bean.SongSheet;
import cn.tiancaifan.enjoy_music.service.MusicServiceRemoteManager;
import cn.tiancaifan.enjoy_music.ui.playlist.SongSheetActivity;
import cn.tiancaifan.enjoy_music.utils.GridAutofitLayoutManager;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import cn.tiancaifan.enjoy_music.utils.SizeUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @ClassName: SearchInfoFragment
 * @Description: TODO
 * @Date: 2022/5/31 11:52
 * @Author: fanxiaofan
 */
public class SearchInfoFragment<T> extends Fragment {
    private RecyclerView musicView, albumView, singerView, songSheetView;
    private List<Music> musicList;
    private List<SongSheet> songSheetList;
    private List<Album> albumList;
    private List<Singer> singerList;
    private String keyword;
    private int resources;
    private static final String MUSIC = "cn.tiancaifan.enjoy_music.bean.Music";
    private static final String SONG_SHEET = "cn.tiancaifan.enjoy_music.bean.SongSheet";
    private static final String SINGER = "cn.tiancaifan.enjoy_music.bean.Singer";
    private static final String ALBUM = "cn.tiancaifan.enjoy_music.bean.Album";
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
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




    public SearchInfoFragment(String keyword, Class<T> cls) {
        this.keyword = keyword;
        String name = cls.getName();
        switch (name) {
            case MUSIC:
                resources = R.layout.search_info_music;
                break;
            case SONG_SHEET:
                resources = R.layout.search_info_song_sheet;
                break;
            case SINGER:
                resources = R.layout.search_info_singer;
                break;
            case ALBUM:
                resources = R.layout.search_info_album;
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
        musicView = getView().findViewById(R.id.search_info_music);
        albumView = getView().findViewById(R.id.search_info_albunm);
        singerView = getView().findViewById(R.id.search_info_singer);
        songSheetView = getView().findViewById(R.id.search_info_song_sheet);

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
            case R.layout.search_info_music://更换为单曲的布局
                initMusic(keyword);
                break;
            case R.layout.search_info_album://更换为专辑的布局
                initAlbum(keyword);
                break;
            case R.layout.search_info_song_sheet://更换为歌单的布局
                initSongSheet(keyword);
                break;
            case R.layout.search_info_singer://更换为歌手的布局
                initSinger(keyword);
                break;
        }
    }

    /**
     * 初始化歌手
     */
    private void initSinger(String keyword) {
        Call<ResponseBody> call = retrofitAPI.search(keyword, 100);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = null;
                try {
                    responseString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("SearchSinger", responseString);
                JsonArray data = JsonParser.parseString(responseString).getAsJsonObject().getAsJsonObject("result").getAsJsonArray("artists");
                singerList = new Gson().fromJson(data, new TypeToken<List<Singer>>() {
                }.getType());
                RecommendedSingerSheetAdapter recommendedSingerSheetAdapter = new RecommendedSingerSheetAdapter(singerList, getActivity());
                singerView.setAdapter(recommendedSingerSheetAdapter);
//                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4,RecyclerView.VERTICAL,false);
                GridAutofitLayoutManager gridAutofitLayoutManager = new GridAutofitLayoutManager(84,getContext());

                singerView.setLayoutManager(gridAutofitLayoutManager);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    /**
     * 初始化歌单
     */
    private void initSongSheet(String keyword) {
        Call<ResponseBody> call = retrofitAPI.search(keyword, 1000);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = null;
                try {
                    responseString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("SearchSongSheet", responseString);
                JsonArray data = JsonParser.parseString(responseString).getAsJsonObject().getAsJsonObject("result").getAsJsonArray("playlists");
                songSheetList = new Gson().fromJson(data, new TypeToken<List<SongSheet>>() {
                }.getType());
                RecommendedSongSheetAdapter recommendedSongSheetAdapter = new RecommendedSongSheetAdapter(songSheetList, getContext());
                GridAutofitLayoutManager gridAutofitLayoutManager = new GridAutofitLayoutManager(84,getContext());
                songSheetView.setLayoutManager(gridAutofitLayoutManager);
                songSheetView.setAdapter(recommendedSongSheetAdapter);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * 初始化专辑
     */
    private void initAlbum(String keyword) {
        Call<ResponseBody> call = retrofitAPI.search(keyword, 10);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = null;
                try {
                    responseString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("SearchAlbum", responseString);
                JsonArray data = JsonParser.parseString(responseString).getAsJsonObject().getAsJsonObject("result").getAsJsonArray("albums");
                albumList = new Gson().fromJson(data, new TypeToken<List<Album>>() {
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

    /**
     * 初始化单曲
     */
    private void initMusic(String keyword) {
        Call<ResponseBody> call = retrofitAPI.search(keyword, 1);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = null;
                try {
                    responseString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("SearchMusic", responseString);
                JsonArray data = JsonParser.parseString(responseString).getAsJsonObject().getAsJsonObject("result").getAsJsonArray("songs");
                musicList = new Gson().fromJson(data, new TypeToken<List<Music>>() {
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
}
