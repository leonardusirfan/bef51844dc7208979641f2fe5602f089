package id.net.gmedia.selby;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.TopCropCircularImageView;

import id.net.gmedia.selby.Home.ArtisActivity;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Util.Constant;

public class OrderGreetingActivity extends AppCompatActivity {
    private String id_artis = "";

    //Variabel UI
    private TopCropCircularImageView img_artis;
    private ImageView btn_search;
    private TextView txt_artis;
    private EditText txt_ucapan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_greeting);

        //Inisialisasi UI
        img_artis = findViewById(R.id.img_artis);
        btn_search = findViewById(R.id.btn_search);
        txt_artis = findViewById(R.id.txt_artis);
        txt_ucapan = findViewById(R.id.txt_ucapan);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_artis.equals("")){
                    Intent i = new Intent(OrderGreetingActivity.this, ArtisActivity.class);
                    startActivityForResult(i, 1);
                }
                else{
                    id_artis = "";

                    txt_artis.setText("");
                    txt_artis.setHint("Tentukan artis favoritmu");
                    img_artis.setVisibility(View.GONE);
                    btn_search.setImageResource(R.drawable.search);
                }
            }
        });

        findViewById(R.id.layout_artis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderGreetingActivity.this, ArtisActivity.class);
                startActivityForResult(i, 1);
            }
        });

        findViewById(R.id.btn_kirim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_artis.equals("")){
                    Toast.makeText(OrderGreetingActivity.this, "Pilih artis terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(txt_ucapan.getText().toString().equals("")){
                    Toast.makeText(OrderGreetingActivity.this, "Tulis ucapan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    //kirim order greeting
                    order();
                }
            }
        });
    }

    private void order(){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id_artis", id_artis);
        body.add("request", txt_ucapan.getText().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_GREETING_ADD, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(OrderGreetingActivity.this, "Order greeting artis berhasil", Toast.LENGTH_SHORT).show();

                        onBackPressed();
                    }

                    @Override
                    public void onFail(String message) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(OrderGreetingActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                if(data != null){
                    Gson gson = new Gson();
                    ArtisModel artis = gson.fromJson(data.getStringExtra(Constant.EXTRA_ARTIS), ArtisModel.class);

                    id_artis = artis.getId();
                    txt_artis.setText(artis.getNama());
                    Glide.with(this).load(artis.getImage()).
                            transition(DrawableTransitionOptions.withCrossFade()).into(img_artis);
                    img_artis.setVisibility(View.VISIBLE);
                    btn_search.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
