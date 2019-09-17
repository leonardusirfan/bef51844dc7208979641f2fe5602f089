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
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Model.TransaksiModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class TransaksiActivity extends AppCompatActivity {

    private TransaksiAdapter adapter;
    private List<TransaksiModel> listTransaksi = new ArrayList<>();
    private LoadMoreScrollListener loadManager;

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
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadTransaksi(false);
            }
        };
        rv_transaksi.addOnScrollListener(loadManager);

        loadTransaksi(true);
    }

    private void loadTransaksi(final boolean init){
        if(init){
            AppLoading.getInstance().showLoading(this);
            loadManager.initLoad();
        }

        final int LOAD_COUNT = 10;
        JSONBuilder body = new JSONBuilder();
        body.add("keyword", "");
        body.add("start", loadManager.getLoaded());
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_TRANSAKSI,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listTransaksi.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listTransaksi.clear();
                            }

                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject transaksi = response.getJSONObject(i);
                                listTransaksi.add(new TransaksiModel(transaksi.getString("nobukti"),
                                        transaksi.getString("nobukti"), transaksi.getString("status"),
                                        transaksi.getString("status_transaksi"),
                                        transaksi.getDouble("total")));
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(TransaksiActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(TransaksiActivity.this, message, Toast.LENGTH_SHORT).show();
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
