package cn.tiancaifan.enjoy_music.ui;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.Album;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.bean.Singer;
import cn.tiancaifan.enjoy_music.bean.SongSheet;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;

public class DowLoadActivity extends AppCompatActivity {
private List<Fragment> fragments;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private ViewPager2 vp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dow_load);



        vp = findViewById(R.id.vp);
        tabLayout = findViewById(R.id.main_tablayout);
        appBarLayout = findViewById(R.id.appbar);
         toolbar = findViewById(R.id.toolbarInfo);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fragments = new ArrayList<>();
        fragments.add(new DownloadingFragment());
        fragments.add(new DownloadedFragment());


        List<String> titles = new ArrayList<>();
        titles.add("正在下载");
        titles.add("已下载");


        int [] colors = {Color.parseColor("#716bff"), Color.parseColor("#d837f6")};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,colors);
        appBarLayout.setBackground(gradientDrawable);
        StatusBarUtil.setGradientColor(DowLoadActivity.this,toolbar);



        //添加适配器
        MyFragmentStateAdapter myFragmentStateAdapter =
                new MyFragmentStateAdapter(this, fragments);

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