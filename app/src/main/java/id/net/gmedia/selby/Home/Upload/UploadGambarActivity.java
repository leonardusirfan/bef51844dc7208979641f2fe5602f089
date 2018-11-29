package id.net.gmedia.selby.Home.Upload;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Model.UploadModel;

import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;

public class UploadGambarActivity extends AppCompatActivity {

    private UploadAdapter adapter;
    private List<UploadModel> listUpload = new ArrayList<>();

    private TextView txt_deskripsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_gambar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Tambah Gambar");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txt_deskripsi = findViewById(R.id.txt_deskripsi);
        RecyclerView rv_upload = findViewById(R.id.rv_upload);
        rv_upload.setItemAnimator(new DefaultItemAnimator());
        rv_upload.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new UploadAdapter(this, listUpload, Constant.URL_UPLOAD_GAMBAR);
        rv_upload.setAdapter(adapter);
    }

    private void upload(){
        if(adapter.isNoPic()){
            try{
                String judul = txt_deskripsi.getText().toString();

                JSONObject body = new JSONObject();
                body.put("jenis", 3);
                body.put("judul", judul);
                body.put("id_gambar", new JSONArray());

                ApiVolleyManager.getInstance().addRequest(UploadGambarActivity.this, Constant.URL_POST, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject jsonresult = new JSONObject(result);
                            int status = jsonresult.getJSONObject("metadata").getInt("status");
                            String message = jsonresult.getJSONObject("metadata").getString("message");

                            if(status == 200){
                                Intent i = new Intent(UploadGambarActivity.this, HomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.putExtra("start", 1);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(UploadGambarActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(UploadGambarActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e("Post", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(UploadGambarActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                        Log.e("Post", result);
                    }
                });
            }
            catch (JSONException e){
                Toast.makeText(UploadGambarActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                Log.e("Post", e.getMessage());
            }
        }
        else if(adapter.isAllUploaded()){
            try{
                //inisialisasi gambar yang di upload
                JSONArray listGambar = new JSONArray();
                listGambar.put(adapter.main_image);
                for(String url : adapter.list_image){
                    listGambar.put(url);
                }

                String judul = txt_deskripsi.getText().toString();

                JSONObject body = new JSONObject();
                body.put("jenis", 2);
                body.put("judul", judul);
                body.put("id_gambar", listGambar);

                ApiVolleyManager.getInstance().addRequest(UploadGambarActivity.this, Constant.URL_POST, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject jsonresult = new JSONObject(result);
                            int status = jsonresult.getJSONObject("metadata").getInt("status");
                            String message = jsonresult.getJSONObject("metadata").getString("message");

                            if(status == 200){
                                Intent i = new Intent(UploadGambarActivity.this, HomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.putExtra("start", 1);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(UploadGambarActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(UploadGambarActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e("Post", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(UploadGambarActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                        Log.e("Post", result);
                    }
                });
            }
            catch (JSONException e){
                Toast.makeText(UploadGambarActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                Log.e("Post", e.getMessage());
            }
        }
        else{
            Toast.makeText(UploadGambarActivity.this, "Belum semua gambar ter-upload", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999 && data.hasExtra(Pix.IMAGE_RESULTS)) {
            adapter.upload(data.getStringArrayListExtra(Pix.IMAGE_RESULTS));
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                upload();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
