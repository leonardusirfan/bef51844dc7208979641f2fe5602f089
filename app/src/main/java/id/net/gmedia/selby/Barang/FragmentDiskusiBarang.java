package id.net.gmedia.selby.Barang;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Model.UlasanModel;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.Converter;


public class FragmentDiskusiBarang extends Fragment {

    //Variabel penampung activity
    private Activity activity;
    private boolean needLoad = true;

    //Variabel penampung id barang
    private String id = "";

    //Variabel dialog untuk tambah dan balas diskusi barang
    private Dialog dialogDiskusi;
    private Dialog dialogBalas;

    //Variabel penampil diskusi barang
    private DiskusiBarangAdapter adapter;
    private List<UlasanModel> listDiskusiBarang;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View layout = inflater.inflate(R.layout.fragment_diskusi_barang, container, false);

        //Menginisialisasi data diskusi barang
        if(getArguments() != null) {
            id = getArguments().getString("id");
        }

        //Inisialisasi Recycler View ulasan
        listDiskusiBarang = new ArrayList<>();
        RecyclerView rv_diskusi = layout.findViewById(R.id.rv_diskusi);
        adapter = new DiskusiBarangAdapter(this, listDiskusiBarang);
        rv_diskusi.setItemAnimator(new DefaultItemAnimator());
        rv_diskusi.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_diskusi.setAdapter(adapter);

        initDiskusiBarang();

        return layout;
    }

    public void tambahDiskusi(){
        //Buka dialog tambah diskusi
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int device_TotalWidth = metrics.widthPixels;
        int device_TotalHeight = metrics.heightPixels;

        dialogDiskusi = new Dialog(activity, R.style.PopupTheme);
        dialogDiskusi.setContentView(R.layout.popup_review_balas);
        if(dialogDiskusi.getWindow() != null){
            dialogDiskusi.getWindow().setLayout(device_TotalWidth * 90 / 100 , device_TotalHeight * 60 / 100); // set here your value
            dialogDiskusi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final EditText dialog_txt_ulasan = dialogDiskusi.findViewById(R.id.txt_ulasan);
        Button btn_kirim = dialogDiskusi.findViewById(R.id.btn_kirim);

        //Mengirim request untuk menambah diskusi barang ke Web Service
        btn_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog_txt_ulasan.getText().toString().equals("")){
                    Toast.makeText(activity, "Isi diskusi barang terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    String id_user = FirebaseAuth.getInstance().getUid();
                    if(id_user == null){
                        Toast.makeText(activity, "Login terlebih dahulu untuk diskusi barang", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try{
                        JSONObject body = new JSONObject();
                        body.put("id", "");
                        body.put("id_barang", id);
                        body.put("rating", 0);
                        body.put("deskripsi", dialog_txt_ulasan.getText().toString());

                        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_DISKUSI_BARANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(id_user), body, new ApiVolleyManager.RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try{
                                    JSONObject jsonresult = new JSONObject(result);
                                    int status = jsonresult.getJSONObject("metadata").getInt("status");
                                    String message = jsonresult.getJSONObject("metadata").getString("message");

                                    if(status == 200){
                                        Toast.makeText(activity, "Diskusi Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                        initDiskusiBarang();
                                        dialogDiskusi.dismiss();
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

        dialogDiskusi.show();
    }

    public void balasDiskusi(final String id_diskusi){
        //Buka dialog balas diskusi
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int device_TotalWidth = metrics.widthPixels;
        int device_TotalHeight = metrics.heightPixels;

        dialogBalas = new Dialog(activity, R.style.PopupTheme);
        dialogBalas.setContentView(R.layout.popup_review_balas);
        if(dialogBalas.getWindow() != null){
            dialogBalas.getWindow().setLayout(device_TotalWidth * 90 / 100 , device_TotalHeight * 60 / 100); // set here your value
            dialogBalas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final EditText dialog_txt_ulasan = dialogBalas.findViewById(R.id.txt_ulasan);
        Button btn_kirim = dialogBalas.findViewById(R.id.btn_kirim);

        //Mengirim request untuk membalas diskusi barang ke Web Service
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
                        body.put("id", id_diskusi);
                        body.put("id_barang", id);
                        body.put("rating", 0);
                        body.put("deskripsi", dialog_txt_ulasan.getText().toString());

                        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_DISKUSI_BARANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(id_user), body, new ApiVolleyManager.RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try{
                                    JSONObject jsonresult = new JSONObject(result);
                                    int status = jsonresult.getJSONObject("metadata").getInt("status");
                                    String message = jsonresult.getJSONObject("metadata").getString("message");

                                    if(status == 200){
                                        Toast.makeText(activity, "Balasan Diskusi berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                        initDiskusiBarang();
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

    private void initDiskusiBarang(){
        //Membaca diskusi barang dari Web Service
        try{
            JSONObject body = new JSONObject();
            body.put("id_barang", id);
            body.put("start", 0);
            body.put("count", 0);

            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_DISKUSI_BARANG, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200 || status == 404){
                            listDiskusiBarang.clear();
                            if(jsonresult.get("response") instanceof JSONObject){
                                JSONArray arrayDiskusi = jsonresult.getJSONObject("response").getJSONArray("review");

                                for(int i = 0; i < arrayDiskusi.length(); i++){
                                    JSONObject diskusi = arrayDiskusi.getJSONObject(i);
                                    UlasanModel ulasan = new UlasanModel(diskusi.getString("id"), diskusi.getString("foto"), diskusi.getString("profile_name"), diskusi.getString("deskripsi"), Converter.stringDTTToDate(diskusi.getString("waktu")));

                                    if(diskusi.has("child")){
                                        JSONArray arrayChild = diskusi.getJSONArray("child");
                                        for(int j = 0; j < arrayChild.length(); j++){
                                            ulasan.addBalasan(new UlasanModel(arrayChild.getJSONObject(j).getString("foto"),
                                                    arrayChild.getJSONObject(j).getString("profile_name"), arrayChild.getJSONObject(j).getString("deskripsi"), Converter.stringDTTToDate(arrayChild.getJSONObject(j).getString("waktu"))));
                                        }
                                    }

                                    listDiskusiBarang.add(ulasan);
                                }
                            }

                            adapter.notifyDataSetChanged();
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
}
