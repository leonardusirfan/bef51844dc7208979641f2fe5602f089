package id.net.gmedia.selby.Artis.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.selby.Model.KegiatanModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Converter;

public class KegiatanAdapter extends RecyclerView.Adapter<KegiatanAdapter.KegiatanViewHolder> {

    private List<KegiatanModel> listKegiatan;

    public KegiatanAdapter(List<KegiatanModel> listKegiatan){
        this.listKegiatan = listKegiatan;
    }

    @NonNull
    @Override
    public KegiatanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new KegiatanViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_kegiatan, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KegiatanViewHolder kegiatanViewHolder, int i) {
        KegiatanModel kegiatan = listKegiatan.get(i);

        kegiatanViewHolder.txt_judul.setText(kegiatan.getJudul());
        kegiatanViewHolder.txt_tempat.setText(kegiatan.getTempat());
        kegiatanViewHolder.txt_tanggal.setText(Converter.DateToString(kegiatan.getTanggal()));
        kegiatanViewHolder.txt_deskripsi.setText(kegiatan.getDeskripsi());
    }

    @Override
    public int getItemCount() {
        return listKegiatan.size();
    }

    class KegiatanViewHolder extends RecyclerView.ViewHolder{

        TextView txt_judul, txt_tempat, txt_tanggal, txt_deskripsi;

        KegiatanViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_judul = itemView.findViewById(R.id.txt_judul);
            txt_deskripsi = itemView.findViewById(R.id.txt_deskripsi);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_tempat = itemView.findViewById(R.id.txt_tempat);
        }
    }
}
