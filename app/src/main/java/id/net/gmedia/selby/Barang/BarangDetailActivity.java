package id.net.gmedia.selby.Barang;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Barang.Adapter.DetailBarangViewPagerAdapter;
import id.net.gmedia.selby.Barang.Fragment.FragmentDetailBarang;
import id.net.gmedia.selby.Barang.Fragment.FragmentDiskusiBarang;
import id.net.gmedia.selby.Barang.Fragment.FragmentUlasan;
import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Util.AppSharedPreferences;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.LoginActivity;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.DialogFactory;
import id.net.gmedia.selby.Util.ImageContainer;
import id.net.gmedia.selby.Util.ImageSliderAdapter;
import id.net.gmedia.selby.Util.ImageSliderViewPager;
import me.relex.circleindicator.CircleIndicator;
import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;

public class BarangDetailActivity extends AppCompatActivity {
    /*
        Activity yang menampilkan informasi detail dari barang
     */

    //Variabel penampung menu action bar
    private Menu menu;

    //Variabel atribut barang
    private String nama_barang;
    private String id = "";
    private String id_penjual = "";
    private String deskripsi = "";
    private String kategori = "";
    private String berat = "";
    private String merk = "";
    private float rating = 0;
    private boolean favorit = false;
    private boolean follow = false;

    //Variabel UI
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageSliderViewPager sliderView;
    private CircleIndicator indicator;
    private FloatingMenuButton fab_tambah;
    private Toolbar toolbar;
    private Button btn_follow;
    private CollapsingToolbarLayout collapsingToolbar;
    //private ImageView btn_chat;
    private Button btn_chat;
    private TextView txt_title, txt_nama, txt_harga, txt_kondisi, txt_dilihat, txt_terkirim;
    private LinearLayout layout_pelapak;

    private DetailBarangViewPagerAdapter adapter;

