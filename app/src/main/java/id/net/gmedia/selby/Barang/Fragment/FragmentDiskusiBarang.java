package id.net.gmedia.selby.Barang.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Barang.Adapter.DiskusiBarangAdapter;
import id.net.gmedia.selby.LoginActivity;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Model.UlasanModel;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.AppRequestCallback;
import id.net.gmedia.selby.Util.AppSharedPreferences;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.DialogFactory;
import id.net.gmedia.selby.Util.JSONBuilder;


public class FragmentDiskusiBarang extends Fragment {

    //Variabel penampung activity
    private Activity activity;

    //Variabel penampung id barang
    private String id = "";

    private final int LOAD_COUNT = 2;
    private int last_loaded = 0;
    private int total = 0;

    //Variabel dialog untuk tambah dan balas diskusi barang
    private Dialog dialogDiskusi;
    private Dialog dialogBalas;

    //Variabel penampil diskusi barang
    private DiskusiBarangAdapter adapter;
    private List<UlasanModel> listDiskusiBarang;
    private RecyclerView rv_diskusi;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View layout = inflater.inflate(R.layout.fragment_barang_diskusi, container, false);

        //Menginisialisasi data diskusi barang
        if(getArguments() != null) {
            id = getArguments().getString("id");
        }

        //Inisialisasi Recycler View ulasan
        listDiskusiBarang = new ArrayList<>();
        rv_diskusi = layout.findViewById(R.id.rv_diskusi);
        rv_diskusi.setItemAnimator(new DefaultItemAnimator());
        rv_diskusi.setLayoutManager(new LinearLayoutManager(getContext()));
        initDiskusiBarang(LOAD_COUNT);

        return layout;
    }

    public void tambahDiskusi(){
        //Buka dialog tambah diskusi
        dialogDiskusi = DialogFactory.getInstance().createDialog(activity, R.layout.popup_barang_diskusi, 70, 45);
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
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                    }

                    JSONBuilder body = new JSONBuilder();
                    body.add("id", "");
                    body.add("id_barang", id);
                    body.add("rating", 0);
                    body.add("deskripsi", dialog_txt_ulasan.getText().toString());

                    ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_DISKUSI_BARANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(id_user), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                        @Override
                        public void onSuccess(String response) {
                            Toast.makeText(activity, "Diskusi Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            initDiskusiBarang(last_loaded);
                            dialogDiskusi.dismiss();
                        }

                        @Override
                        public void onFail(String message) {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
            }
        });

        dialogDiskusi.show();
    }

    public void balasDiskusi(final String id_diskusi){
        if(!AppSharedPreferences.isLoggedIn(activity)){
            activity.startActivity(new Intent(activity, LoginActivity.class));
        }
        else {
            //Buka dialog balas diskusi
            dialogBalas = DialogFactory.getInstance().createDialog(activity, R.layout.popup_barang_balas, 70, 45);
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

                        JSONBuilder body = new JSONBuilder();
                        body.add("id", id_diskusi);
                        body.add("id_barang", id);
                        body.add("rating", 0);
                        body.add("deskripsi", dialog_txt_ulasan.getText().toString());

                        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_DISKUSI_BARANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(id_user), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                            @Override
                            public void onSuccess(String response) {
                                Toast.makeText(activity, "Balasan Diskusi berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                initDiskusiBarang(last_loaded);
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

    private void initDiskusiBarang(int count){
        total = 0;
        last_loaded = 0;
        //Membaca diskusi barang dari Web Service

        JSONBuilder body = new JSONBuilder();
        body.add("id_barang", id);
        body.add("start", 0);
        body.add("count", count);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_DISKUSI_BARANG, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    listDiskusiBarang.clear();
                    Object json = new JSONTokener(response).nextValue();
                    if(json instanceof JSONObject){
                        JSONObject jsonresult = (JSONObject) json;
                        total = jsonresult.getInt("total_records");
                        JSONArray arrayDiskusi = jsonresult.getJSONArray("review");

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
                            last_loaded += 1;
                        }
                    }

                    adapter = new DiskusiBarangAdapter(FragmentDiskusiBarang.this, listDiskusiBarang);
                    if(last_loaded == total){
                        adapter.setAll_loaded();
                    }
                    rv_diskusi.setAdapter(adapter);
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

    public void loadDiskusiBarang(){
        //Membaca diskusi barang dari Web Service

        JSONBuilder body = new JSONBuilder();
        body.add("id_barang", id);
        body.add("start", last_loaded);
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_DISKUSI_BARANG, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    listDiskusiBarang.clear();
                    Object json = new JSONTokener(response).nextValue();
                    if(json instanceof JSONObject){
                        JSONObject jsonresult = (JSONObject) json;
                        total = jsonresult.getInt("total_records");
                        JSONArray arrayDiskusi = jsonresult.getJSONArray("review");

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
                            last_loaded += 1;
                        }
                    }

                    if(last_loaded == total){
                        adapter.setAll_loaded();
                    }
                    adapter.notifyDataSetChanged();
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
}
