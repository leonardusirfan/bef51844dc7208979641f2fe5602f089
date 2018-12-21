package id.net.gmedia.selby.Home.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import id.net.gmedia.selby.Barang.MerchandiseDetailActivity;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Converter;

public class MerchandiseAdapter extends RecyclerView.Adapter<MerchandiseAdapter.MerchandiseViewHolder> {

    private Context context;
    private List<BarangModel> listBarang;

    public MerchandiseAdapter(List<BarangModel> listBarang){
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public MerchandiseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new MerchandiseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_merchandise, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MerchandiseViewHolder barangViewHolder, int i) {
        final BarangModel barang = listBarang.get(i);

        //Barang
        barangViewHolder.txt_nama.setText(barang.getNama());
        barangViewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));
        Glide.with(context).load(barang.getUrl()).thumbnail(0.5f).into(barangViewHolder.img_barang);

        barangViewHolder.layout_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MerchandiseDetailActivity.class);
                i.putExtra("merchandise", barang.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    public class MerchandiseViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout layout_barang;

        //Variabel barang
        public ImageView img_barang;
        public TextView txt_nama, txt_harga;

        public Button btn_pesan;

        MerchandiseViewHolder(@NonNull View view) {
            super(view);
            layout_barang = view.findViewById(R.id.layout_barang);
            img_barang = view.findViewById(R.id.img_barang);
            txt_nama = view.findViewById(R.id.txt_nama);
            txt_harga = view.findViewById(R.id.txt_harga);
            btn_pesan = view.findViewById(R.id.btn_pesan);
        }
    }
}