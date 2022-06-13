package cn.tiancaifan.enjoy_music.ui.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hmy.popwindow.PopItemAction;
import com.hmy.popwindow.PopWindow;

import java.io.File;
import java.util.ArrayList;

import cn.tiancaifan.enjoy_music.R;

import cn.tiancaifan.enjoy_music.ui.AboutActivity;
import cn.tiancaifan.enjoy_music.ui.DowLoadActivity;
import cn.tiancaifan.enjoy_music.ui.HistoryActivity;
import cn.tiancaifan.enjoy_music.ui.MainActivity;
import cn.tiancaifan.enjoy_music.ui.MusicCollectionActivity;
import cn.tiancaifan.enjoy_music.ui.MusicSheetCollectionActivity;
import cn.tiancaifan.enjoy_music.ui.widget.LocalItemView;
import cn.tiancaifan.enjoy_music.utils.DataBaseUtil;
import cn.tiancaifan.enjoy_music.utils.FileUtil;
import cn.tiancaifan.enjoy_music.utils.download.DownLoadManager;
import cn.tiancaifan.enjoy_music.utils.download.DownLoadService;
import cn.tiancaifan.enjoy_music.utils.download.TaskInfo;

public class MyFragment extends Fragment implements View.OnClickListener {

    private DataBaseUtil dataBaseUtil;
    private LocalItemView historyLocalItemView,songSheetLocalItemView,musicLocalItemView,downloadLocalItemView;
    private DownLoadManager manager;
    private LinearLayout cardAboutView,cardExitView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        manager = DownLoadService.getDownLoadManager();
        return inflater.inflate(R.layout.fragment_my,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historyLocalItemView = getView().findViewById(R.id.historyLocalItemView);
        songSheetLocalItemView = getView().findViewById(R.id.songSheetLocalItemView);
        musicLocalItemView = getView().findViewById(R.id.musicLocalItemView);
        downloadLocalItemView = getView().findViewById(R.id.downloadLocalItemView);

        cardAboutView = getView().findViewById(R.id.cardAboutView);
        cardExitView = getView().findViewById(R.id.cardExitView);


        cardAboutView.setOnClickListener(this);
        cardExitView.setOnClickListener(this);
        songSheetLocalItemView.setOnClickListener(this);
        downloadLocalItemView.setOnClickListener(this);
        musicLocalItemView.setOnClickListener(this);

        File file = new File(FileUtil.getMusicPath() + File.separator + "download");
        File[] files = file.listFiles();
        if (files!=null)
            downloadLocalItemView.setSongsNum(files.length,0);
        dataBaseUtil = new DataBaseUtil(new DataBaseUtil.DataBaseHelper(getContext(), "MusicInfo.db", null, 1));

    }

    @Override
    public void onStart() {
        super.onStart();
        historyLocalItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HistoryActivity.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        int historyCount = dataBaseUtil.getHistoryCount();
        historyLocalItemView.setSongsNum(historyCount,0);
        int collectionMusicCount = dataBaseUtil.getCollectionMusicCount();
        musicLocalItemView.setSongsNum(collectionMusicCount,0);
        int remoteSongSheetCount = dataBaseUtil.getRemoteSongSheetCount();
        songSheetLocalItemView.setSongSheetNum(remoteSongSheetCount,"个");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.cardAboutView:
                startActivity(new Intent(getContext(), AboutActivity.class));
                break;
            case R.id.cardExitView:
                PopWindow popWindow = new PopWindow.Builder(getActivity())
                        .setStyle(PopWindow.PopWindowStyle.PopUp)
                        .setTitle("注意")
                        .setMessage("这将停止音乐播放")
                        .addItemAction(new PopItemAction("确定", PopItemAction.PopItemStyle.Warning, new PopItemAction.OnClickListener() {
                            @Override
                            public void onClick() {
                                System.exit(0);
                            }
                        }))
                        .addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Cancel))
                        .create();
                popWindow.show();
                break;
            case R.id.songSheetLocalItemView:
                startActivity(new Intent(getContext(), MusicSheetCollectionActivity.class));
                break;
            case R.id.musicLocalItemView:
                startActivity(new Intent(getContext(), MusicCollectionActivity.class));
                break;
            case R.id.downloadLocalItemView:
                startActivity(new Intent(getContext(), DowLoadActivity.class));
                break;
        }
    }
}
