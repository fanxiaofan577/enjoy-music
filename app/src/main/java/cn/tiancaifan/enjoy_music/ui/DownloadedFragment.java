package cn.tiancaifan.enjoy_music.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.PopWindowsListViewAdapter;
import cn.tiancaifan.enjoy_music.bean.LocalMusic;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.service.MusicServiceRemoteManager;
import cn.tiancaifan.enjoy_music.utils.FileUtil;

public class DownloadedFragment extends Fragment {
    private RecyclerView downloadCompleteList;
    private List<Music> musicList = new ArrayList<>();
    private MusicServiceRemote mService;
    private  Handler mHandler;
    @Nullable
@Override
public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    return inflater.inflate(R.layout.download_complete,container,false);
}
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicServiceRemoteManager manager = new MusicServiceRemoteManager();
        manager.bindToService(getContext(), new ServiceConnection() {
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        downloadCompleteList = view.findViewById(R.id.downloadLists);

         mHandler = new Handler(new Handler.Callback() {
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



    }

    private void init() {
        File file = new File(FileUtil.getMusicPath() + File.separator + "download");
        File[] files = file.listFiles();
        if(files!=null){
            for (int i = 0; i < files.length; i++) {
                String name = files[i].getName();
                String substring = name.substring(0, name.indexOf("."));
                String musicName = substring.substring(0, substring.indexOf("-"));
                String singerName = substring.substring(substring.indexOf("-") + 1);
                System.out.println(musicName);
                System.out.println(singerName);
                LocalMusic localMusic = new LocalMusic(singerName, musicName, files[i]);
                musicList.add(localMusic);
            }
        }else {
            String name = file.getName();

        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        PopWindowsListViewAdapter popWindowsListViewAdapter = new PopWindowsListViewAdapter(getContext(), musicList, mService);
        downloadCompleteList.setAdapter(popWindowsListViewAdapter);
        downloadCompleteList.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
