package id.net.gmedia.selby.Barang;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Model.UlasanModel;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.DialogFactory;

public class FragmentUlasan extends Fragment {

    //Variabel penampung activity
    private Activity activity;

    //Variabel atribut barang
    private String id = "";
    private float rating = 0;

    //Variabel dialog untuk menambah dan membalas ulasan
    private Dialog dialogReview;
    private Dialog dialogBalas;

    //Variabel UI
    private TextView txt_jumlah_ulasan, txt_rating_barang;

    //Variabel penampil rating dan ulasan
    private List<UlasanModel> listUlasan;
    private List<Integer> listRating;
    private RatingAdapter ratingAdapter;
    private UlasanAdapter ulasanAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View layout = inflater.inflate(R.layout.fragment_ulasan, container, false);

        //Inisialisasi barang
        if(getArguments() != null) {
            id = getArguments().getString("id");
            rating = getArguments().getFloat("rating");
        }

        //Inisialisasi UI
        txt_jumlah_ulasan = layout.findViewById(R.id.txt_jumlah_ulasan);
        txt_rating_barang = layout.findViewById(R.id.txt_rating_barang);
        AppCompatRatingBar rate_produk = layout.findViewById(R.id.rate_produk);
        txt_rating_barang.setText(String.valueOf(rating));

        rate_produk.setRating(rating);

        //Inisialisasi Recycler View Ulasan
        listUlasan = new ArrayList<>();
        RecyclerView rv_ulasan = layout.findViewById(R.id.rv_ulasan);
        ulasanAdapter = new UlasanAdapter(this, listUlasan);
        rv_ulasan.setItemAnimator(new DefaultItemAnimator());
        rv_ulasan.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_ulasan.setAdapter(ulasanAdapter);

        //Inisialisasi Recycler View Rating
        listRating  = new ArrayList<>(Arrays.asList(0,0,0,0,0));
        RecyclerView rv_rating = layout.findViewById(R.id.rv_rating);
        ratingAdapter = new RatingAdapter(listRating);
        rv_rating.setItemAnimator(new DefaultItemAnimator());
        rv_rating.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_rating.setAdapter(ratingAdapter);

        loadUlasan();

        return layout;
    }

    public void loadUlasan(){
        //final int LOAD_COUNT = 2;
        try{
            JSONObject body = new JSONObject();
            body.put("id_barang", id);
            body.put("start", 0);
            body.put("count", 0);

            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_DETAIL_PRODUK_REVIEW, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200 || status == 404){
                            listUlasan.clear();
                            for(int i = 0; i < listRating.size(); i++){
                                listRating.set(i, 0);
                            }

                            if(jsonresult.get("response") instanceof JSONObject){
                                String jumlah_ulasan = jsonresult.getJSONObject("response").getString("total_records") + " Ulasan";
                                txt_jumlah_ulasan.setText(jumlah_ulasan);

                                JSONArray ulasanlist = jsonresult.getJSONObject("response").getJSONArray("review");

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

                                    listRating.set(Math.round(rating) - 1, listRating.get(Math.round(rating) - 1) + 1);
                                    listUlasan.add(ulasan);
                                }
                                //lastItem += ulasanlist.length();

                                ratingAdapter.notifyDataSetChanged();
                                ratingAdapter.calculateSum();
                                ulasanAdapter.notifyDataSetChanged();
                            }
                        }
                        else{
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Ulasan", e.toString());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Ulasan", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Ulasan", e.toString());
        }
    }

    public void bukaDialogReview(){
        //Buka dialog
        dialogReview = DialogFactory.getInstance().createDialog(activity, R.layout.popup_review, 90, 60);

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
                    builder.setTitle("Anda tidak memberi rating");
                    builder.setMessage("Yakin ingin memberi rating kosong?");
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
        if(id_user == null){
            Toast.makeText(activity, "Login terlebih dahulu untuk mengulas barang", Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            JSONObject body = new JSONObject();
            body.put("id", "");
            body.put("id_barang", id);
            body.put("rating", rating);
            body.put("deskripsi", teks);

            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_ULASAN, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(id_user), body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            Toast.makeText(activity, "Ulasan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            txt_rating_barang.setText(String.valueOf(jsonresult.getJSONObject("response").getDouble("rating")));
                            loadUlasan();
                            dialogReview.dismiss();
                        }
                        else{
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Review", e.toString());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Review", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Review", e.toString());
        }
    }

    public void balasUlasan(final String id_ulasan){
        //Buka dialog balas ulasan
        dialogBalas = DialogFactory.getInstance().createDialog(activity, R.layout.popup_review_balas, 90, 60);
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
                    if(id_user == null){
                        Toast.makeText(activity, "Login terlebih dahulu untuk mengulas barang", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try{
                        JSONObject body = new JSONObject();
                        body.put("id", id_ulasan);
                        body.put("id_barang", id);
                        body.put("rating", 0);
                        body.put("deskripsi", dialog_txt_ulasan.getText().toString());

                        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_ULASAN, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(id_user), body, new ApiVolleyManager.RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try{
                                    JSONObject jsonresult = new JSONObject(result);
                                    int status = jsonresult.getJSONObject("metadata").getInt("status");
                                    String message = jsonresult.getJSONObject("metadata").getString("message");

                                    if(status == 200){
                                        Toast.makeText(activity, "Ulasan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                        loadUlasan();
                                        dialogBalas.dismiss();
                                    }
                                    else{
                                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (JSONException e){
                                    Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                                    Log.e("Review", e.toString());
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                                Log.e("Review", result);
                            }
                        });
                    }
                    catch (JSONException e){
                        Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Review", e.toString());
                    }
                }
            }
        });

        dialogBalas.show();
    }
}
