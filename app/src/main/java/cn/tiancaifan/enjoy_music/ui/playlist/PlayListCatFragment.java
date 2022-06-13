package cn.tiancaifan.enjoy_music.ui.playlist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.RecommendedSongSheetAdapter;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.SongSheet;
import cn.tiancaifan.enjoy_music.utils.GridAutofitLayoutManager;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import cn.tiancaifan.enjoy_music.utils.SizeUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @ClassName: PlayListCatFragment
 * @Description: TODO
 * @Date: 2022/6/6 20:29
 * @Author: fanxiaofan
 */
public class PlayListCatFragment extends Fragment {
    private String cat;
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
    private RecyclerView songSheetRecyclerview;

    public PlayListCatFragment(String cat) {
        this.cat = cat;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_cat, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        int deviceDisplayWidth = SizeUtils.getDeviceDisplayWidth(getActivity());
//        Log.d("getDeviceDisplayWidth",deviceDisplayWidth+"");

        songSheetRecyclerview = getView().findViewById(R.id.song_sheet_recyclerview);
        Call<ResponseBody> call = retrofitAPI.topPlaylist(cat);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("SongSheet", string);

                JsonArray data = JsonParser.parseString(string).getAsJsonObject().getAsJsonArray("playlists");
                List<SongSheet> songSheets = new Gson().fromJson(data, new TypeToken<List<SongSheet>>() {
                }.getType());
                RecommendedSongSheetAdapter adapter = new RecommendedSongSheetAdapter(songSheets, getContext());

//                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false);
                GridAutofitLayoutManager gridAutofitLayoutManager = new GridAutofitLayoutManager(84,getContext());

                songSheetRecyclerview.setLayoutManager(gridAutofitLayoutManager);

                songSheetRecyclerview.setAdapter(adapter);


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


}


