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
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Home.Adapter.ArtisAdapter;
import id.net.gmedia.selby.Home.Adapter.KategoriAdapter;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.ObjectModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.AppRequestCallback;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.JSONBuilder;

public class ArtisActivity extends AppCompatActivity {

    //flag activity
    //apakah menampilkan semua artis atau hanya yang difollow
    private boolean follow = false;

    //variabel pencarian artis/penjual
    private String kategori = "";

    //variabel load more
    private boolean loading = false;
    private final int LOAD_COUNT = 9;
    private int last_loaded = 0;

    //variabel UI
    private ProgressBar pb_artis;
    private EditText txt_search;

    //variabel data & adapter
    private GridLayoutManager layoutManager;
    private List<ArtisModel> listArtis = new ArrayList<>();
    private List<ObjectModel> listKategori = new ArrayList<>();
    private ArtisAdapter artisAdapter;
    private KategoriAdapter kategoriAdapter;
    private RecyclerView rv_artis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artis);

        //Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
        }

        //Inisialisasi UI
        pb_artis = findViewById(R.id.pb_artis);
        txt_search = findViewById(R.id.txt_search);

        //Init Recycler View dan Adapter
        RecyclerView rv_kategori = findViewById(R.id.rv_kategori);
        kategoriAdapter = new KategoriAdapter(this, listKategori);
        rv_kategori.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setAdapter(kategoriAdapter);

        rv_artis = findViewById(R.id.rv_artis);
        artisAdapter = new ArtisAdapter(this, listArtis);
        layoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        rv_artis.setLayoutManager(layoutManager);
        rv_artis.setAdapter(artisAdapter);
        rv_artis.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // scroll down
                    if (!loading && (layoutManager.getItemCount() - layoutManager.getChildCount() <= layoutManager.findFirstVisibleItemPosition())) {
                        loading = true;
                        if(follow){
                            loadArtisFollow();
                        }
                        else{
                            loadArtis();
                        }
                    }
                }
            }
        });

        //Inisialisasi Kategori
        initKategori();

        //Inisialisasi Artis
        follow = getIntent().hasExtra("follow");
        if(follow){
            initArtisFollow("");
        }
        else{
            initArtis("");
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
                if(follow){
                    initArtisFollow(s.toString());
                }
                else{
                    initArtis(s.toString());
                }
            }
        });
    }

    private void initKategori(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KATEGORI_ARTIS, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    listKategori.add(new ObjectModel("", "Semua Kategori"));
                    JSONArray arraykategori = new JSONArray(result);
                    for(int i = 0; i < arraykategori.length(); i++){
                        listKategori.add(new ObjectModel(arraykategori.getJSONObject(i).getString("id"),
                                arraykategori.getJSONObject(i).getString("pekerjaan")));
                    }
                    kategoriAdapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(ArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Kategori", e.getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(ArtisActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initArtis(String keyword){
        last_loaded = 0;

        rv_artis.setVisibility(View.INVISIBLE);
        pb_artis.setVisibility(View.VISIBLE);

        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", LOAD_COUNT);
        body.add("id", "");
        body.add("keyword", keyword);
        body.add("pekerjaan", kategori);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ARTIS, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    listArtis.clear();

                    Object json = new JSONTokener(result).nextValue();
                    if(json instanceof JSONObject){
                        JSONArray array = ((JSONObject)json).getJSONArray("pelapak");
                        //int total_count = jsonresult.getJSONObject("response").getInt("total_records");
                        for(int i = 0; i < array.length(); i++){
                            JSONObject artis = array.getJSONObject(i);
                            listArtis.add(new ArtisModel(artis.getString("id"), artis.getString("nama"), artis.getString("image"), "Amerika Serikat","2 juni 1995", 167, artis.getString("deskripsi")));
                            last_loaded += 1;
                        }
                    }

                    artisAdapter.notifyDataSetChanged();

                    rv_artis.setVisibility(View.VISIBLE);
                    pb_artis.setVisibility(View.INVISIBLE);
                }
                catch (JSONException e){
                    Toast.makeText(ArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("InitArtis", e.getMessage());

                    rv_artis.setVisibility(View.VISIBLE);
                    pb_artis.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(ArtisActivity.this, message, Toast.LENGTH_SHORT).show();

                rv_artis.setVisibility(View.VISIBLE);
                pb_artis.setVisibility(View.INVISIBLE);
            }
        }));
    }

    private void initArtisFollow(String keyword){
        last_loaded = 0;

        rv_artis.setVisibility(View.INVISIBLE);
        pb_artis.setVisibility(View.VISIBLE);

        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", LOAD_COUNT);
        body.add("keyword", keyword);
        body.add("pekerjaan", kategori);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ARTIS_DIFOLLOW, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    listArtis.clear();

                    Object json = new JSONTokener(result).nextValue();
                    if(json instanceof JSONObject){
                        JSONArray array = ((JSONObject) json).getJSONArray("pelapak");

                        for(int i = 0; i < array.length(); i++){
                            JSONObject artis = array.getJSONObject(i);
                            listArtis.add(new ArtisModel(artis.getString("id"), artis.getString("nama"), artis.getString("image"), "Amerika Serikat","2 juni 1995", 167, artis.getString("deskripsi")));
                            last_loaded += 1;
                        }
                    }

                    artisAdapter.notifyDataSetChanged();

                    rv_artis.setVisibility(View.VISIBLE);
                    pb_artis.setVisibility(View.INVISIBLE);
                }
                catch (JSONException e){
                    Toast.makeText(ArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("InitArtis", e.getMessage());

                    rv_artis.setVisibility(View.VISIBLE);
                    pb_artis.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(ArtisActivity.this, message, Toast.LENGTH_SHORT).show();

                rv_artis.setVisibility(View.VISIBLE);
                pb_artis.setVisibility(View.INVISIBLE);
            }
        }));
    }

    private void loadArtis(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", last_loaded);
        body.add("count", LOAD_COUNT);
        body.add("id", "");
        body.add("keyword", txt_search.getText().toString());
        body.add("pekerjaan", kategori);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ARTIS, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
            @Override
            public void onEmpty(String message) {
                loading = false;
            }

            @Override
            public void onSuccess(String result) {
                try{
                    JSONArray array = new JSONObject(result).getJSONArray("pelapak");
                    //int total_count = jsonresult.getJSONObject("response").getInt("total_records");
                    for(int i = 0; i < array.length(); i++){
                        JSONObject artis = array.getJSONObject(i);
                        listArtis.add(new ArtisModel(artis.getString("id"), artis.getString("nama"), artis.getString("image"), "Amerika Serikat","2 juni 1995", 167, artis.getString("deskripsi")));
                        last_loaded += 1;
                    }

                    artisAdapter.notifyDataSetChanged();

                    loading = false;
                }
                catch (JSONException e){
                    Toast.makeText(ArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("InitArtis", e.getMessage());
                    loading = false;
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(ArtisActivity.this, message, Toast.LENGTH_SHORT).show();
                loading = false;
            }
        }));
    }

    private void loadArtisFollow(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", last_loaded);
        body.add("count", LOAD_COUNT);
        body.add("keyword", txt_search.getText().toString());
        body.add("pekerjaan", kategori);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ARTIS_DIFOLLOW, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
            @Override
            public void onEmpty(String message) {
                loading = false;
            }

            @Override
            public void onSuccess(String result) {
                try{
                    JSONArray array = new JSONObject("response").getJSONArray("pelapak");

                    for(int i = 0; i < array.length(); i++){
                        JSONObject artis = array.getJSONObject(i);
                        listArtis.add(new ArtisModel(artis.getString("id"), artis.getString("nama"), artis.getString("image"), "Amerika Serikat","2 juni 1995", 167, artis.getString("deskripsi")));
                        last_loaded += 1;
                    }

                    artisAdapter.notifyDataSetChanged();

                    loading = false;
                }
                catch (JSONException e){
                    Toast.makeText(ArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("InitArtis", e.getMessage());
                    loading = false;
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(ArtisActivity.this, message, Toast.LENGTH_SHORT).show();
                loading = false;
            }
        }));
    }

    public void setKategori(String kategori){
        this.kategori = kategori;
        if(follow){
            initArtisFollow(txt_search.getText().toString());
        }
        else{
            initArtis(txt_search.getText().toString());
        }
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
