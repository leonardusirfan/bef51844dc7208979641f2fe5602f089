package id.net.gmedia.selby.Barang.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.net.gmedia.selby.Barang.Adapter.RatingAdapter;
import id.net.gmedia.selby.Barang.Adapter.UlasanAdapter;
import id.net.gmedia.selby.LoginActivity;
import id.net.gmedia.selby.Util.AppRequestCallback;
import id.net.gmedia.selby.Util.AppSharedPreferences;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Model.UlasanModel;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.DialogFactory;
import id.net.gmedia.selby.Util.JSONBuilder;

public class FragmentUlasan extends Fragment {

    private final int LOAD_COUNT = 2;
    private int last_loaded = 0;
    private String bintang = "";
    private int total = 0;

    //Variabel penampung activity
    private Activity activity;

    //Variabel atribut barang
    private String id = "";

    //Variabel dialog untuk menambah dan membalas ulasan
    private Dialog dialogReview;
    private Dialog dialogBalas;

    //Variabel UI
    private TextView txt_jumlah_ulasan, txt_rating_barang;
    private AppCompatRatingBar rate_produk;

    //Variabel penampil rating dan ulasan
    private List<UlasanModel> listUlasan;
    private List<Integer> listRating;
    private RatingAdapter ratingAdapter;
    private UlasanAdapter ulasanAdapter;
    private RecyclerView rv_ulasan;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View layout = inflater.inflate(R.layout.fragment_barang_ulasan, container, false);

        //Inisialisasi barang
        if(getArguments() != null) {
            id = getArguments().getString("id");
        }

        //Inisialisasi UI
        Button btn_semua, btn_bintang1, btn_bintang2, btn_bintang3, btn_bintang4, btn_bintang5;
        btn_semua = layout.findViewById(R.id.btn_semua);
        btn_bintang1 = layout.findViewById(R.id.btn_bintang1);
        btn_bintang2 = layout.findViewById(R.id.btn_bintang2);
        btn_bintang3 = layout.findViewById(R.id.btn_bintang3);
        btn_bintang4 = layout.findViewById(R.id.btn_bintang4);
        btn_bintang5 = layout.findViewById(R.id.btn_bintang5);

