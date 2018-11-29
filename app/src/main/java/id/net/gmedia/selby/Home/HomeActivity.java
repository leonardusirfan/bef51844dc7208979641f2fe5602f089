package id.net.gmedia.selby.Home;

import android.app.Activity;
import android.content.Intent;

import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Pix;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import id.net.gmedia.selby.Util.AppSharedPreferences;
import id.net.gmedia.selby.Home.Akun.EditProfilActivity;
import id.net.gmedia.selby.Home.Akun.FragmentAkun;
import id.net.gmedia.selby.Home.Favorit.FragmentFavorit;
import id.net.gmedia.selby.Home.Feed.FragmentFeed;
import id.net.gmedia.selby.Home.Home.FragmentArtis;
import id.net.gmedia.selby.Home.Keranjang.FragmentKeranjang;
import id.net.gmedia.selby.LoginActivity;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Home.Upload.UploadBarangActivity;
import id.net.gmedia.selby.Home.Upload.UploadKegiatanActivity;
import id.net.gmedia.selby.Home.Upload.UploadGambarActivity;
import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;
import rjsv.floatingmenu.floatingmenubutton.subbutton.FloatingSubButton;

public class HomeActivity extends AppCompatActivity {
    /*
        Activity utama (Home) yang mengatur navigasi ke fragment berikut:
        > Home : list public figure
        > Feed : Menampilkan update terbaru dari artis yang difollow
        > Favorit : Menampilkan barang2 yang sudah ditandai favorit oleh user
        > Keranjang : Menampilkan barang2 yang sudah dimasukkan keranjang oleh user -> menuju transaksi pembayaran
        > Akun : Pengaturan akun, bagi public figure sebagai pelapak bisa mengisi post dari sini
     */

    //flag untuk double klik exit
    private boolean exit = false;
    //flag apakah user dalam kondisi sudah login atau belum
    private boolean login = false;

    //pointer tab navigasi yang aktif
    private int selected_tab = 0;
    private int previous_tab = 0;

    //variabel fragment
    private FragmentArtis fragmentArtis;
    private FragmentFeed fragmentFeed;
    private FragmentAkun fragmentAkun;
    //private FragmentKeranjang fragmentKeranjang;

    //variabel UI
    public AppBarLayout appbar;
    private TabLayout tab_home;
    public ProgressBar progress_bar;
    private FloatingMenuButton fab_home;
    private ImageView img_galeri_selected;
    private ConstraintLayout layout_overlay;
    private CardView layout_galeri_selected;

    //Variabel UI galeri (animasi, foto tampil)
    private Animation anim_popin, anim_popout;
    private int imgHeight = 0;
    private int imgWidth = 0;
    private int selectedImage = 0;
    public List<String> listImage = new ArrayList<>();

    //flag apakah galeri sedang menampilkan foto detail secara popup atau tidak
    private boolean detail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Mengecek apakah user dalam kondisi login
        login = AppSharedPreferences.isLoggedIn(this);

        //Inisialisasi UI
        Toolbar toolbar = findViewById(R.id.toolbar);
        appbar = findViewById(R.id.appbar);
        progress_bar = findViewById(R.id.progress_bar);
        layout_overlay = findViewById(R.id.layout_overlay);
        Button btn_next, btn_previous;
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        layout_galeri_selected = findViewById(R.id.layout_galeri_selected);
        img_galeri_selected = findViewById(R.id.img_galeri_selected);

        int start_page = getIntent().getIntExtra("start", 0);

