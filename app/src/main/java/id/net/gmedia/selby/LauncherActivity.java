package id.net.gmedia.selby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import id.net.gmedia.selby.Home.HomeActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        startActivity(new Intent(LauncherActivity.this, HomeActivity.class));
        finish();
    }
}
