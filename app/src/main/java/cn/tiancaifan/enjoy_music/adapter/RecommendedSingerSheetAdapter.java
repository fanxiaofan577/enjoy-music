package cn.tiancaifan.enjoy_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.Singer;
import cn.tiancaifan.enjoy_music.ui.SingerDetailActivity;
import cn.tiancaifan.enjoy_music.ui.widget.NiceImageView;

public class RecommendedSingerSheetAdapter extends RecyclerView.Adapter<RecommendedSingerSheetAdapter.ViewHolder>{

    private List<Singer> singerSheets;

    private Context context;
    public RecommendedSingerSheetAdapter(List<Singer> singerList, Context context){
        this.singerSheets=singerList;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singer_recommended,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Singer singer = singerSheets.get(position);
        NiceImageView imageView = holder.imageView;
        holder.title.setText(singer.getName());
        Glide.with(context).load(singer.getPicUrl()+"?param=130y130").apply(new RequestOptions()
                .override(130, 130)).into(imageView);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(singer.getId());
                Intent intent = new Intent(context, SingerDetailActivity.class);
                intent.putExtra("singerId",singer.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return singerSheets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        NiceImageView imageView;
        TextView title;
        LinearLayout item;

        public ViewHolder (View view)
        {
            super(view);
            imageView = (NiceImageView) view.findViewById(R.id.recommend_singer_sheet_image);
            title = (TextView) view.findViewById(R.id.recommend_singer_name);
            item = view.findViewById(R.id.recommend_singer_sheet_item);
        }
    }
}