        fab_home = findViewById(R.id.fab_post);
        if(login && AppSharedPreferences.isPenjual(this)){
            fab_home.setVisibility(View.VISIBLE);
            //Jika user sudah login dan merupakan "artis"
            FloatingSubButton sub_barang, sub_gambar, sub_kegiatan;
            sub_kegiatan = findViewById(R.id.sub_kegiatan);
            sub_barang = findViewById(R.id.sub_barang);
            sub_gambar = findViewById(R.id.sub_gambar);
            sub_gambar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, UploadGambarActivity.class));
                }
            });
            sub_barang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, UploadBarangActivity.class));
                }
            });
            sub_kegiatan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, UploadKegiatanActivity.class));
                }
            });
        }

        //Inisialisasi toolbar
        setSupportActionBar(toolbar);

        //Inisialisasi Fragment
        fragmentArtis = new FragmentArtis();

        if(login){
            //fragment feed dan akun hanya muncul jika sudah login
            fragmentFeed = new FragmentFeed();
            fragmentAkun = new FragmentAkun();
            //fragmentKeranjang = new FragmentKeranjang();
        }

        //Inisialisasi tabLayout & Viewpager
        tab_home = findViewById(R.id.tab_home);
        tab_home.addTab(tab_home.newTab().setIcon(R.drawable.homeblack));
        if(login){
            //tampilkan menu feed hanya jika login
            tab_home.addTab(tab_home.newTab().setIcon(R.drawable.feed));
            tab_home.addTab(tab_home.newTab().setIcon(R.drawable.loveline));
            tab_home.addTab(tab_home.newTab().setIcon(R.drawable.cart));
        }
        tab_home.addTab(tab_home.newTab().setIcon(R.drawable.akun));
        tab_home.setTabGravity(TabLayout.GRAVITY_FILL);
        tab_home.setTabMode(TabLayout.MODE_FIXED);

        tab_home.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        selected_tab = 0;
                        changeIcon();
                        loadFragment(fragmentArtis);
                        invalidateOptionsMenu();
                        break;
                    case 1:
                        selected_tab = 1;
                        if(login){
                            //Jika login, menu kedua tampilkan feed
                            changeIcon();
                            loadFragment(fragmentFeed);
                        }
                        else{
                            //Jika belum login munculkan activity login
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        }

                        invalidateOptionsMenu();
                        break;
                    case 2:
                        selected_tab = 2;
                        changeIcon();
                        loadFragment(new FragmentFavorit());
                        invalidateOptionsMenu();
                        break;
                    case 3:
                        selected_tab = 3;
                        changeIcon();
                        loadFragment(new FragmentKeranjang());
                        invalidateOptionsMenu();
                        break;
                    case 4:
                        selected_tab = 4;
                        changeIcon();
                        loadFragment(fragmentAkun);
                        invalidateOptionsMenu();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Inisialisasi popup detail foto galeri
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imgWidth = displayMetrics.widthPixels - displayMetrics.widthPixels/7;
        imgHeight = displayMetrics.heightPixels - displayMetrics.heightPixels/5;

        //Inisialisasi animasi popup
        anim_popin = AnimationUtils.loadAnimation(this, R.anim.anim_pop_in);
        anim_popout = AnimationUtils.loadAnimation(this, R.anim.anim_pop_out);
        anim_popout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                detail=false;
                layout_overlay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layout_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_galeri_selected.startAnimation(anim_popout);
            }
        });


        //Next foto dalam galeri
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage < listImage.size() - 1){
                    selectedImage++;
                }
                else{
                    selectedImage = 0;
                }

                Glide.with(HomeActivity.this).load(listImage.get(selectedImage)).apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
            }
        });

        //Previous foto dalam galeri
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage > 0){
                    selectedImage--;

                }
                else{
                    selectedImage = listImage.size() - 1;
                }

                Glide.with(HomeActivity.this).load(listImage.get(selectedImage)).apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
            }
        });

        switch (start_page){
            case 1:
                Objects.requireNonNull(tab_home.getTabAt(1)).select();
                break;
            case 4:
                Objects.requireNonNull(tab_home.getTabAt(4)).select();
                break;
                default:loadFragment(fragmentArtis);
        }


    }

    private void loadFragment(Fragment fragment){
        //fungsi untuk mengubah fragment yang ditampilkan
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.frame_home, fragment);
        trans.commit();

        appbar.setExpanded(true);
        fab_home.closeMenu();
    }

    public void setView(List<String> listImage, int position){
        //Fungsi untuk menampilkan foto secara popup
        this.listImage = listImage;

        selectedImage = position;
        Glide.with(this).load(listImage.get(selectedImage)).apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
        layout_overlay.setVisibility(View.VISIBLE);
        detail = true;

        layout_galeri_selected.startAnimation(anim_popin);
    }

    private void changeIcon(){
        //mengubah icon tabLayout
        switch (previous_tab){
            case 0:Objects.requireNonNull(tab_home.getTabAt(0)).setIcon(R.drawable.home);break;
            case 1:Objects.requireNonNull(tab_home.getTabAt(1)).setIcon(R.drawable.feed);break;
            case 2:Objects.requireNonNull(tab_home.getTabAt(2)).setIcon(R.drawable.loveline);break;
            case 3:Objects.requireNonNull(tab_home.getTabAt(3)).setIcon(R.drawable.cart);break;
            case 4:Objects.requireNonNull(tab_home.getTabAt(4)).setIcon(R.drawable.akun);break;
        }
        switch (selected_tab){
            case 0:Objects.requireNonNull(tab_home.getTabAt(0)).setIcon(R.drawable.homeblack);break;
            case 1:Objects.requireNonNull(tab_home.getTabAt(1)).setIcon(R.drawable.feedblack);break;
            case 2:Objects.requireNonNull(tab_home.getTabAt(2)).setIcon(R.drawable.loveblok);break;
            case 3:Objects.requireNonNull(tab_home.getTabAt(3)).setIcon(R.drawable.cartblock);break;
            case 4:Objects.requireNonNull(tab_home.getTabAt(4)).setIcon(R.drawable.akunblack);break;
        }
        previous_tab = selected_tab;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_view:
                boolean view_slider = fragmentArtis.changeView();
                if(view_slider){
                    appbar.setExpanded(true);
                    item.setIcon(R.drawable.ic_thumbnail);
                }
                else{
                    item.setIcon(R.drawable.ic_slide);
                }
                return true;
            case R.id.action_setting:
                if(fragmentAkun.user != null){
                    Gson gson = new Gson();
                    Intent i = new Intent(this, EditProfilActivity.class);
                    i.putExtra("user", gson.toJson(fragmentAkun.user));
                    startActivity(i);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (selected_tab){
            case 0:getMenuInflater().inflate(R.menu.menu_artis, menu);
                if(!fragmentArtis.isView_slider()){
                    menu.getItem(1).setIcon(R.drawable.ic_slide);
                }
                break;
            case 1:getMenuInflater().inflate(R.menu.menu_feed, menu);break;
            case 2:getMenuInflater().inflate(R.menu.menu_feed, menu);break;
            case 3:getMenuInflater().inflate(R.menu.menu_feed, menu);break;
            case 4:getMenuInflater().inflate(R.menu.menu_akun, menu);break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999 && data.hasExtra(Pix.IMAGE_RESULTS)) {
            fragmentAkun.upload(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0), false);
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == 998 && data.hasExtra(Pix.IMAGE_RESULTS)) {
            fragmentAkun.upload(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0), true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(selected_tab == 1 && !AppSharedPreferences.isLoggedIn(this)){
            Objects.requireNonNull(tab_home.getTabAt(0)).select();
        }
        else if(selected_tab == 2){
            Objects.requireNonNull(tab_home.getTabAt(2)).select();
            loadFragment(new FragmentFavorit());
        }
    }

    @Override
    public void onBackPressed() {
        //Jika ditekan back saat bukan di home dan saat belum login, maka kembali ke home
        if(detail){
            layout_galeri_selected.startAnimation(anim_popout);
        }
        else if(selected_tab != 0 && AppSharedPreferences.isLoggedIn(this)){
            Objects.requireNonNull(tab_home.getTabAt(0)).select();
        }
        //jika flag exit sudah menyala, maka keluar aplikasi
        else if(exit){
            super.onBackPressed();
        }
        //jika flag exit belum menyala, maka nyalakan flag exit selama 2 detik
        else{
            exit = true;
            Toast.makeText(HomeActivity.this, "Klik sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
            Handler handle = new Handler();
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        fab_home.closeMenu();
    }
}