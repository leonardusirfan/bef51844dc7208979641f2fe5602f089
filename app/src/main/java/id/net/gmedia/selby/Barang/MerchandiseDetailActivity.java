package id.net.gmedia.selby.Barang;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.ImageContainer;
import id.net.gmedia.selby.Util.ImageSliderAdapter;
import id.net.gmedia.selby.Util.ImageSliderViewPager;
import me.relex.circleindicator.CircleIndicator;

public class MerchandiseDetailActivity extends AppCompatActivity {

    private String id = "";
    private String nama_barang = "";

    private TextView txt_title, txt_nama, txt_harga, txt_berat, txt_merk, txt_kategori, txt_deskripsi;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageSliderViewPager slider;
    private CircleIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_merchandise);

        //Inisialisasi UI
        txt_title = findViewById(R.id.txt_title);
        collapsingToolbar = findViewById(R.id.main_collapsing);
        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        toolbar = findViewById(R.id.toolbar);
        slider = findViewById(R.id.slider);
        indicator = findViewById(R.id.indicator);
        txt_berat = findViewById(R.id.txt_berat);
        txt_merk = findViewById(R.id.txt_merk);
        txt_kategori = findViewById(R.id.txt_kategori);
        txt_deskripsi = findViewById(R.id.txt_deskripsi);

        //Inisialisasi Toolbar
        initToolbar();

        //Inisialisasi barang
        if (getIntent().hasExtra("merchandise")) {
            initBarang();
        }

        //membeli barang
        findViewById(R.id.btn_pesan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pesan barang
            }
        });
    }

    private void initBarang(){
        //Membaca detail barang dari Web Service
        try{
            JSONObject body = new JSONObject();
            id = getIntent().getStringExtra("merchandise");
            body.put("id", id);

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DETAIL_PRODUK, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        System.out.println(result);
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            JSONObject barang = jsonresult.getJSONObject("response");

                            nama_barang = barang.getString("nama");
                            txt_nama.setText(nama_barang);
                            txt_harga.setText(Converter.doubleToRupiah(barang.getDouble("harga")));

                            txt_deskripsi.setText(barang.getString("deskripsi"));
                            String deskripsi = ": " + barang.getString("category");
                            txt_kategori.setText(deskripsi);
                            deskripsi = ": " + String.valueOf(barang.getInt("berat")) + " " + barang.getString("berat_satuan");
                            txt_berat.setText(deskripsi);
                            deskripsi = ": " + barang.getString("brand");
                            txt_merk.setText(deskripsi);

                            ArrayList<ImageContainer> listImage = new ArrayList<>();
                            listImage.add(new ImageContainer(barang.getString("image")));
                            JSONArray galeri = barang.getJSONArray("gallery");
                            for(int i = 0; i < galeri.length(); i++){
                                listImage.add(new ImageContainer(galeri.getJSONObject(i).getString("image")));
                            }

                            initSlider(listImage);
                        }
                        else{
                            Toast.makeText(MerchandiseDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(MerchandiseDetailActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                        Log.e("Barang Detail", e.toString());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(MerchandiseDetailActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Barang Detail", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(MerchandiseDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Barang Detail", e.toString());
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

    private void initSlider(ArrayList<ImageContainer> listImage){
        //Inisialisasi Slider
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, slider, listImage, true);
        slider.setAdapter(sliderAdapter);

        indicator.setViewPager(slider);

        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(slider.getAdapter()!=null){
                    ((ImageSliderAdapter)slider.getAdapter()).setPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        sliderAdapter.startTimer();
    }

   /* private void initSlider(List<String> listImage){
        //Inisialisasi slider
        if(listImage.size() == 1){
            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView.image(listImage.get(0)).setScaleType(BaseSliderView.ScaleType.CenterInside);
            slider.addSlider(sliderView);
            slider.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float v) {
                }
            });
            slider.setCustomIndicator(indicator);
        }
        else{
            DefaultSliderView sliderView;
            for(int i = 0; i < listImage.size(); i++){
                sliderView = new DefaultSliderView(this);
                sliderView.image(listImage.get(i)).setScaleType(BaseSliderView.ScaleType.CenterInside);
                slider.addSlider(sliderView);
            }
            slider.movePrevPosition(false);
            slider.setDuration(3000);
            slider.setCustomIndicator(indicator);
        }
    }*/

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
