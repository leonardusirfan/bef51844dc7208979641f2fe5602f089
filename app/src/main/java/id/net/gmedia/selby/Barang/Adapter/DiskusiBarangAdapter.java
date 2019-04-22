package id.net.gmedia.selby.Barang.Adapter;

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
import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.selby.Barang.Fragment.FragmentDiskusiBarang;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Model.UlasanModel;
import id.net.gmedia.selby.Util.LoadMoreViewHolder;

public class DiskusiBarangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int LOAD_MORE = 999;

    private FragmentDiskusiBarang fragment;
    private Context context;
    private List<UlasanModel> listUlasan;
    private boolean all_loaded = false;

    public DiskusiBarangAdapter(FragmentDiskusiBarang fragment, List<UlasanModel> listUlasan){
        this.fragment = fragment;
        this.listUlasan = listUlasan;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == listUlasan.size()){
            return LOAD_MORE;
        }
        else{
            return super.getItemViewType(position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        if(i == LOAD_MORE){
            return new LoadMoreViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_load_more, viewGroup, false));
        }
        else{
            return new UlasanViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_barang_diskusi_barang, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if(holder instanceof LoadMoreViewHolder){
            final LoadMoreViewHolder viewHolder = (LoadMoreViewHolder) holder;

            viewHolder.btn_load.setVisibility(View.VISIBLE);
            viewHolder.pb_load.setVisibility(View.INVISIBLE);

            viewHolder.btn_load.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.btn_load.setVisibility(View.INVISIBLE);
                    viewHolder.pb_load.setVisibility(View.VISIBLE);

                    fragment.loadDiskusiBarang();
                }
            });
        }
        else{
            final UlasanModel ulasan = listUlasan.get(i);
            UlasanViewHolder viewHolder = (UlasanViewHolder) holder;

            viewHolder.txt_nama.setText(ulasan.getNama());
            viewHolder.txt_tanggal.setText(Converter.DToString(ulasan.getTanggal()));
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
    }

    @Override
    public int getItemCount() {
        if(all_loaded){
            return listUlasan.size();
        }
        else{
            return listUlasan.size() + 1;
        }
    }

    public void setAll_loaded() {
        this.all_loaded = true;
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
