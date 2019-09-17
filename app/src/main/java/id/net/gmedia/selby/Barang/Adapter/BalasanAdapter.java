package id.net.gmedia.selby.Barang.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Model.UlasanModel;

public class BalasanAdapter extends RecyclerView.Adapter<BalasanAdapter.BalasanViewHolder> {

    private List<UlasanModel> listUlasan;

    BalasanAdapter(List<UlasanModel> listUlasan){
        this.listUlasan = listUlasan;
    }

    @NonNull
    @Override
    public BalasanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BalasanViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_barang_balasan, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BalasanViewHolder balasanViewHolder, int i) {
        UlasanModel ulasan = listUlasan.get(i);

        balasanViewHolder.txt_nama.setText(ulasan.getNama());
        balasanViewHolder.txt_tanggal.setText(Converter.DToString(ulasan.getTanggal()));
        balasanViewHolder.txt_balasan.setText(ulasan.getUlasan());
    }

    @Override
    public int getItemCount() {
        return listUlasan.size();
    }

    class BalasanViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_tanggal, txt_balasan;

        BalasanViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_balasan = itemView.findViewById(R.id.txt_balasan);
        }
    }
}
