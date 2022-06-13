package cn.tiancaifan.enjoy_music.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;


import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.MusicSheetCollectionAdapter;
import cn.tiancaifan.enjoy_music.utils.DataBaseUtil;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;

public class MusicSheetCollectionActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private RecyclerView musicSheetRecyclerView;
    private DataBaseUtil dataBaseUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_sheet_collection);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        musicSheetRecyclerView = findViewById(R.id.musicSheetRecyclerView);

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

        dataBaseUtil = new DataBaseUtil(new DataBaseUtil.DataBaseHelper(this, "MusicInfo.db", null, 1));

        List<Long> ids = dataBaseUtil.getRemoteSongSheet();

        MusicSheetCollectionAdapter adapter = new MusicSheetCollectionAdapter(this, ids);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        musicSheetRecyclerView.setLayoutManager(manager);
        musicSheetRecyclerView.setAdapter(adapter);
    }
}