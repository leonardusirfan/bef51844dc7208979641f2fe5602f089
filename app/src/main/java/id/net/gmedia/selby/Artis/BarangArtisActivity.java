package id.net.gmedia.selby.Artis;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Artis.Adapter.LelangArtisAdapter;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.SimpleObjectModel;

import id.net.gmedia.selby.Home.Adapter.MerchandiseAdapter;
import id.net.gmedia.selby.Home.Adapter.PrelovedAdapter;
import id.net.gmedia.selby.Model.PrelovedModel;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.Model.LelangModel;
import id.net.gmedia.selby.R;

public class BarangArtisActivity extends AppCompatActivity {
    /*
        Activity yang menampilkan barang - barang yang sedang dijual artis
     */

    //Variabel penampung artis yang sedang ditampilkan
    private ArtisModel artis;
    private boolean preloved = true;
    private String search = "";
    private boolean search_visible = false;

    //Variabel UI, adapter, dan list Barang
    private EditText txt_search;
    private TabLayout tab_barang;
    private RecyclerView rv_lelang;
    private RecyclerView rv_barang;
    private PrelovedAdapter prelovedAdapter;
    private MerchandiseAdapter merchandiseAdapter;
    private LelangArtisAdapter lelangAdapter;
    private List<BarangModel> listPreloved = new ArrayList<>();
    private List<BarangModel> listMerchandise = new ArrayList<>();
    private List<LelangModel> listLelang = new ArrayList<>();
    private List<SimpleObjectModel> listKategori = new ArrayList<>();
    private KategoriBarangArtisAdapter kategoriAdapter;

    //variabel load more
    private boolean canLoad = true;
    private boolean loading = false;
    private int last_loaded = 0;

