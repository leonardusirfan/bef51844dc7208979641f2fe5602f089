package id.net.gmedia.selby.Pembayaran;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.selby.Model.AlamatModel;
import id.net.gmedia.selby.R;

public class PembayaranAlamatAdapter extends RecyclerView.Adapter<PembayaranAlamatAdapter.PembayaranAlamatViewHolder> {

    private PembayaranAlamatGanti activity;
    private List<AlamatModel> listAlamat;

    PembayaranAlamatAdapter(PembayaranAlamatGanti activity, List<AlamatModel> listAlamat){
        this.activity = activity;
        this.listAlamat = listAlamat;
    }

    @NonNull
    @Override
    public PembayaranAlamatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PembayaranAlamatViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_alamat, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PembayaranAlamatViewHolder holder, int i) {
        final AlamatModel a = listAlamat.get(i);

        String alamat = a.getNama_kota() + ", " + a.getNama_provinsi();
        holder.txt_kota.setText(alamat);
        holder.txt_kodepos.setText(a.getKodepos());
        holder.txt_alamat.setText(a.toString());

        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setAlamat(a);
            }
        });

        holder.img_hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setTitle("Hapus alamat")
                        .setMessage("Yakin ingin menghapus alamat?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.hapusAlamat(a.getId());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAlamat.size();
    }

    class PembayaranAlamatViewHolder extends RecyclerView.ViewHolder{

        View layout_parent;
        ImageView img_check, img_hapus;
        TextView txt_alamat, txt_kota, txt_kodepos;

        PembayaranAlamatViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            img_check = itemView.findViewById(R.id.img_check);
            txt_kota = itemView.findViewById(R.id.txt_kota);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txt_kodepos = itemView.findViewById(R.id.txt_kodepos);
            img_hapus = itemView.findViewById(R.id.img_hapus);
        }
    }
}
