package id.net.gmedia.selby.Artis;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.leonardus.irfan.SimpleObjectModel;

import java.util.List;

import id.net.gmedia.selby.R;

public class KategoriBarangArtisAdapter extends
        RecyclerView.Adapter<KategoriBarangArtisAdapter.KategoriBarangArtisViewHolder> {

    private List<SimpleObjectModel> listKategori;

    KategoriBarangArtisAdapter(List<SimpleObjectModel> listKategori){
        this.listKategori = listKategori;
    }

    @NonNull
    @Override
    public KategoriBarangArtisViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new KategoriBarangArtisViewHolder(LayoutInflater.
                from(viewGroup.getContext()).inflate(R.layout.item_barang_artis_kategori, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriBarangArtisViewHolder kategoriBarangArtisViewHolder, int i) {
        SimpleObjectModel k = listKategori.get(i);

        kategoriBarangArtisViewHolder.txt_kategori.setText(k.getValue());
    }

    @Override
    public int getItemCount() {
        return listKategori.size();
    }

    class KategoriBarangArtisViewHolder extends RecyclerView.ViewHolder{

        CheckBox cb_kategori;
        TextView txt_kategori;

        KategoriBarangArtisViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_kategori = itemView.findViewById(R.id.cb_kategori);
            txt_kategori = itemView.findViewById(R.id.txt_kategori);
        }
    }
}
