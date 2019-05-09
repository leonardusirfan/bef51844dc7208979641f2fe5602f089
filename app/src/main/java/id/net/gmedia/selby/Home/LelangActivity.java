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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Home.Adapter.KategoriAdapter;
import id.net.gmedia.selby.Home.Adapter.LelangAdapter;
import id.net.gmedia.selby.Home.Adapter.SliderAdapter;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.LelangModel;
import id.net.gmedia.selby.R;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.irfan.SimpleObjectModel;

import id.net.gmedia.selby.Util.Constant;

public class LelangActivity extends AppCompatActivity {

    private String kategori = "";
    private String id_penjual = "";

    //Variabel list dan adapter
    private List<LelangModel> listLelang = new ArrayList<>();
    private List<SimpleObjectModel> listKategori = new ArrayList<>();
    private LelangAdapter lelangAdapter;
    private KategoriAdapter kategoriAdapter;
    private LoadMoreScrollListener loadManager;

    //variabel load more
    private String search = "";

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

        if(getIntent().hasExtra(Constant.EXTRA_PENJUAL_ID)){
            id_penjual = getIntent().getStringExtra(Constant.EXTRA_PENJUAL_ID);
        }

        EditText txt_search = findViewById(R.id.txt_search);
        txt_search.setHint(R.string.cari_lelang);

        //Init Recycler View dan Adapter
        RecyclerView rv_kategori = findViewById(R.id.rv_kategori);
        listKategori.add(new SimpleObjectModel("", "Semua Kategori"));
        kategoriAdapter = new KategoriAdapter(this, listKategori);
        rv_kategori.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setAdapter(kategoriAdapter);

        RecyclerView rv_barang = findViewById(R.id.rv_list);
        lelangAdapter = new LelangAdapter(listLelang);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        rv_barang.setLayoutManager(layoutManager);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadLelang(false, search, false);
            }
        };
        rv_barang.setAdapter(lelangAdapter);

        initKategori();
        //loadLelang(LOAD_COUNT, "");

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
                loadLelang(true, search, false);
            }
        });

        initSlider();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLelang(true, search, true);
    }

    //Mengubah kategori
    public void setKategori(String kategori){
        this.kategori = kategori;
        loadLelang(true, search, true);
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
                    Toast.makeText(LelangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(LelangActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void loadLelang(final boolean init, String keyword, boolean show_loading){
        final int LOAD_COUNT = 4;

        if(show_loading){
            AppLoading.getInstance().showLoading(this);
        }
        if(init){
            loadManager.initLoad();
        }

        //Inisialisasi barang lelang artis dari Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("id_penjual", id_penjual);
        body.add("keyword", keyword);
        body.add("kategori", kategori);
        body.add("start", loadManager.getLoaded());
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_LELANG, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    if(init){
                        listLelang.clear();
                    }

                    JSONArray response = new JSONArray(result);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject lelang = response.getJSONObject(i);
                        listLelang.add(new LelangModel(lelang.getString("id"), lelang.getString("nama"),
                                lelang.getString("image"), lelang.getDouble("bid_akhir"),
                                lelang.getDouble("bid_awal"), new ArtisModel(lelang.getString("penjual"),
                                lelang.getString("foto"), (float)lelang.getDouble("rating")),
                                Converter.stringDTToDate(lelang.getString("end")), lelang.getInt("donasi")==1));
                    }

                    loadManager.finishLoad(response.length());
                    lelangAdapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(LelangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.toString());

                    loadManager.failedLoad();
                }

                AppLoading.getInstance().stopLoading();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(LelangActivity.this, message, Toast.LENGTH_SHORT).show();
                AppLoading.getInstance().stopLoading();

                loadManager.failedLoad();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
