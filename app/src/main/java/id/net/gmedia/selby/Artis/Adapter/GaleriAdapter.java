package id.net.gmedia.selby.Artis.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import id.net.gmedia.selby.Artis.EventActivity;
import id.net.gmedia.selby.R;

public class GaleriAdapter extends RecyclerView.Adapter<GaleriAdapter.GaleriViewHolder> {

    private Context context;
    private List<String> listImage;

    public GaleriAdapter(List<String> listImage){
        this.listImage = listImage;
    }

    @NonNull
    @Override
    public GaleriViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new GaleriViewHolder(LayoutInflater.from(context).inflate(R.layout.item_galeri, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GaleriViewHolder galeriViewHolder, int i) {
        Glide.with(context).load(listImage.get(i)).transition(DrawableTransitionOptions.withCrossFade()).apply(new RequestOptions().placeholder(R.color.dark_grey)).thumbnail(0.5f).into(galeriViewHolder.img_artis);

        galeriViewHolder.img_artis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EventActivity)context).setView(galeriViewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    class GaleriViewHolder extends RecyclerView.ViewHolder{

        private ImageView img_artis;

        GaleriViewHolder(@NonNull View itemView) {
            super(itemView);

            img_artis = itemView.findViewById(R.id.img_artis);
        }
    }
}