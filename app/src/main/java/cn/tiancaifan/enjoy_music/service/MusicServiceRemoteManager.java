package cn.tiancaifan.enjoy_music.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;

/**
 * @ClassName: MusicServiceRemoteManager
 * @Description: TODO
 * @Date: 2022/5/11 16:20
 * @Author: fanxiaofan
 */
public class MusicServiceRemoteManager {



    public  void bindToService(Context context,ServiceConnection conn){
        Intent intent = new Intent(context, MusicService.class);
        context.bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    public void unBindToService(Context context){
    }
}
