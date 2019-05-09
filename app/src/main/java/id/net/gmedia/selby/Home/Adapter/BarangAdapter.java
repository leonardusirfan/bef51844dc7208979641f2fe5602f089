package id.net.gmedia.selby.Home.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;

import java.util.List;

import id.net.gmedia.selby.Barang.BarangDetailActivity;
import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.LoginActivity;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.AppSharedPreferences;
import id.net.gmedia.selby.Util.Constant;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.TopCropCircularImageView;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.BarangViewHolder> {

    private Activity activity;
    private Context context;
    private List<BarangModel> listBarang;

    public BarangAdapter(Activity activity, List<BarangModel> listBarang){
        this.activity = activity;
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public BarangViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new BarangViewHolder(LayoutInflater.from(context).inflate(R.layout.item_barang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BarangViewHolder barangViewHolder, int i) {
        final BarangModel barang = listBarang.get(i);

        //Barang
        barangViewHolder.txt_nama.setText(barang.getNama());
        barangViewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));
        Glide.with(context).load(barang.getUrl()).thumbnail(0.5f).
                transition(DrawableTransitionOptions.withCrossFade()).
                into(barangViewHolder.img_barang);

        if(barang.isDonasi()){
            barangViewHolder.img_donasi.setVisibility(View.VISIBLE);
        }
        else{
            barangViewHolder.img_donasi.setVisibility(View.INVISIBLE);
        }

        //Pelapak
        barangViewHolder.txt_nama_pelapak.setText(barang.getPenjual().getNama());
        Glide.with(context).load(barang.getPenjual().getImage()).thumbnail(0.5f).
                apply(new RequestOptions()).
                into(barangViewHolder.img_pelapak);
        barangViewHolder.rate_pelapak.setRating(barang.getPenjual().getRating());

        barangViewHolder.layout_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BarangDetailActivity.class);
                i.putExtra(Constant.EXTRA_BARANG, barang.getId());
                context.startActivity(i);
            }
        });

        barangViewHolder.img_keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSharedPreferences.isLoggedIn(activity)){
                    final Dialog dialog = DialogFactory.getInstance().createDialog(activity, R.layout.popup_keranjang_tambah, 70, 50);

                    Button btn_tambah = dialog.findViewById(R.id.btn_tambah);
                    TextView txt_kurang, txt_tambah;
                    txt_kurang = dialog.findViewById(R.id.txt_kurang);
                    txt_tambah = dialog.findViewById(R.id.txt_tambah);
                    final TextView txt_jumlah = dialog.findViewById(R.id.txt_jumlah);
                    txt_jumlah.setText("1");
                    //final ProgressBar bar_loading = dialog.findViewById(R.id.bar_loading);

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
                else{
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }
            }
        });

        barangViewHolder.btn_beli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSharedPreferences.isLoggedIn(activity)){
                    JSONBuilder body = new JSONBuilder();
                    body.add("id_barang", barang.getId());
                    body.add("jumlah", 1);

                    ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_KERANJANG, ApiVolleyManager.METHOD_POST,
                            Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                            new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                        @Override
                        public void onSuccess(String response) {
                            Intent i = new Intent(activity, HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.putExtra(Constant.EXTRA_START, 3);
                            activity.startActivity(i);
                        }

                        @Override
                        public void onFail(String message) {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
                else{
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    public class BarangViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout layout_barang;

        //Variabel barang
        public ImageView img_barang;
        TextView txt_nama, txt_harga;

        //Variabel pelapak
        ImageView img_keranjang, img_donasi;
        TopCropCircularImageView img_pelapak;
        TextView txt_nama_pelapak;
        RatingBar rate_pelapak;

        Button btn_beli;

        BarangViewHolder(@NonNull View view) {
            super(view);
            layout_barang = view.findViewById(R.id.layout_barang);
            img_barang = view.findViewById(R.id.img_barang);
            img_donasi = view.findViewById(R.id.img_donasi);
            txt_nama = view.findViewById(R.id.txt_nama);
            txt_harga = view.findViewById(R.id.txt_harga);
            img_pelapak = view.findViewById(R.id.img_pelapak);
            img_keranjang = view.findViewById(R.id.img_keranjang);
            txt_nama_pelapak = view.findViewById(R.id.txt_nama_pelapak);
            rate_pelapak = view.findViewById(R.id.rate_pelapak);
            btn_beli = view.findViewById(R.id.btn_beli);
        }
    }
}
