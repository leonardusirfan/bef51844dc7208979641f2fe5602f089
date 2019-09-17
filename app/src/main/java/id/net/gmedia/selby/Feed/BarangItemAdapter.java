package id.net.gmedia.selby.Feed;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import id.net.gmedia.selby.Barang.BarangDetailActivity;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class BarangItemAdapter extends RecyclerView.Adapter<BarangItemAdapter.BarangSmallViewHolder> {

    private List<BarangModel> listBarang;
    private Context context;

    BarangItemAdapter(List<BarangModel> listBarang){
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public BarangSmallViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new BarangSmallViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_feed_barang_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BarangSmallViewHolder barangSmallViewHolder, int i) {
        final BarangModel item = listBarang.get(i);

        Glide.with(context).load(item.getUrl()).thumbnail(0.3f).into(barangSmallViewHolder.img_barang);
        barangSmallViewHolder.txt_barang.setText(item.getNama());

        barangSmallViewHolder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BarangDetailActivity.class);
                i.putExtra(Constant.EXTRA_BARANG, item.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class BarangSmallViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView layout_root;
        TextView txt_barang;
        ImageView img_barang;

        BarangSmallViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_barang = itemView.findViewById(R.id.txt_barang);
            img_barang = itemView.findViewById(R.id.img_barang);
            layout_root = itemView.findViewById(R.id.layout_root);
        }
    }
}
