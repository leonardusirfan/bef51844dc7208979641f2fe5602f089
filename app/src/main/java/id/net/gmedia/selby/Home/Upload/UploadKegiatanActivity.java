package id.net.gmedia.selby.Home.Upload;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.DateTimeChooser;

public class UploadKegiatanActivity extends AppCompatActivity {
    /*
        Activity untuk artis input kegiatan
     */

    private EditText txt_judul, txt_tempat, txt_deskripsi;
    private TextView txt_tanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_kegiatan);

        //Inisialisasi toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Tambah Kegiatan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        txt_judul = findViewById(R.id.txt_judul);
        txt_tempat = findViewById(R.id.txt_tempat);
        txt_deskripsi = findViewById(R.id.txt_deskripsi);
        txt_tanggal = findViewById(R.id.txt_tanggal);

        txt_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(UploadKegiatanActivity.this, new DateTimeChooser.OnDateTimeSelected() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_tanggal.setText(dateString);
                    }
                });
            }
        });
    }

    private void upload(){
        try{
            JSONObject body = new JSONObject();
            body.put("jenis", 1);
            body.put("judul", txt_judul.getText().toString());
            body.put("tgl", txt_tanggal.getText().toString());
            body.put("tempat", txt_tempat.getText().toString());
            body.put("deskripsi", txt_deskripsi.getText().toString());

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_POST, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            Intent i = new Intent(UploadKegiatanActivity.this, HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.putExtra("start", 1);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(UploadKegiatanActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(UploadKegiatanActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("UploadKegiatan", e.getMessage());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(UploadKegiatanActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("UploadKegiatan",result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(this, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("UploadKegiatan", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tambah, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_post:
                if(txt_judul.getText().toString().equals("")){
                    Toast.makeText(this, "Isi judul kegiatan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    upload();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
