package cn.tiancaifan.enjoy_music.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.util.BannerUtils;

import java.util.List;

import cn.tiancaifan.enjoy_music.MusicServiceRemote;
import cn.tiancaifan.enjoy_music.bean.BannerBean;

public class BannerImageAdapter extends BannerAdapter<BannerBean, BannerImageAdapter.BannerViewHolder> {
    private Context context;

    public BannerImageAdapter(List<BannerBean> datas, Context context) {
        super(datas);
        this.context=context;
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindView(BannerViewHolder holder, BannerBean data, int position, int size) {
        ImageView imageView = holder.imageView;
        Glide.with(context).load(data.getPic()).apply(new RequestOptions().override(730,284)).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "不知道啥玩意儿";
                if (data.getTargetType() ==1){
                    str = "单曲";
                }else if (data.getTargetType() == 10){
                    str = "专辑";
                }
                Toast.makeText(context,"这是："+str,Toast.LENGTH_SHORT).show();
            }
        });
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public BannerViewHolder(@NonNull View imageView) {
            super(imageView);
            this.imageView = ((ImageView) imageView);
        }
    }

}

