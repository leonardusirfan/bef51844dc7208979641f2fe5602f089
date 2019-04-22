package id.net.gmedia.selby.Home;

import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Home.Adapter.BarangAdapter;
import id.net.gmedia.selby.Home.Adapter.KategoriAdapter;
import id.net.gmedia.selby.Home.Adapter.MerchandiseAdapter;
import id.net.gmedia.selby.Home.Adapter.PrelovedAdapter;
import id.net.gmedia.selby.Home.Adapter.SliderAdapter;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.Model.DonasiModel;
import id.net.gmedia.selby.Model.PrelovedModel;
import id.net.gmedia.selby.R;
import com.leonardus.irfan.AppLoading;
import id.net.gmedia.selby.Util.Constant;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.irfan.SimpleObjectModel;

public class BarangActivity extends AppCompatActivity {

    //flag activity
    //apakah preloved, merchandise, atau hot item
    private int JENIS_BARANG;

    //Variabel pencarian barang
    private String kategori = "";

    //variabel load more
    private final int LOAD_COUNT = 12;
    private String search = "";

    //Variabel list dan adapter
    private List<BarangModel> listBarang = new ArrayList<>();
    private List<SimpleObjectModel> listKategori = new ArrayList<>();
    private KategoriAdapter kategoriAdapter;
    private RecyclerView.Adapter barangAdapter;
    private LoadMoreScrollListener loadMoreScrollListener;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView rv_barang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);

        //Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        //Inisialisasi UI
        RecyclerView rv_kategori = findViewById(R.id.rv_kategori);
        rv_barang = findViewById(R.id.rv_list);
        EditText txt_search = findViewById(R.id.txt_search);
        txt_search.setHint(R.string.cari_barang);

        //Init Recycler View dan Adapter
        listKategori.add(new SimpleObjectModel("", "Semua Kategori"));
        kategoriAdapter = new KategoriAdapter(this, listKategori);
        rv_kategori.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setAdapter(kategoriAdapter);

        //Inisialisasi Kategori
        initKategori();

        //Inisialisasi Jenis Barang
        if(getIntent().hasExtra(Constant.EXTRA_JENIS_BARANG)){
            JENIS_BARANG = getIntent().getIntExtra(Constant.EXTRA_JENIS_BARANG, 0);
        }

        switch (JENIS_BARANG) {
            case Constant.BARANG_PRELOVED:{
                //Inisialisasi Recycler View & Adapter
                layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                barangAdapter = new PrelovedAdapter(this, listBarang);
                rv_barang.setLayoutManager(layoutManager);
                rv_barang.setAdapter(barangAdapter);
                //rv_barang.setAnimation(null);

                loadMoreScrollListener = new LoadMoreScrollListener(){
                    @Override
                    public void onLoadMore() {
                        loadPreloved(false, search, false);
                    }
                };
                rv_barang.addOnScrollListener(loadMoreScrollListener);

                loadPreloved(true, search, true);
                break;
            }
            case Constant.BARANG_MERCHANDISE:{
                //Inisialisasi Recycler View & Adapter
                layoutManager = new GridLayoutManager(BarangActivity.this, 2);
                barangAdapter = new MerchandiseAdapter(BarangActivity.this, listBarang);
                rv_barang.setItemAnimator(new DefaultItemAnimator());
                rv_barang.setLayoutManager(layoutManager);
                rv_barang.setAdapter(barangAdapter);

                loadMoreScrollListener = new LoadMoreScrollListener(){
                    @Override
                    public void onLoadMore() {
                        loadMerchandise(false, search, false);
                    }
                };
                rv_barang.addOnScrollListener(loadMoreScrollListener);

                loadMerchandise(true, search, true);
                break;
            }
            case Constant.BARANG_DONASI:{
                //Inisialisasi Recycler View & Adapter
                layoutManager = new GridLayoutManager(BarangActivity.this, 2);
                barangAdapter = new MerchandiseAdapter(BarangActivity.this, listBarang);
                rv_barang.setItemAnimator(new DefaultItemAnimator());
                rv_barang.setLayoutManager(layoutManager);
                rv_barang.setAdapter(barangAdapter);

                loadMoreScrollListener = new LoadMoreScrollListener(){
                    @Override
                    public void onLoadMore() {
                        loadDonasi(false, search, false);
                    }
                };
                rv_barang.addOnScrollListener(loadMoreScrollListener);

                loadDonasi(true, search, true);
                break;
            }
            default:{
                //Inisialisasi Recycler View & Adapter
                layoutManager = new GridLayoutManager(BarangActivity.this, 2);
                barangAdapter = new BarangAdapter(BarangActivity.this, listBarang);
                rv_barang.setItemAnimator(new DefaultItemAnimator());
                rv_barang.setLayoutManager(layoutManager);
                rv_barang.setAdapter(barangAdapter);
                loadMoreScrollListener = new LoadMoreScrollListener() {
                    @Override
                    public void onLoadMore() {
                        loadHotItem(false, search, false);
                    }
                };
                rv_barang.addOnScrollListener(loadMoreScrollListener);

                loadHotItem(true, search, true);
                break;
            }
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
                search = s.toString();
                switch (JENIS_BARANG) {
                    case Constant.BARANG_PRELOVED:
                        loadPreloved(true, search, false);
                        break;
                    case Constant.BARANG_MERCHANDISE:
                        loadMerchandise(true, search, false);
                        break;
                    case Constant.BARANG_DONASI:
                        loadDonasi(true, search, true);
                        break;
                    default:
                        loadHotItem(true, search, false);
                        break;
                }
            }
        });

        initSlider();
    }

    //Mengubah kategori
    public void setKategori(String kategori){
        this.kategori = kategori;
        switch (JENIS_BARANG) {
            case Constant.BARANG_PRELOVED:
                loadPreloved(true, search, true);
                break;
            case Constant.BARANG_MERCHANDISE:
                loadMerchandise(true, search, true);
                break;
            case Constant.BARANG_DONASI:
                loadDonasi(true, search, true);
                break;
            default:
                loadHotItem(true, search, true);
                break;
        }
    }

    private void initKategori(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KATEGORI_BARANG, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    JSONArray arraykategori = new JSONArray(result);
                    for(int i = 0; i < arraykategori.length(); i++){
                        listKategori.add(new SimpleObjectModel(arraykategori.getJSONObject(i).getString("id"),
                                arraykategori.getJSONObject(i).getString("category")));
                    }
                    kategoriAdapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void loadHotItem(final boolean init, String keyword, boolean show_loading){
        if(show_loading){
            AppLoading.getInstance().showLoading(this);
        }
        if(init){
            loadMoreScrollListener.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadMoreScrollListener.getLoaded());
        body.add("count", LOAD_COUNT);
        body.add("keyword", keyword);
        body.add("kategori", kategori);

        ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_HOT_ITEM,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    if(init){
                        listBarang.clear();
                    }

                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject barang = response.getJSONObject(i);
                        listBarang.add(new BarangModel(barang.getString("id_barang"), barang.getString("nama"),
                                barang.getString("image"), barang.getDouble("harga"),
                                barang.getString("jenis").equals("1")?Constant.BARANG_PRELOVED:Constant.BARANG_MERCHANDISE,
                                new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"),
                                        (float)barang.getDouble("rating")), false));
                    }

                    barangAdapter.notifyDataSetChanged();
                    loadMoreScrollListener.finishLoad(response.length());
                }
                catch (JSONException e){
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.toString());
                    loadMoreScrollListener.failedLoad();
                }

                AppLoading.getInstance().stopLoading();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.failedLoad();
            }
        }));
    }

    private void loadPreloved(final boolean init, String keyword, boolean show_loading){
        if(show_loading){
            AppLoading.getInstance().showLoading(this);
        }

        if(init){
            loadMoreScrollListener.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadMoreScrollListener.getLoaded());
        body.add("count", LOAD_COUNT);
        body.add("keyword", keyword);
        body.add("brand", "");
        body.add("kategori", kategori);
        body.add("penjual", "");
        body.add("jenis", "1");

        ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_BARANG_MASTER,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listBarang.clear();
                            barangAdapter.notifyDataSetChanged();
                        }

                        loadMoreScrollListener.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
            public void onSuccess(String result) {
                try{
                    if(init){
                        listBarang.clear();
                    }

                    int previous_loaded = loadMoreScrollListener.getLoaded();
                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject barang = response.getJSONObject(i);
                        boolean isfavorit = false;
                        if(barang.getString("is_favorit").equals("1")){
                            isfavorit = true;
                        }

                        PrelovedModel b = new PrelovedModel(barang.getString("id_barang"), barang.getString("nama"),
                                barang.getString("image"), barang.getDouble("harga"), isfavorit,
                                new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"),
                                        (float)barang.getDouble("rating")), barang.getInt("pemakaian"),
                                barang.getString("satuan_pemakaian"), barang.getInt("donasi")==1);

                        listBarang.add(b);
                    }

                    AppLoading.getInstance().stopLoading();
                    loadMoreScrollListener.finishLoad(response.length());

                    if(init){
                        //barangAdapter.notifyDataSetChanged();
                        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        barangAdapter = new PrelovedAdapter(BarangActivity.this, listBarang);
                        rv_barang.setLayoutManager(layoutManager);
                        rv_barang.setAdapter(barangAdapter);
                    }
                    else{
                        barangAdapter.notifyItemRangeInserted(previous_loaded, loadMoreScrollListener.getLoaded());
                    }
                }
                catch (JSONException e){
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.toString());

                    AppLoading.getInstance().stopLoading();
                    loadMoreScrollListener.failedLoad();
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.failedLoad();
            }
        }));
    }

    /*private void checkAllLoaded(boolean init){
        boolean all_loaded = true;
        for(BarangModel b : listBarang){
            if(!b.isImgLoaded()){
                all_loaded = false;
                break;
            }
        }

        if(all_loaded){
            if(init){
                barangAdapter.notifyDataSetChanged();
            }
            else{
                barangAdapter.notifyItemRangeInserted(previous_loaded, last_loaded);
            }

            AppLoading.getInstance().stopLoading();
            loadMoreScrollListener.finishLoad();
        }
    }*/

    private void loadMerchandise(final boolean init, String keyword, boolean show_loading){
        if(show_loading){
            AppLoading.getInstance().showLoading(this);
        }
        if(init){
            loadMoreScrollListener.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadMoreScrollListener.getLoaded());
        body.add("count", LOAD_COUNT);
        body.add("keyword", keyword);
        body.add("brand", "");
        body.add("kategori", kategori);
        body.add("penjual", "");
        body.add("jenis", "2");

        ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_BARANG_MASTER,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    if(init){
                        listBarang.clear();
                    }

                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject barang = response.getJSONObject(i);
                        boolean isfavorit = false;
                        if(barang.getString("is_favorit").equals("1")){
                            isfavorit = true;
                        }

                        listBarang.add(new BarangModel(barang.getString("id_barang"), barang.getString("nama"),
                                barang.getString("image"), barang.getDouble("harga"), isfavorit,
                                new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"),
                                        (float)barang.getDouble("rating")), barang.getInt("donasi")==1));
                    }

                    barangAdapter.notifyDataSetChanged();
                    AppLoading.getInstance().stopLoading();
                    loadMoreScrollListener.finishLoad(response.length());
                }
                catch (JSONException e){
                    Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.toString());

                    AppLoading.getInstance().stopLoading();
                    loadMoreScrollListener.failedLoad();
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();

                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.failedLoad();
            }
        }));
    }

    private void loadDonasi(final boolean init, String keyword, boolean show_loading){
        if(show_loading){
            AppLoading.getInstance().showLoading(this);
        }
        if(init){
            loadMoreScrollListener.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadMoreScrollListener.getLoaded());
        body.add("count", LOAD_COUNT);
        body.add("keyword", keyword);
        body.add("brand", "");
        body.add("kategori", kategori);
        body.add("penjual", "");

        ApiVolleyManager.getInstance().addRequest(BarangActivity.this, Constant.URL_BARANG_MASTER,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listBarang.clear();
                            }

                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject barang = response.getJSONObject(i);
                                boolean isfavorit = false;
                                if(barang.getString("is_favorit").equals("1")){
                                    isfavorit = true;
                                }

                                if(barang.getInt("donasi") == 1){
                                    listBarang.add(new DonasiModel(barang.getString("id_barang"), barang.getString("nama"),
                                            barang.getString("image"), barang.getDouble("harga"), isfavorit,
                                            barang.getString("jenis").equals("Preloved")?Constant.BARANG_PRELOVED:Constant.BARANG_MERCHANDISE,
                                            new ArtisModel(barang.getString("penjual"), barang.getString("foto_penjual"),
                                                    (float)barang.getDouble("rating"))));
                                }
                            }

                            barangAdapter.notifyDataSetChanged();
                            AppLoading.getInstance().stopLoading();
                            loadMoreScrollListener.finishLoad(response.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.toString());

                            AppLoading.getInstance().stopLoading();
                            loadMoreScrollListener.failedLoad();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.failedLoad();
                    }
                }));
    }

    private void initSlider(){
        List<String> listImage = new ArrayList<>();
        listImage.add(Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" + R.drawable.slider3).toString());
        listImage.add(Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" + R.drawable.slider2).toString());
        listImage.add(Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" + R.drawable.slider1).toString());

        RecyclerView rv_slider = findViewById(R.id.rv_slider);
        rv_slider.setItemAnimator(new DefaultItemAnimator());
        SliderAdapter sliderAdapter = new SliderAdapter(listImage);
        rv_slider.setAdapter(sliderAdapter);
        rv_slider.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rv_slider);
        rv_slider.setVisibility(View.VISIBLE);
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
