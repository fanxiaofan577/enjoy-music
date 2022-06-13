package cn.tiancaifan.enjoy_music.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;

public class AboutActivity extends AppCompatActivity {
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbarlayout);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //颜色
        int [] colors = {Color.parseColor("#716bff"), Color.parseColor("#d837f6")};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,colors);
        appBarLayout.setBackground(gradientDrawable);
        StatusBarUtil.setGradientColor(this,toolbar);
    }
}