package id.net.gmedia.selby.Pembayaran;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Model.AlamatModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class PembayaranAlamatGanti extends AppCompatActivity {

    private Gson gson = new Gson();
    private List<AlamatModel> listAlamat = new ArrayList<>();
    private PembayaranAlamatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_alamat_ganti);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Ubah Alamat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadAlamat();

        RecyclerView rv_alamat = findViewById(R.id.rv_alamat);
        rv_alamat.setItemAnimator(new DefaultItemAnimator());
        rv_alamat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PembayaranAlamatAdapter(this, listAlamat);
        rv_alamat.setAdapter(adapter);
    }

    private void loadAlamat(){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id", "");
        body.add("keyword", "");
        body.add("start", "");
        body.add("count", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ALAMAT_PENGIRIMAN_LIST,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listAlamat.clear();
                        adapter.notifyDataSetChanged();

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listAlamat.clear();
                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject alamat = response.getJSONObject(i);
                                listAlamat.add(new AlamatModel(alamat.getString("id"), alamat.getString("label"),
                                        alamat.getString("penerima"), alamat.getString("telepon"),
                                        alamat.getString("ref_kota"), alamat.getString("kota"),
                                        alamat.getString("ref_provinsi"), alamat.getString("provinsi"),
                                        alamat.getString("alamat"), alamat.getString("kodepos")));
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(PembayaranAlamatGanti.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PembayaranAlamatGanti.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    public void setAlamat(AlamatModel a){
        Intent i = new Intent();
        i.putExtra(Constant.RESULT_ALAMAT, gson.toJson(a));
        setResult(RESULT_OK, i);
        finish();
    }

    public void hapusAlamat(String id){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id", id);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ALAMAT_PENGIRIMAN_HAPUS,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(PembayaranAlamatGanti.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        loadAlamat();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PembayaranAlamatGanti.this, message, Toast.LENGTH_SHORT).show();
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
