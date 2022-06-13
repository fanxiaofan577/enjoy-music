package cn.tiancaifan.enjoy_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.ui.MediaPlayer;
import cn.tiancaifan.enjoy_music.ui.widget.NiceImageView;

/**
 * @ClassName: RecommendedSongSheetAdapter
 * @Description: 推荐歌单 RecyclerView的适配器
 * @Date: 2022/4/7 18:02
 * @Author: fanxiaofan
 */
public class RecommendedMusicListAdapter extends RecyclerView.Adapter<RecommendedMusicListAdapter.ViewHolder> {
    private List<Music> musicList;

    private Context context;
    private MusicServiceRemote mService;

    public RecommendedMusicListAdapter(List<Music> musicList, MusicServiceRemote mService, Context context){
        this.musicList=musicList;
        this.context =context;
        this.mService = mService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_song_list_recyclerview_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = musicList.get(position);
        NiceImageView imageView = holder.imageView;
        holder.name.setText(music.getName());
        Glide.with(context).load(music.getAl().getPicUrl()+"?param=130y130").apply(new RequestOptions().override(130,130)).into(imageView);
        holder.artistName.setText(music.getAr().get(0).getName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mService.play(music);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context, MediaPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putLong("songId",music.getId());
                intent.putExtras(bundle);
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }



    static class ViewHolder extends RecyclerView.ViewHolder{
        NiceImageView imageView;
        TextView name;
        TextView artistName;
        CardView item;

        public ViewHolder (View view)
        {
            super(view);
            imageView = (NiceImageView) view.findViewById(R.id.song_list_image);
            name = (TextView) view.findViewById(R.id.recommend_song_list_name);
            artistName = view.findViewById(R.id.recommend_song_list_ar);
            item = view.findViewById(R.id.recommend_song_list_item);
        }
    }
}
