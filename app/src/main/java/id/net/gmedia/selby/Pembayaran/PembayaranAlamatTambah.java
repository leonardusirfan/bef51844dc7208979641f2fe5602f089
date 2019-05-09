package id.net.gmedia.selby.Pembayaran;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Model.AlamatModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class PembayaranAlamatTambah extends AppCompatActivity {

    private AlamatModel alamat;

    private SimpleObjectModel provinsi;
    private SimpleObjectModel kota;
    private EditText txt_penerima, txt_alamat, txt_kodepos, txt_telepon, txt_label;

    private List<SimpleObjectModel> listProvinsi = new ArrayList<>();
    private List<SimpleObjectModel> listKota = new ArrayList<>();
    private List<String> listKodePos = new ArrayList<>();

    private ArrayAdapter<SimpleObjectModel> adapterProvinsi;
    private ArrayAdapter<SimpleObjectModel> adapterKota;

    private AppCompatSpinner spn_kota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_alamat_tambah);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Tambah alamat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AppCompatSpinner spn_provinsi = findViewById(R.id.spn_provinsi);
        spn_kota = findViewById(R.id.spn_kota);
        txt_penerima = findViewById(R.id.txt_penerima);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_kodepos = findViewById(R.id.txt_kodepos);
        txt_telepon = findViewById(R.id.txt_telepon);
        txt_label = findViewById(R.id.txt_label);

        adapterProvinsi = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listProvinsi);
        spn_provinsi.setAdapter(adapterProvinsi);

        adapterKota = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listKota);
        spn_kota.setAdapter(adapterKota);

        spn_provinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                provinsi = listProvinsi.get(position);

                kota = null;
                listKota.clear();
                adapterKota = new ArrayAdapter<>(PembayaranAlamatTambah.this,
                        android.R.layout.simple_list_item_1, listKota);
                //txt_provinsi.setThreshold(1);
                spn_kota.setAdapter(adapterKota);

                loadKota(listProvinsi.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_kota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kota = listKota.get(position);
                txt_kodepos.setText(listKodePos.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.btn_tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_label.getText().toString().equals("")){
                    Toast.makeText(PembayaranAlamatTambah.this,
                            "Label pengiriman belum terisi", Toast.LENGTH_SHORT).show();
                }
                else if(txt_telepon.getText().toString().equals("")){
                    Toast.makeText(PembayaranAlamatTambah.this,
                            "Nomor telepon belum terisi", Toast.LENGTH_SHORT).show();
                }
                else if(provinsi == null){
                    Toast.makeText(PembayaranAlamatTambah.this,
                            "Pilih provinsi terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(kota == null){
                    Toast.makeText(PembayaranAlamatTambah.this,
                            "Pilih kota terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(txt_penerima.getText().toString().equals("")){
                    Toast.makeText(PembayaranAlamatTambah.this,
                            "Data penerima belum terisi", Toast.LENGTH_SHORT).show();
                }
                else if(txt_alamat.getText().toString().equals("")){
                    Toast.makeText(PembayaranAlamatTambah.this,
                            "Alamat belum terisi", Toast.LENGTH_SHORT).show();
                }
                else if(txt_kodepos.getText().toString().equals("")){
                    Toast.makeText(PembayaranAlamatTambah.this,
                            "Kodepos belum terisi", Toast.LENGTH_SHORT).show();
                }
                else{
                    alamat = new AlamatModel("", txt_label.getText().toString(), txt_penerima.getText().toString(),
                            txt_telepon.getText().toString(), kota.getId(), kota.getValue(), provinsi.getId(),
                            provinsi.getValue(), txt_alamat.getText().toString(), txt_kodepos.getText().toString());
                    simpanAlamat();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        loadProvinsi();
        super.onResume();
    }

    private void simpanAlamat(){
        AppLoading.getInstance().showLoading(this);

        JSONBuilder body = new JSONBuilder();
        body.add("label", alamat.getLabel());
        body.add("telepon", alamat.getTelepon());
        body.add("penerima", alamat.getPenerima());
        body.add("alamat", alamat.getAlamat());
        body.add("kodepos", alamat.getKodepos());
        body.add("id_provinsi", alamat.getId_provinsi());
        body.add("provinsi", alamat.getNama_provinsi());
        body.add("id_kota", alamat.getId_kota());
        body.add("kota", alamat.getNama_kota());
        Log.d(Constant.TAG, body.create().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ALAMAT_PENGIRIMAN_TAMBAH,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(PembayaranAlamatTambah.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();

                        Gson gson = new Gson();
                        Intent data = new Intent();
                        data.putExtra(Constant.RESULT_ALAMAT, gson.toJson(alamat));
                        setResult(RESULT_OK,data);
                        finish();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PembayaranAlamatTambah.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void loadProvinsi(){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KURIR_PROVINSI, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listProvinsi.clear();
                        adapterProvinsi.notifyDataSetChanged();

                        Toast.makeText(PembayaranAlamatTambah.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listProvinsi.clear();
                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject provinsi = response.getJSONObject(i);
                                listProvinsi.add(new SimpleObjectModel(provinsi.getString("province_id"),
                                        provinsi.getString("province")));
                            }

                            adapterProvinsi.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(PembayaranAlamatTambah.this,
                                    R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PembayaranAlamatTambah.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadKota(String id_provinsi){
        JSONBuilder body = new JSONBuilder();
        body.add("id_provinsi", id_provinsi);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KURIR_KOTA, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listKota.clear();
                        listKodePos.clear();
                        adapterKota.notifyDataSetChanged();

                        Toast.makeText(PembayaranAlamatTambah.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listKota.clear();
                            listKodePos.clear();

                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject kota = response.getJSONObject(i);
                                listKota.add(new SimpleObjectModel(kota.getString("city_id"),
                                        kota.getString("type") + " " + kota.getString("city_name")));
                                listKodePos.add(kota.getString("postal_code"));
                            }

                            adapterKota.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(PembayaranAlamatTambah.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PembayaranAlamatTambah.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
