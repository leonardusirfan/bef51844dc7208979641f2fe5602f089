package id.net.gmedia.selby.Barang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.Model.LelangModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Converter;

public class BarangActivity extends AppCompatActivity {
    /*
        Activity yang menampilkan barang - barang yang sedang dijual artis
     */

    //Variabel penampung artis yang sedang ditampilkan
    private ArtisModel artis;

    //Variabel UI, adapter, dan list Barang
    private RecyclerView rv_lelang;
    private RecyclerView rv_barang;
    private BarangAdapter barangAdapter;
    private LelangAdapter lelangAdapter;
    private List<BarangModel> listBarang = new ArrayList<>();
    private List<LelangModel> listLelang = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);

        //Inisialisasi toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi Artis
        if(getIntent().hasExtra("artis")){
            Gson gson = new Gson();
            artis = gson.fromJson(getIntent().getStringExtra("artis"), ArtisModel.class);
            getSupportActionBar().setTitle(artis.getNama());
        }

        //Inisialisasi barang jual artis
        initBarang();
        initLelang();
    }

    private void initLelang(){
        //Inisialisasi barang lelang artis dari Web Service
        try{
            JSONObject body = new JSONObject();
            body.put("id_penjual", artis.getId());

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_LELANG, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonnresult = new JSONObject(result);
                        int status = jsonnresult.getJSONObject("metadata").getInt("status");
                        String message = jsonnresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            JSONArray response = jsonnresult.getJSONArray("response");
                            for(int i = 0; i < response.length(); i++){
                                JSONObject lelang = response.getJSONObject(i);
                                listLelang.add(new LelangModel(lelang.getString("id"), lelang.getString("nama"), lelang.getString("image"), lelang.getDouble("bid_awal"), lelang.getDouble("bid_awal"), Converter.stringDTTToDate(lelang.getString("end"))));
                            }

                            rv_lelang = findViewById(R.id.rv_lelang);
                            rv_lelang.setVisibility(View.VISIBLE);
                            lelangAdapter = new LelangAdapter(listLelang);
                            rv_lelang.setLayoutManager(new LinearLayoutManager(BarangActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            rv_lelang.setItemAnimator(new DefaultItemAnimator());
                            rv_lelang.setAdapter(lelangAdapter);
                        }
                        else if(status != 404){
                            Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Barang Artis", e.toString());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(BarangActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Barang Artis", e.toString());
        }
    }

    private void initBarang(){
        //Inisialisasi Barang Jualan Artis dari Web Service
        try{
            JSONObject body = new JSONObject();
            body.put("start", 0);
            body.put("count", 0);
            body.put("keyword", "");
            body.put("brand", "");
            body.put("kategori", "");
            body.put("penjual", artis.getId());

            ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_BARANG_ARTIS, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        listBarang.clear();
                        JSONObject jsonnresult = new JSONObject(result);
                        int status = jsonnresult.getJSONObject("metadata").getInt("status");
                        String message = jsonnresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            JSONArray response = jsonnresult.getJSONArray("response");
                            for(int i = 0; i < response.length(); i++){
                                JSONObject barang = response.getJSONObject(i);
                                boolean isfavorit = false;
                                if(barang.getString("is_favorit").equals("1")){
                                    isfavorit = true;
                                }

                                listBarang.add(new BarangModel(barang.getString("id_barang"), barang.getString("nama"),barang.getString("image"), barang.getDouble("harga"), isfavorit));
                            }

                            rv_barang = findViewById(R.id.rv_barang);
                            barangAdapter = new BarangAdapter(BarangActivity.this, listBarang);
                            rv_barang.setLayoutManager(new GridLayoutManager(BarangActivity.this, 2, LinearLayoutManager.VERTICAL, false));
                            rv_barang.setItemAnimator(new DefaultItemAnimator());
                            rv_barang.setAdapter(barangAdapter);
                        }
                        else if(status == 404){
                            findViewById(R.id.rv_barang).setVisibility(View.GONE);
                            findViewById(R.id.txt_kosong).setVisibility(View.VISIBLE);
                        }
                        else{
                            Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Barang Artis", e.toString());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(BarangActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Barang Artis", e.toString());
        }
    }

    //FUNGSI MENU ACTION BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artis, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBarang();
    }
}
