package id.net.gmedia.selby.Artis;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Barang.BarangActivity;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;

public class ArtisDetailActivity extends AppCompatActivity {
    /*
        Activity yang menampilkan informasi artis
        Dari activity ini bisa melihat barang jualan, kegiatan dan foto artis
    */

    //flag apakah info detail artis sedang tampil sebagai popup atau tidak
    private boolean detail = false;

    //variabel yang menampung informasi artis yang sedang ditampilkan
    private ArtisModel artis;

    //Variabel UI
    private Button btn_detail;
    private CardView layout_button;
    private FrameLayout layout_detail_artis;
    private TextView txt_detail, txt_artis;
    private ImageView img_artis;

    //Variabel gesture UI
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artis_detail);

        //Inisialisasi UI
        img_artis = findViewById(R.id.img_artis);
        Button btn_lihat_event = findViewById(R.id.btn_lihat_event);
        Button btn_lihat_produk = findViewById(R.id.btn_lihat_produk);
        layout_detail_artis = findViewById(R.id.layout_detail_artis);
        layout_button = findViewById(R.id.layout_button);
        txt_artis = findViewById(R.id.txt_artis);
        txt_detail = findViewById(R.id.txt_detail);
        btn_detail = findViewById(R.id.btn_detail);

        //Animasi + Transisi dari activity sebelumnya
        Transition enterTransition = getWindow().getSharedElementEnterTransition();
        enterTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                layout_detail_artis.setVisibility(View.VISIBLE);
                layout_button.setVisibility(View.VISIBLE);

                final Animation enterAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.anim_move_inscreen_up);

                layout_button.startAnimation(enterAnimation);
                layout_detail_artis.startAnimation(enterAnimation);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {}

            @Override
            public void onTransitionPause(Transition transition) {}

            @Override
            public void onTransitionResume(Transition transition) {}
        });

        //Inisialisasi Informasi Artis
        initArtis();

        //Apabila button detail diklik akan memunculkan transisi layout menuju detail artis
        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout constraintLayout = findViewById(R.id.layout_root);
                ConstraintSet constraintSet = new ConstraintSet();

                Animation rotate;
                if(!detail){
                    constraintSet.clone(ArtisDetailActivity.this, R.layout.activity_artis_detail_info);
                    rotate = AnimationUtils.loadAnimation(ArtisDetailActivity.this, R.anim.anim_rotate);
                }
                else{
                    constraintSet.clone(ArtisDetailActivity.this, R.layout.activity_artis_detail);
                    rotate = AnimationUtils.loadAnimation(ArtisDetailActivity.this, R.anim.anim_rotate_reverse);
                }

                detail = !detail;
                TransitionManager.beginDelayedTransition(constraintLayout);
                btn_detail.startAnimation(rotate);
                constraintSet.applyTo(constraintLayout);
            }
        });

        //gesture agar detail artis bisa dibuka dengan fling
        mDetector = new GestureDetector(this, new MyGestureListener());
        layout_detail_artis.setOnTouchListener(touchListener);

        //Menuju Activity Barang
        btn_lihat_produk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(ArtisDetailActivity.this, BarangActivity.class);
                i.putExtra("artis", gson.toJson(artis));
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //Menuju Activity Event
        btn_lihat_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(ArtisDetailActivity.this, EventActivity.class);
                i.putExtra("artis", gson.toJson(artis));
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void initArtis(){
        if(getIntent().hasExtra("artis")){
            Gson gson = new Gson();
            artis = gson.fromJson(getIntent().getStringExtra("artis"), ArtisModel.class);

            //Inisialisasi UI terkait artis
            Glide.with(this).load(artis.getImage()).thumbnail(0.3f).apply(new RequestOptions().dontTransform().dontAnimate().priority(Priority.IMMEDIATE).override(500, 300)).transition(DrawableTransitionOptions.withCrossFade()).into(img_artis);
            txt_artis.setText(artis.getNama());
            txt_detail.setText(artis.getDeskripsi());

            try{
                JSONObject body = new JSONObject();
                body.put("id", artis.getId());
                body.put("start", 0);
                body.put("count", 0);

                ApiVolleyManager.getInstance().addRequest(ArtisDetailActivity.this, Constant.URL_ARTIS, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body, new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            System.out.println("ARTIS : " + result);
                            JSONObject jsonresult = new JSONObject(result);
                            int status = jsonresult.getJSONObject("metadata").getInt("status");
                            String message = jsonresult.getJSONObject("metadata").getString("message");

                            if(status == 200){
                                txt_detail.setText(jsonresult.getJSONObject("response").getJSONArray("pelapak").getJSONObject(0).getString("deskripsi"));
                            }
                            else{
                                Toast.makeText(ArtisDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Log.e("Artis", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Log.e("Artis", result);
                    }
                });
            }
            catch (JSONException e){
                Log.e("Artis", e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Jika ditekan back ketika memunculkan detail, akan menutup detail terlebih dulu
        if(!detail){
            super.onBackPressed();
        }
        else{
            //Animasi/transisi dari detail artis ke layout normal
            ConstraintLayout constraintLayout = findViewById(R.id.layout_root);
            ConstraintSet constraintSet = new ConstraintSet();

            constraintSet.clone(ArtisDetailActivity.this, R.layout.activity_artis_detail);

            detail = !detail;
            TransitionManager.beginDelayedTransition(constraintLayout);
            btn_detail.startAnimation(AnimationUtils.loadAnimation(ArtisDetailActivity.this, R.anim.anim_rotate_reverse));
            constraintSet.applyTo(constraintLayout);
        }
    }

    //Inisialisasi Gesture
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event
            v.performClick();
            return mDetector.onTouchEvent(event);
        }
    };
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d("TAG", "onFling: ");
            if(!detail){
                if(event1.getY() - event2.getY() > 200){
                    ConstraintLayout constraintLayout = findViewById(R.id.layout_root);
                    ConstraintSet constraintSet = new ConstraintSet();

                    Animation rotate;
                    constraintSet.clone(ArtisDetailActivity.this, R.layout.activity_artis_detail_info);
                    rotate = AnimationUtils.loadAnimation(ArtisDetailActivity.this, R.anim.anim_rotate);

                    detail = !detail;
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    btn_detail.startAnimation(rotate);
                    constraintSet.applyTo(constraintLayout);

                }
            }
            else{
                if(event1.getY() - event2.getY() < 200){
                    ConstraintLayout constraintLayout = findViewById(R.id.layout_root);
                    ConstraintSet constraintSet = new ConstraintSet();

                    Animation rotate;
                    constraintSet.clone(ArtisDetailActivity.this, R.layout.activity_artis_detail);
                    rotate = AnimationUtils.loadAnimation(ArtisDetailActivity.this, R.anim.anim_rotate_reverse);

                    detail = !detail;
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    btn_detail.startAnimation(rotate);
                    constraintSet.applyTo(constraintLayout);
                }
            }

            return true;
        }
    }
}