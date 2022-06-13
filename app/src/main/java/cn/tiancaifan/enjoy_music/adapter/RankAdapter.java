package cn.tiancaifan.enjoy_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.TopList;
import cn.tiancaifan.enjoy_music.ui.playlist.SongSheetActivity;

/**
 * @ClassName: RankAdapter
 * @Description: TODO
 * @Date: 2022/4/19 15:56
 * @Author: fanxiaofan
 */
public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {
    private Context context;
    private int resource;
    private List<TopList> topLists;

    public RankAdapter(@NonNull Context context, int resource, @NonNull List<TopList> topLists) {
        this.context = context;
        this.resource = resource;
        this.topLists = topLists;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rank_listview, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        TopList topList = topLists.get(position);
        Glide.with(context).load(topList.getCoverImgUrl()).apply(new RequestOptions().override(280,280)).into(viewHolder.rankListImage);
        viewHolder.rankListTitle.setText(topList.getName());
        viewHolder.rankListDec.setText(topList.getDescription());
        if (topList.getTracks() != null && topList.getTracks().size() != 0) {
            System.out.println(topList.getTracks());
            TopList.Track track0 = topList.getTracks().get(0);
            TopList.Track track1 = topList.getTracks().get(1);
            TopList.Track track2 = topList.getTracks().get(2);

            viewHolder.songFirst.setText("1." + track0.getFirst() + "——" + track0.getSecond());
            viewHolder.songSecond.setText("2." + track1.getFirst() + "——" + track1.getSecond());
            viewHolder.songThird.setText("3." + track2.getFirst() + "——" + track2.getSecond());
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SongSheetActivity.class);
                intent.putExtra("playListId",topList.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return topLists.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView rankListImage;
        TextView rankListTitle;
        TextView rankListDec;
        TextView songFirst;
        TextView songSecond;
        TextView songThird;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankListImage = itemView.findViewById(R.id.rank_list_image);
            rankListTitle = itemView.findViewById(R.id.rank_list_title);
            rankListDec = itemView.findViewById(R.id.rank_list_dec);
            songFirst = itemView.findViewById(R.id.song_frist);
            songSecond = itemView.findViewById(R.id.song_second);
            songThird = itemView.findViewById(R.id.song_third);
        }
    }
}
