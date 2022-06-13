package cn.tiancaifan.enjoy_music.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.Album;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.bean.SingerDetails;
import cn.tiancaifan.enjoy_music.ui.widget.NiceImageView;
import cn.tiancaifan.enjoy_music.utils.BlurTransformation;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingerDetailActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private NiceImageView singer_image;
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
    private  SingerDetails singerDetails;
    private Long singerId;
    private List<Fragment> fragments;
    private ViewPager2 vp;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private TextView toolbarTitle,status;
    private TextView singer_name;

    private boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_details);
        Bundle bundle = getIntent().getExtras();
        singerId = bundle.getLong("singerId");
        Log.d("getSingerDetails1",singerId+"");

        singer_image = findViewById(R.id.singer_image);
        toolbar = findViewById(R.id.song_sheet_toolbar);
        singer_name = findViewById(R.id.singer_name);
        vp = findViewById(R.id.vp1);
        collapsingToolbarLayout = findViewById(R.id.toolbar_collapsing);
        appBarLayout = findViewById(R.id.singer_appBarlayout);
        toolbarTitle = findViewById(R.id.song_sheet_toolbar_title);
        tabLayout = findViewById(R.id.main_tablayout);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSingerDetails(singerId);
        fragments = new ArrayList<>();
        fragments.add(new SingerDetailsFragment<Music>(singerId,Music.class));
        fragments.add(new SingerDetailsFragment<Album>(singerId,Album.class));
        fragments.add(new SingerDetailsFragment<SingerDetails>(singerId,SingerDetails.class));


        //添加适配器

        MyFragmentStateAdapter myFragmentStateAdapter =
                new MyFragmentStateAdapter(this, fragments);

        vp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        vp.setAdapter(myFragmentStateAdapter);

        List<String> titles = new ArrayList<>();
        titles.add("单曲");
        titles.add("专辑");
        titles.add("歌手详情");


        new TabLayoutMediator(tabLayout, vp, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles.get(position));
            }
        }).attach();


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (flag) {
                        toolbarTitle.setText(singer_name.getText());
                        singer_name.setVisibility(View.INVISIBLE);
                        flag = false;
                    }
                } else if (verticalOffset == 0) {
                    if (!flag) {
                        toolbarTitle.setText("歌手");
                        singer_name.setVisibility(View.VISIBLE);
                        flag = true;
                    }
                }

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

    /**
     * 初始化歌手详情
     * */
    private void getSingerDetails(Long singerId){
        Log.d("getSingerDetails",singerId+"");
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
                singer_name.setText(singerDetails.getName());

                Glide.with(SingerDetailActivity.this)
                        .load(singerDetails.getCover())
                        .transition(new DrawableTransitionOptions().crossFade(1000))
                        .into(singer_image);

                Glide.with(SingerDetailActivity.this)
                        .load(singerDetails.getCover())
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(30, 10)))
                        .transition(new DrawableTransitionOptions().crossFade(2000))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                collapsingToolbarLayout.setBackground(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    }
