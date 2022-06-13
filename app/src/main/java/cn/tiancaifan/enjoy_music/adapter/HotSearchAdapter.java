package cn.tiancaifan.enjoy_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yang.flowlayoutlibrary.FlowLayout;

import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.HotSearch;
import cn.tiancaifan.enjoy_music.ui.SearchActivity;
import cn.tiancaifan.enjoy_music.ui.SearchInfoActivity;
import cn.tiancaifan.enjoy_music.utils.DataBaseUtil;


/**
 * @ClassName: HotSearchAdapter
 * @Description: 热搜列表 RecyclerView的适配器
 * @Date: 2022/4/9 17:41
 * @Author: wangjunlin
 */
public class HotSearchAdapter extends RecyclerView.Adapter<HotSearchAdapter.ViewHolder> {
    private Context context;
    private List<HotSearch> hotSearches;
    private DataBaseUtil dataBaseUtil;
    private FlowLayout searchHistory;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        notifyItemChanged(index);
        this.index = index;
    }

    private int index;

    public HotSearchAdapter(List<HotSearch> hotSearches, int index, Context context, FlowLayout searchHistory, DataBaseUtil dataBaseUtil){
        this.hotSearches=hotSearches;
        this.context=context;
        this.index=index;
        this.searchHistory =searchHistory;
        this.dataBaseUtil = dataBaseUtil;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_hot_search_recyclerview_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.index.setText(position+1+"");
        HotSearch hotSearch = hotSearches.get(position);
        ImageView imageView = holder.imageView;
        holder.title.setText(hotSearch.getSearchWord());
        if (position>=0&&position<3){
            holder.index.setTextColor(Color.parseColor("#716bff"));
            holder.title.setTextColor(Color.parseColor("#716bff"));
        }
        Glide.with(context).load(hotSearch.getIconUrl()).into(imageView);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBaseUtil.addKeywordsToSearchHistory(hotSearch.getSearchWord());
                searchHistory.addView(hotSearch.getSearchWord(), new FlowLayout.OnItemClickListener() {
                    @Override
                    public void onItemClick(String content) {
                        Intent intent = new Intent(context, SearchInfoActivity.class);
                        intent.putExtra("keyword",content);
                        context.startActivity(intent);
                    }
                });
                Intent intent = new Intent(context, SearchInfoActivity.class);
                intent.putExtra("keyword",hotSearch.getSearchWord());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return index;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title;
        LinearLayout item;
        TextView index;
        public ViewHolder (View view)
        {
            super(view);
            index = view.findViewById(R.id.hot_search_index);
            imageView = view.findViewById(R.id.hot_search_image);
            title = (TextView) view.findViewById(R.id.hot_search_title);
            item = view.findViewById(R.id.hot_search_item);
        }
    }
}
