package id.net.gmedia.selby.Home.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import id.net.gmedia.selby.Barang.BarangDetailActivity;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.Model.PrelovedModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.DynamicHeightImageView;
import com.leonardus.irfan.TopCropCircularImageView;

public class PrelovedAdapter extends RecyclerView.Adapter<PrelovedAdapter.PrelovedViewHolder> {

    private Activity activity;
    private List<BarangModel> listBarang;

    public PrelovedAdapter(Activity activity, List<BarangModel> listBarang){
        this.activity = activity;
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public PrelovedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PrelovedViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_preloved, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PrelovedViewHolder prelovedViewHolder, int i) {
        final BarangModel preloved = listBarang.get(i);

        prelovedViewHolder.txt_nama.setText(preloved.getNama());
        prelovedViewHolder.txt_nama_pelapak.setText(preloved.getPenjual().getNama());

        if(preloved instanceof PrelovedModel){
            String terpakai = "Terpakai : " + ((PrelovedModel)preloved).getTerpakai();
            prelovedViewHolder.txt_terpakai.setText(terpakai);
        }

        if(preloved.isDonasi()){
            prelovedViewHolder.img_donasi.setVisibility(View.VISIBLE);
        }
        else{
            prelovedViewHolder.img_donasi.setVisibility(View.INVISIBLE);
        }

        if(preloved.getImgBitmap() != null){
            prelovedViewHolder.img_barang.setAspectRatio((float)preloved.getImgBitmap().getWidth()/(float)preloved.getImgBitmap().getHeight());
            Glide.with(activity).load(preloved.getImgBitmap()).apply(new RequestOptions().fitCenter()).
                    transition(DrawableTransitionOptions.withCrossFade()).into(prelovedViewHolder.img_barang);
        }
        else{
            Glide.with(activity)
                    .asBitmap()
                    .load(preloved.getUrl())
                    .apply(new RequestOptions().placeholder(new ColorDrawable(activity.getResources().getColor(R.color.white))).
                            //override(500).
                            diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            preloved.setImgBitmap(resource);

                            prelovedViewHolder.img_barang.setAspectRatio((float)preloved.getImgBitmap().getWidth()/(float)preloved.getImgBitmap().getHeight());
                            Glide.with(activity).load(preloved.getImgBitmap()).apply(new RequestOptions().fitCenter()).
                                    transition(DrawableTransitionOptions.withCrossFade()).into(prelovedViewHolder.img_barang);
                            //prelovedViewHolder.img_barang.setImageBitmap(preloved.getImgBitmap());
                        }
                    });
        }
        Glide.with(activity).load(preloved.getPenjual().getImage()).apply(new RequestOptions()).
                into(prelovedViewHolder.img_pelapak);

        prelovedViewHolder.layout_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, BarangDetailActivity.class);
                i.putExtra(Constant.EXTRA_BARANG, preloved.getId());
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class PrelovedViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nama, txt_nama_pelapak, txt_terpakai;
        TopCropCircularImageView img_pelapak;
        DynamicHeightImageView img_barang;
        LinearLayout layout_barang;
        ImageView img_donasi;

        PrelovedViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_terpakai = itemView.findViewById(R.id.txt_terpakai);
            txt_nama_pelapak = itemView.findViewById(R.id.txt_nama_pelapak);
            img_pelapak = itemView.findViewById(R.id.img_pelapak);
            img_barang = itemView.findViewById(R.id.img_barang);
            layout_barang = itemView.findViewById(R.id.layout_barang);
            img_donasi = itemView.findViewById(R.id.img_donasi);
        }
    }
}
