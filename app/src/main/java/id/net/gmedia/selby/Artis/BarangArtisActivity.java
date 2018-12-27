package id.net.gmedia.selby.Artis;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Artis.Adapter.BarangArtisAdapter;
import id.net.gmedia.selby.Barang.Adapter.LelangArtisAdapter;
import id.net.gmedia.selby.Util.AppRequestCallback;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.Model.LelangModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.JSONBuilder;

public class BarangArtisActivity extends AppCompatActivity {
    /*
        Activity yang menampilkan barang - barang yang sedang dijual artis
     */

    //Variabel penampung artis yang sedang ditampilkan
    private ArtisModel artis;

    //Variabel UI, adapter, dan list Barang
    private RecyclerView rv_lelang;
    private BarangArtisAdapter barangArtisAdapter;
    private LelangArtisAdapter lelangArtisAdapter;
    private List<BarangModel> listBarang = new ArrayList<>();
    private List<LelangModel> listLelang = new ArrayList<>();

    //variabel load more
    private boolean canLoad = true;
    private boolean loading = false;
    private final int LOAD_COUNT = 4;
    private int last_loaded = 0;

    private NestedScrollView layout_barang;
    private ProgressBar pb_barang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artis_barang);

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

        RecyclerView rv_barang = findViewById(R.id.rv_barang);
        barangArtisAdapter = new BarangArtisAdapter(BarangArtisActivity.this, listBarang);
        rv_barang.setLayoutManager(new GridLayoutManager(BarangArtisActivity.this, 2, LinearLayoutManager.VERTICAL, false));
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setAdapter(barangArtisAdapter);

        pb_barang = findViewById(R.id.pb_barang);
        layout_barang = findViewById(R.id.layout_barang);
        layout_barang.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if(canLoad && !loading){
                    View view = layout_barang.getChildAt(layout_barang.getChildCount() - 1);
                    int diff = (view.getBottom() - (layout_barang.getHeight() + layout_barang.getScrollY()));

                    if (diff == 0) {
                        pb_barang.setVisibility(View.VISIBLE);
                        loading = true;
                        loadBarang();
                    }
                }
            }
        });

        //Inisialisasi barang jual artis
        initBarang();
        initLelang();
    }

    private void initLelang(){
        //Inisialisasi barang lelang artis dari Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("id_penjual", artis.getId());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_LELANG, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    JSONArray result = new JSONArray(response);
                    for(int i = 0; i < result.length(); i++){
                        JSONObject lelang = result.getJSONObject(i);
                        listLelang.add(new LelangModel(lelang.getString("id"), lelang.getString("nama"), lelang.getString("image"), lelang.getDouble("bid_awal"), lelang.getDouble("bid_awal"), Converter.stringDTTToDate(lelang.getString("end"))));
                    }

                    rv_lelang = findViewById(R.id.rv_lelang);
                    rv_lelang.setVisibility(View.VISIBLE);
                    lelangArtisAdapter = new LelangArtisAdapter(listLelang);
                    rv_lelang.setLayoutManager(new LinearLayoutManager(BarangArtisActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    rv_lelang.setItemAnimator(new DefaultItemAnimator());
                    rv_lelang.setAdapter(lelangArtisAdapter);
                }
                catch (JSONException e){
                    Toast.makeText(BarangArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangArtisActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initBarang(){
        //Inisialisasi Barang Jualan Artis dari Web Service
        last_loaded = 0;
        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", LOAD_COUNT);
        body.add("keyword", "");
        body.add("brand", "");
        body.add("kategori", "");
        body.add("penjual", artis.getId());

        ApiVolleyManager.getInstance().addRequest(BarangArtisActivity.this, Constant.URL_BARANG_ARTIS, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    JSONArray result = new JSONArray(response);
                    for(int i = 0; i < result.length(); i++){
                        JSONObject barang = result.getJSONObject(i);
                        boolean isfavorit = false;
                        if(barang.getString("is_favorit").equals("1")){
                            isfavorit = true;
                        }

                        listBarang.add(new BarangModel(barang.getString("id_barang"), barang.getString("nama"),barang.getString("image"), barang.getDouble("harga"), isfavorit, barang.getString("jenis"), new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"), (float)barang.getDouble("rating"))));
                        last_loaded += 1;
                    }

                    if(result.length() == 0){
                        findViewById(R.id.rv_barang).setVisibility(View.GONE);
                        findViewById(R.id.txt_kosong).setVisibility(View.VISIBLE);
                    }

                    barangArtisAdapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(BarangArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangArtisActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void loadBarang(){
        //Inisialisasi Barang Jualan Artis dari Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("start", last_loaded);
        body.add("count", LOAD_COUNT);
        body.add("keyword", "");
        body.add("brand", "");
        body.add("kategori", "");
        body.add("penjual", artis.getId());

        ApiVolleyManager.getInstance().addRequest(BarangArtisActivity.this, Constant.URL_BARANG_ARTIS, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    JSONArray result = new JSONArray(response);
                    for(int i = 0; i < result.length(); i++){
                        JSONObject barang = result.getJSONObject(i);
                        boolean isfavorit = false;
                        if(barang.getString("is_favorit").equals("1")){
                            isfavorit = true;
                        }

                        listBarang.add(new BarangModel(barang.getString("id_barang"),
                                barang.getString("nama"),barang.getString("image"),
                                barang.getDouble("harga"), isfavorit, barang.getString("jenis"),
                                new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"), (float)barang.getDouble("rating"))));
                        last_loaded += 1;
                    }

                    if(result.length() == 0){
                        canLoad = false;
                    }

                    barangArtisAdapter.notifyDataSetChanged();
                    loading = false;
                    pb_barang.setVisibility(View.INVISIBLE);
                }
                catch (JSONException e){
                    Toast.makeText(BarangArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());

                    loading = false;
                    pb_barang.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangArtisActivity.this, message, Toast.LENGTH_SHORT).show();

                loading = false;
                pb_barang.setVisibility(View.INVISIBLE);
            }
        }));
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
    }
}
