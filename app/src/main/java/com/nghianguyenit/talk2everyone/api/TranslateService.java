package com.nghianguyenit.talk2everyone.api;

import android.os.AsyncTask;

import com.nghianguyenit.talk2everyone.utils.AudioHelper;
import com.nghianguyenit.talk2everyone.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.github.firemaples.language.Language;

/**
 * Created by NghiaNH on 4/21/2017.
 */

public class TranslateService {
    /*
    * Put constants here
    * */

    private static final String GOOGLE_API_KEY = "AIzaSyBEsci659GFruzsKNDPR0fpsXlHt19mXTc";
    private static final String GOOGLE_API_URL = "https://translate.googleapis.com/translate_a/single?client=gtx&dj=1&dt=t&ie=UTF-8&oe=UTF-8&";
    private static final String BING_API_KEY = "508b1fec83fd47018f5dfafcf1c9d3e9";
    private static final String BING_API_URL = "https://api.microsofttranslator.com/V2/Http.svc/Translate";
    private static final String YANDEX_API_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
    private static final String YANDEX_API_KEY = "trnsl.1.1.20170421T070751Z.44f8eb32f4499f7c.5f6c4bdbf3039c17934b329fdb17536ce4ff9408";
    private static final String MY_API_URL = "https://code4w.com/api/translate.php?";
    private static final String TTS_API_URL = "https://code4w.com/api/generateTTS.php?";

    /*public static String translateGogl(String text, String sourceLang, String targetLang) {
        // Instantiates a client
        Translate translate = TranslateOptions.newBuilder().setApiKey(API_KEY).build().getService();
        //Translate translateGogl = TranslateOptions.getDefaultInstance().getService();
        // Translates some text into Russian
        Translation translation =
                translate.translate(
                        text,
                        Translate.TranslateOption.sourceLanguage(sourceLang),
                        Translate.TranslateOption.targetLanguage(targetLang));


        System.out.printf("Text: %s%n", text);
        System.out.printf("Translation: %s%n", translation.getTranslatedText());

        return translation.getTranslatedText();
    }*/

    public static String translateYandex(String text, String sourceLang, String targetLang) {
        String resultString = "";
        HashMap<String, String> reqParam = new HashMap<>();
        reqParam.put("key", YANDEX_API_KEY);
        reqParam.put("text", text);
        reqParam.put("lang", sourceLang + "-" + targetLang);

        try {
            String reqQuery = Helper.createQueryStringForParameters(reqParam);
            JSONObject resultObject = Helper.getJSONObjectFromUrl(YANDEX_API_URL + reqQuery);

            if ("200".equals(resultObject.getString("code"))) {
                resultString = (String) resultObject.getJSONArray("text").get(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.printf("Text: %s%n", text);
        System.out.printf("Translation: %s%n", resultString);

        return resultString;
    }

    public static String detectLangYandex(String text, String sourceLang, String targetLang) {
        String resultString = "";
        HashMap<String, String> reqParam = new HashMap<>();
        reqParam.put("key", YANDEX_API_KEY);
        reqParam.put("text", text);
        reqParam.put("lang", sourceLang + "-" + targetLang);

        try {
            String reqQuery = Helper.createQueryStringForParameters(reqParam);
            JSONObject resultObject = Helper.getJSONObjectFromUrl(YANDEX_API_URL + reqQuery);

            if ("200".equals(resultObject.getString("code"))) {
                resultString = (String) resultObject.getJSONArray("text").get(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.printf("Text: %s%n", text);
        System.out.printf("Translation: %s%n", resultString);

        return resultString;
    }

    public static String translateGol(String text, String sourceLang, String targetLang) {
        String resultString = "";
        HashMap<String, String> reqParam = new HashMap<>();
        reqParam.put("sl", sourceLang);
        reqParam.put("tl", targetLang);
        //reqParam.put("dt", "t");
        reqParam.put("q", text);

        try {
            String reqQuery = Helper.createQueryStringForParameters(reqParam);
            JSONObject resultObject = Helper.getJSONObjectFromUrl(GOOGLE_API_URL + reqQuery);

            resultString = ((JSONObject) resultObject.getJSONArray("sentences").get(0)).getString("trans");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.printf("Text: %s%n", text);
        System.out.printf("Translation: %s%n", resultString);

        return resultString;
    }

    public static String translateBing(String text, String sourceLang, String targetLang) {
        String resultString = "";

        io.github.firemaples.translate.Translate.setSubscriptionKey(BING_API_KEY);

        try {
            resultString = io.github.firemaples.translate.Translate.execute(text, Language.fromString(sourceLang), Language.fromString(targetLang));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.printf("Text: %s%n", text);
        System.out.printf("Translation: %s%n", resultString);

        return resultString;
    }

    public static String translateCode4w(String text, String sourceLang, String targetLang) {
        String resultString = "";
        HashMap<String, String> reqParam = new HashMap<>();
        reqParam.put("sl", sourceLang);
        reqParam.put("tl", targetLang);
        //reqParam.put("dt", "t");
        reqParam.put("text", text);

        try {
            String reqQuery = Helper.createQueryStringForParameters(reqParam);
            JSONObject resultObject = Helper.getJSONObjectFromUrl(MY_API_URL + reqQuery);

            resultString = resultObject.getString("trans");
            //System.out.printf("url1: %s%n", resultObject.getString("saudio"));
            //System.out.printf("url2: %s%n", resultObject.getString("taudio"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.printf("Text: %s%n", text);
        System.out.printf("Translation: %s%n", resultString);

        return resultString;
    }

    public static String translate(String text, String sourceLang, String targetLang) {
        String resultString = "";

        resultString = translateGol(text, sourceLang, targetLang);
        if (resultString != "") return resultString;
        resultString = translateBing(text, sourceLang, targetLang);
        if (resultString != "") return resultString;
        resultString = translateYandex(text, sourceLang, targetLang);
        if (resultString != "") return resultString;

        return resultString;
    }

    public static void ttsOnline(String text, String targetLang) {
        HashMap<String, String> reqParam = new HashMap<>();
        reqParam.put("tl", targetLang);
        reqParam.put("text", text);
        try {
            final String reqQuery = Helper.createQueryStringForParameters(reqParam);

            new AsyncTask<String, Integer, JSONObject>() {
                @Override
                protected JSONObject doInBackground(String... params) {
                    JSONObject resultObject = Helper.getJSONObjectFromUrl(TTS_API_URL + reqQuery);
                    return resultObject;
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    super.onPostExecute(jsonObject);
                    try {
                        String resultString = jsonObject.getString("audio");
                        AudioHelper.playAudio(resultString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
