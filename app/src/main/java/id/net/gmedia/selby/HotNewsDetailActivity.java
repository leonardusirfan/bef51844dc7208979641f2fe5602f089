package id.net.gmedia.selby;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;

import id.net.gmedia.selby.Util.Constant;

public class HotNewsDetailActivity extends AppCompatActivity {

    private HotNewsModel news;
    private ImageView img_news;
    private TextView txt_judul, txt_tanggal, txt_teks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_news_detail);

        //Inisialisasi toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        //Inisialisasi UI
        img_news = findViewById(R.id.img_news);
        txt_judul = findViewById(R.id.txt_judul);
        txt_tanggal = findViewById(R.id.txt_tanggal);
        txt_teks = findViewById(R.id.txt_teks);

        //Inisialisasi berita
        if(getIntent().hasExtra(Constant.EXTRA_BERITA)){
            Gson gson = new Gson();
            news = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_BERITA), HotNewsModel.class);

            initNews();
        }
    }

    private void initNews(){
        Glide.with(this).load(news.getImage()).transition(DrawableTransitionOptions.withCrossFade()).into(img_news);
        txt_judul.setText(news.getJudul());
        txt_tanggal.setText(news.getTanggal());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txt_teks.setText(Html.fromHtml(news.getTeks(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            txt_teks.setText(Html.fromHtml(news.getTeks()));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
