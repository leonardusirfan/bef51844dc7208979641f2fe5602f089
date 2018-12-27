package id.net.gmedia.selby.Barang;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import id.net.gmedia.selby.Barang.Adapter.DetailBarangViewPagerAdapter;
import id.net.gmedia.selby.Barang.Fragment.FragmentDetailBarang;
import id.net.gmedia.selby.Barang.Fragment.FragmentDiskusiBarang;
import id.net.gmedia.selby.Barang.Fragment.FragmentUlasan;
import id.net.gmedia.selby.Util.AppRequestCallback;
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
import id.net.gmedia.selby.Util.JSONBuilder;
import me.relex.circleindicator.CircleIndicator;
import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;

public class LelangDetailActivity extends AppCompatActivity {
    /*
        Activity yang menampilkan informasi detail dari barang lelang
     */

    //Variabel atribut Lelang
    private String nama_barang;
    private String id_lelang = "";
    private String id_barang = "";
    private String deskripsi = "";
    private float rating = 0;
    private String kategori = "";
    private String berat = "";
    private String merk = "";
    private String id_penjual = "";
    private boolean follow = false;

    //Variabel UI
    private Button btn_follow;
    private TextView txt_title, txt_dilihat, txt_terkirim, txt_kondisi;
    private ImageSliderViewPager sliderView;
    private CircleIndicator indicator;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView txt_nama, txt_harga, txt_waktu, txt_bid;
    private FloatingMenuButton fab_tambah;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private Button btn_chat;
    private LinearLayout layout_pelapak;

    private DetailBarangViewPagerAdapter adapter;

    //flag apakah user bisa bidding atau tidak
    private boolean canBid = false;

