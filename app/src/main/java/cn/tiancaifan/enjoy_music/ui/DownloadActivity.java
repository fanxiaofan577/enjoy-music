package cn.tiancaifan.enjoy_music.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.DownloadingAdapter;
import cn.tiancaifan.enjoy_music.utils.download.DownLoadManager;
import cn.tiancaifan.enjoy_music.utils.download.DownLoadService;

public class DownloadActivity extends AppCompatActivity {


    private ListView downloadList;
    private DownloadingAdapter adapter;

    private DownLoadManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_download);
        this.startService(new Intent(this, DownLoadService.class));
        //下载管理器需要启动一个Service,在刚启动应用的时候需要等Service启动起来后才能获取下载管理器，所以稍微延时获取下载管理器
        handler.sendEmptyMessageDelayed(1, 50);
        downloadList = findViewById(R.id.downloadList);


    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /*获取下载管理器*/
            manager = DownLoadService.getDownLoadManager();
            /*断点续传需要服务器的支持，设置该项时要先确保服务器支持断点续传功能*/
            manager.setSupportBreakpoint(true);
            adapter = new DownloadingAdapter(DownloadActivity.this,manager);
            downloadList.setAdapter(adapter);
        }
    };

}