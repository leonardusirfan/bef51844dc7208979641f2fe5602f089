package id.net.gmedia.selby.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Pix;
import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Akun.FragmentAkun;
import id.net.gmedia.selby.Favorit.FragmentFavorit;
import id.net.gmedia.selby.Feed.FragmentFeed;
import id.net.gmedia.selby.Keranjang.FragmentKeranjang;
import id.net.gmedia.selby.LoginActivity;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.AppSharedPreferences;
import id.net.gmedia.selby.Util.Constant;

public class HomeActivity extends AppCompatActivity {

    //flag untuk double klik exit
    private boolean exit = false;
    //flag untuk tab/fragment yang aktif
    //private boolean fab_visible = false;

    //flag apakah user dalam kondisi sudah login atau belum
    private boolean login = false;

    //variabel fragment
    private FragmentHome fragmentHome;
    private FragmentFeed fragmentFeed;
    private FragmentAkun fragmentAkun;

    //Variabel UI
    private BottomNavigationView bottombar;
    private ImageView img_galeri_selected;
    private ConstraintLayout layout_overlay;
    private CardView layout_galeri_selected;
    private ZoomLayout layout_zoom;
    /*private FloatingActionButton fab_post;
    private SubActionButton button1, button2, button3;*/

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
        layout_overlay = findViewById(R.id.layout_overlay);
        Button btn_next, btn_previous;
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        layout_galeri_selected = findViewById(R.id.layout_galeri_selected);
        img_galeri_selected = findViewById(R.id.img_galeri_selected);
        layout_zoom = findViewById(R.id.layout_zoom);

        int start_page = getIntent().getIntExtra(Constant.EXTRA_START, 0);

        //Inisialisasi Floating Action Button
        /*if(login && AppSharedPreferences.isPenjual(this)){
            initFloatingAction();
            fab_post.setVisibility(View.INVISIBLE);
        }*/

        //Inisialisasi toolbar
        setSupportActionBar(toolbar);

        //Inisialisasi Fragment
        fragmentHome = new FragmentHome();
        if(login){
            //fragment feed dan akun hanya muncul jika sudah login
            fragmentFeed = new FragmentFeed();
            fragmentAkun = new FragmentAkun();
        }

