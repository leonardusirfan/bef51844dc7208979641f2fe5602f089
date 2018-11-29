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
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Model.UlasanModel;
import id.net.gmedia.selby.Util.Converter;

public class UlasanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FragmentUlasan fragment;
    private Context context;
    private List<UlasanModel> listUlasan;

    UlasanAdapter(FragmentUlasan fragment, List<UlasanModel> listUlasan){
        this.fragment = fragment;
        this.listUlasan = listUlasan;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new UlasanViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_barang_ulasan, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final UlasanModel ulasan = listUlasan.get(i);
        UlasanViewHolder viewHolder = (UlasanViewHolder) holder;

        viewHolder.txt_nama.setText(ulasan.getNama());
        viewHolder.txt_tanggal.setText(Converter.DateToString(ulasan.getTanggal()));
        viewHolder.txt_ulasan.setText(ulasan.getUlasan());
        viewHolder.rate_barang.setRating(ulasan.getRating());

        BalasanAdapter balasan = new BalasanAdapter(ulasan.getListBalasan());
        viewHolder.rv_balasan.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        viewHolder.rv_balasan.setItemAnimator(new DefaultItemAnimator());
        viewHolder.rv_balasan.setAdapter(balasan);

        viewHolder.txt_balas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.balasUlasan(ulasan.getId());
            }
        });

        Glide.with(context).load(ulasan.getUrl()).apply(new RequestOptions().circleCrop()).into(viewHolder.img_pengulas);
    }

    @Override
    public int getItemCount() {
        return listUlasan.size();
    }

    class UlasanViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_tanggal, txt_ulasan;
        private ImageView img_pengulas;
        private RecyclerView rv_balasan;
        private RatingBar rate_barang;
        private TextView txt_balas;

        UlasanViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_ulasan = itemView.findViewById(R.id.txt_ulasan);
            img_pengulas = itemView.findViewById(R.id.img_pengulas);
            rv_balasan = itemView.findViewById(R.id.rv_balasan);
            rate_barang = itemView.findViewById(R.id.rate_barang);
            txt_balas = itemView.findViewById(R.id.txt_balas);
        }
    }
}
