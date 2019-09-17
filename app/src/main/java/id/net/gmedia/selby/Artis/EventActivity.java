package id.net.gmedia.selby.Artis;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import id.net.gmedia.selby.Artis.Fragment.FragmentGaleri;
import id.net.gmedia.selby.Artis.Fragment.FragmentKegiatan;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class EventActivity extends AppCompatActivity {
    /*
        Activity untuk menampilkan kegiatan dan foto - foto dari artis terkait
     */

    //Variabel penampung id artis
    private String id = "";

    //Variabel UI
    public AppBarLayout appbar;
    private ImageView img_galeri_selected;
    private ConstraintLayout layout_overlay;
    private LinearLayout layout_galeri_selected;
    private ZoomLayout layout_zoom;

    //Variabel UI galeri (animasi, foto tampil)
    private Animation anim_popin, anim_popout;
    private int selectedImage = 0;
    private int imgHeight = 0;
    private int imgWidth = 0;

    //Variabel fragment galeri & Kegiatan
    private FragmentGaleri fragmentGaleri;
    private FragmentKegiatan fragmentKegiatan;

    //flag apakah galeri sedang menampilkan foto detail secara popup atau tidak
    private boolean detail = false;

    private List<String> listImage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //Inisialisasi Toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi Artis
        if(getIntent().hasExtra(Constant.EXTRA_ARTIS)){
            Gson gson = new Gson();
            ArtisModel artis = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_ARTIS), ArtisModel.class);
            id = artis.getId();
            getSupportActionBar().setTitle(artis.getNama());
        }

        //Inisialisasi fragment galeri & kegiatan
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        fragmentGaleri = new FragmentGaleri();
        fragmentGaleri.setArguments(bundle);
        fragmentKegiatan = new FragmentKegiatan();
        fragmentKegiatan.setArguments(bundle);

        //Inisialisasi UI
        TabLayout tablayout = findViewById(R.id.tab_event);
        appbar = findViewById(R.id.appbar);
        layout_overlay = findViewById(R.id.layout_overlay);
        Button btn_next, btn_previous;
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        layout_galeri_selected = findViewById(R.id.layout_galeri_selected);
        img_galeri_selected = findViewById(R.id.img_galeri_selected);
        layout_zoom = findViewById(R.id.layout_zoom);

        //Inisialisasi popup detail foto galeri
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imgWidth = displayMetrics.widthPixels - displayMetrics.widthPixels/7;
        imgHeight = displayMetrics.heightPixels - displayMetrics.heightPixels/5;

        //Inisialisasi animasi popup
        anim_popin = AnimationUtils.loadAnimation(EventActivity.this, R.anim.anim_pop_in);
        anim_popout = AnimationUtils.loadAnimation(EventActivity.this, R.anim.anim_pop_out);
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
                //img_galeri_selected.startAnimation(anim_popout);
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

                Glide.with(EventActivity.this).load(listImage.get(selectedImage)).
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

                Glide.with(EventActivity.this).load(listImage.get(selectedImage)).
                        apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
            }
        });

        //Inisialisasi tab layout untuk navigasi antara fragment galeri dan kegiatan
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    loadFragment(fragmentKegiatan);
                }
                else{
                    loadFragment(fragmentGaleri);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(getIntent().hasExtra("page")){
            Objects.requireNonNull(tablayout.getTabAt(1)).select();
        }
        else{
            loadFragment(fragmentKegiatan);
        }
    }

    public void setView(List<String> listImage, int position){
        //Fungsi untuk menampilkan foto secara popup
        selectedImage = position;
        this.listImage = listImage;

        /*Glide.with(this).load(listImage.get(selectedImage)).
                apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);*/
        Glide.with(this).load(listImage.get(selectedImage)).into(img_galeri_selected);
        layout_zoom.zoomTo(1, false);
        layout_overlay.setVisibility(View.VISIBLE);
        detail = true;

        layout_galeri_selected.startAnimation(anim_popin);
        //img_galeri_selected.startAnimation(anim_popin);
    }


    private void loadFragment(Fragment fragment){
        //fungsi untuk mengubah fragment
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.frame_event, fragment);
        trans.commit();

        appbar.setExpanded(true);
    }

    @Override
    public void onBackPressed() {
        if(!detail){
            //Jika tidak sedang menampilkan popup foto galeri, maka mundur ke activity sebelumnya
            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        else{
            //jika sedang tampil popup foto galeri, maka tutup popup terlebih dahulu
            layout_galeri_selected.startAnimation(anim_popout);
            //img_galeri_selected.startAnimation(anim_popout);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
