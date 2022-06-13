package cn.tiancaifan.enjoy_music.ui.radio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.BannerImageAdapter;
import cn.tiancaifan.enjoy_music.adapter.HotDjAdapter;
import cn.tiancaifan.enjoy_music.adapter.RecommendedSongSheetAdapter;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.BannerBean;
import cn.tiancaifan.enjoy_music.bean.HotDj;
import cn.tiancaifan.enjoy_music.bean.SongSheet;
import cn.tiancaifan.enjoy_music.ui.widget.RecyclerViewAtViewPager2;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RadioFragment extends Fragment {
private RecyclerViewAtViewPager2 hotDj;
private List<HotDj> hotDjs;
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_radio,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initHotDj();
        Banner bannerImage = getView().findViewById(R.id.radio_banner);
        RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
        Call<ResponseBody> responseBodyCall = retrofitAPI.djBanner();
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("BANNER",string);

                JsonArray data = JsonParser.parseString(string).getAsJsonObject().getAsJsonArray("data");
                List<BannerBean> bannerBeans = new Gson().fromJson(data, new TypeToken<List<BannerBean>>() {
                }.getType());


                bannerImage.setAdapter(new BannerImageAdapter(bannerBeans,getContext()));
                bannerImage.addBannerLifecycleObserver(RadioFragment.this);
                bannerImage.setIndicator(new CircleIndicator(getContext()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }
    /**
     * 初始化热门电台
     */
    private void initHotDj(){
        hotDj = getView().findViewById(R.id.hot_list_dj_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        hotDj.setLayoutManager(gridLayoutManager);

        Call<ResponseBody> call = retrofitAPI.hotDj(30,1);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("hotDj",string);

                JsonArray data = JsonParser.parseString(string).getAsJsonObject().getAsJsonArray("djRadios");
                hotDjs = new Gson().fromJson(data, new TypeToken<List<HotDj>>() {
                }.getType());
                HotDjAdapter adapter = new HotDjAdapter(hotDjs,getContext());
                hotDj.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
