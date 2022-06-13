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

import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.HotDj;
import cn.tiancaifan.enjoy_music.bean.SongSheet;
import cn.tiancaifan.enjoy_music.ui.playlist.SongSheetActivity;
import cn.tiancaifan.enjoy_music.ui.widget.NiceImageView;
/**
 * @ClassName: HotDjAdapter
 * @Description: 热门电台 RecyclerView的适配器
 * @Date: 2022/4/17 20:41
 * @Author: wangjunlin
 */
public class HotDjAdapter extends RecyclerView.Adapter<HotDjAdapter.ViewHolder> {

    private List<HotDj> hotDjs;
    private Context context;
    public HotDjAdapter(List<HotDj> hotDjs,Context context){
        this.hotDjs=hotDjs;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_list_dj,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HotDj hotDj = hotDjs.get(position);
        NiceImageView imageView = holder.imageView;
        holder.title.setText(hotDj.getName());
        Glide.with(context).load(hotDj.getPicUrl()).into(imageView);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return hotDjs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        NiceImageView imageView;
        TextView title;
        LinearLayout item;

        public ViewHolder (View view)
        {
            super(view);
            imageView = (NiceImageView) view.findViewById(R.id.radio_hot_dj_image);
            title = (TextView) view.findViewById(R.id.radio_ht_dj_title);
            item = view.findViewById(R.id.hot_dj_item);
        }
    }
}
