package id.net.gmedia.selby.Barang.Fragment;

import android.app.Activity;
import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Barang.Adapter.DiskusiBarangAdapter;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Model.UlasanModel;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.DialogFactory;


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
                                        initDiskusiBarang(last_loaded);
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
                                        initDiskusiBarang(last_loaded);
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

    private void initDiskusiBarang(int count){
        total = 0;
        last_loaded = 0;
        //Membaca diskusi barang dari Web Service
        try{
            JSONObject body = new JSONObject();
            body.put("id_barang", id);
            body.put("start", 0);
            body.put("count", count);

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
                                total = jsonresult.getJSONObject("response").getInt("total_records");
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
                                    last_loaded += 1;
                                }
                            }

                            adapter = new DiskusiBarangAdapter(FragmentDiskusiBarang.this, listDiskusiBarang);
                            if(last_loaded == total){
                                adapter.setAll_loaded();
                            }
                            rv_diskusi.setAdapter(adapter);
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

    public void loadDiskusiBarang(){
        //Membaca diskusi barang dari Web Service
        try{
            JSONObject body = new JSONObject();
            body.put("id_barang", id);
            body.put("start", last_loaded);
            body.put("count", LOAD_COUNT);

            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_DISKUSI_BARANG, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200 || status == 404){
                            if(jsonresult.get("response") instanceof JSONObject){
                                total = jsonresult.getJSONObject("response").getInt("total_records");
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
                                    last_loaded += 1;
                                }
                            }

                            if(last_loaded == total){
                                adapter.setAll_loaded();
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
