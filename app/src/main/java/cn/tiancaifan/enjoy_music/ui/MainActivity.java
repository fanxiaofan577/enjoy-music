package cn.tiancaifan.enjoy_music.ui;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hmy.popwindow.PopItemAction;
import com.hmy.popwindow.PopWindow;

import java.util.ArrayList;
import java.util.List;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.PopWindowsListViewAdapter;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.constant.CONSTANT;
import cn.tiancaifan.enjoy_music.service.MusicServiceRemoteManager;
import cn.tiancaifan.enjoy_music.ui.recommended.RecommendedFragment;
import cn.tiancaifan.enjoy_music.ui.my.MyFragment;
import cn.tiancaifan.enjoy_music.ui.radio.RadioFragment;
import cn.tiancaifan.enjoy_music.ui.rank.RankFragment;
import cn.tiancaifan.enjoy_music.ui.widget.NiceImageView;
import cn.tiancaifan.enjoy_music.ui.widget.PlayPauseView;
import cn.tiancaifan.enjoy_music.utils.ByteObjectUtil;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import cn.tiancaifan.enjoy_music.utils.SizeUtils;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;
import cn.tiancaifan.enjoy_music.utils.download.DownLoadService;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private ViewPager2 vp;
    private PlayPauseView playPauseView;
    private NiceImageView mainAlbum,playlistButton;
    private TextView mainMusicName,popWindowsCount;
    private CardView mainPlayer;

    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
    private MusicServiceRemote mService;
    private Music music;
    private MusicServiceReceiver receiver;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
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
        setContentView(R.layout.activity_main);
        this.startService(new Intent(this, DownLoadService.class));
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
                mService = null;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     *初始化控件
     */
    public void init(){
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        tabLayout = findViewById(R.id.main_tablayout);
        vp = findViewById(R.id.vp);
        playPauseView = findViewById(R.id.main_controller_player);
        mainAlbum = findViewById(R.id.main_album);
        mainMusicName = findViewById(R.id.main_music_name);
        mainPlayer = findViewById(R.id.main_player);
        playlistButton = findViewById(R.id.main_play_list_button);

        setSupportActionBar(toolbar);

        playPauseView.setBtnColor(Color.parseColor("#000000"));

        fragments = new ArrayList<>();
        fragments.add(new MyFragment());
        fragments.add(new RecommendedFragment());
        fragments.add(new RankFragment());
        fragments.add(new RadioFragment());

        titles = new ArrayList<>();
        titles.add("我的");
        titles.add("推荐");
        titles.add("排行榜");
        titles.add("电台");



        //颜色
        int [] colors = {Color.parseColor("#716bff"), Color.parseColor("#d837f6")};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,colors);
        appBarLayout.setBackground(gradientDrawable);
        StatusBarUtil.setGradientColor(MainActivity.this,toolbar);


        playPauseView.setPlayPauseListener(new PlayPauseView.PlayPauseListener() {
            @Override
            public void play() {
                try {
                    mService.pauseOrStartMusic();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void pause() {
                try {
                    mService.pauseOrStartMusic();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        mainPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MediaPlayer.class);
                if (music!=null){
                    i.putExtra("songId",music.getId());
                }
                ActivityOptionsCompat optionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, mainAlbum, "album");
                startActivity(i,optionsCompat.toBundle());
            }
        });

        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showCustomSheet(mService.getListMusic());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //添加适配器
        MyFragmentStateAdapter myFragmentStateAdapter =
                new MyFragmentStateAdapter(this,fragments);

        vp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        vp.setAdapter(myFragmentStateAdapter);

        //默认打开推荐fragment
        vp.setCurrentItem(1);


        new TabLayoutMediator(tabLayout, vp, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles.get(position));
            }
        }).attach();

        try {
            int position = mService.getPosition();
            if (position != -1){
                music = mService.getListMusic().get(position);
                Glide.with(MainActivity.this)
                        .load(MainActivity.this.music.getAl().getPicUrl())
                        .transition(new DrawableTransitionOptions().crossFade(1000))
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()).override(260,260))
                        .into(mainAlbum);
                mainMusicName.setText(music.getName());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        receiver = new MusicServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONSTANT.ACTION_STATUS);
        filter.addAction(CONSTANT.MUSIC_INFO);
        registerReceiver(receiver,filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showCustomSheet(List<Music> musics){
        View view = LayoutInflater.from(this).inflate(R.layout.pop_windows_view, null, false);
        popWindowsCount = view.findViewById(R.id.popWindowsCount);
        popWindowsCount.setText("("+musics.size()+")");
        RecyclerView recyclerView = view.findViewById(R.id.popWindowsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        PopWindowsListViewAdapter adapter = new PopWindowsListViewAdapter(this, musics,mService);
        recyclerView.setAdapter(adapter);
        int index = adapter.getIndex();
        view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(this,500)));
        adapter.smoothMoveToPosition(recyclerView,index);
        PopWindow popWindow = new PopWindow.Builder(this)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .addContentView(view)
                .addItemAction(new PopItemAction("关闭", new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                    }
                }))
                .create();
        popWindow.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_icon:
                startActivity(new Intent(this,SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    class MyFragmentStateAdapter extends FragmentStateAdapter {

        private ArrayList<Fragment> fragments;

        public MyFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity,ArrayList<Fragment> fragments) {
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

    class MusicServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String action = intent.getAction();
            switch (action){
                case CONSTANT.ACTION_STATUS:
                    playStatusChange(bundle);
                    break;
                case CONSTANT.MUSIC_INFO:
                    setMainMusicInfo(bundle);
                    break;
            }

        }
        //修改播放按钮状态
        public void playStatusChange(Bundle bundle){
            boolean status = bundle.getBoolean("status");
            if (status){
                playPauseView.setPlaying(true);
            }else {
                playPauseView.setPlaying(false);
            }
        }

        public void setMainMusicInfo(Bundle bundle){
            byte[] temp = bundle.getByteArray("MusicInfo");
            music = (Music) ByteObjectUtil.byteArrayToObject(temp);
            Glide.with(MainActivity.this)
                    .load(MainActivity.this.music.getAl().getPicUrl())
                    .transition(new DrawableTransitionOptions().crossFade(1000))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()).override(260,260))
                    .into(mainAlbum);
            mainMusicName.setText(music.getName());
        }
    }
}