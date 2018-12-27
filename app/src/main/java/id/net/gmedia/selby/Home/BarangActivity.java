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

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Home.Adapter.BarangAdapter;
import id.net.gmedia.selby.Home.Adapter.KategoriAdapter;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.Model.ObjectModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.AppRequestCallback;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.JSONBuilder;

public class BarangActivity extends AppCompatActivity {

    //flag activity
    //apakah preloved, merchandise, atau hot item
    private String jenis = "";

    //Variabel pencarian barang
    private String kategori = "";

    //variabel load more
    private boolean loading = false;
    private final int LOAD_COUNT = 4;
    private int last_loaded = 0;

    //Variabel list dan adapter
    private List<BarangModel> listBarang = new ArrayList<>();
    private List<ObjectModel> listKategori = new ArrayList<>();
    private KategoriAdapter kategoriAdapter;
    private BarangAdapter barangAdapter;
    private GridLayoutManager layoutManager;
    private RecyclerView rv_barang;

    private ProgressBar pb_barang;
    private EditText txt_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);

        //Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        //Inisialisasi UI
        pb_barang = findViewById(R.id.pb_barang);
        RecyclerView rv_kategori = findViewById(R.id.rv_kategori);
        rv_barang = findViewById(R.id.rv_barang);
        txt_search = findViewById(R.id.txt_search);

        //Init Recycler View dan Adapter
        kategoriAdapter = new KategoriAdapter(this, listKategori);
        rv_kategori.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setAdapter(kategoriAdapter);

        //Inisialisasi Kategori
        initKategori();

        //Inisialisasi Jenis Barang
        if(getIntent().hasExtra("jenis")){
            jenis = getIntent().getStringExtra("jenis");
        }

        //Inisialisasi Recycler View & Adapter
        layoutManager = new GridLayoutManager(BarangActivity.this, 2);
        barangAdapter = new BarangAdapter(BarangActivity.this, listBarang);
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(layoutManager);
        rv_barang.setAdapter(barangAdapter);

        switch (jenis) {
            case "Preloved":
                rv_barang.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) { // scroll down
                            if (!loading && (layoutManager.getItemCount() - layoutManager.getChildCount() <= layoutManager.findFirstVisibleItemPosition())) {
                                loading = true;
                                loadPreloved();
                            }
                        }
                    }
                });

                initPreloved("");
                break;
            case "Merchandise":
                rv_barang.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) { // scroll down
                            if (!loading && (layoutManager.getItemCount() - layoutManager.getChildCount() <= layoutManager.findFirstVisibleItemPosition())) {
                                loading = true;
                                loadMerchandise();
                            }
                        }
                    }
                });

                initMerchandise("");
                break;
            default:
                rv_barang.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) { // scroll down
                            if (!loading && (layoutManager.getItemCount() - layoutManager.getChildCount() <= layoutManager.findFirstVisibleItemPosition())) {
                                loading = true;
                                loadHotItem();
                            }
                        }
                    }
                });

                initHotItem("");
                break;
        }

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
                switch (jenis) {
                    case "Preloved":
                        initPreloved(s.toString());
                        break;
                    case "Merchandise":
                        initMerchandise(s.toString());
                        break;
                    default:
                        initHotItem(s.toString());
                        break;
                }
            }
        });
    }

    //Mengubah kategori
    public void setKategori(String kategori){
        this.kategori = kategori;
        switch (jenis) {
            case "Preloved":
                initPreloved(txt_search.getText().toString());
                break;
            case "Merchandise":
                initMerchandise(txt_search.getText().toString());
                break;
            default:
                initHotItem(txt_search.getText().toString());
                break;
        }
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
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Kategori", e.getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initHotItem(String keyword){
        last_loaded = 0;

        rv_barang.setVisibility(View.INVISIBLE);
        pb_barang.setVisibility(View.VISIBLE);

        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", LOAD_COUNT);
        body.add("keyword", keyword);
        body.add("kategori", kategori);

        ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_HOT_ITEM, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    listBarang.clear();

                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject barang = response.getJSONObject(i);
                        listBarang.add(new BarangModel(barang.getString("id_barang"), barang.getString("nama"),barang.getString("image"), barang.getDouble("harga"), barang.getString("jenis"), new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"), (float)barang.getDouble("rating"))));
                        last_loaded += 1;
                    }

                    barangAdapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());
                }
                finally {
                    rv_barang.setVisibility(View.VISIBLE);
                    pb_barang.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                rv_barang.setVisibility(View.VISIBLE);
                pb_barang.setVisibility(View.INVISIBLE);
            }
        }));
    }

    private void loadHotItem(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", last_loaded);
        body.add("count", LOAD_COUNT);
        body.add("keyword", txt_search.getText().toString());
        body.add("kategori", kategori);

        ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_HOT_ITEM, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject barang = response.getJSONObject(i);
                        listBarang.add(new BarangModel(barang.getString("id_barang"), barang.getString("nama"),barang.getString("image"), barang.getDouble("harga"), barang.getString("jenis"), new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"), (float)barang.getDouble("rating"))));
                        last_loaded += 1;
                    }

                    barangAdapter.notifyDataSetChanged();
                    loading = false;
                }
                catch (JSONException e){
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());
                    loading = false;
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                loading = false;
            }
        }));
    }

    private void initPreloved(String keyword){
        last_loaded = 0;

        rv_barang.setVisibility(View.INVISIBLE);
        pb_barang.setVisibility(View.VISIBLE);

        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", LOAD_COUNT);
        body.add("keyword", keyword);
        body.add("brand", "");
        body.add("kategori", kategori);
        body.add("penjual", "");
        body.add("jenis", "1");

        ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_BARANG_ARTIS, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    listBarang.clear();

                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject barang = response.getJSONObject(i);
                        boolean isfavorit = false;
                        if(barang.getString("is_favorit").equals("1")){
                            isfavorit = true;
                        }

                        listBarang.add(new BarangModel(barang.getString("id_barang"), barang.getString("nama"),barang.getString("image"), barang.getDouble("harga"), isfavorit, new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"), (float)barang.getDouble("rating"))));
                        last_loaded += 1;
                    }

                    barangAdapter.notifyDataSetChanged();

                    rv_barang.setVisibility(View.VISIBLE);
                    pb_barang.setVisibility(View.INVISIBLE);
                }
                catch (JSONException e){
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());

                    rv_barang.setVisibility(View.VISIBLE);
                    pb_barang.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                rv_barang.setVisibility(View.VISIBLE);
                pb_barang.setVisibility(View.INVISIBLE);
            }
        }));
    }

    private void loadPreloved(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", last_loaded);
        body.add("count", LOAD_COUNT);
        body.add("keyword", txt_search.getText().toString());
        body.add("brand", "");
        body.add("kategori", kategori);
        body.add("penjual", "");
        body.add("jenis", "1");

        ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_BARANG_ARTIS, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject barang = response.getJSONObject(i);
                        boolean isfavorit = false;
                        if(barang.getString("is_favorit").equals("1")){
                            isfavorit = true;
                        }

                        listBarang.add(new BarangModel(barang.getString("id_barang"), barang.getString("nama"),barang.getString("image"), barang.getDouble("harga"), isfavorit, new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"), (float)barang.getDouble("rating"))));
                        last_loaded += 1;
                    }

                    barangAdapter.notifyDataSetChanged();
                    loading = false;
                }
                catch (JSONException e){
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());
                    loading = false;
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                loading = false;
            }
        }));
    }

    private void initMerchandise(String keyword){
        last_loaded = 0;

        rv_barang.setVisibility(View.INVISIBLE);
        pb_barang.setVisibility(View.VISIBLE);

        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", LOAD_COUNT);
        body.add("keyword", keyword);
        body.add("brand", "");
        body.add("kategori", kategori);
        body.add("penjual", "");
        body.add("jenis", "2");

        ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_BARANG_ARTIS, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    listBarang.clear();

                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject barang = response.getJSONObject(i);
                        boolean isfavorit = false;
                        if(barang.getString("is_favorit").equals("1")){
                            isfavorit = true;
                        }

                        listBarang.add(new BarangModel(barang.getString("id_barang"), barang.getString("nama"),barang.getString("image"), barang.getDouble("harga"), isfavorit,new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"), (float)barang.getDouble("rating"))));
                        last_loaded += 1;
                    }

                    barangAdapter.notifyDataSetChanged();

                    rv_barang.setVisibility(View.VISIBLE);
                    pb_barang.setVisibility(View.INVISIBLE);
                }
                catch (JSONException e){
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());

                    rv_barang.setVisibility(View.VISIBLE);
                    pb_barang.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                rv_barang.setVisibility(View.VISIBLE);
                pb_barang.setVisibility(View.INVISIBLE);
            }
        }));
    }

    private void loadMerchandise(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", last_loaded);
        body.add("count", LOAD_COUNT);
        body.add("keyword", txt_search.getText().toString());
        body.add("brand", "");
        body.add("kategori", kategori);
        body.add("penjual", "");
        body.add("jenis", "2");

        ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_BARANG_ARTIS, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject barang = response.getJSONObject(i);
                        boolean isfavorit = false;
                        if(barang.getString("is_favorit").equals("1")){
                            isfavorit = true;
                        }

                        listBarang.add(new BarangModel(barang.getString("id_barang"), barang.getString("nama"),barang.getString("image"), barang.getDouble("harga"), isfavorit));
                        last_loaded += 1;
                    }

                    barangAdapter.notifyDataSetChanged();
                    pb_barang.setVisibility(View.GONE);
                }
                catch (JSONException e){
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Artis", e.toString());

                    pb_barang.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                pb_barang.setVisibility(View.GONE);
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
