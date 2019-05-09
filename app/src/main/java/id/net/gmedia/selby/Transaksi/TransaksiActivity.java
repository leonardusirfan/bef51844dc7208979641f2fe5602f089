package id.net.gmedia.selby.Transaksi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Model.TransaksiModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class TransaksiActivity extends AppCompatActivity {

    private TransaksiAdapter adapter;
    private List<TransaksiModel> listTransaksi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Transaksi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_transaksi = findViewById(R.id.rv_transaksi);
        rv_transaksi.setItemAnimator(new DefaultItemAnimator());
        rv_transaksi.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransaksiAdapter(this, listTransaksi);
        rv_transaksi.setAdapter(adapter);

        loadTransaksi();
    }

    private void loadTransaksi(){
        /*JSONBuilder body = new JSONBuilder();
        body.add("keyword", "");
        body.add("start", "");
        body.add("count", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_TRANSAKSI,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Log.d(Constant.TAG, message);
                    }

                    @Override
                    public void onSuccess(String result) {
                        Log.d(Constant.TAG, result);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(TransaksiActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));*/
        listTransaksi.add(new TransaksiModel("", "BL1912QYNPM6INV", "Selesai", 205500));
        listTransaksi.add(new TransaksiModel("", "BL1811RW1NYKINV", "Proses", 192000));
        listTransaksi.add(new TransaksiModel("", "BL1811NU6ZADINV", "Proses", 1250000));
        listTransaksi.add(new TransaksiModel("", "BL1811NUTMV7INV", "Dibatalkan", 210500));

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
