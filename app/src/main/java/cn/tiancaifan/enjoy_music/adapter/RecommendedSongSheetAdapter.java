package cn.tiancaifan.enjoy_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.SongSheet;
import cn.tiancaifan.enjoy_music.ui.playlist.SongSheetActivity;
import cn.tiancaifan.enjoy_music.ui.widget.NiceImageView;

/**
 * @ClassName: RecommendedSongSheetAdapter
 * @Description: 推荐歌单 RecyclerView的适配器
 * @Date: 2022/4/7 18:02
 * @Author: fanxiaofan
 */
public class RecommendedSongSheetAdapter extends RecyclerView.Adapter<RecommendedSongSheetAdapter.ViewHolder> {
    private List<SongSheet> songSheets;

    private Context context;

    public RecommendedSongSheetAdapter(List<SongSheet> songSheets, Context context){
        this.songSheets=songSheets;
        this.context =context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_song_sheet_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongSheet songSheet = songSheets.get(position);
        NiceImageView imageView = holder.imageView;
        holder.title.setText(songSheet.getName());
        Glide.with(context).load(songSheet.getPicUrl()+"?param=140y140").apply(new RequestOptions().override(140)).into(imageView);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(songSheet.getId());
                Toast.makeText(context,songSheet.getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, SongSheetActivity.class);
                intent.putExtra("playListId",songSheet.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songSheets.size();
    }



    static class ViewHolder extends RecyclerView.ViewHolder{
        NiceImageView imageView;
        TextView title;
        LinearLayout item;

        public ViewHolder (View view)
        {
            super(view);
            imageView = (NiceImageView) view.findViewById(R.id.recommend_song_sheet_image);
            title = (TextView) view.findViewById(R.id.recommend_song_sheet_title);
            item = view.findViewById(R.id.recommend_song_sheet_item);
        }
    }
}
