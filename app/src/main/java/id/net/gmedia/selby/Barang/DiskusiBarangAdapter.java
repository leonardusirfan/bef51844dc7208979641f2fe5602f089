package id.net.gmedia.selby.Barang;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Model.UlasanModel;
import id.net.gmedia.selby.Util.Converter;

public class DiskusiBarangAdapter extends RecyclerView.Adapter<DiskusiBarangAdapter.UlasanViewHolder> {

    private FragmentDiskusiBarang fragment;
    private Context context;
    private List<UlasanModel> listUlasan;

    DiskusiBarangAdapter(FragmentDiskusiBarang fragment, List<UlasanModel> listUlasan){
        this.fragment = fragment;
        this.listUlasan = listUlasan;
    }

    @NonNull
    @Override
    public UlasanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new UlasanViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_barang_diskusi_barang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UlasanViewHolder viewHolder, int i) {
        final UlasanModel ulasan = listUlasan.get(i);

        viewHolder.txt_nama.setText(ulasan.getNama());
        viewHolder.txt_tanggal.setText(Converter.DateToString(ulasan.getTanggal()));
        viewHolder.txt_ulasan.setText(ulasan.getUlasan());

        BalasanAdapter balasan = new BalasanAdapter(ulasan.getListBalasan());
        viewHolder.rv_balasan.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        viewHolder.rv_balasan.setItemAnimator(new DefaultItemAnimator());
        viewHolder.rv_balasan.setAdapter(balasan);
        Glide.with(context).load(ulasan.getUrl()).apply(new RequestOptions().circleCrop()).into(viewHolder.img_pengulas);

        viewHolder.txt_balas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.balasDiskusi(ulasan.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUlasan.size();
    }

    class UlasanViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_tanggal, txt_ulasan, txt_balas;
        private ImageView img_pengulas;
        private RecyclerView rv_balasan;

        UlasanViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_ulasan = itemView.findViewById(R.id.txt_ulasan);
            img_pengulas = itemView.findViewById(R.id.img_pengulas);
            rv_balasan = itemView.findViewById(R.id.rv_balasan);
            txt_balas = itemView.findViewById(R.id.txt_balas);
        }
    }
}
