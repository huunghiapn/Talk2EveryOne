package com.nghianguyenit.talk2everyone.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.nghianguyenit.talk2everyone.R;

public class AboutActivity extends AppCompatActivity {

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    public void sendEmail(View view) {

        switch (view.getId()) {


            case R.id.email:

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto: huunghia.it11@gmailcom"));
                startActivity(Intent.createChooser(emailIntent, "Send feedback"));

                break;

        }


    }


    public void openGithub(View view) {
        String url = getString(R.string.nghianhgit);

        switch (view.getId()) {

            case R.id.github:
                viewIntent(url);
                break;
        }

    }

    private void viewIntent(String url) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void openGplus(View view) {

        String url = getString(R.string.nghianhplus);

        switch (view.getId()) {

            case R.id.gplus:
                viewIntent(url);
                break;
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

    }
}
