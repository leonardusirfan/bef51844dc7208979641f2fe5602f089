package id.net.gmedia.selby.Home;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Home.Adapter.KategoriAdapter;
import id.net.gmedia.selby.Home.Adapter.LelangAdapter;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.LelangModel;
import id.net.gmedia.selby.Model.ObjectModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.AppRequestCallback;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.JSONBuilder;

public class LelangActivity extends AppCompatActivity {

    private String kategori = "";

    //Variabel list dan adapter
    private List<LelangModel> listLelang = new ArrayList<>();
    private List<ObjectModel> listKategori = new ArrayList<>();
    private LelangAdapter lelangAdapter;
    private KategoriAdapter kategoriAdapter;

    private EditText txt_search;

    //variabel load more
    private boolean loading = false;
    private final int LOAD_COUNT = 4;
    private int last_loaded = 0;

    //Variabel load more
    private GridLayoutManager layoutManager;
    private RecyclerView rv_barang;

    private ProgressBar pb_lelang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_lelang);

        //Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        txt_search = findViewById(R.id.txt_search);
        pb_lelang = findViewById(R.id.pb_lelang);

        //Init Recycler View dan Adapter
        RecyclerView rv_kategori = findViewById(R.id.rv_kategori);
        kategoriAdapter = new KategoriAdapter(this, listKategori);
        rv_kategori.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setAdapter(kategoriAdapter);

        rv_barang = findViewById(R.id.rv_lelang);
        lelangAdapter = new LelangAdapter(listLelang);
        layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        rv_barang.setLayoutManager(layoutManager);
        rv_barang.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // scroll down
                    if (!loading && (layoutManager.getItemCount() - layoutManager.getChildCount() <= layoutManager.findFirstVisibleItemPosition())) {
                        loading = true;
                        loadLelang();
                    }
                }
            }
        });
        rv_barang.setAdapter(lelangAdapter);

        initKategori();
        initLelang(LOAD_COUNT, "");

        //Melakukan search ketika edittext searching diubah
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                initLelang(LOAD_COUNT, s.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLelang(last_loaded, txt_search.getText().toString());
    }

    //Mengubah kategori
    public void setKategori(String kategori){
        this.kategori = kategori;
        initLelang(LOAD_COUNT, txt_search.getText().toString());
    }

    private void initKategori(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KATEGORI_BARANG, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    listKategori.add(new ObjectModel("", "Semua Kategori"));
                    JSONArray arraykategori = new JSONArray(result);
                    for(int i = 0; i < arraykategori.length(); i++){
                        listKategori.add(new ObjectModel(arraykategori.getJSONObject(i).getString("id"),
                                arraykategori.getJSONObject(i).getString("category")));
                    }
                    kategoriAdapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(LelangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Kategori", e.getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(LelangActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initLelang(int load, String keyword){
        rv_barang.setVisibility(View.INVISIBLE);
        pb_lelang.setVisibility(View.VISIBLE);

        //Inisialisasi barang lelang artis dari Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("id_penjual", "");
        body.add("keyword", keyword);
        body.add("kategori", kategori);
        body.add("start", 0);
        body.add("count", load);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_LELANG, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    listLelang.clear();
                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject lelang = response.getJSONObject(i);
                        listLelang.add(new LelangModel(lelang.getString("id"), lelang.getString("nama"), lelang.getString("image"), lelang.getDouble("bid_akhir"), lelang.getDouble("bid_awal"), new ArtisModel(lelang.getString("penjual"), lelang.getString("foto"), (float)lelang.getDouble("rating")), Converter.stringDTTToDate(lelang.getString("end"))));
                        last_loaded += 1;
                    }

                    lelangAdapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(LelangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());
                }
                finally {
                    rv_barang.setVisibility(View.VISIBLE);
                    pb_lelang.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(LelangActivity.this, message, Toast.LENGTH_SHORT).show();
                rv_barang.setVisibility(View.VISIBLE);
                pb_lelang.setVisibility(View.INVISIBLE);
            }
        }));
    }

    private void loadLelang(){
        //Inisialisasi barang lelang artis dari Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("id_penjual", "");
        body.add("keyword", txt_search.getText().toString());
        body.add("kategori", kategori);
        body.add("start", last_loaded);
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_LELANG, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject lelang = response.getJSONObject(i);
                        listLelang.add(new LelangModel(lelang.getString("id"), lelang.getString("nama"), lelang.getString("image"), lelang.getDouble("bid_awal"), lelang.getDouble("bid_awal"), new ArtisModel(lelang.getString("penjual"), lelang.getString("foto"), (float)lelang.getDouble("rating")), Converter.stringDTTToDate(lelang.getString("end"))));
                        last_loaded += 1;
                    }

                    lelangAdapter.notifyDataSetChanged();
                    loading = false;
                }
                catch (JSONException e){
                    Toast.makeText(LelangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());
                    loading = false;
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(LelangActivity.this, message, Toast.LENGTH_SHORT).show();
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
}
