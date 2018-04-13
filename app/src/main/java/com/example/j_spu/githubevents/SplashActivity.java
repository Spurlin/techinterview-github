package com.example.j_spu.githubevents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start PublicEventActivity
        startActivity(new Intent(SplashActivity.this, PublicEventActivity.class));

        // close splash activity
        finish();
    }
}
