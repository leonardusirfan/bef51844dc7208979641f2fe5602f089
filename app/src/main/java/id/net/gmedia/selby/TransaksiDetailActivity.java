package id.net.gmedia.selby;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Util.Constant;

public class TransaksiDetailActivity extends AppCompatActivity {

    private AppCompatSpinner txt_provinsi;
    private AppCompatSpinner txt_kota;

    private ArrayAdapter<SimpleObjectModel> adapterProvinsi;
    private ArrayAdapter<SimpleObjectModel> adapterKota;

    private List<SimpleObjectModel> listProvinsi = new ArrayList<>();
    private List<SimpleObjectModel> listKota = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_detail);

        txt_provinsi = findViewById(R.id.txt_provinsi);
        adapterProvinsi = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listProvinsi);
        //txt_provinsi.setThreshold(1);
        txt_provinsi.setAdapter(adapterProvinsi);

        txt_kota = findViewById(R.id.txt_kota);
        adapterKota = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listKota);
        //txt_provinsi.setThreshold(1);
        txt_kota.setAdapter(adapterKota);

        txt_provinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadKota(listProvinsi.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txt_kota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hitungOngkir(listKota.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadProvinsi();
    }

    private void loadProvinsi(){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KURIR_PROVINSI, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listProvinsi.clear();
                        adapterProvinsi.notifyDataSetChanged();

                        Toast.makeText(TransaksiDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listProvinsi.clear();
                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject provinsi = response.getJSONObject(i);
                                listProvinsi.add(new SimpleObjectModel(provinsi.getString("province_id"), provinsi.getString("province")));
                            }

                            adapterProvinsi.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(TransaksiDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(TransaksiDetailActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        adapterKota.notifyDataSetChanged();
                        Toast.makeText(TransaksiDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listKota.clear();
                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject kota = response.getJSONObject(i);
                                listKota.add(new SimpleObjectModel(kota.getString("city_id"), kota.getString("city_name")));
                            }

                            adapterKota.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(TransaksiDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(TransaksiDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void hitungOngkir(String id_kota){
        JSONBuilder body = new JSONBuilder();
        body.add("asal", "1");
        body.add("tujuan", id_kota);
        body.add("berat", "200");
        body.add("kurir", "jne");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KURIR_ONGKIR, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(TransaksiDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(TransaksiDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}
