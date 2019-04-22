package id.net.gmedia.selby.Home;

import android.app.Activity;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Home.Adapter.ArtisAdapter;
import id.net.gmedia.selby.Home.Adapter.KategoriAdapter;
import id.net.gmedia.selby.Home.Adapter.SliderAdapter;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.R;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.irfan.SimpleObjectModel;

import id.net.gmedia.selby.Util.Constant;

public class ArtisActivity extends AppCompatActivity {

    //flag activity
    //apakah menampilkan semua artis atau hanya yang difollow
    private boolean follow = false;

    //variabel pencarian artis/penjual
    private String kategori = "";

    //variabel load more
    private final int LOAD_COUNT = 12;
    private String search = "";

    //variabel data & adapter
    private List<ArtisModel> listArtis = new ArrayList<>();
    private List<SimpleObjectModel> listKategori = new ArrayList<>();
    private LoadMoreScrollListener loadManager;
    private ArtisAdapter artisAdapter;
    private KategoriAdapter kategoriAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);

        //Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
        }

        //Inisialisasi UI
        EditText txt_search = findViewById(R.id.txt_search);
        txt_search.setHint(R.string.cari_artis);

        //Init Recycler View dan Adapter
        RecyclerView rv_kategori = findViewById(R.id.rv_kategori);
        listKategori.add(new SimpleObjectModel("", "Semua Kategori"));
        kategoriAdapter = new KategoriAdapter(this, listKategori);
        rv_kategori.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setAdapter(kategoriAdapter);

        RecyclerView rv_artis = findViewById(R.id.rv_list);
        artisAdapter = new ArtisAdapter(this, listArtis);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        rv_artis.setLayoutManager(layoutManager);
        rv_artis.setAdapter(artisAdapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                if(follow){
                    loadArtisFollow(false, search, false);
                }
                else{
                    loadArtis(false, search, false);
                }
            }
        };
        rv_artis.addOnScrollListener(loadManager);

        //Inisialisasi Kategori
        initKategori();

        //Inisialisasi Artis
        follow = getIntent().hasExtra("follow");
        if(follow){
            loadArtisFollow(true, search, true);
        }
        else{
            loadArtis(true, search, true);
        }

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
                if(follow){
                    loadArtisFollow(true, search, false);
                }
                else{
                    loadArtis(true, search, false);
                }
            }
        });

        initSlider();
    }

    public void sendResult(ArtisModel artis){
        Gson gson = new Gson();
        Intent returnIntent = getIntent();
        returnIntent.putExtra(Constant.EXTRA_ARTIS, gson.toJson(artis));
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void initKategori(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KATEGORI_ARTIS, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    JSONArray arraykategori = new JSONArray(result);
                    for(int i = 0; i < arraykategori.length(); i++){
                        listKategori.add(new SimpleObjectModel(arraykategori.getJSONObject(i).getString("id"),
                                arraykategori.getJSONObject(i).getString("pekerjaan")));
                    }
                    kategoriAdapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(ArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(ArtisActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void setKategori(String kategori){
        this.kategori = kategori;
        if(follow){
            loadArtisFollow(true, search, true);
        }
        else{
            loadArtis(true, search, true);
        }
    }

    private void loadArtis(final boolean init, String keyword, boolean show_loading){
        if(show_loading){
            AppLoading.getInstance().showLoading(this);
        }
        if(init){
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("count", LOAD_COUNT);
        body.add("id", "");
        body.add("keyword", keyword);
        body.add("pekerjaan", kategori);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ARTIS, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    if(init){
                        listArtis.clear();
                    }

                    Object json = new JSONTokener(result).nextValue();
                    if(json instanceof JSONObject){
                        JSONArray array = ((JSONObject)json).getJSONArray("pelapak");
                        //int total_count = jsonresult.getJSONObject("response").getInt("total_records");
                        for(int i = 0; i < array.length(); i++){
                            JSONObject artis = array.getJSONObject(i);
                            listArtis.add(new ArtisModel(artis.getString("id"), artis.getString("nama"),
                                    artis.getString("image"), "Amerika Serikat","2 juni 1995",
                                    167, artis.getString("deskripsi")));
                        }
                        loadManager.finishLoad(array.length());
                    }
                    else{
                        loadManager.finishLoad(0);
                    }

                    artisAdapter.notifyDataSetChanged();
                    AppLoading.getInstance().stopLoading();

                }
                catch (JSONException e){
                    Toast.makeText(ArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());

                    AppLoading.getInstance().stopLoading();
                    loadManager.failedLoad();
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(ArtisActivity.this, message, Toast.LENGTH_SHORT).show();

                AppLoading.getInstance().stopLoading();
                loadManager.failedLoad();
            }
        }));
    }

    private void loadArtisFollow(final boolean init, String keyword, boolean show_loading){
        if(show_loading){
            AppLoading.getInstance().showLoading(this);
        }
        if(init){
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("count", LOAD_COUNT);
        body.add("keyword", keyword);
        body.add("pekerjaan", kategori);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ARTIS_DIFOLLOW, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    if(init){
                        listArtis.clear();
                    }

                    Object json = new JSONTokener(result).nextValue();
                    if(json instanceof JSONObject){
                        JSONArray array = ((JSONObject) json).getJSONArray("pelapak");

                        for(int i = 0; i < array.length(); i++){
                            JSONObject artis = array.getJSONObject(i);
                            listArtis.add(new ArtisModel(artis.getString("id"), artis.getString("nama"),
                                    artis.getString("image"), "Amerika Serikat","2 juni 1995",
                                    167, artis.getString("deskripsi")));
                        }
                        loadManager.finishLoad(array.length());
                    }
                    else {
                        loadManager.finishLoad(0);
                    }

                    artisAdapter.notifyDataSetChanged();
                    AppLoading.getInstance().stopLoading();

                }
                catch (JSONException e){
                    Toast.makeText(ArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());

                    AppLoading.getInstance().stopLoading();
                    loadManager.failedLoad();
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(ArtisActivity.this, message, Toast.LENGTH_SHORT).show();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
