package id.net.gmedia.selby.Pembayaran;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.selby.Model.OngkirModel;
import id.net.gmedia.selby.R;

public class PembayaranOngkirAdapter extends RecyclerView.Adapter<PembayaranOngkirAdapter.PembayaranDetailOngkirViewHolder> {

    private Activity activity;
    private List<OngkirModel> listOngkir;
    private int selected = -1;

    PembayaranOngkirAdapter(Activity activity, List<OngkirModel> listOngkir){
        this.activity = activity;
        this.listOngkir = listOngkir;
    }

    @NonNull
    @Override
    public PembayaranDetailOngkirViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PembayaranDetailOngkirViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pembayaran_detail_ongkir, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PembayaranDetailOngkirViewHolder holder, int i) {
        OngkirModel o = listOngkir.get(i);
        final PembayaranDetailOngkirViewHolder final_holder = holder;

        holder.txt_service.setText(o.getService());
        holder.txt_deskripsi.setText(o.getDeskripsi());
        holder.txt_harga.setText(Converter.doubleToRupiah(o.getHarga()));
        if(!o.getCatatan().equals("")){
            holder.txt_catatan.setVisibility(View.VISIBLE);
            holder.txt_catatan.setText(o.getCatatan());
        }
        else{
            holder.txt_catatan.setVisibility(View.GONE);
        }

        holder.txt_sampai.setText(o.getPerkiraan_sampai());

        if(i == selected){
            holder.img_check.setImageResource(R.drawable.ic_cb_active);
        }
        else{
            holder.img_check.setImageResource(R.drawable.ic_cb_uncheck);
        }

        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(final_holder.getAdapterPosition() == selected){
                    selected = -1;
                    notifyItemChanged(final_holder.getAdapterPosition());
                }
                else{
                    int previous_selected = selected;
                    selected = final_holder.getAdapterPosition();

                    notifyItemChanged(previous_selected);
                    notifyItemChanged(selected);
                }
            }
        });
    }

    int getSelected() {
        return selected;
    }

    @Override
    public int getItemCount() {
        return listOngkir.size();
    }

    class PembayaranDetailOngkirViewHolder extends RecyclerView.ViewHolder {

        View layout_parent;
        ImageView img_check;
        TextView txt_service, txt_deskripsi, txt_harga, txt_catatan, txt_sampai;

        PembayaranDetailOngkirViewHolder(@NonNull View itemView) {
            super(itemView);
            img_check = itemView.findViewById(R.id.img_check);
            txt_service = itemView.findViewById(R.id.txt_service);
            txt_deskripsi = itemView.findViewById(R.id.txt_deskripsi);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_catatan = itemView.findViewById(R.id.txt_catatan);
            txt_sampai = itemView.findViewById(R.id.txt_sampai);
            layout_parent = itemView.findViewById(R.id.layout_parent);
        }
    }

}
