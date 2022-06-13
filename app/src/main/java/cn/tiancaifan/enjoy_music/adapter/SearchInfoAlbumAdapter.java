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
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.bean.Album;
import cn.tiancaifan.enjoy_music.bean.Singer;
import cn.tiancaifan.enjoy_music.ui.playlist.SongSheetActivity;
import cn.tiancaifan.enjoy_music.ui.widget.NiceImageView;

public class SearchInfoAlbumAdapter extends RecyclerView.Adapter<SearchInfoAlbumAdapter.ViewHolder>{
    private List<Album> albumList;

    private Context context;
    public SearchInfoAlbumAdapter(List<Album> albumList, Context context){
        this.albumList=albumList;
        this.context=context;
    }
    @NonNull
    @Override
    public SearchInfoAlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_info_album_item,parent,false);
        SearchInfoAlbumAdapter.ViewHolder holder = new SearchInfoAlbumAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchInfoAlbumAdapter.ViewHolder holder, int position) {
        Album album = albumList.get(position);
        NiceImageView imageView = holder.imageView;
        holder.albumName.setText(album.getName());
        holder.artistName.setText(album.getArtist().getName());
        Glide.with(context).load(album.getPicUrl()+"?param=140y140").apply(new RequestOptions().override(140)).into(imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SongSheetActivity.class);
                intent.putExtra("albumId",album.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        NiceImageView imageView;
        TextView albumName,artistName;


        public ViewHolder (View view)
        {
            super(view);
            imageView = (NiceImageView) view.findViewById(R.id.search_info_album_image);
            albumName = (TextView) view.findViewById(R.id.search_info_album_name);
            artistName = view.findViewById(R.id.search_info_album_artist_name);
        }
    }
}
