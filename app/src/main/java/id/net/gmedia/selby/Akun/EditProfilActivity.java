package id.net.gmedia.selby.Akun;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Model.UserModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.AppRequestCallback;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.JSONBuilder;

public class EditProfilActivity extends AppCompatActivity {

    /*
        Activity edit profil user
    */

    //Variabel UI
    EditText txt_nama, txt_alamat, txt_telepon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

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
            getSupportActionBar().setTitle("Edit Profil");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        txt_nama = findViewById(R.id.txt_nama);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_telepon = findViewById(R.id.txt_telepon);

        //Inisialisasi User
        if(getIntent().hasExtra("user")){
            Gson gson = new Gson();
            UserModel user = gson.fromJson(getIntent().getStringExtra("user"), UserModel.class);

            txt_nama.setText(user.getNama());
            txt_alamat.setText(user.getAlamat());
            txt_telepon.setText(user.getTelepon());
        }
    }

    private void saveProfile(){
        //menyimpan profil ke database
        JSONBuilder body = new JSONBuilder();
        body.add("nama", txt_nama.getText().toString());
        body.add("alamat", txt_alamat.getText().toString());
        body.add("no_telp", txt_telepon.getText().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_EDIT_PROFIL, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(EditProfilActivity.this, "Edit Profil Berhasil", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EditProfilActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("start", 4);
                startActivity(i);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(EditProfilActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_simpan:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Simpan Profil");
                builder.setMessage("Yakin menyimpan profil?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveProfile();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