        btn_semua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bintang = "";
                initUlasan(LOAD_COUNT);
            }
        });
        btn_bintang1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bintang = "1";
                initUlasan(LOAD_COUNT);
            }
        });
        btn_bintang2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bintang = "2";
                initUlasan(LOAD_COUNT);
            }
        });
        btn_bintang3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bintang = "3";
                initUlasan(LOAD_COUNT);
            }
        });
        btn_bintang4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bintang = "4";
                initUlasan(LOAD_COUNT);
            }
        });
        btn_bintang5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bintang = "5";
                initUlasan(LOAD_COUNT);
            }
        });

        txt_jumlah_ulasan = layout.findViewById(R.id.txt_jumlah_ulasan);
        txt_rating_barang = layout.findViewById(R.id.txt_rating_barang);
        rate_produk = layout.findViewById(R.id.rate_produk);
        txt_jumlah_ulasan.setText(getResources().getString(R.string.ulasan_jumlah_ulasan, 0));

        //Inisialisasi Recycler View Ulasan
        listUlasan = new ArrayList<>();
        rv_ulasan = layout.findViewById(R.id.rv_ulasan);
        rv_ulasan.setItemAnimator(new DefaultItemAnimator());
        rv_ulasan.setLayoutManager(new LinearLayoutManager(getContext()));

        //Inisialisasi Recycler View Rating
        listRating  = new ArrayList<>(Arrays.asList(0,0,0,0,0));
        RecyclerView rv_rating = layout.findViewById(R.id.rv_rating);
        rv_rating.setItemAnimator(new DefaultItemAnimator());
        rv_rating.setLayoutManager(new LinearLayoutManager(getContext()));
        ratingAdapter = new RatingAdapter(listRating);
        rv_rating.setAdapter(ratingAdapter);

        initRating();
        initUlasan(LOAD_COUNT);

        return layout;
    }

    private void initRating(){
        JSONBuilder body = new JSONBuilder();
        body.add("id_barang", id);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_BARANG_RATING, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    int jumlah = 0;
                    for(int i = 0; i < listRating.size(); i++){
                        listRating.set(i, 0);
                    }

                    JSONObject rating = new JSONObject(response);

                    listRating.set(0, rating.getInt("rating_1"));
                    listRating.set(1, rating.getInt("rating_2"));
                    listRating.set(2, rating.getInt("rating_3"));
                    listRating.set(3, rating.getInt("rating_4"));
                    listRating.set(4, rating.getInt("rating_5"));

                    for(int i = 0; i < listRating.size(); i++){
                        jumlah += listRating.get(i);
                    }

                    float rating_float = (float)rating.getDouble("rating");
                    rate_produk.setRating(rating_float);
                    txt_rating_barang.setText(String.valueOf(rating_float));

                    ratingAdapter.setSum((float) jumlah);
                    ratingAdapter.notifyDataSetChanged();

                    txt_jumlah_ulasan.setText(getResources().getString(R.string.ulasan_jumlah_ulasan, jumlah));
                }
                catch (JSONException e){
                    Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Ulasan", e.toString());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void initUlasan(int count){
        total = 0;
        last_loaded = 0;

        JSONBuilder body = new JSONBuilder();
        body.add("id_barang", id);
        body.add("start", 0);
        body.add("count", count);
        body.add("rating", bintang);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_DETAIL_PRODUK_REVIEW, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    listUlasan.clear();

                    Object json = new JSONTokener(response).nextValue();
                    if(json instanceof JSONObject){
                        JSONObject jsonresult = (JSONObject) json;
                        total = jsonresult.getInt("total_records");
                        JSONArray ulasanlist = jsonresult.getJSONArray("review");

                        for(int i = 0; i < ulasanlist.length(); i++){
                            JSONObject obj = ulasanlist.getJSONObject(i);
                            float rating = (float) obj.getDouble("rating");
                            UlasanModel ulasan = new UlasanModel(obj.getString("id"), obj.getString("foto"), obj.getString("profile_name"), obj.getString("deskripsi"), rating, Converter.stringDTTToDate(obj.getString("waktu")));

                            if(obj.has("child")){
                                JSONArray balasan = obj.getJSONArray("child");
                                for(int j = 0; j < balasan.length(); j++){
                                    ulasan.addBalasan(new UlasanModel(balasan.getJSONObject(j).getString("foto"),
                                            balasan.getJSONObject(j).getString("profile_name"), balasan.getJSONObject(j).getString("deskripsi"), Converter.stringDTTToDate(balasan.getJSONObject(j).getString("waktu"))));
                                }
                            }

                            listUlasan.add(ulasan);
                            last_loaded += 1;
                        }
                    }

                    ulasanAdapter = new UlasanAdapter(FragmentUlasan.this, listUlasan);
                    if(total == last_loaded){
                        ulasanAdapter.setAll_loaded();
                    }
                    rv_ulasan.setAdapter(ulasanAdapter);
                }
                catch (JSONException e){
                    Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Ulasan", e.toString());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void loadUlasan(){
        JSONBuilder body = new JSONBuilder();
        body.add("id_barang", id);
        body.add("start", last_loaded);
        body.add("count", LOAD_COUNT);
        body.add("rating", bintang);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_DETAIL_PRODUK_REVIEW, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    Object json = new JSONTokener(response).nextValue();
                    if(json instanceof JSONObject) {
                        JSONObject jsonresult = (JSONObject) json;
                        total = jsonresult.getInt("total_records");
                        JSONArray ulasanlist = jsonresult.getJSONArray("review");

                        for(int i = 0; i < ulasanlist.length(); i++){
                            JSONObject obj = ulasanlist.getJSONObject(i);
                            float rating = (float) obj.getDouble("rating");
                            UlasanModel ulasan = new UlasanModel(obj.getString("id"), obj.getString("foto"), obj.getString("profile_name"), obj.getString("deskripsi"), rating, Converter.stringDTTToDate(obj.getString("waktu")));

                            if(obj.has("child")){
                                JSONArray balasan = obj.getJSONArray("child");
                                for(int j = 0; j < balasan.length(); j++){
                                    ulasan.addBalasan(new UlasanModel(balasan.getJSONObject(j).getString("foto"),
                                            balasan.getJSONObject(j).getString("profile_name"), balasan.getJSONObject(j).getString("deskripsi"), Converter.stringDTTToDate(balasan.getJSONObject(j).getString("waktu"))));
                                }
                            }

                            listUlasan.add(ulasan);
                            last_loaded += 1;
                        }
                    }

                    if(total == last_loaded){
                        ulasanAdapter.setAll_loaded();
                    }
                    ulasanAdapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Ulasan", e.toString());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void bukaDialogReview(){
        //Buka dialog
        dialogReview = DialogFactory.getInstance().createDialog(activity, R.layout.popup_barang_review, 70, 55);

        final AppCompatRatingBar dialog_rating_barang = dialogReview.findViewById(R.id.rating_barang);
        final EditText dialog_txt_ulasan = dialogReview.findViewById(R.id.txt_ulasan);
        Button btn_kirim = dialogReview.findViewById(R.id.btn_kirim);

        btn_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog_txt_ulasan.getText().toString().equals("")){
                    Toast.makeText(activity, "Isi ulasan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(dialog_rating_barang.getRating() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Anda belum memberi rating");
                    builder.setMessage("Berikan rating untuk barang yang sudah anda beli");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            kirimUlasan(dialog_rating_barang.getRating(), dialog_txt_ulasan.getText().toString());
                        }
                    });
                    builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }
                else{
                    kirimUlasan(dialog_rating_barang.getRating(), dialog_txt_ulasan.getText().toString());
                }
            }
        });

        dialogReview.show();
    }

    private void kirimUlasan(final float rating, String teks){
        String id_user = FirebaseAuth.getInstance().getUid();
        JSONBuilder body = new JSONBuilder();
        body.add("id", "");
        body.add("id_barang", id);
        body.add("rating", rating);
        body.add("deskripsi", teks);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_ULASAN, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(id_user), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(activity, "Ulasan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                initRating();
                initUlasan(last_loaded);
                dialogReview.dismiss();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void balasUlasan(final String id_ulasan){
        if(!AppSharedPreferences.isLoggedIn(activity)){
            activity.startActivity(new Intent(activity, LoginActivity.class));
        }
        else{
            //Buka dialog balas ulasan
            dialogBalas = DialogFactory.getInstance().createDialog(activity, R.layout.popup_barang_balas, 70, 45);
            final EditText dialog_txt_ulasan = dialogBalas.findViewById(R.id.txt_ulasan);
            Button btn_kirim = dialogBalas.findViewById(R.id.btn_kirim);

            //Mengirim request untuk membalas ulasan ke Web Service
            btn_kirim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialog_txt_ulasan.getText().toString().equals("")){
                        Toast.makeText(activity, "Isi ulasan terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String id_user = FirebaseAuth.getInstance().getUid();
                        JSONBuilder body = new JSONBuilder();
                        body.add("id", id_ulasan);
                        body.add("id_barang", id);
                        body.add("rating", 0);
                        body.add("deskripsi", dialog_txt_ulasan.getText().toString());

                        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_ULASAN, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(id_user), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                            @Override
                            public void onSuccess(String response) {
                                Toast.makeText(activity, "Ulasan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                initUlasan(last_loaded);
                                dialogBalas.dismiss();
                            }

                            @Override
                            public void onFail(String message) {
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            }
                        }));
                    }
                }
            });

            dialogBalas.show();
        }
    }
}
