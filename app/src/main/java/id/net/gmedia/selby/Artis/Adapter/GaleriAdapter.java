package id.net.gmedia.selby.Artis.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.leonardus.irfan.SimpleObjectModel;

import java.util.LinkedHashMap;
import java.util.List;

import id.net.gmedia.selby.Artis.EventActivity;
import id.net.gmedia.selby.R;

public class GaleriAdapter extends RecyclerView.Adapter<GaleriAdapter.GaleriViewHolder> {

    private Activity activity;
    private List<SimpleObjectModel> listHeader;
    private LinkedHashMap<SimpleObjectModel, List<String>> listGaleri;

    public GaleriAdapter(Activity activity, List<SimpleObjectModel> listHeader,
                         LinkedHashMap<SimpleObjectModel, List<String>> listGaleri){
        this.activity = activity;
        this.listHeader = listHeader;
        this.listGaleri = listGaleri;
    }

    @NonNull
    @Override
    public GaleriViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GaleriViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_galeri, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GaleriViewHolder holder, int i) {
        SimpleObjectModel key = listHeader.get(i);

        holder.txt_judul.setText(key.getId());
        holder.txt_tanggal.setText(key.getValue());

        holder.rv_galeri.setItemAnimator(new DefaultItemAnimator());
        holder.rv_galeri.setLayoutManager(new GridLayoutManager(activity, 3));
        holder.rv_galeri.setAdapter(new GaleriChildAdapter(listGaleri.get(key)));
    }

    @Override
    public int getItemCount() {
        return listGaleri.size();
    }

    class GaleriViewHolder extends RecyclerView.ViewHolder{

        TextView txt_judul, txt_tanggal;
        RecyclerView rv_galeri;

        GaleriViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_judul = itemView.findViewById(R.id.txt_judul);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            rv_galeri = itemView.findViewById(R.id.rv_galeri);
        }
    }

    class GaleriChildAdapter extends RecyclerView.Adapter<GaleriChildAdapter.GaleriChildViewHolder>{

        private List<String> listImage;

        GaleriChildAdapter(List<String> listImage){
            this.listImage = listImage;
        }

        @NonNull
        @Override
        public GaleriChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new GaleriChildViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_galeri_child, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull GaleriChildViewHolder holder, int i) {
            final GaleriChildViewHolder finalHolder = holder;
            Glide.with(activity).load(listImage.get(i)).transition(DrawableTransitionOptions.withCrossFade()).
                    apply(new RequestOptions().placeholder(R.color.dark_grey)).thumbnail(0.5f).into(holder.img_artis);
            holder.img_artis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((EventActivity)activity).setView(listImage, finalHolder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return listImage.size();
        }

        class GaleriChildViewHolder extends RecyclerView.ViewHolder{

            ImageView img_artis;

            GaleriChildViewHolder(@NonNull View itemView) {
                super(itemView);
                img_artis = itemView.findViewById(R.id.img_artis);
            }
        }
    }
}