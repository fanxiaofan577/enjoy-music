package cn.tiancaifan.enjoy_music.ui.recommended;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import java.io.IOException;
import java.util.List;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.BannerImageAdapter;
import cn.tiancaifan.enjoy_music.adapter.RecommendedSingerSheetAdapter;
import cn.tiancaifan.enjoy_music.adapter.RecommendedMusicListAdapter;
import cn.tiancaifan.enjoy_music.adapter.RecommendedSongSheetAdapter;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.BannerBean;
import cn.tiancaifan.enjoy_music.bean.Singer;
import cn.tiancaifan.enjoy_music.bean.SongSheet;
import cn.tiancaifan.enjoy_music.service.MusicServiceRemoteManager;
import cn.tiancaifan.enjoy_music.ui.playlist.PlaylistCatActivity;
import cn.tiancaifan.enjoy_music.ui.playlist.SongSheetActivity;
import cn.tiancaifan.enjoy_music.ui.widget.RecyclerViewAtViewPager2;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendedFragment extends Fragment implements View.OnClickListener {

    private List<SongSheet> songSheets;
    private List<Singer> singerSheets;
    private TextView rePlayList;

    private int index = 0;

    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
    private Banner bannerImage;
    private RecyclerViewAtViewPager2 songSheet;
    private RecyclerViewAtViewPager2 singerSheet;
    private RecyclerViewAtViewPager2 songListRecycler;
    private TextView songlistTitle,seeAllSongSheet;
    private MusicServiceRemote mService;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = MusicServiceRemote.Stub.asInterface(service);
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommended,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seeAllSongSheet = getView().findViewById(R.id.seeAllSongSheet);
        initBanner();
        initSongSheet();
        initSingerSheet();
        rePlayList = getView().findViewById(R.id.recommend_re_playlist);
        rePlayList.setOnClickListener(this);

        MusicServiceRemoteManager manager = new MusicServiceRemoteManager();
        manager.bindToService(getContext(), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = MusicServiceRemote.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        });
        seeAllSongSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PlaylistCatActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 初始化banner
     */
    private void initBanner(){
        bannerImage = getView().findViewById(R.id.recommend_banner);

        Call<ResponseBody> call = retrofitAPI.banner(1);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("BANNER",string);

                JsonArray data = JsonParser.parseString(string).getAsJsonObject().getAsJsonArray("banners");
                List<BannerBean> bannerBeans = new Gson().fromJson(data, new TypeToken<List<BannerBean>>() {
                }.getType());

                bannerImage.setAdapter(new BannerImageAdapter(bannerBeans,getContext()));
                bannerImage.addBannerLifecycleObserver(RecommendedFragment.this);
                bannerImage.setIndicator(new CircleIndicator(getContext()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * 初始化推荐歌单
     */
    private void initSongSheet(){
        songSheet = getView().findViewById(R.id.recommend_song_sheet_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        songSheet.setLayoutManager(gridLayoutManager);

        Call<ResponseBody> call = retrofitAPI.personalized(30);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("SongSheet",string);

                JsonArray data = JsonParser.parseString(string).getAsJsonObject().getAsJsonArray("result");
                songSheets = new Gson().fromJson(data, new TypeToken<List<SongSheet>>() {
                }.getType());
                RecommendedSongSheetAdapter adapter = new RecommendedSongSheetAdapter(songSheets, getContext());
                songSheet.setAdapter(adapter);
                initSongList(songSheets.get(0).getId(),songSheets.get(0).getName());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * 初始化歌曲列表
     */

    private void initSongList(Long id,String title){
        songListRecycler = getView().findViewById(R.id.recommend_song_list_recyclerview);
        songlistTitle = getView().findViewById(R.id.recommend_song_list_title);
        songlistTitle.setText(title);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        songListRecycler.setLayoutManager(gridLayoutManager);

        getPlayList(id);
    }

    private void getPlayList(Long id) {
        Call<ResponseBody> call = retrofitAPI.playlistDetail(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("SongSheet",string);

                JsonObject data = JsonParser.parseString(string).getAsJsonObject().getAsJsonObject("playlist");
                SongSheet songSheet = new Gson().fromJson(data, SongSheet.class);
                RecommendedMusicListAdapter recommendedMusicListAdapter = new RecommendedMusicListAdapter(songSheet.getTracks(),mService,getContext());
                songListRecycler.setAdapter(recommendedMusicListAdapter);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    //歌单列表换一换
    private void rePlayList(){
        //回滚下标
        index++;
        if (index == songSheets.size()){
            index = 0;
        }
        SongSheet songSheet = songSheets.get(index);
        songlistTitle.setText(songSheet.getName());
        getPlayList(songSheet.getId());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recommend_re_playlist:
                rePlayList();
                break;
        }
    }

    /**
     * 初始化推荐歌手
     */
    private void initSingerSheet(){
        singerSheet = getView().findViewById(R.id.recommend_singer_sheet_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        singerSheet.setLayoutManager(gridLayoutManager);

        Call<ResponseBody> call = retrofitAPI.toplistArtist();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("SingerSheet",string);

                JsonArray data = JsonParser.parseString(string).getAsJsonObject().getAsJsonObject("list").getAsJsonArray("artists");
                singerSheets = new Gson().fromJson(data, new TypeToken<List<Singer>>() {
                }.getType());
                RecommendedSingerSheetAdapter adapter = new RecommendedSingerSheetAdapter(singerSheets, getContext());
                singerSheet.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
