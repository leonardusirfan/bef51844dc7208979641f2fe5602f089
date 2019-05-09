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

import java.util.List;

import id.net.gmedia.selby.Model.TransaksiModel;
import id.net.gmedia.selby.R;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder> {

    private Context context;
    private List<TransaksiModel> listTransaksi;

    TransaksiAdapter(Context context, List<TransaksiModel> listTransaksi){
        this.context = context;
        this.listTransaksi = listTransaksi;
    }

    @NonNull
    @Override
    public TransaksiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TransaksiViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_transaksi, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiViewHolder holder, int i) {
        TransaksiModel t = listTransaksi.get(i);

        holder.txt_nomor.setText(t.getNomor());
        String status = "Status : " + t.getStatus();
        holder.txt_status.setText(status);
        holder.txt_total.setText(Converter.doubleToRupiah(t.getTotal()));
    }

    @Override
    public int getItemCount() {
        return listTransaksi.size();
    }

    class TransaksiViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nomor, txt_status, txt_total;
        ImageView img_status_bayar, img_status_delivery, img_status_terima, img_status_konfirmasi;

        TransaksiViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nomor = itemView.findViewById(R.id.txt_nomor);
            txt_status = itemView.findViewById(R.id.txt_status);
            txt_total = itemView.findViewById(R.id.txt_total);
            img_status_bayar = itemView.findViewById(R.id.img_status_bayar);
            img_status_delivery = itemView.findViewById(R.id.img_status_delivery);
            img_status_terima = itemView.findViewById(R.id.img_status_terima);
            img_status_konfirmasi = itemView.findViewById(R.id.img_status_konfirmasi);
        }
    }
}
