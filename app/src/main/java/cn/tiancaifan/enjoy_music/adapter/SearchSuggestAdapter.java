package cn.tiancaifan.enjoy_music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.SearchSuggest;

/**
 * @ClassName: SearchSuggestAdapter
 * @Description: TODO
 * @Date: 2022/5/30 20:14
 * @Author: fanxiaofan
 */
public class SearchSuggestAdapter extends RecyclerView.Adapter<SearchSuggestAdapter.ViewHolder> {

    private List<SearchSuggest> searchSuggestList;
    private Context context;

    private OnRvItemClick onRvItemClick;

    public SearchSuggestAdapter(List<SearchSuggest> searchSuggestList, Context context) {
        this.searchSuggestList = searchSuggestList;
        this.context = context;
    }

    public void setOnRvItemClick(OnRvItemClick onRvItemClick){
        this.onRvItemClick = onRvItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggest_recyclerview_item, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchSuggest searchSuggest = searchSuggestList.get(position);
        holder.index.setText((position+1)+"");
        holder.content.setText(searchSuggest.getKeyword());

    }

    @Override
    public int getItemCount() {
        return searchSuggestList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView index,content;

        public ViewHolder(@NonNull View view) {
            super(view);
            index = view.findViewById(R.id.search_suggest_index);
            content = view.findViewById(R.id.search_suggest_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onRvItemClick != null){
                onRvItemClick.onItemClick(v, getLayoutPosition());
            }
        }
    }

    public interface OnRvItemClick {

        void onItemClick(View v, int position);

    }
}
