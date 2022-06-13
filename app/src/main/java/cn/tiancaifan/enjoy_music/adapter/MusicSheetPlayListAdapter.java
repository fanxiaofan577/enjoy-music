package cn.tiancaifan.enjoy_music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.bean.Song;

/**
 * @ClassName: MusicSheetPlayListAdapter
 * @Description: TODO
 * @Date: 2022/4/20 21:10
 * @Author: fanxiaofan
 */
public class MusicSheetPlayListAdapter extends RecyclerView.Adapter<MusicSheetPlayListAdapter.SongSheetPlayListViewHolder> {

    private Context context;
    private List<Music> musicList;
    private int index = -1;
    private MusicServiceRemote mService;

    public MusicSheetPlayListAdapter(Context context, List<Music> musicList,MusicServiceRemote mService) {
        this.context = context;
        this.musicList = musicList;
        this.mService = mService;
    }

    @NonNull
    @Override
    public SongSheetPlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_sheet_playlist_item,parent,false);
        SongSheetPlayListViewHolder viewHolder = new SongSheetPlayListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongSheetPlayListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Music temp = null;
        try {
            temp = mService.getMusic();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Music music = musicList.get(position);
        if (temp != null){
            if (temp.getId() == music.getId()){
                index = position;
            }
        }
        holder.name.setText(music.getName());
        String subTitle = null;
        if (music.getAl().getName() != null){

            subTitle= music.getAr().get(0).getName()+" - "+music.getAl().getName();
        }else {
            subTitle= music.getAr().get(0).getName();
        }
        holder.subTitle.setText(subTitle);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index != position){
                    index = position;
                    try {
                        mService.play(music);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                notifyDataSetChanged();
            }
        });
        if (index == position){
            holder.view.setVisibility(View.VISIBLE);
        }else {
            holder.view.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class SongSheetPlayListViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView subTitle;
        View view;
        public SongSheetPlayListViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.v_playing);
            name = itemView.findViewById(R.id.song_sheet_play_list_name);
            subTitle = itemView.findViewById(R.id.song_sheet_play_list_sub_title);
        }
    }
}
