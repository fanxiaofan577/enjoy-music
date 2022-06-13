package cn.tiancaifan.enjoy_music.ui.playlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.Album;
import cn.tiancaifan.enjoy_music.bean.Category;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.bean.Singer;
import cn.tiancaifan.enjoy_music.bean.SongSheet;
import cn.tiancaifan.enjoy_music.ui.SearchInfoActivity;
import cn.tiancaifan.enjoy_music.ui.SearchInfoFragment;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import cn.tiancaifan.enjoy_music.utils.SizeUtils;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistCatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private ViewPager2 vp;
    private List<Fragment> fragments;
    private TabLayout tabLayout;
    private List<String> titles;
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_cat);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        vp = findViewById(R.id.vp);
        tabLayout = findViewById(R.id.main_tablayout);

        //颜色
        int [] colors = {Color.parseColor("#716bff"), Color.parseColor("#d837f6")};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,colors);
        appBarLayout.setBackground(gradientDrawable);
        StatusBarUtil.setGradientColor(PlaylistCatActivity.this,toolbar);

        init();

    }

    private void init(){
        Call<ResponseBody> call = retrofitAPI.playlistCatlist();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String resp = null;
                try {
                    resp = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JsonArray data = JsonParser.parseString(resp).getAsJsonObject().get("sub").getAsJsonArray();
                List<Category> categories = new Gson().fromJson(data, new TypeToken<List<Category>>() {
                }.getType());
                fragments = new ArrayList<>();
                titles = new ArrayList<>();
                for (Category category : categories) {
                    fragments.add(new PlayListCatFragment(category.getName()));
                    titles.add(category.getName());
                }

                //添加适配器
                PlaylistCatActivity.MyFragmentStateAdapter myFragmentStateAdapter =
                        new PlaylistCatActivity.MyFragmentStateAdapter(PlaylistCatActivity.this,fragments);

                vp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                vp.setAdapter(myFragmentStateAdapter);
                new TabLayoutMediator(tabLayout, vp, new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(titles.get(position));
                    }
                }).attach();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }


        });
    }

    class MyFragmentStateAdapter extends FragmentStateAdapter {

        private List<Fragment> fragments;

        public MyFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
            super(fragmentActivity);
            this.fragments = fragments;
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }
    }
}