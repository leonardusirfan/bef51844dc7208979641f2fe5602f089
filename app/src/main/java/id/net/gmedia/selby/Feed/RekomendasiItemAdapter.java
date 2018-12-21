package id.net.gmedia.selby.Feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import id.net.gmedia.selby.Artis.ArtisDetailActivity;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.R;

public class RekomendasiItemAdapter extends RecyclerView.Adapter<RekomendasiItemAdapter.BarangSmallViewHolder> {

    private List<ArtisModel> listArtis;
    private Context context;

    RekomendasiItemAdapter(List<ArtisModel> listArtis){
        this.listArtis = listArtis;
    }

    @NonNull
    @Override
    public BarangSmallViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new BarangSmallViewHolder(LayoutInflater.from(context).inflate(R.layout.item_feed_rekomendasi_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BarangSmallViewHolder barangSmallViewHolder, int i) {
        final ArtisModel item = listArtis.get(i);

        Glide.with(context).load(item.getImage()).thumbnail(0.3f).into(barangSmallViewHolder.img_artis);
        barangSmallViewHolder.txt_artis.setText(item.getNama());

        barangSmallViewHolder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent intent = new Intent(context, ArtisDetailActivity.class);
                intent.putExtra("artis", gson.toJson(item));

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, barangSmallViewHolder.img_artis, "artis");
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listArtis.size();
    }

    class BarangSmallViewHolder extends RecyclerView.ViewHolder{

        TextView txt_artis;
        ImageView img_artis;
        MaterialCardView layout_root;

        BarangSmallViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_artis = itemView.findViewById(R.id.txt_artis);
            img_artis = itemView.findViewById(R.id.img_artis);
            layout_root = itemView.findViewById(R.id.layout_root);
        }
    }
}
