package cn.tiancaifan.enjoy_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.PlayList;
import cn.tiancaifan.enjoy_music.ui.MusicSheetCollectionActivity;
import cn.tiancaifan.enjoy_music.ui.playlist.SongSheetActivity;
import cn.tiancaifan.enjoy_music.ui.widget.NiceImageView;
import cn.tiancaifan.enjoy_music.utils.BlurTransformation;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @ClassName: MusicSheetCollectionAdapter
 * @Description: TODO
 * @Date: 2022/6/12 11:11
 * @Author: fanxiaofan
 */
public class MusicSheetCollectionAdapter extends RecyclerView.Adapter<MusicSheetCollectionAdapter.MusicSheetCollectionViewHolder>{
    private Context context;
    private List<Long> ids;
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);

    public MusicSheetCollectionAdapter(Context context, List<Long> ids){
        this.ids = ids;
        this.context = context;
    }

    @NonNull
    @Override
    public MusicSheetCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_info_album_item, parent, false);
        return new MusicSheetCollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicSheetCollectionViewHolder holder, int position) {
        Long id = ids.get(position);
        Call<ResponseBody> call = retrofitAPI.playlistDetail(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("PLAYLIST_DETAIL", "\n" + string);
                JsonObject data = JsonParser.parseString(string).getAsJsonObject().getAsJsonObject("playlist");
                PlayList playList = new Gson().fromJson(data, PlayList.class);

                holder.name.setText(playList.getName());
                holder.info.setText(playList.getDescription());
                Glide.with(context)
                        .load(playList.getCoverImgUrl())
                        .apply(new RequestOptions().override(52,52))
                        .transition(new DrawableTransitionOptions().crossFade(1000))
                        .into(holder.image);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SongSheetActivity.class);
                        intent.putExtra("playListId",playList.getId());
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }

    class MusicSheetCollectionViewHolder extends RecyclerView.ViewHolder{
        public NiceImageView image;
        public TextView name,info;

        public MusicSheetCollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.search_info_album_image);
            name = itemView.findViewById(R.id.search_info_album_name);
            info = itemView.findViewById(R.id.search_info_album_artist_name);
        }
    }
}
