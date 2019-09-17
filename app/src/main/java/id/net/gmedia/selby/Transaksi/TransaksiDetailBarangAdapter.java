package id.net.gmedia.selby.Transaksi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonardus.irfan.Converter;
import com.leonardus.irfan.ImageLoader;

import java.util.List;

import id.net.gmedia.selby.R;

public class TransaksiDetailBarangAdapter extends RecyclerView.Adapter
        <RecyclerView.ViewHolder> {

    private Context context;
    private List<TransaksiDetailBaseModel> listBarang;

    TransaksiDetailBarangAdapter(Context context, List<TransaksiDetailBaseModel> listBarang){
        this.context = context;
        this.listBarang = listBarang;
    }

    @Override
    public int getItemViewType(int position) {
        if(listBarang.get(position).getType() == TransaksiDetailBaseModel.KURIR){
            return TransaksiDetailBaseModel.KURIR;
        }
        else{
            return TransaksiDetailBaseModel.BARANG;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == TransaksiDetailBaseModel.KURIR){
            return new TransaksiDetailKurirViewHolder(LayoutInflater.from(context).
                    inflate(R.layout.item_transaksi_detail_kurir, viewGroup, false));
        }
        else{
            return new TransaksiDetailBarangViewHolder(LayoutInflater.from(context).
                    inflate(R.layout.item_transaksi_detail_barang, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        TransaksiDetailBaseModel b = listBarang.get(i);

        if(holder instanceof TransaksiDetailKurirViewHolder){
            ((TransaksiDetailKurirViewHolder)holder).txt_nama.setText(b.getItem().getNama());
            ((TransaksiDetailKurirViewHolder)holder).txt_biaya.setText(Converter.doubleToRupiah(b.getItem().getHarga()));
        }
        else if(holder instanceof TransaksiDetailBarangViewHolder){
            ((TransaksiDetailBarangViewHolder)holder).txt_nama.setText(b.getItem().getNama());
            String jumlah = "Jumlah : " + b.getItem().getJumlah();
            ((TransaksiDetailBarangViewHolder)holder).txt_jumlah.setText(jumlah);
            String subtotal = "Subtotal : " + Converter.doubleToRupiah(b.getItem().getHarga() * b.getItem().getJumlah());
            ((TransaksiDetailBarangViewHolder)holder).txt_subtotal.setText(subtotal);

            ImageLoader.load(context, b.getItem().getUrl(), ((TransaksiDetailBarangViewHolder)holder).img_barang);
        }
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class TransaksiDetailBarangViewHolder extends RecyclerView.ViewHolder{

        ImageView img_barang;
        TextView txt_nama, txt_jumlah, txt_subtotal;

        TransaksiDetailBarangViewHolder(@NonNull View itemView) {
            super(itemView);
            img_barang = itemView.findViewById(R.id.img_barang);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_subtotal = itemView.findViewById(R.id.txt_subtotal);
        }
    }

    class TransaksiDetailKurirViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nama, txt_biaya;

        TransaksiDetailKurirViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_biaya = itemView.findViewById(R.id.txt_biaya);
        }
    }
}
