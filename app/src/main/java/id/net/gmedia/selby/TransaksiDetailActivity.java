package id.net.gmedia.selby;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;

import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.Util.Converter;

public class TransaksiDetailActivity extends AppCompatActivity {

    private int jumlah = 1;
    private double harga = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_detail);

        final TextView txt_nama, txt_harga, txt_jumlah, txt_minus, txt_plus, txt_total;
        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        txt_minus = findViewById(R.id.txt_minus);
        txt_plus = findViewById(R.id.txt_plus);
        txt_total = findViewById(R.id.txt_total);
        ImageView img_barang = findViewById(R.id.img_barang);

        txt_jumlah.setText(String.valueOf(jumlah));
        txt_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika kurang dari stok
                if(jumlah < 10){
                    jumlah++;
                    txt_jumlah.setText(String.valueOf(jumlah));
                    txt_total.setText(Converter.doubleToRupiah(harga * jumlah));
                }
            }
        });

        txt_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika lebih dari 1
                if(jumlah > 1){
                    jumlah--;
                    txt_jumlah.setText(String.valueOf(jumlah));
                    txt_total.setText(Converter.doubleToRupiah(harga * jumlah));
                }
            }
        });

        if(getIntent().hasExtra("barang")){
            Gson gson = new Gson();
            BarangModel barang = gson.fromJson(getIntent().getStringExtra("barang"), BarangModel.class);

            txt_nama.setText(barang.getNama());
            Glide.with(TransaksiDetailActivity.this).load(barang.getUrl()).transition(DrawableTransitionOptions.withCrossFade()).into(img_barang);
            harga = barang.getHarga();
            txt_harga.setText(Converter.doubleToRupiah(harga));
            txt_total.setText(Converter.doubleToRupiah(harga));
        }
    }
}
