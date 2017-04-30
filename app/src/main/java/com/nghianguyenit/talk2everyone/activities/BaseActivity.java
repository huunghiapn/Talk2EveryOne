package com.nghianguyenit.talk2everyone.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nghianguyenit.talk2everyone.App;
import com.nghianguyenit.talk2everyone.R;

/**
 * Created by NghiaNH on 4/21/2017.
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected GoogleApiClient mGoogleApiClient;
    protected FirebaseAnalytics mFirebaseAnalytics;
    protected AdView mAdView;
    protected ProgressDialog progress;
    protected static final String TAG = "TALK2E1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage(getString(R.string.dialog_loading_message));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //Check if connected
        if(App.getGoogleApiHelper().isConnected())
        {
            //Get google api client
            mGoogleApiClient = App.getGoogleApiHelper().getGoogleApiClient();
        }

        // Initialize Firebase Measurement.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public void showLoading() {
        if (!progress.isShowing()) {
            progress.show();
        }
    }

    public void showLoading(String message) {
        if (!progress.isShowing()) {
            progress.setMessage(message);
            progress.show();
        }
    }

    public void hideLoading() {
        if (progress.isShowing()) {
            progress.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    public void showAlertDialog(String mes) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(mes);
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int btnID) {
                        onDialogButtonClick(btnID);
                        //Toast.makeText(getApplicationContext(),"You clicked yes button",Toast.LENGTH_LONG).show();
                    }
                });
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onDialogButtonClick(int id) {
    }

    @Override
    public void onClick(View v) {

    }

    public void showLongToast(String mes) {
        Toast.makeText(this, mes, Toast.LENGTH_LONG).show();
    }
}
