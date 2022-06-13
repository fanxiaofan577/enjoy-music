package cn.tiancaifan.enjoy_music.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.MusicSheetCollectionAdapter;
import cn.tiancaifan.enjoy_music.adapter.MusicSheetPlayListAdapter;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.service.MusicServiceRemoteManager;
import cn.tiancaifan.enjoy_music.utils.DataBaseUtil;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;

public class MusicCollectionActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private RecyclerView musicRecyclerView;
    private DataBaseUtil dataBaseUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_collection);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        musicRecyclerView = findViewById(R.id.musicSheetRecyclerView);

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

        MusicServiceRemoteManager manager = new MusicServiceRemoteManager();
        manager.bindToService(this, new ServiceConnection() {
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
        List<Music> musics = dataBaseUtil.getMusicFormCollectionMusic();

        MusicSheetPlayListAdapter adapter = new MusicSheetPlayListAdapter(this, musics,mService);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRecyclerView.setAdapter(adapter);
        musicRecyclerView.setLayoutManager(linearLayoutManager);
    }
}