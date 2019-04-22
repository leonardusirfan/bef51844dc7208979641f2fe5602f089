package id.net.gmedia.selby.Home.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import id.net.gmedia.selby.Barang.BarangDetailActivity;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.R;
import com.leonardus.irfan.TopCropImageView;

public class MerchandiseAdapter extends RecyclerView.Adapter<MerchandiseAdapter.MerchandiseViewHolder> {

    //public static final int VIEW_THUMBNAIL = 1;

    private Activity activity;
    private Context context;
    private List<BarangModel> listMerchandise;
    //private int view;

    public MerchandiseAdapter(Activity activity, List<BarangModel> listMerchandise){
        this.activity = activity;
        //this.view = view;
        this.listMerchandise = listMerchandise;
    }

    @NonNull
    @Override
    public MerchandiseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new MerchandiseViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_merchandise, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MerchandiseViewHolder artisViewHolder, int i) {
        final BarangModel merchandise = listMerchandise.get(i);

        artisViewHolder.txt_merchandise.setText(merchandise.getNama());
        Glide.with(context).load(merchandise.getUrl()).apply(new RequestOptions().dontAnimate().dontTransform()).
                thumbnail(0.5f).transition(DrawableTransitionOptions.withCrossFade()).into(artisViewHolder.img_merchandise);
        if(merchandise.isDonasi()){
            artisViewHolder.img_donasi.setVisibility(View.VISIBLE);
        }
        artisViewHolder.img_merchandise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BarangDetailActivity.class);
                i.putExtra("barang", merchandise.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMerchandise.size();
    }

    class MerchandiseViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_merchandise;
        private TopCropImageView img_merchandise;
        private CardView layout_merchandise;
        private ImageView img_donasi;

        MerchandiseViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_merchandise = itemView.findViewById(R.id.layout_merchandise);
            txt_merchandise = itemView.findViewById(R.id.txt_merchandise);
            img_merchandise = itemView.findViewById(R.id.img_merchandise);
            img_donasi = itemView.findViewById(R.id.img_donasi);
        }
    }
}
