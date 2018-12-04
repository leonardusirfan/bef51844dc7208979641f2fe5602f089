package id.net.gmedia.selby.Home.Favorit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import id.net.gmedia.selby.Barang.BarangDetailActivity;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.LoginActivity;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.DialogFactory;

public class FavoritAdapter extends RecyclerView.Adapter<FavoritAdapter.FavoritViewHolder> {

    private Activity activity;
    private List<BarangModel> listBarang;

    FavoritAdapter(Activity activity, List<BarangModel> listBarang){
        this.activity = activity;
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public FavoritViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FavoritViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_favorit, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoritViewHolder favoritViewHolder, int i) {
        final BarangModel barang = listBarang.get(i);

        favoritViewHolder.txt_nama.setText(barang.getNama());
        favoritViewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));

        Glide.with(activity).load(barang.getUrl()).transition(DrawableTransitionOptions.withCrossFade()).into(favoritViewHolder.img_barang);

        favoritViewHolder.btn_keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = DialogFactory.getInstance().createDialog(activity, R.layout.popup_tambah, 70, 45);

                Button btn_tambah = dialog.findViewById(R.id.btn_tambah);
                TextView txt_kurang, txt_tambah;
                txt_kurang = dialog.findViewById(R.id.txt_kurang);
                txt_tambah = dialog.findViewById(R.id.txt_tambah);
                final TextView txt_jumlah = dialog.findViewById(R.id.txt_jumlah);
                final ProgressBar bar_loading = dialog.findViewById(R.id.bar_loading);

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
                        bar_loading.setVisibility(View.VISIBLE);

                        try{
                            JSONObject body = new JSONObject();
                            body.put("id_barang", barang.getId());
                            body.put("jumlah", Integer.parseInt(txt_jumlah.getText().toString()));

                            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_KERANJANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        JSONObject jsonresult = new JSONObject(result);
                                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                                        String message = jsonresult.getJSONObject("metadata").getString("message");

                                        if(status == 200){
                                            Toast.makeText(activity, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                        else if(status == 401){
                                            activity.startActivity(new Intent(activity, LoginActivity.class));
                                        }
                                        else{
                                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    catch (JSONException e){
                                        Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                                        Log.e("Tambah Keranjang", e.toString());
                                    }
                                }

                                @Override
                                public void onError(String result) {
                                    Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                                    Log.e("Tambah Keranjang",result);
                                }
                            });
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e("Tambah Keranjang", e.toString());
                        }

                        bar_loading.setVisibility(View.INVISIBLE);
                    }
                });
                dialog.show();
            }
        });

        favoritViewHolder.btn_lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, BarangDetailActivity.class);
                i.putExtra("barang", barang.getId());

                /*ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, FavoritViewHolder.img_barang, "barang");
                activity.startActivity(i, options.toBundle());*/

                activity.startActivity(i);
//                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class FavoritViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_harga;
        private ImageView img_barang, btn_keranjang;
        private Button btn_lihat;

        FavoritViewHolder(@NonNull View itemView) {
            super(itemView);
            img_barang = itemView.findViewById(R.id.img_barang);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            btn_lihat = itemView.findViewById(R.id.btn_lihat);
            btn_keranjang = itemView.findViewById(R.id.btn_keranjang);
        }
    }
}
