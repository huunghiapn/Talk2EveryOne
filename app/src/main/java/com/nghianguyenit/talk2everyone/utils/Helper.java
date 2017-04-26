package com.nghianguyenit.talk2everyone.utils;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nghianguyenit.talk2everyone.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

public class Helper {
    private static final String TAG = "TALK2E1";
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    private static boolean DISPLAY_DEBUG = true;

    public static void admobLoader(Context c, Resources resources, View AdmobView) {
        String adId = resources.getString(R.string.banner_ad_unit_id);
        if (adId != null && !adId.equals("")) {
            AdView adView = (AdView) AdmobView;
            adView.setVisibility(View.VISIBLE);

            // Look up the AdView as a resource and load a request.
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            adView.loadAd(adRequest);
        }
    }

    @SuppressLint("NewApi")
    public static void revealView(View toBeRevealed, View frame) {
        //Make sure that the view is still attached (e.g. we haven't switched to another screen in the meantime)
        if (ViewCompat.isAttachedToWindow(toBeRevealed)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // get the center for the clipping circle
                int cx = (frame.getLeft() + frame.getRight()) / 2;
                int cy = (frame.getTop() + frame.getBottom()) / 2;

                // get the final radius for the clipping circle
                int finalRadius = Math.max(frame.getWidth(), frame.getHeight());

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(
                        toBeRevealed, cx, cy, 0, finalRadius);

                // make the view visible and start the animation
                toBeRevealed.setVisibility(View.VISIBLE);
                anim.start();
            } else {
                toBeRevealed.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("NewApi")
    public static void setStatusBarColor(Activity mActivity, int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mActivity.getWindow().setStatusBarColor(color);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    //Makes high numbers readable (e.g. 5000 -> 5K)
    public static String formatValue(double value) {
        if (value > 0) {
            int power;
            String suffix = " kmbt";
            String formattedNumber = "";

            NumberFormat formatter = new DecimalFormat("#,###.#");
            power = (int) StrictMath.log10(value);
            value = value / (Math.pow(10, (power / 3) * 3));
            formattedNumber = formatter.format(value);
            formattedNumber = formattedNumber + suffix.charAt(power / 3);
            return formattedNumber.length() > 4 ? formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
        } else {
            return "0";
        }
    }

    //Get response from an URL request (GET)
    public static String getDataFromUrl(String url) {
        // Making HTTP request
        Log.v("INFO", "Requesting: " + url);

        StringBuffer chaine = new StringBuffer("");
        try {
            URL urlCon = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) urlCon
                    .openConnection();
            connection.setRequestProperty("User-Agent", "Universal/2.0 (Android)");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }

        } catch (IOException e) {
            // writing exception to log
            Log.e(TAG, e.getMessage());
        }

        return chaine.toString();
    }

    //Get JSON from an url and parse it to a JSON Object.
    public static JSONObject getJSONObjectFromUrl(String url) {
        String data = getDataFromUrl(url);
        //Log.e(TAG, data);

        try {
            return new JSONObject(data);
        } catch (Exception e) {
            Log.e("INFO", "Error parsing JSON. Printing stacktrace now");
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    //Get JSON from an url and parse it to a JSON Array.
    public static JSONArray getJSONArrayFromUrl(String url) {
        String data = getDataFromUrl(url);

        try {
            return new JSONArray(data);
        } catch (Exception e) {
            Log.e("INFO", "Error parsing JSON. Printing stacktrace now");
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public static String requestUrl(String url, String postParameters) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
            //urlConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString("vipcard:vip123".getBytes(), 1));
            if (postParameters != null) {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                Log.d("param", postParameters);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();
            }
            if (urlConnection.getResponseCode() != 200) {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                return null;
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return null;
        } catch (MalformedURLException e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } catch (SocketTimeoutException e2) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } catch (IOException e3) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } catch (Throwable th) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;
            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }
                parametersAsQueryString.append(parameterName).append(PARAMETER_EQUALS_CHAR).append(URLEncoder.encode(parameters.get(parameterName)));
                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }

}