    //Variabel list gambar slider barang
    ArrayList<ImageContainer> listImage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_barang);

        //Inisialisasi UI
        txt_title = findViewById(R.id.txt_title);
        collapsingToolbar = findViewById(R.id.main_collapsing);
        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        txt_kondisi = findViewById(R.id.txt_kondisi);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);
        sliderView = findViewById(R.id.pager);
        indicator = findViewById(R.id.indicator);
        fab_tambah = findViewById(R.id.fab_tambah);
        txt_dilihat = findViewById(R.id.txt_dilihat);
        txt_terkirim = findViewById(R.id.txt_terkirim);
        btn_follow = findViewById(R.id.btn_follow);
        btn_chat = findViewById(R.id.btn_chat);
        layout_pelapak = findViewById(R.id.layout_pelapak);

        //Inisialisasi Toolbar
        initToolbar();

        //Inisialisasi barang
        if (getIntent().hasExtra("barang")) {
            initBarang();
        }

        //Follow/unfollow penjual
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followPenjual();
            }
        });

        //Menambah barang ke keranjang
        findViewById(R.id.btn_keranjang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahKeranjang();
            }
        });

        //membeli barang
        findViewById(R.id.btn_beli).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //beli barang -> transaksi
            /*Intent i = new Intent(BarangDetailActivity.this, TransaksiDetailActivity.class);
            i.putExtra("barang", gson.toJson(barang));
            startActivity(i);*/
            }
        });

        if(AppSharedPreferences.isLoggedIn(this)){
            initFloatingActionButton();
        }
    }

    private void initFloatingActionButton(){
        //Floating Action Button untuk menambah ulasan atau diskusi barang tergantung fragment yang aktif
        fab_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabLayout.getSelectedTabPosition() == 1){
                    //tambah ulasan
                    ((FragmentUlasan)adapter.getItem(1)).bukaDialogReview();
                }
                else if(tabLayout.getSelectedTabPosition() == 2){
                    //tambah diskusi barang
                    ((FragmentDiskusiBarang)adapter.getItem(2)).tambahDiskusi();
                }
            }
        });

        //Mengubah tampilan Floating Action Button tergantung fragment yang aktif
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:fab_tambah.setVisibility(View.INVISIBLE);break;
                    case 1:
                        fab_tambah.setBackgroundResource(R.drawable.ulas);
                        fab_tambah.setVisibility(View.VISIBLE);break;
                    case 2:
                        fab_tambah.setBackgroundResource(R.drawable.diskusi);
                        fab_tambah.setVisibility(View.VISIBLE);break;
                }
                super.onPageSelected(position);
            }
        });
    }

    private void followPenjual(){
        //Mengubah status follow/unfollow terhadap penjual
        if(!id_penjual.equals("")){
            try{
                JSONObject body = new JSONObject();
                body.put("id_penjual", id_penjual);

                ApiVolleyManager.getInstance().addRequest(this, Constant.URL_FOLLOW_PENJUAL, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject jsonresult = new JSONObject(result);
                            int status = jsonresult.getJSONObject("metadata").getInt("status");
                            String message = jsonresult.getJSONObject("metadata").getString("message");

                            if(status == 200){
                                if(follow){
                                    Toast.makeText(BarangDetailActivity.this, "Berhenti Follow berhasil", Toast.LENGTH_SHORT).show();
                                    btn_follow.setText(R.string.penjual_follow);
                                }
                                else{
                                    Toast.makeText(BarangDetailActivity.this, "Follow berhasil", Toast.LENGTH_SHORT).show();
                                    btn_follow.setText(R.string.penjual_unfollow);
                                }

                                follow = !follow;
                            }
                            else{
                                Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(BarangDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e("Follow", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(BarangDetailActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                        Log.e("Follow", result);
                    }
                });
            }
            catch (JSONException e){
                Toast.makeText(BarangDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                Log.e("Follow", e.getMessage());
            }
        }
    }

    private void initToolbar(){
        //Inisialisasi Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        collapsingToolbar.setTitle(" ");
        txt_title.setText("");
        AppBarLayout appBarLayout = findViewById(R.id.main_appbar);
        appBarLayout.setExpanded(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (appBarLayout.getTotalScrollRange() + verticalOffset <= getActionBarHeight()) {
                        if(nama_barang != null){
                            txt_title.setText(nama_barang);
                        }
                        isShow = true;
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    } else if (isShow) {
                        txt_title.setText("");
                        isShow = false;
                        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.style_rectangle_gradient_black));
                    }
                }
            });
        }
    }

    private void initBarang(){
        //Membaca detail barang dari Web Service
        try{
            JSONObject body = new JSONObject();
            id = getIntent().getStringExtra("barang");
            body.put("id", id);

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DETAIL_PRODUK, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            JSONObject barang = jsonresult.getJSONObject("response");

                            nama_barang = barang.getString("nama");
                            txt_nama.setText(nama_barang);
                            txt_harga.setText(Converter.doubleToRupiah(barang.getDouble("harga")));
                            txt_kondisi.setText(barang.getString("kondisi"));
                            txt_terkirim.setText(barang.getString("terjual"));
                            txt_dilihat.setText(barang.getString("dilihat"));

                            deskripsi = barang.getString("deskripsi");
                            kategori = barang.getString("category");
                            berat = String.valueOf(barang.getInt("berat")) + " " + barang.getString("berat_satuan");
                            merk = barang.getString("brand");
                            rating = (float) barang.getDouble("rating");

                            favorit = barang.getString("is_favorit").equals("1");
                            if(favorit){
                                menu.getItem(1).setIcon(R.drawable.ic_favorit_isi);
                            }
                            else{
                                menu.getItem(1).setIcon(R.drawable.ic_favorit_kosong);
                            }

                            id_penjual = barang.getJSONObject("penjual").getString("id");
                            if(!barang.getJSONObject("penjual").getString("uid").equals(FirebaseAuth.getInstance().getUid())){
                                layout_pelapak.setVisibility(View.VISIBLE);
                            }
                            Glide.with(BarangDetailActivity.this).load(barang.getJSONObject("penjual").getString("image")).apply(new RequestOptions().circleCrop().priority(Priority.LOW)).thumbnail(0.1f).into((ImageView)findViewById(R.id.img_artis));
                            follow = barang.getJSONObject("penjual").getInt("followed") == 1;
                            if(follow){
                                btn_follow.setText(R.string.penjual_unfollow);
                            }

                            ImageContainer imageContainer = new ImageContainer();
                            imageContainer.setImage(barang.getString("image"));
                            listImage.add(imageContainer);
                            JSONArray galeri = barang.getJSONArray("gallery");
                            for(int i = 0; i < galeri.length(); i++){
                                imageContainer = new ImageContainer();
                                imageContainer.setImage(galeri.getJSONObject(i).getString("image"));
                                listImage.add(imageContainer);
                            }

                            initSlider();
                        }
                        else{
                            Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(BarangDetailActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                        Log.e("Barang Detail", e.toString());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(BarangDetailActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Detail", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(BarangDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Barang Detail", e.toString());
        }
    }

    private void tambahKeranjang(){
        //Memunculkan dialog dan menambahkan barang ke keranjang
        final Dialog dialog = DialogFactory.getInstance().createDialog(BarangDetailActivity.this, R.layout.popup_keranjang_tambah, 70, 50);

        Button btn_tambah = dialog.findViewById(R.id.btn_tambah);
        TextView txt_kurang, txt_tambah;
        txt_kurang = dialog.findViewById(R.id.txt_kurang);
        txt_tambah = dialog.findViewById(R.id.txt_tambah);
        final TextView txt_jumlah = dialog.findViewById(R.id.txt_jumlah);
        txt_jumlah.setText("1");
        //final ProgressBar bar_loading = dialog.findViewById(R.id.bar_loading);

        txt_kurang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jumlah = Integer.parseInt(txt_jumlah.getText().toString());
                if(jumlah > 1){
                    jumlah--;
                }
                txt_jumlah.setText(String.valueOf(jumlah));
            }
        });

        txt_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jumlah = Integer.parseInt(txt_jumlah.getText().toString());
                jumlah++;
                txt_jumlah.setText(String.valueOf(jumlah));
            }
        });

        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!id.equals("")){
                    //bar_loading.setVisibility(View.VISIBLE);

                    try{
                        JSONObject body = new JSONObject();
                        body.put("id_barang", id);
                        body.put("jumlah", Integer.parseInt(txt_jumlah.getText().toString()));

                        ApiVolleyManager.getInstance().addRequest(BarangDetailActivity.this, Constant.URL_TAMBAH_KERANJANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject jsonresult = new JSONObject(result);
                                    int status = jsonresult.getJSONObject("metadata").getInt("status");
                                    String message = jsonresult.getJSONObject("metadata").getString("message");

                                    if(status == 200){
                                        Toast.makeText(BarangDetailActivity.this, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                    else if(status == 401){
                                        startActivity(new Intent(BarangDetailActivity.this, LoginActivity.class));
                                    }
                                    else{
                                        Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (JSONException e){
                                    Toast.makeText(BarangDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                                    Log.e("Tambah Keranjang", e.toString());
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(BarangDetailActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                                Log.e("Tambah Keranjang",result);
                            }
                        });
                    }
                    catch (JSONException e){
                        Toast.makeText(BarangDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Tambah Keranjang", e.toString());
                    }

                    //bar_loading.setVisibility(View.INVISIBLE);
                }
            }
        });
        dialog.show();
    }

    private void setupViewPager(final ViewPager viewPager) {
        //Inisialisasi View Pager dan Fragment Detail barang, Ulasan, dan Diskusi Barang
        adapter = new DetailBarangViewPagerAdapter(this, getSupportFragmentManager());

        Bundle bundle;
        bundle = new Bundle();
        FragmentDetailBarang detailBarang = new FragmentDetailBarang();
        bundle.putString("deskripsi", deskripsi);
        bundle.putString("kategori", kategori);
        bundle.putString("berat", berat);
        bundle.putString("merk", merk);
        detailBarang.setArguments(bundle);
        adapter.addFrag(detailBarang);

        bundle = new Bundle();
        FragmentUlasan fragmentUlasan = new FragmentUlasan();
        bundle.putString("id", id);
        bundle.putFloat("rating", rating);
        fragmentUlasan.setArguments(bundle);
        adapter.addFrag(fragmentUlasan);

        bundle = new Bundle();
        FragmentDiskusiBarang fragmentDiskusiBarang = new FragmentDiskusiBarang();
        bundle.putString("id", id);
        fragmentDiskusiBarang.setArguments(bundle);
        adapter.addFrag(fragmentDiskusiBarang);

        viewPager.setAdapter(adapter);
    }

    private void initSlider(){
        //Inisialisasi Slider
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, sliderView, listImage, true);
        sliderView.setAdapter(sliderAdapter);

        indicator.setViewPager(sliderView);

        sliderView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(sliderView.getAdapter()!=null){
                    ((ImageSliderAdapter)sliderView.getAdapter()).setPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        sliderAdapter.startTimer();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        if(getSupportActionBar() != null){
            actionBarHeight = getSupportActionBar().getHeight();
            if (actionBarHeight != 0)
                return actionBarHeight;
            final TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    //FUNGSI MENU ACTION BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_barang, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //setCount(String.valueOf(1));
        return super.onPrepareOptionsMenu(menu);
    }

    /*public void setCount(String count) {
        MenuItem menuItem = menu.findItem(R.id.action_keranjang);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(this, R.dimen.text12, R.color.orange);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }*/

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_keranjang:
                Intent i = new Intent(this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("start", 3);
                startActivity(i);
                return true;
            case R.id.action_favorit:
                if(!favorit){
                    //Mengubah status favorit
                    try{
                        JSONObject body = new JSONObject();
                        body.put("id_barang", id);

                        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_TAMBAH_FAVORIT, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject jsonresult = new JSONObject(result);
                                    int status = jsonresult.getJSONObject("metadata").getInt("status");
                                    String message = jsonresult.getJSONObject("metadata").getString("message");

                                    if(status == 200){
                                        //Toast.makeText(BarangDetailActivity.this, "Barang berhasil ditambah", Toast.LENGTH_SHORT).show();
                                        final Dialog dialog = DialogFactory.getInstance().createDialog(BarangDetailActivity.this, R.layout.popup_message, 65, 30);
                                        TextView txt_pesan = dialog.findViewById(R.id.txt_pesan);
                                        dialog.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });

                                        //img_pesan.setImageResource(R.drawable.lovepink);
                                        favorit = true;
                                        item.setIcon(R.drawable.ic_favorit_isi);
                                        txt_pesan.setText(R.string.barang_tambah_favorit);
                                        dialog.show();
                                    }
                                    else if(status == 401){
                                        startActivity(new Intent(BarangDetailActivity.this, LoginActivity.class));
                                    }
                                    else{
                                        Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (JSONException e){
                                    Toast.makeText(BarangDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                                    Log.e("Tambah Favorit", e.toString());
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(BarangDetailActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                                Log.e("Tambah Favorit",result);
                            }
                        });
                    }
                    catch (JSONException e){
                        Toast.makeText(BarangDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Hapus Favorit", e.toString());
                    }
                }
                else{
                    try{
                        JSONObject body = new JSONObject();
                        List<String> listId = new ArrayList<>();
                        listId.add(id);
                        body.put("id_barang", new JSONArray(listId));

                        ApiVolleyManager.getInstance().addRequest(BarangDetailActivity.this, Constant.URL_HAPUS_FAVORIT, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject jsonresult = new JSONObject(result);
                                    int status = jsonresult.getJSONObject("metadata").getInt("status");
                                    String message = jsonresult.getJSONObject("metadata").getString("message");

                                    if(status == 200){
                                        Toast.makeText(BarangDetailActivity.this, "Barang berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        favorit = false;
                                        menu.getItem(1).setIcon(R.drawable.ic_favorit_kosong);
                                    }
                                    else{
                                        Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (JSONException e){
                                    Toast.makeText(BarangDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                                    Log.e("Hapus Favorit", e.toString());
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(BarangDetailActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                                Log.e("Hapus Favorit",result);
                            }
                        });
                    }
                    catch (JSONException e){
                        Toast.makeText(BarangDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Hapus Favorit", e.toString());
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