    //Variabel list image slider
    private ArrayList<ImageContainer> listImage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lelang);

        //Inisialisasi UI
        collapsingToolbar = findViewById(R.id.main_collapsing);
        toolbar = findViewById(R.id.toolbar);
        txt_dilihat = findViewById(R.id.txt_dilihat);
        txt_terkirim = findViewById(R.id.txt_terkirim);
        txt_kondisi = findViewById(R.id.txt_kondisi);
        txt_waktu = findViewById(R.id.txt_waktu);
        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        txt_title = findViewById(R.id.txt_title);
        txt_bid = findViewById(R.id.txt_bid);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);
        sliderView = findViewById(R.id.pager);
        indicator = findViewById(R.id.indicator);
        fab_tambah = findViewById(R.id.fab_tambah);
        btn_follow = findViewById(R.id.btn_follow);
        btn_chat = findViewById(R.id.btn_chat);
        layout_pelapak = findViewById(R.id.layout_pelapak);

        //Inisialisasi Toolbar
        initToolbar();

        //Follow/unfollow penjual
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!id_penjual.equals("")){
                    followPenjual();
                }
            }
        });

        //Inisialisasi Lelang
        initLelang();

        //Membuka activity list bid yang menampilkan history bid dari barang yang bersangkutan
        findViewById(R.id.btn_list_bid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!id_lelang.equals("")){
                    Intent i = new Intent(LelangDetailActivity.this, ActivityBidList.class);
                    i.putExtra("id_lelang", id_lelang);
                    startActivity(i);
                }
            }
        });

        findViewById(R.id.btn_bid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Menampilkan dialog bid
                if(!AppSharedPreferences.isLoggedIn(LelangDetailActivity.this)){
                    startActivity(new Intent(LelangDetailActivity.this, LoginActivity.class));
                }
                else if(canBid){
                    bid();
                }
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

    private void initLelang(){
        //Membaca detail barang lelang dari Web Service
        if (getIntent().hasExtra("lelang")) {
            JSONBuilder body = new JSONBuilder();
            id_lelang = getIntent().getStringExtra("lelang");
            body.add("id", id_lelang);

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DETAIL_LELANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                @Override
                public void onEmpty(String message) {
                    Toast.makeText(LelangDetailActivity.this, "Barang tidak ditemukan", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject lelang = new JSONObject(result);

                        id_barang = lelang.getString("id_barang");
                        deskripsi = lelang.getString("deskripsi");
                        kategori = lelang.getString("category");
                        berat = String.valueOf(lelang.getInt("berat")) + " " + lelang.getString("berat_satuan");
                        merk = lelang.getString("brand");
                        rating = (float) lelang.getDouble("rating");

                        txt_dilihat.setText(lelang.getString("dilihat"));
                        txt_terkirim.setText(lelang.getString("terjual"));
                        txt_kondisi.setText(lelang.getString("kondisi"));

                        nama_barang = lelang.getString("nama");
                        txt_nama.setText(nama_barang);
                        txt_harga.setText(Converter.doubleToRupiah(lelang.getDouble("bid_awal")));
                        txt_bid.setText(Converter.doubleToRupiah(lelang.getDouble("bid_akhir")));

                        id_penjual = lelang.getJSONObject("penjual").getString("id");
                        if(!lelang.getJSONObject("penjual").getString("uid").equals(FirebaseAuth.getInstance().getUid())){
                            layout_pelapak.setVisibility(View.VISIBLE);
                        }
                        Glide.with(LelangDetailActivity.this).load(lelang.getJSONObject("penjual").getString("image")).apply(new RequestOptions().circleCrop()).thumbnail(0.1f).into((ImageView)findViewById(R.id.img_artis));
                        follow = lelang.getJSONObject("penjual").getInt("followed") == 1;
                        if(follow){
                            btn_follow.setText(R.string.penjual_unfollow);
                        }

                        ImageContainer imageContainer = new ImageContainer();
                        imageContainer.setImage(lelang.getString("image"));
                        listImage.add(imageContainer);
                        JSONArray galeri = lelang.getJSONArray("gallery");
                        for(int i = 0; i < galeri.length(); i++){
                            imageContainer = new ImageContainer();
                            imageContainer.setImage(galeri.getJSONObject(i).getString("image"));
                            listImage.add(imageContainer);
                        }

                        //Inisialisasi Timer lelang
                        Date now = new Date();
                        Date end = Converter.stringDTTToDate(lelang.getString("end"));
                        long timeLeft = 0;
                        if(end != null){
                            timeLeft = end.getTime() - now.getTime();
                        }
                        new CountDownTimer(timeLeft, 1000){

                            long secondsInMilli = 1000;
                            long minutesInMilli = secondsInMilli * 60;
                            long hoursInMilli = minutesInMilli * 60;
                            long daysInMilli = hoursInMilli * 24;

                            @Override
                            public void onTick(long millisUntilFinished) {
                                long elapsedDays = millisUntilFinished / daysInMilli;
                                millisUntilFinished =  millisUntilFinished % daysInMilli;

                                long elapsedHours =  millisUntilFinished / hoursInMilli;
                                millisUntilFinished =  millisUntilFinished % hoursInMilli;

                                long elapsedMinutes =  millisUntilFinished / minutesInMilli;
                                millisUntilFinished =  millisUntilFinished % minutesInMilli;

                                long elapsedSeconds =  millisUntilFinished / secondsInMilli;
                                String waktu = String.format(Locale.getDefault(), "%02d : %02d : %02d : %02d",elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
                                txt_waktu.setText(waktu);
                            }

                            @Override
                            public void onFinish() {
                                canBid = false;
                            }
                        }.start();

                        //Apabila lelang sudah siap, proses bidding bisa dilakukan
                        canBid = true;

                        //Menginisialisasi slider
                        initSlider();
                    } catch (JSONException e) {
                        Toast.makeText(LelangDetailActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                        Log.e("Barang Detail", e.toString());
                    }
                }

                @Override
                public void onFail(String message) {
                    Toast.makeText(LelangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }));
        }
    }

    private void bid(){
        //Menampilkan dialog bid
        final Dialog dialog = DialogFactory.getInstance().createDialog(LelangDetailActivity.this, R.layout.popup_lelang_bid, 75, 45);
        final TextView txt_bid = dialog.findViewById(R.id.txt_bid);
        Button btn_bid = dialog.findViewById(R.id.btn_bid);

        //Melakukan request ke web service untuk memasukkan bid
        btn_bid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txt_bid.getText().toString().equals("")){
                    final double bid = Double.parseDouble(txt_bid.getText().toString());

                    JSONBuilder body = new JSONBuilder();
                    body.add("id_lelang", id_lelang);
                    body.add("nominal", bid);

                    ApiVolleyManager.getInstance().addRequest(LelangDetailActivity.this, Constant.URL_BID, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                        @Override
                        public void onSuccess(String result) {
                            dialog.dismiss();
                            LelangDetailActivity.this.txt_bid.setText(Converter.doubleToRupiah(bid));
                            Toast.makeText(LelangDetailActivity.this, "Bid sebesar " + Converter.doubleToRupiah(bid) + " berhasil", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(String message) {
                            Toast.makeText(LelangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
                else{
                    Toast.makeText(LelangDetailActivity.this, "Masukkan nilai bid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void initToolbar(){
        //Insialisasi toolbar
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

    private void followPenjual(){
        //Mengubah status follow/unfollow terhadap penjual
        //Mengubah status follow/unfollow terhadap penjual
        if(!id_penjual.equals("")) {
            JSONBuilder body = new JSONBuilder();
            body.add("id_penjual", id_penjual);

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_FOLLOW_PENJUAL, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                @Override
                public void onSuccess(String response) {
                    if (follow) {
                        Toast.makeText(LelangDetailActivity.this, "Berhenti Follow berhasil", Toast.LENGTH_SHORT).show();
                        btn_follow.setText(R.string.penjual_follow);
                    } else {
                        Toast.makeText(LelangDetailActivity.this, "Follow berhasil", Toast.LENGTH_SHORT).show();
                        btn_follow.setText(R.string.penjual_unfollow);
                    }

                    follow = !follow;
                }

                @Override
                public void onFail(String message) {
                    Toast.makeText(LelangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }));
        }
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
        bundle.putString("id", id_barang);
        bundle.putFloat("rating", rating);
        fragmentUlasan.setArguments(bundle);
        adapter.addFrag(fragmentUlasan);

        bundle = new Bundle();
        FragmentDiskusiBarang fragmentDiskusiBarang = new FragmentDiskusiBarang();
        bundle.putString("id", id_barang);
        fragmentDiskusiBarang.setArguments(bundle);
        adapter.addFrag(fragmentDiskusiBarang);

        viewPager.setAdapter(adapter);
    }

    private void initSlider(){
        //Inisialisasi Slider
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, sliderView, listImage, true);
        sliderView.setAdapter(sliderAdapter);
        indicator.setViewPager(sliderView);

        sliderView.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if(sliderView.getAdapter()!=null){
                    ((ImageSliderAdapter)sliderView.getAdapter()).setPosition(position);
                }
            }
        });

        //sliderAdapter.startTimer();
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
}
