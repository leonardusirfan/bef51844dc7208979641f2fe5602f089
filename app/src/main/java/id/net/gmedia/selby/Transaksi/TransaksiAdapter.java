package id.net.gmedia.selby.Transaksi;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.selby.Model.TransaksiModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder> {

    private Context context;
    private List<TransaksiModel> listTransaksi;
    private int[] list_imageStatus = {
            R.drawable.bayar,
            R.drawable.delivery,
            R.drawable.terima,
            R.drawable.konfirmasi,
    };

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
        final TransaksiModel t = listTransaksi.get(i);

        holder.txt_nomor.setText(t.getNomor());
        String status = "Status : " + t.getStatus_string();
        holder.txt_status.setText(status);
        holder.txt_total.setText(Converter.doubleToRupiah(t.getTotal()));

        try{
            int s = Integer.parseInt(t.getStatus());
            for(int x = 0; x <= s; x++){
                holder.list_imageHolder[x].setImageResource(list_imageStatus[x]);
            }
        }
        catch (Exception e){
            Log.e(Constant.TAG, e.getMessage());
        }

        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TransaksiDetailActivity.class);
                i.putExtra(Constant.EXTRA_TRANSAKSI_ID, t.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTransaksi.size();
    }

    class TransaksiViewHolder extends RecyclerView.ViewHolder{

        View layout_parent;
        TextView txt_nomor, txt_status, txt_total;
        ImageView[] list_imageHolder = new ImageView[4];

        TransaksiViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_nomor = itemView.findViewById(R.id.txt_nomor);
            txt_status = itemView.findViewById(R.id.txt_status);
            txt_total = itemView.findViewById(R.id.txt_total);
            list_imageHolder[0] = itemView.findViewById(R.id.img_status_bayar);
            list_imageHolder[1] = itemView.findViewById(R.id.img_status_delivery);
            list_imageHolder[2] = itemView.findViewById(R.id.img_status_terima);
            list_imageHolder[3] = itemView.findViewById(R.id.img_status_konfirmasi);
        }
    }
}
