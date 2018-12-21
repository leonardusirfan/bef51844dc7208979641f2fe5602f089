package id.net.gmedia.selby.Upload;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Model.ObjectModel;
import id.net.gmedia.selby.Model.UploadModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.DialogFactory;

public class UploadBarangActivity extends AppCompatActivity {

    //Variabel UI
    private Dialog dialog;
    private EditText txt_nama, txt_harga, txt_berat, txt_deskripsi, txt_ukuran;
    private EditText txt_harga_normal;
    private TextView lbl_harga, lbl_harga_normal, lbl_selesai_lelang, txt_selesai_lelang;
    private Spinner spn_satuan_berat, spn_kondisi, spn_kategori, spn_brand;

    //Adapter & List Upload Gambar
    private UploadAdapter uploadAdapter;
    private List<UploadModel> listUpload = new ArrayList<>();

    //list objek kategori
    private List<ObjectModel> listKategori = new ArrayList<>();
    private List<ObjectModel> listBrand = new ArrayList<>();

    //flag apakah barang yang diupload adalah barang lelang
    private String jenis = "";

    //variabel waktu lelang
    private int endyear, endmonth, enddate, endhour, endminute;
    private int startyear, startmonth, startdate, starthour, startminute;
    private String end = "";
    private String start = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_barang);

        //Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.upload_barang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        txt_berat = findViewById(R.id.txt_berat);
        txt_deskripsi = findViewById(R.id.txt_deskripsi);
        txt_ukuran = findViewById(R.id.txt_ukuran);
        txt_harga_normal = findViewById(R.id.txt_harga_normal);
        txt_selesai_lelang = findViewById(R.id.txt_selesai_lelang);
        lbl_harga = findViewById(R.id.lbl_harga);
        lbl_harga_normal = findViewById(R.id.lbl_harga_normal);
        lbl_selesai_lelang = findViewById(R.id.lbl_selesai_lelang);
        spn_satuan_berat = findViewById(R.id.spn_satuan_berat);
        spn_kategori = findViewById(R.id.spn_kategori);
        spn_kondisi = findViewById(R.id.spn_kondisi);
        spn_brand = findViewById(R.id.spn_brand);
        RecyclerView rv_foto = findViewById(R.id.rv_foto);

        initKategori();
        initBrand();

        initDialog();

        //Inisialisasi Recycler View Upload Gambar Barang
        uploadAdapter = new UploadAdapter(this, listUpload,  Constant.URL_UPLOAD_GAMBAR_BARANG);
        rv_foto.setItemAnimator(new DefaultItemAnimator());
        rv_foto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_foto.setAdapter(uploadAdapter);
    }

    private void initKategori(){
        try{
            JSONObject body = new JSONObject();
            body.put("start", 0);
            body.put("count", 0);

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_HOME_CATEGORY, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            List<String> spinnerItem = new ArrayList<>();
                            JSONArray array = jsonresult.getJSONArray("response");
                            for(int i = 0; i < array.length(); i++){
                                listKategori.add(new ObjectModel(array.getJSONObject(i).getString("id"), array.getJSONObject(i).getString("category")));
                                spinnerItem.add(listKategori.get(i).getValue());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    UploadBarangActivity.this, android.R.layout.simple_spinner_item, spinnerItem);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spn_kategori.setAdapter(adapter);
                        }
                        else{
                            Toast.makeText(UploadBarangActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(UploadBarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Kategori", e.getMessage());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(UploadBarangActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Kategori", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(this, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Kategori", e.getMessage());
        }
    }

    private void initBrand(){
        try{
            JSONObject body = new JSONObject();
            body.put("start", 0);
            body.put("count", 0);

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_HOME_BRAND, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            List<String> spinnerItem = new ArrayList<>();
                            JSONArray array = jsonresult.getJSONArray("response");
                            spinnerItem.add("");
                            listBrand.add(new ObjectModel("0", ""));
                            for(int i = 0; i < array.length(); i++){
                                listBrand.add(new ObjectModel(array.getJSONObject(i).getString("id"), array.getJSONObject(i).getString("brand")));
                                spinnerItem.add(array.getJSONObject(i).getString("brand"));
                            }
                            spinnerItem.add("Lainnya");
                            listBrand.add(new ObjectModel("0", "Lainnya"));

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    UploadBarangActivity.this, android.R.layout.simple_spinner_item, spinnerItem);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spn_brand.setAdapter(adapter);
                        }
                        else{
                            Toast.makeText(UploadBarangActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(UploadBarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Kategori", e.getMessage());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(UploadBarangActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Kategori", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(this, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Kategori", e.getMessage());
        }
    }

    private void initDialog(){
        dialog = DialogFactory.getInstance().createDialog(this, R.layout.popup_barang_tambah, 75, 60);
        dialog.setCancelable(false);

        //ImageView img_cancel = dialog.findViewById(R.id.img_cancel);
        Button btn_preloved, btn_merchandise, btn_lelang;
        btn_preloved = dialog.findViewById(R.id.btn_preloved);
        btn_merchandise = dialog.findViewById(R.id.btn_merchandise);
        btn_lelang = dialog.findViewById(R.id.btn_lelang);
        TextView txt_pesan = dialog.findViewById(R.id.txt_pesan);

        btn_lelang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenis = "lelang";

                lbl_harga.setText(R.string.lelang_bid_awal);
                lbl_harga_normal.setVisibility(View.VISIBLE);
                txt_harga_normal.setVisibility(View.VISIBLE);
                lbl_selesai_lelang.setVisibility(View.VISIBLE);
                txt_selesai_lelang.setVisibility(View.VISIBLE);
                txt_selesai_lelang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar c = Calendar.getInstance();
                        startyear = c.get(Calendar.YEAR);
                        startmonth = c.get(Calendar.MONTH);
                        startdate = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(UploadBarangActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                endyear = year;
                                endmonth = monthOfYear;
                                enddate = dayOfMonth;

                                Calendar c = Calendar.getInstance();
                                starthour = c.get(Calendar.HOUR_OF_DAY);
                                startminute = c.get(Calendar.MINUTE);

                                TimePickerDialog timePickerDialog = new TimePickerDialog(UploadBarangActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                endhour = hourOfDay;
                                                endminute = minute;

                                                start = Converter.DTTToString(startyear, startmonth + 1, startdate, starthour, startminute, 0);
                                                end = Converter.DTTToString(endyear, endmonth + 1,
                                                        enddate, endhour, endminute, 0);
                                                String show = start + " s/d\n" + end;
                                                txt_selesai_lelang.setText(show);
                                                System.out.println(validDate());
                                            }
                                        }, starthour, startminute, true);
                                timePickerDialog.show();
                            }
                        }, startyear, startmonth, startdate);
                        datePickerDialog.show();
                    }
                });

                dialog.dismiss();
            }
        });

        btn_preloved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenis = "preloved";
                dialog.dismiss();
            }
        });

        btn_merchandise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenis = "merchandise";
                dialog.dismiss();
            }
        });

        /*img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                dialog.cancel();
            }
        });*/

        txt_pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pesan Merchandise
                /*Intent i = new Intent(UploadBarangActivity.this, BarangActivity.class);
                i.putExtra("jenis", "Merchandise");
                startActivity(i);*/
            }
        });

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onBackPressed();
                    dialog.cancel();
                }
                return true;
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999 && data.hasExtra(Pix.IMAGE_RESULTS)) {
            uploadAdapter.upload(data.getStringArrayListExtra(Pix.IMAGE_RESULTS));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tambah, menu);
        return true;
    }

    private void uploadBarang(){
        try{
            JSONObject body = new JSONObject();
            body.put("nama", txt_nama.getText().toString());
            body.put("harga", txt_harga.getText().toString());
            body.put("berat", txt_berat.getText().toString());
            body.put("satuan_berat", spn_satuan_berat.getSelectedItem().toString());
            body.put("deskripsi", txt_deskripsi.getText().toString());
            body.put("ukuran", txt_ukuran.getText().toString());
            body.put("foto", uploadAdapter.getMainImage());
            body.put("gallery", new JSONArray(uploadAdapter.getListImage()));
            body.put("kondisi", spn_kondisi.getSelectedItemPosition() == 0 ? 1 : 0 );
            body.put("brand", listBrand.get(spn_brand.getSelectedItemPosition()).getId());
            body.put("kategori", listKategori.get(spn_kategori.getSelectedItemPosition()).getId());

            body.put("lelang", jenis.equals("lelang")?"1":"0");
            switch (jenis) {
                case "lelang":
                    body.put("start", start);
                    body.put("end", end);
                    break;
                case "preloved":
                    body.put("start", "");
                    body.put("end", "");
                    body.put("jenis", "1");
                    break;
                case "merchandise":
                    body.put("start", "");
                    body.put("end", "");
                    body.put("jenis", "2");
                    break;
            }

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_UPLOAD_BARANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            Intent i = new Intent(UploadBarangActivity.this, HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.putExtra("start", 1);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(UploadBarangActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(UploadBarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("InputBarang", e.getMessage());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(UploadBarangActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("InputBarang", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(this, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("InputBarang", e.getMessage());
        }
    }

    private boolean validDate(){
        if(startyear < endyear){
            return true;
        }
        else if(startyear == endyear){
            if(startmonth < endmonth){
                return true;
            }
            else if(startmonth == endmonth){
                if(startdate < enddate){
                    return true;
                }
                else if(startdate == enddate){
                    return starthour < endhour;
                }
            }
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_post:
                if(txt_nama.getText().toString().equals("")){
                    Toast.makeText(this, "Nama barang belum terisi", Toast.LENGTH_SHORT).show();
                }
                else if(txt_harga.getText().toString().equals("")){
                    Toast.makeText(this, "Harga barang belum terisi", Toast.LENGTH_SHORT).show();
                }
                else if(uploadAdapter.getMainImage().equals("")){
                    Toast.makeText(this, "Upload minimal 1 gambar", Toast.LENGTH_SHORT).show();
                }
                else if(!uploadAdapter.isAllUploaded()){
                    Toast.makeText(this, "Tunggu semua gambar ter-Upload", Toast.LENGTH_SHORT).show();
                }
                else if(jenis.equals("lelang")) {
                    if(end.equals("") || start.equals("")){
                        Toast.makeText(this, "Waktu lelang belum diisi", Toast.LENGTH_SHORT).show();
                    }
                    else if(!validDate()){
                        Toast.makeText(this, "Waktu lelang tidak valid", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        uploadBarang();
                    }
                }
                else{
                    uploadBarang();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}