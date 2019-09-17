package id.net.gmedia.selby.Pembayaran;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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

import id.net.gmedia.selby.Model.OngkirModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class PembayaranOngkirGanti extends AppCompatActivity {

    //private int position = 0;
    //private String kurir = "";

    private String id_kota_asal = "";
    private String id_kota_tujuan = "";
    private int berat = 0;

    //private List<SimpleObjectModel> listKurir = new ArrayList<>();
    private List<OngkirModel> listOngkir = new ArrayList<>();
    private PembayaranOngkirAdapter adapter;
    //private ArrayAdapter<SimpleObjectModel> adapterKurir;
    private RecyclerView rv_ongkir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_ongkir_ganti);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Pilih Ongkir");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(Constant.EXTRA_ALAMAT_ID_ASAL)){
            id_kota_asal = getIntent().getStringExtra(Constant.EXTRA_ALAMAT_ID_ASAL);
        }
        if(getIntent().hasExtra(Constant.EXTRA_ALAMAT_ID_TUJUAN)){
            id_kota_tujuan = getIntent().getStringExtra(Constant.EXTRA_ALAMAT_ID_TUJUAN);
        }
        if(getIntent().hasExtra(Constant.EXTRA_BERAT_BARANG)){
            berat = getIntent().getIntExtra(Constant.EXTRA_BERAT_BARANG, 0);
        }
        /*if(getIntent().hasExtra(Constant.EXTRA_POSITION)){
            position = getIntent().getIntExtra(Constant.EXTRA_POSITION, 0);
        }*/

        /*AppCompatSpinner spn_kurir = findViewById(R.id.spn_kurir);
        adapterKurir = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listKurir);
        spn_kurir.setAdapter(adapterKurir);
        spn_kurir.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kurir = listKurir.get(position).getId();
                loadOngkir();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        rv_ongkir = findViewById(R.id.rv_ongkir);
        rv_ongkir.setItemAnimator(new DefaultItemAnimator());
        rv_ongkir.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PembayaranOngkirAdapter(this, listOngkir);
        rv_ongkir.setAdapter(adapter);

        findViewById(R.id.btn_pilih).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getSelected() == -1){
                    Toast.makeText(PembayaranOngkirGanti.this, "Pilih salah satu layanan ongkir", Toast.LENGTH_SHORT).show();
                }
                else{
                    Gson gson = new Gson();
                    OngkirModel o = listOngkir.get(adapter.getSelected());

                    Intent i = new Intent();
                    i.putExtra(Constant.EXTRA_ONGKIR, gson.toJson(o));
                    //i.putExtra(Constant.EXTRA_POSITION, position);
                    setResult(RESULT_OK, i);

                    finish();
                }
            }
        });

        //loadKurir();
        loadOngkir();
        AppLoading.getInstance().showLoading(this, new AppLoading.CancelListener() {
            @Override
            public void onCancel() {
                onBackPressed();
            }
        });
    }

    /*private void loadKurir(){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KURIR_MASTER, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listKurir.clear();
                        adapter.notifyDataSetChanged();

                        loadOngkir();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listKurir.clear();
                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject kurir = response.getJSONObject(i);
                                listKurir.add(new SimpleObjectModel(kurir.getString("kode"),
                                        kurir.getString("label")));
                            }

                            adapterKurir.notifyDataSetChanged();
                        }
                        catch (Exception e){
                            Toast.makeText(PembayaranOngkirGanti.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PembayaranOngkirGanti.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }*/

    private void loadOngkir(){
        listOngkir.clear();
        adapter.notifyDataSetChanged();

        AppLoading.getInstance().showLoading(this);

        JSONBuilder body = new JSONBuilder();
        body.add("asal", id_kota_asal);
        body.add("tujuan", id_kota_tujuan);
        body.add("berat", berat);
        //body.add("kurir", kurir);
        body.add("kurir", "");
        Log.d(Constant.TAG, body.create().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KURIR_ONGKIR, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        adapter = new PembayaranOngkirAdapter(PembayaranOngkirGanti.this, listOngkir);
                        rv_ongkir.setAdapter(adapter);

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject ongkir = response.getJSONObject(i);
                                listOngkir.add(new OngkirModel(ongkir.getString("code"), ongkir.getString("name" )
                                        + " " + ongkir.getString("service"), ongkir.getString("description"),
                                        ongkir.getDouble("value"), ongkir.getString("etd"), ongkir.getString("note")));
                            }

                            adapter = new PembayaranOngkirAdapter(PembayaranOngkirGanti.this, listOngkir);
                            rv_ongkir.setAdapter(adapter);
                        }
                        catch (JSONException e){
                            Toast.makeText(PembayaranOngkirGanti.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PembayaranOngkirGanti.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
