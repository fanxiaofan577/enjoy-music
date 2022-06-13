package cn.tiancaifan.enjoy_music.ui.playlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.MusicSheetPlayListAdapter;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.bean.PlayList;
import cn.tiancaifan.enjoy_music.service.MusicServiceRemoteManager;
import cn.tiancaifan.enjoy_music.ui.MediaPlayer;
import cn.tiancaifan.enjoy_music.ui.widget.NiceImageView;
import cn.tiancaifan.enjoy_music.utils.BlurTransformation;
import cn.tiancaifan.enjoy_music.utils.DataBaseUtil;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @ClassName: SongSheetActivity
 * @Description: 歌单界面Activity
 * @Date: 2022/4/7 20:49
 * @Author: fanxiaofan
 */
public class SongSheetActivity extends AppCompatActivity {
    private Long playListId;
    private AppBarLayout appBarLayout;
    private NiceImageView niceImageView;
    private ImageView collectionSongSheetBtn;
    private Toolbar toolbar;
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
    private PlayList playList;
    private TextView titleTextView;
    private TextView creatorTextView;
    private TextView descriptionTextView;
    private TextView toolbarTitle;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RelativeLayout allPlay;
    private boolean flag = true,collectionSongSheetFlag;
    private MusicServiceRemote mService;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    initPlaylist();
                    break;
                case 2:
                    playAllListener();
                    break;
            }
            return false;
        }
    });
    private List<Music> musicList;
    private DataBaseUtil dataBaseUtil;
    private long albumId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_sheet);
        Bundle bundle = getIntent().getExtras();
        playListId = bundle.getLong("playListId");
        albumId = bundle.getLong("albumId");
        toolbar = findViewById(R.id.song_sheet_toolbar);
        toolbarTitle = findViewById(R.id.song_sheet_toolbar_title);
        collapsingToolbarLayout = findViewById(R.id.toolbar_collapsing);
        collectionSongSheetBtn = findViewById(R.id.collectionSongSheetBtn);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        allPlay = findViewById(R.id.song_sheet_all_play);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        init();
        new MusicServiceRemoteManager().bindToService(this, new ServiceConnection() {
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
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (flag) {
                        toolbarTitle.setText(titleTextView.getText());
                        flag = false;
                    }
                } else if (verticalOffset == 0) {
                    if (!flag) {
                        toolbarTitle.setText("歌单");
                        flag = true;
                    }
                }

            }
        });
    }

    /**
     * 初始化代码
     */
    private void init() {

        niceImageView = findViewById(R.id.song_sheet_appbar_image);
        appBarLayout = findViewById(R.id.song_sheet_appBarlayout);
        titleTextView = findViewById(R.id.song_sheet_title);
        creatorTextView = findViewById(R.id.song_sheet_creator);
        descriptionTextView = findViewById(R.id.song_sheet_description);

        StatusBarUtil.setTransparentForWindow(this);
        dataBaseUtil = new DataBaseUtil(new DataBaseUtil.DataBaseHelper(this, "MusicInfo.db", null, 1));
        Long temp = dataBaseUtil.getRemoteSongSheetFromCollectionSongSheetById(playListId);
        if (temp != null){
            collectionSongSheetBtn.setImageResource(R.drawable.ic_baseline_favorite_24);
            collectionSongSheetFlag = true;
        }
        collectionSongSheetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!collectionSongSheetFlag){
                    dataBaseUtil.addRemoteSongSheetToCollectionSongSheet(playListId);
                    collectionSongSheetBtn.setImageResource(R.drawable.ic_baseline_favorite_24);
                    collectionSongSheetFlag = true;
                }else {
                    dataBaseUtil.deleteRemoteSongSheetFromCollectionSongSheetById(playListId);
                    collectionSongSheetBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    collectionSongSheetFlag = false;

                }
            }
        });


        Call<ResponseBody> call = null;
        if (playListId == 0){
            call = retrofitAPI.playAlbum(albumId);
        }else {
            call = retrofitAPI.playlistDetail(playListId);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("PLAYLIST_DETAIL", "\n" + string);
                JsonObject data = JsonParser.parseString(string).getAsJsonObject().getAsJsonObject("playlist");
                playList = new Gson().fromJson(data, PlayList.class);

                titleTextView.setText(playList.getName());
                creatorTextView.setText(playList.getCreator().getNickname());
                descriptionTextView.setText(playList.getDescription());
                Glide.with(SongSheetActivity.this)
                        .load(playList.getCoverImgUrl())
                        .transition(new DrawableTransitionOptions().crossFade(1000))
                        .into(niceImageView);

                Glide.with(SongSheetActivity.this)
                        .load(playList.getCoverImgUrl())
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

    private void initPlaylist() {
        RecyclerView songListRc = findViewById(R.id.song_sheet_play_list);
        Call<ResponseBody> call = retrofitAPI.playlistTrackAll(playListId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("PLAYLIST_DETAIL", "\n" + string);
                JsonArray data = JsonParser.parseString(string).getAsJsonObject().getAsJsonArray("songs");
                musicList = new Gson().fromJson(data, new TypeToken<List<Music>>() {
                }.getType());

                MusicSheetPlayListAdapter adapter = new MusicSheetPlayListAdapter(SongSheetActivity.this, musicList,mService);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SongSheetActivity.this, LinearLayoutManager.VERTICAL, false);
                songListRc.setAdapter(adapter);
                songListRc.setLayoutManager(linearLayoutManager);
                Message msg = Message.obtain();
                msg.what = 2;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void playAllListener() {
        allPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicList != null) {
                    try {
                        mService.playList(musicList);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                Intent i = new Intent(SongSheetActivity.this, MediaPlayer.class);
                startActivity(i);
            }
        });
    }


}