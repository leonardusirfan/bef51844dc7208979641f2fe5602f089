package id.net.gmedia.selby.Home.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.selby.Barang.LelangDetailActivity;
import id.net.gmedia.selby.Model.LelangModel;
import id.net.gmedia.selby.R;
import com.leonardus.irfan.TopCropCircularImageView;

public class LelangAdapter extends RecyclerView.Adapter<LelangAdapter.LelangViewHolder> {

    private Context context;
    private List<LelangModel> listLelang;

    public LelangAdapter(List<LelangModel> listBarang){
        this.listLelang = listBarang;
    }

    @NonNull
    @Override
    public LelangViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new LelangViewHolder(LayoutInflater.from(context).inflate(R.layout.item_lelang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LelangViewHolder barangViewHolder, int i) {
        final LelangModel lelang = listLelang.get(i);

        //Barang
        barangViewHolder.txt_nama.setText(lelang.getNama());
        barangViewHolder.txt_harga.setText(Converter.doubleToRupiah(lelang.getHarga()));
        Glide.with(context).load(lelang.getUrl()).thumbnail(0.5f).into(barangViewHolder.img_barang);
        if(lelang.isDonasi()){
            barangViewHolder.img_donasi.setVisibility(View.VISIBLE);
        }

        //Pelapak
        barangViewHolder.txt_nama_pelapak.setText(lelang.getPenjual().getNama());
        Glide.with(context).load(lelang.getPenjual().getImage()).thumbnail(0.5f).apply(new RequestOptions()).
                into(barangViewHolder.img_pelapak);

        barangViewHolder.layout_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, LelangDetailActivity.class);
                i.putExtra("lelang", lelang.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listLelang.size();
    }

    class LelangViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout_barang;

        //Variabel barang
        ImageView img_barang, img_donasi;
        TextView txt_nama, txt_harga;

        //Variabel pelapak
        TopCropCircularImageView img_pelapak;
        TextView txt_nama_pelapak;
        RatingBar rate_pelapak;

        LelangViewHolder(@NonNull View view) {
            super(view);
            layout_barang = view.findViewById(R.id.layout_barang);
            img_barang = view.findViewById(R.id.img_barang);
            img_donasi = view.findViewById(R.id.img_donasi);
            txt_nama = view.findViewById(R.id.txt_nama);
            txt_harga = view.findViewById(R.id.txt_harga);
            img_pelapak = view.findViewById(R.id.img_pelapak);
            txt_nama_pelapak = view.findViewById(R.id.txt_nama_pelapak);
            rate_pelapak = view.findViewById(R.id.rate_pelapak);
        }
    }
}
