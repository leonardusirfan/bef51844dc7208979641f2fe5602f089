package id.net.gmedia.selby.Artis.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;

import java.util.List;

import id.net.gmedia.selby.Barang.BarangDetailActivity;
import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Util.AppSharedPreferences;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.LoginActivity;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.R;
import com.leonardus.irfan.DialogFactory;

public class BarangArtisAdapter extends RecyclerView.Adapter<BarangArtisAdapter.BarangViewHolder> {

    private Activity activity;
    private List<BarangModel> listBarang;

    public BarangArtisAdapter(Activity activity, List<BarangModel> listBarang){
        this.activity = activity;
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public BarangViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BarangViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_artis_barang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BarangViewHolder barangViewHolder, int i) {
        final BarangModel barang = listBarang.get(i);

        //barang
        barangViewHolder.txt_nama.setText(barang.getNama());
        barangViewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));
        Glide.with(activity).load(barang.getUrl()).transition(DrawableTransitionOptions.withCrossFade()).into(barangViewHolder.img_barang);

        //pelapak
        barangViewHolder.txt_nama_pelapak.setText(barang.getPenjual().getNama());
        Glide.with(activity).load(barang.getPenjual().getImage()).transition(DrawableTransitionOptions.withCrossFade()).into(barangViewHolder.img_pelapak);
        barangViewHolder.rate_pelapak.setRating(barang.getPenjual().getRating());

        //tambah keranjang
        barangViewHolder.btn_keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek login atau belum
                if(AppSharedPreferences.isLoggedIn(activity)){
                    final Dialog dialog = DialogFactory.getInstance().createDialog(activity, R.layout.popup_keranjang_tambah, 70, 50);

                    Button btn_tambah = dialog.findViewById(R.id.btn_tambah);
                    TextView txt_kurang, txt_tambah;
                    txt_kurang = dialog.findViewById(R.id.txt_kurang);
                    txt_tambah = dialog.findViewById(R.id.txt_tambah);
                    final TextView txt_jumlah = dialog.findViewById(R.id.txt_jumlah);
                    txt_jumlah.setText("1");

                    txt_kurang.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int jumlah = Integer.parseInt(txt_jumlah.getText().toString());
                            if(jumlah > 1){
                                jumlah--;
                            }
                            txt_jumlah.setText(String.valueOf(jumlah));
                        }
                    });

                    txt_tambah.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int jumlah = Integer.parseInt(txt_jumlah.getText().toString());
                            jumlah++;
                            txt_jumlah.setText(String.valueOf(jumlah));
                        }
                    });

                    btn_tambah.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JSONBuilder body = new JSONBuilder();
                            body.add("id_barang", barang.getId());
                            body.add("jumlah", txt_jumlah.getText().toString());

                            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_KERANJANG,
                                    ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                                    body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                                @Override
                                public void onSuccess(String response) {
                                    Toast.makeText(activity, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFail(String message) {
                                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                }
                            }));

                        }
                    });
                    dialog.show();
                }
                else {
                    //Jika belum login, arahkan ke activity login
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }
            }
        });

        //beli langsung barang
        barangViewHolder.btn_beli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek login
                if(AppSharedPreferences.isLoggedIn(activity)){
                    JSONBuilder body = new JSONBuilder();
                    body.add("id_barang", barang.getId());
                    body.add("jumlah", 1);

                    ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_KERANJANG,
                            ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                            new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                        @Override
                        public void onSuccess(String response) {
                            Intent i = new Intent(activity, HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.putExtra("start", 3);
                            activity.startActivity(i);
                        }

                        @Override
                        public void onFail(String message) {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
                else {
                    //Jika belum login, arahkan ke activity login
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }
            }
        });

        //lihat barang detail
        barangViewHolder.btn_lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Membuka activity detail berdasarkan jenis barang
                Intent i = new Intent(activity, BarangDetailActivity.class);
                i.putExtra("barang", barang.getId());
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class BarangViewHolder extends RecyclerView.ViewHolder{

        //Barang
        private TextView txt_nama, txt_harga;
        private ImageView img_barang, btn_keranjang;
        private LinearLayout btn_lihat;

        //Pelapak
        TextView txt_nama_pelapak;
        ImageView img_pelapak;
        RatingBar rate_pelapak;

        Button btn_beli;

        BarangViewHolder(@NonNull View itemView) {
            super(itemView);
            img_barang = itemView.findViewById(R.id.img_barang);

            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            btn_lihat = itemView.findViewById(R.id.layout_barang);
            btn_keranjang = itemView.findViewById(R.id.img_keranjang);

            txt_nama_pelapak = itemView.findViewById(R.id.txt_nama_pelapak);
            img_pelapak = itemView.findViewById(R.id.img_pelapak);
            rate_pelapak = itemView.findViewById(R.id.rate_pelapak);

            btn_beli = itemView.findViewById(R.id.btn_beli);
        }
    }
}
