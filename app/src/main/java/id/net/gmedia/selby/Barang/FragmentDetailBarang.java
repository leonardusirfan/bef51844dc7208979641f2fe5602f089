package id.net.gmedia.selby.Barang;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.net.gmedia.selby.R;

public class FragmentDetailBarang extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_detail_barang, container, false);

        //Inisialisasi UI
        TextView txt_deskripsi = layout.findViewById(R.id.txt_deskripsi);
        TextView txt_kategori = layout.findViewById(R.id.txt_kategori);
        TextView txt_berat = layout.findViewById(R.id.txt_berat);
        TextView txt_merk = layout.findViewById(R.id.txt_merk);

        //Mengisi tampilan dengan data dari activity
        if(getArguments() != null){
            System.out.println(getArguments().getString("kategori"));
            System.out.println(getArguments().getString("berat"));
            System.out.println(getArguments().getString("merk"));

            txt_deskripsi.setText(getArguments().getString("deskripsi"));
            txt_kategori.setText(getArguments().getString("kategori"));
            txt_berat.setText(getArguments().getString("berat"));
            txt_merk.setText(getArguments().getString("merk"));
        }

        //ViewMoreTeksView.makeTextViewResizable(txt_deskripsi,10,"Selengkapnya",true);
        return layout;
    }
}