        //Inisialisasi Bottom Navigation View
        bottombar = findViewById(R.id.act_home_bottombar);
        if(login){
            bottombar.inflateMenu(R.menu.menu_home);
        }
        else{
            bottombar.inflateMenu(R.menu.menu_home_not_login);
        }
        //Ketika menu bottombar dipilih, switch fragment
        bottombar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        bottombar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        loadFragment(fragmentHome);
                        break;
                    case R.id.action_feed:
                        bottombar.setBackgroundColor(getResources().getColor(R.color.white));
                        loadFragment(fragmentFeed);
                        /*if(AppSharedPreferences.isPenjual(HomeActivity.this)){
                            fab_post.setVisibility(View.VISIBLE);
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                            button3.setVisibility(View.VISIBLE);
                            fab_visible = true;
                        }*/
                        break;
                    case R.id.action_favorit:
                        bottombar.setBackgroundColor(getResources().getColor(R.color.white));
                        loadFragment(new FragmentFavorit());
                        break;
                    case R.id.action_keranjang:
                        bottombar.setBackgroundColor(getResources().getColor(R.color.white));
                        loadFragment(new FragmentKeranjang());
                        break;
                    case R.id.action_akun:
                        if(login){
                            //Jika sudah login, masuk ke menu akun
                            bottombar.setBackgroundColor(getResources().getColor(R.color.white));
                            loadFragment(fragmentAkun);
                        }
                        else{
                            //Jika belum login munculkan activity login
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        }
                        break;
                }
                return true;
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

                Glide.with(HomeActivity.this).load(listImage.get(selectedImage)).
                        apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
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

                Glide.with(HomeActivity.this).load(listImage.get(selectedImage)).
                        apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
            }
        });

        switch (start_page){
            case 1:
                bottombar.setSelectedItemId(R.id.action_feed);
                break;
            case 3:
                bottombar.setSelectedItemId(R.id.action_keranjang);
                break;
            case 4:
                bottombar.setSelectedItemId(R.id.action_akun);
                break;
            default:bottombar.setSelectedItemId(R.id.action_home);
        }
    }

    private void loadFragment(Fragment fragment){
       /* if(fab_visible){
            fab_post.setVisibility(View.INVISIBLE);
            button1.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
        }*/

        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.frame_home, fragment);
        trans.commit();
    }

    //Options Menu
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (bottombar.getSelectedItemId()){
            case R.id.action_home:getMenuInflater().inflate(R.menu.menu_artis, menu);break;
            case R.id.action_feed:getMenuInflater().inflate(R.menu.menu_feed, menu);break;
            case R.id.action_favorit:getMenuInflater().inflate(R.menu.menu_feed, menu);break;
            case R.id.action_keranjang:getMenuInflater().inflate(R.menu.menu_feed, menu);break;
            case R.id.action_akun:getMenuInflater().inflate(R.menu.menu_akun, menu);break;
        }

        return true;
    }*/

    public void setView(List<String> listImage, int position){
        //Fungsi untuk menampilkan foto secara popup
        this.listImage = listImage;

        selectedImage = position;
        Glide.with(this).load(listImage.get(selectedImage)).apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
        layout_zoom.zoomTo(1, false);
        layout_overlay.setVisibility(View.VISIBLE);
        detail = true;

        layout_galeri_selected.startAnimation(anim_popin);
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
    public void onBackPressed() {
        //Jika ditekan back saat bukan di home dan saat belum login, maka kembali ke home
        if(detail){
            layout_galeri_selected.startAnimation(anim_popout);
        }
        else if(bottombar.getSelectedItemId() != R.id.action_home && AppSharedPreferences.isLoggedIn(this)){
            bottombar.setSelectedItemId(R.id.action_home);
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

    /*private void initFloatingAction(){
        //Inisialisasi view tombol floating action button
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.plus);

        //Hitung ukuran bottom bar
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        //Atur posisi dan ukuran floating action button
        int margin = getResources().getDimensionPixelSize(com.oguzdev.circularfloatingactionmenu.library.R.dimen.action_button_margin);
        FloatingActionButton.LayoutParams position = new FloatingActionButton.LayoutParams(getResources().getDimensionPixelSize(R.dimen.floating_size), getResources().getDimensionPixelSize(R.dimen.floating_size), 85);
        position.setMargins(margin,margin,margin, actionBarHeight + margin);

        //Buat floating action button
        fab_post = new FloatingActionButton.Builder(this)
                .setContentView(imageView, new FloatingActionButton.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .setLayoutParams(position)
                .build();

        //Buat menu floating action button
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageResource(R.drawable.kegiatan);
        button1 = itemBuilder.setContentView(itemIcon, new FloatingActionButton.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)).build();
        itemBuilder = new SubActionButton.Builder(this);
        itemIcon = new ImageView(this);
        itemIcon.setImageResource(R.drawable.barang);
        button2 = itemBuilder.setContentView(itemIcon, new FloatingActionButton.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)).build();
        itemBuilder = new SubActionButton.Builder(this);
        itemIcon = new ImageView(this);
        itemIcon.setImageResource(R.drawable.foto);
        button3 = itemBuilder.setContentView(itemIcon, new FloatingActionButton.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)).build();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, UploadKegiatanActivity.class));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, UploadBarangActivity.class));
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, UploadGambarActivity.class));
            }
        });

        //Buat floating action dengan menu
        new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .attachTo(fab_post)
                .build();
    }*/

    @Override
    protected void onResume() {
        if(bottombar.getSelectedItemId() == R.id.action_favorit){
            loadFragment(new FragmentFavorit());
        }
        else if(bottombar.getSelectedItemId() == R.id.action_keranjang){
            loadFragment(new FragmentKeranjang());
        }

        //update FCM id
        JSONBuilder body = new JSONBuilder();
        body.add("fcm_id", AppSharedPreferences.getFcmId(this));

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_FCM_UPDATE, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {

                    }

                    @Override
                    public void onFail(String message) {

                    }
                }));

        super.onResume();
    }
}
