package id.net.gmedia.selby;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;

import java.util.List;

import id.net.gmedia.selby.Util.Constant;

public class HotNewsAdapter extends RecyclerView.Adapter<HotNewsAdapter.HotNewsViewHolder> {

    private HotNewsActivity activity;
    private List<HotNewsModel> listNews;

    public HotNewsAdapter(HotNewsActivity activity, List<HotNewsModel> listNews){
        this.activity = activity;
        this.listNews = listNews;
    }

    @NonNull
    @Override
    public HotNewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HotNewsViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_hot_news, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HotNewsViewHolder holder, int i) {
        final HotNewsModel news = listNews.get(i);

        holder.txt_tanggal.setText(news.getTanggal());
        holder.txt_news.setText(news.getJudul());
        Glide.with(activity).load(news.getImage()).transition(
                DrawableTransitionOptions.withCrossFade()).into(holder.img_news);

        holder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, HotNewsDetailActivity.class);
                i.putExtra(Constant.EXTRA_BERITA, new Gson().toJson(news));
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNews.size();
    }

    class HotNewsViewHolder extends RecyclerView.ViewHolder{

        View layout_root;
        TextView txt_tanggal, txt_news;
        ImageView img_news;

        HotNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_news = itemView.findViewById(R.id.txt_news);
            img_news = itemView.findViewById(R.id.img_news);
        }
    }
}
