package cn.tiancaifan.enjoy_music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.RemoteException;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.LocalMusic;
import cn.tiancaifan.enjoy_music.bean.Music;

/**
 * @ClassName: PopWindowsListViewAdapter
 * @Description: TODO
 * @Date: 2022/5/19 17:22
 * @Author: fanxiaofan
 */
public class PopWindowsListViewAdapter extends RecyclerView.Adapter<PopWindowsListViewAdapter.ViewHolder> {
    private Context context;
    private List<Music> musics;
    //目标项是否在最后一个可见项以后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    public int getIndex() {
        try {
            index = mService.getPosition();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return index;
    }

    private int index = -1;
    private MusicServiceRemote mService;


    public PopWindowsListViewAdapter(@NonNull Context context, @NonNull List<Music> musics, MusicServiceRemote mService) {
        this.context = context;
        this.musics = musics;
        this.mService = mService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_window_listview_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        Music music = musics.get(position);
        try {
            if(mService.getMusic().getId()==music.getId()){
                index = mService.getPosition();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        viewHolder.playListIndex.setText(position+1+"");
        viewHolder.playListMusicName.setText(music.getName());
        if(music.getAr()!=null)
            viewHolder.arName.setText(" - "+music.getAr().get(0).getName());
        else
            viewHolder.arName.setText(" - "+ ((LocalMusic) music).getSinger());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = position;
                try {
                    mService.play(music);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }
        });
        if (index == position){
            viewHolder.playListIndex.setTextColor(Color.RED);
            viewHolder.playListMusicName.setTextColor(Color.RED);
            viewHolder.arName.setTextColor(Color.RED);
        }else {
            viewHolder.playListIndex.setTextColor(Color.BLACK);
            viewHolder.playListMusicName.setTextColor(Color.BLACK);
            viewHolder.arName.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView playListIndex;
        TextView playListMusicName;
        TextView arName;
        ImageView playListDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playListIndex = itemView.findViewById(R.id.popWindowsIndex);
            playListMusicName = itemView.findViewById(R.id.popWindowsName);
            arName = itemView.findViewById(R.id.arName);
            playListDelete = itemView.findViewById(R.id.delete);
        }
    }

    /** * 滑动到指定位置 */
    public void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置以前，使用smoothScrollToPosition
            mRecyclerView.scrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置以后，最后一个可见项以前
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项以后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再经过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.scrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }
}