    private NestedScrollView layout_barang;

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
        if(getIntent().hasExtra(Constant.EXTRA_ARTIS)){
            Gson gson = new Gson();
            artis = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_ARTIS), ArtisModel.class);
            getSupportActionBar().setTitle(artis.getNama());
        }

        //Inisialisasi TabLayout
        tab_barang = findViewById(R.id.tab_barang);
        tab_barang.addTab(tab_barang.newTab().setText("Preloved"));
        tab_barang.addTab(tab_barang.newTab().setText("Merchandise"));
        tab_barang.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchBarang(tab.getPosition()==0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        rv_barang = findViewById(R.id.rv_barang);
        prelovedAdapter = new PrelovedAdapter(this, listPreloved);
        merchandiseAdapter = new MerchandiseAdapter(this, listMerchandise);
        rv_barang.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setAdapter(prelovedAdapter);

        layout_barang = findViewById(R.id.layout_barang);
        layout_barang.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if(canLoad && !loading){
                    View view = layout_barang.getChildAt(layout_barang.getChildCount() - 1);
                    int diff = (view.getBottom() - (layout_barang.getHeight() + layout_barang.getScrollY()));

                    if (diff == 0) {
                        loading = true;
                        loadBarang(false);
                    }
                }
            }
        });

        txt_search = findViewById(R.id.txt_search);
        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!search_visible){
                    setSearch(true);
                }
                else{
                    search = txt_search.getText().toString();
                    loadBarang(true);

                    setSearch(false);
                }
            }
        });

        txt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search = txt_search.getText().toString();
                    loadBarang(true);

                    setSearch(false);
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.btn_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKategori();
            }
        });

        //Inisialisasi barang jual artis
        initKategori();
        loadBarang(true);
        initLelang();
    }

    private void showKategori(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int device_TotalWidth = size.x;
        int device_TotalHeight = size.y;

        final Dialog dialog = new Dialog(BarangArtisActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_barang_artis_kategori);

        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(Math.round(device_TotalWidth* 0.6f), device_TotalHeight);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.gravity = Gravity.END;
            lp.windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setAttributes(lp);
        }

        RecyclerView rv_kategori = dialog.findViewById(R.id.rv_kategori);
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        kategoriAdapter = new KategoriBarangArtisAdapter(listKategori);
        rv_kategori.setAdapter(kategoriAdapter);
        rv_kategori.setLayoutManager(new LinearLayoutManager(this));

        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load barang by kategori filter
                dialog.dismiss();
            }
        });

        dialog.show();
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

                            if(kategoriAdapter != null){
                                kategoriAdapter.notifyDataSetChanged();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(BarangArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(BarangArtisActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void switchBarang(boolean is_prelove){
        if(is_prelove){
            preloved = true;

            prelovedAdapter = new PrelovedAdapter(this, listPreloved);
            rv_barang.setAdapter(prelovedAdapter);
            rv_barang.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            loadBarang(true);
        }
        else{
            preloved = false;

            merchandiseAdapter = new MerchandiseAdapter(this, listMerchandise);
            rv_barang.setAdapter(merchandiseAdapter);
            rv_barang.setLayoutManager(new GridLayoutManager(BarangArtisActivity.this, 2, LinearLayoutManager.VERTICAL, false));
            loadBarang(true);
        }
    }

    private void initLelang(){
        //Inisialisasi barang lelang artis dari Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("id_penjual", artis.getId());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_LELANG, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    JSONArray result = new JSONArray(response);
                    for(int i = 0; i < result.length(); i++){
                        JSONObject lelang = result.getJSONObject(i);
                        listLelang.add(new LelangModel(lelang.getString("id"), lelang.getString("nama"),
                                lelang.getString("image"), lelang.getDouble("bid_awal"),
                                lelang.getDouble("bid_awal"), Converter.stringDTToDate(lelang.getString("end"))));
                    }

                    if(result.length() != 0){
                        rv_lelang = findViewById(R.id.rv_lelang);
                        rv_lelang.setVisibility(View.VISIBLE);
                        lelangAdapter = new LelangArtisAdapter(listLelang, artis.getId());
                        rv_lelang.setLayoutManager(new LinearLayoutManager(BarangArtisActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        rv_lelang.setItemAnimator(new DefaultItemAnimator());
                        rv_lelang.setAdapter(lelangAdapter);

                        ((AppBarLayout)findViewById(R.id.main_appbar)).setExpanded(true, true);
                    }
                }
                catch (JSONException e){
                    Toast.makeText(BarangArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.toString());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangArtisActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void loadBarang(final boolean init){
        final int LOAD_COUNT = 10;

        if(init){
            AppLoading.getInstance().showLoading(this);
            last_loaded = 0;
            canLoad = true;
        }

        //Inisialisasi Barang Jualan Artis dari Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("start", last_loaded);
        body.add("count", LOAD_COUNT);
        body.add("keyword", search);
        body.add("brand", "");
        body.add("kategori", "");
        body.add("penjual", artis.getId());
        body.add("jenis", preloved?"1":"2");

        ApiVolleyManager.getInstance().addRequest(BarangArtisActivity.this, Constant.URL_BARANG_MASTER,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            findViewById(R.id.rv_barang).setVisibility(View.GONE);
                            findViewById(R.id.txt_kosong).setVisibility(View.VISIBLE);
                        }

                        AppLoading.getInstance().stopLoading();
                        canLoad = false;
                        loading = false;
                    }

                    @Override
                    public void onSuccess(String response) {
                        try{
                            if(init){
                                findViewById(R.id.rv_barang).setVisibility(View.VISIBLE);
                                findViewById(R.id.txt_kosong).setVisibility(View.GONE);

                                if(preloved){
                                    listPreloved.clear();
                                }
                                else{
                                    listMerchandise.clear();
                                }
                            }

                            JSONArray result = new JSONArray(response);
                            for(int i = 0; i < result.length(); i++){
                                JSONObject barang = result.getJSONObject(i);
                                boolean isfavorit = false;
                                if(barang.getString("is_favorit").equals("1")){
                                    isfavorit = true;
                                }

                                if(preloved){
                                    listPreloved.add(new PrelovedModel(barang.getString("id_barang"),
                                            barang.getString("nama"),barang.getString("image"),
                                            barang.getDouble("harga"), isfavorit,
                                            new ArtisModel(barang.getString("penjual"),
                                            barang.getString("foto_penjual"), (float)barang.getDouble("rating")),
                                            barang.getInt("pemakaian"), barang.getString("satuan_pemakaian"),
                                            barang.getInt("donasi")==1));
                                }
                                else{
                                    listMerchandise.add(new BarangModel(barang.getString("id_barang"),
                                            barang.getString("nama"),barang.getString("image"),
                                            barang.getDouble("harga"), isfavorit,
                                            Constant.BARANG_MERCHANDISE, new ArtisModel(barang.getString("penjual"),
                                            barang.getString("foto_penjual"), (float)barang.getDouble("rating")),
                                            barang.getInt("donasi")==1));
                                }

                                last_loaded += 1;
                            }

                            if(preloved){
                                prelovedAdapter = new PrelovedAdapter(BarangArtisActivity.this, listPreloved);
                                rv_barang.setAdapter(prelovedAdapter);
                            }
                            else{
                                merchandiseAdapter = new MerchandiseAdapter(BarangArtisActivity.this, listMerchandise);
                                rv_barang.setAdapter(merchandiseAdapter);
                            }

                            AppLoading.getInstance().stopLoading();
                            loading = false;
                        }
                        catch (JSONException e){
                            Toast.makeText(BarangArtisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.toString());

                            AppLoading.getInstance().stopLoading();
                            loading = false;
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(BarangArtisActivity.this, message, Toast.LENGTH_SHORT).show();

                        AppLoading.getInstance().stopLoading();
                        loading = false;
                    }
        }));
    }

    private void setSearch(boolean is_search){
        if(is_search){
            search_visible = true;

            tab_barang.setVisibility(View.GONE);
            txt_search.setVisibility(View.VISIBLE);

            txt_search.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(txt_search, InputMethodManager.SHOW_IMPLICIT);
        }
        else{
            search_visible = false;
            /*txt_search.setText("");
            search = "";*/

            tab_barang.setVisibility(View.VISIBLE);
            txt_search.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(search_visible){
            setSearch(false);
        }
        else{
            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
