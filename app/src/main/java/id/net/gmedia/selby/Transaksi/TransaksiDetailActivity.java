package id.net.gmedia.selby.Transaksi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Model.BarangJualModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class TransaksiDetailActivity extends AppCompatActivity {

    private String id_transaksi = "";

    //Variabel Ui
    private TextView txt_no_transaksi, txt_status, txt_total, txt_alamat;
    private TransaksiDetailBarangAdapter adapter;
    private List<TransaksiDetailBaseModel> listBarang = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail Transaksi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(Constant.EXTRA_TRANSAKSI_ID)){
            id_transaksi = getIntent().getStringExtra(Constant.EXTRA_TRANSAKSI_ID);
        }

        //Inisialisasi UI
        txt_no_transaksi = findViewById(R.id.txt_no_transaksi);
        txt_status = findViewById(R.id.txt_status);
        txt_total = findViewById(R.id.txt_total);
        txt_alamat = findViewById(R.id.txt_alamat);

        RecyclerView rv_barang = findViewById(R.id.rv_barang);
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransaksiDetailBarangAdapter(this, listBarang);
        rv_barang.setAdapter(adapter);

        loadTransaksi();
    }

    private void loadTransaksi(){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id", id_transaksi);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DETAIL_TRANSAKSI,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(TransaksiDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject transaksi = new JSONObject(result);

                            txt_no_transaksi.setText(transaksi.getString("nobukti"));
                            txt_total.setText(Converter.doubleToRupiah(transaksi.getDouble("total")));
                            txt_status.setText(transaksi.getString("status_transaksi"));
                            txt_alamat.setText(transaksi.getString("alamat"));

                            JSONArray barang = transaksi.getJSONArray("barang");
                            for(int i = 0; i < barang.length(); i++){
                                JSONObject obj = barang.getJSONObject(i);
                                if(obj.getString("id").equals("")){
                                    listBarang.add(new TransaksiDetailBaseModel(new BarangJualModel(obj.getString("id"),
                                            obj.getString("nama"), obj.getString("image"),
                                            obj.getDouble("harga"), obj.getInt("jumlah")),
                                            TransaksiDetailBaseModel.KURIR));
                                }
                                else{
                                    listBarang.add(new TransaksiDetailBaseModel(new BarangJualModel(obj.getString("id"),
                                            obj.getString("nama"), obj.getString("image"),
                                            obj.getDouble("harga"), obj.getInt("jumlah")),
                                            TransaksiDetailBaseModel.BARANG));
                                }
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(TransaksiDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(TransaksiDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
