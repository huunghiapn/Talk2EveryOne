package com.nghianguyenit.talk2everyone.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kcode.lib.UpdateWrapper;
import com.kcode.lib.bean.VersionModel;
import com.kcode.lib.log.L;
import com.kcode.lib.net.CheckUpdateTask;
import com.nghianguyenit.talk2everyone.R;
import com.nghianguyenit.talk2everyone.api.TranslateService;
import com.nghianguyenit.talk2everyone.models.Language;
import com.nghianguyenit.talk2everyone.utils.DBHelper;
import com.nghianguyenit.talk2everyone.utils.Helper;
import com.nghianguyenit.talk2everyone.utils.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements
        TextToSpeech.OnInitListener {

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUsername;
    private String mPhotoUrl;
    private AdView mAdView;
    CircleImageView accountImageView;
    private TextToSpeech tts;
    private TextView txtSpeechInput;
    private TextView txtSpeechOutput;
    private ImageButton btnSpeak;
    private ImageButton btnReplay;
    private Spinner sourceSpinner;
    private Spinner targetSpinner;
    private Spinner apiSpinner;
    private int apiIndex;
    private int defaultLang;
    private JSONObject langs;

    //Constans here
    private static final int REQUEST_INVITE = 1;
    public static final String ANONYMOUS = "anonymous";
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        txtSpeechOutput = (TextView) findViewById(R.id.txtSpeechOutput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnReplay = (ImageButton) findViewById(R.id.btnReplay);
        accountImageView = (CircleImageView) findViewById(R.id.account_img);
        btnReplay.setEnabled(false);

        prepareUserInfo();
        getLangsData();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        btnReplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                speakOut(txtSpeechOutput.getText().toString());
            }
        });

        // Spinner element
        sourceSpinner = (Spinner) findViewById(R.id.source_spinner);
        targetSpinner = (Spinner) findViewById(R.id.target_spinner);
        apiSpinner = (Spinner) findViewById(R.id.api_spinner);

        //setSpinnerData();

        // Spinner click listener
        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                Language item = (Language) parent.getItemAtPosition(position);

                // Showing selected spinner item
                //showLongToast("Source: " + item.getCode());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        targetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                Language item = (Language) parent.getItemAtPosition(position);

                // Showing selected spinner item
                //showLongToast("Target: " + item.getCode());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        apiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                apiIndex = position;
                //showLongToast("Target: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (Network.isNetworkConnected(this) == false) {
            showAlertDialog(getString(R.string.no_network_message));
        }


        showLoading();
        checkNow();
        tts = new TextToSpeech(this, this);


        // Initialize and request AdMob ad.
        mAdView = (AdView) findViewById(R.id.adView);
        Helper.admobLoader(this, getResources(), mAdView);
    }

    private void prepareUserInfo() {
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            //startActivity(new Intent(this, SignInActivity.class));
            //finish();
            Glide.with(this)
                    .load(R.drawable.ic_account_circle_black_36dp)
                    .into(accountImageView);
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                Glide.with(this)
                        .load(mPhotoUrl)
                        .into(accountImageView);
            }
        }
    }

    private void getLangsData() {
        new AsyncTask<String, Integer, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                JSONObject jsonObject = Helper.getJSONObjectFromUrl("https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20170421T070751Z.44f8eb32f4499f7c.5f6c4bdbf3039c17934b329fdb17536ce4ff9408&ui=en");
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                try {
                    langs = jsonObject.getJSONObject("langs");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideLoading();
            }
        }.execute();
    }

    private void createLangs(JSONObject langs) {
        List<Language> categories = new ArrayList<>();
        try {
            Iterator<String> temp = langs.keys();
            while (temp.hasNext()) {
                String key = temp.next();
                String name = (String) langs.get(key);
                Language lang = new Language(name, new Locale(key));
                categories.add(lang);
                /*if(Locale.getDefault().getLanguage().equals(key)) {
                    defaultLang = categories.size()-1;
                }*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //categories = removeUnsupportLang(categories);

        // Creating adapter for spinner
        ArrayAdapter<Language> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        ArrayAdapter<CharSequence> apiAdapter = ArrayAdapter.createFromResource(this,
                R.array.api_array, android.R.layout.simple_spinner_item);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sourceSpinner.setAdapter(dataAdapter);
        targetSpinner.setAdapter(dataAdapter);
        apiSpinner.setAdapter(apiAdapter);

        setDefaultLang(categories);


    }

    private void setDefaultLang(List<Language> categories) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getCode().getLanguage().equals(Locale.getDefault().getLanguage())) {
                defaultLang = i;
            }
        }
        sourceSpinner.setSelection(defaultLang);
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Language sourceLang = (Language) sourceSpinner.getSelectedItem();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        //RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, sourceLang.getCode());
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            showAlertDialog(getString(R.string.googleapp_missing_alert));
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    final Language sourceLang = (Language) sourceSpinner.getSelectedItem();
                    final Language targetLang = (Language) targetSpinner.getSelectedItem();

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtSpeechInput.setText(result.get(0));

                    showLoading(getString(R.string.translating_message));

                    new AsyncTask<String, Integer, String>() {
                        @Override
                        protected String doInBackground(String... params) {
                            String translatedText = TranslateService.translateCode4w(params[0],
                                    sourceLang.getCode().getLanguage(),
                                    targetLang.getCode().getLanguage());
                            /*switch (apiIndex) {
                                case 0:
                                    translatedText = TranslateService.translateBing(params[0],
                                            sourceLang.getCode().getLanguage(),
                                            targetLang.getCode().getLanguage());
                                    break;
                                case 1:
                                    translatedText = TranslateService.translateGol(params[0],
                                            sourceLang.getCode().getLanguage(),
                                            targetLang.getCode().getLanguage());
                                    break;
                                case 2:
                                    translatedText = TranslateService.translateYandex(params[0],
                                            sourceLang.getCode().getLanguage(),
                                            targetLang.getCode().getLanguage());
                                    break;
                            }*/
                            return translatedText;
                        }

                        @Override
                        protected void onPostExecute(String text) {
                            super.onPostExecute(text);
                            hideLoading();
                            txtSpeechOutput.setText(text);
                            btnReplay.setEnabled(true);
                            speakOut(text);
                            // write to db
                            DBHelper.writeHistory(mDatabase, mFirebaseUser.getUid(),
                                    mFirebaseUser.getDisplayName(),
                                    txtSpeechInput.getText().toString(),
                                    txtSpeechOutput.getText().toString());
                        }
                    }.execute(result.get(0));
                }
                break;
            }
            case REQUEST_INVITE:
                if (resultCode == RESULT_OK) {
                    // Use Firebase Measurement to log that invitation was sent.
                    Bundle payload = new Bundle();
                    payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                    // Check how many invitations were sent and log.
                    String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                    Log.d(TAG, "Invitations sent: " + ids.length);
                } else {
                    // Use Firebase Measurement to log that invitation was not sent
                    Bundle payload = new Bundle();
                    payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                    // Sending failed or it was canceled, show failure message to the user
                    Log.d(TAG, "Failed to send invitation.");
                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(mFirebaseUser != null) {
            getMenuInflater().inflate(R.menu.main_logged, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_logout, menu);
        }
        return true;
    }

    @Override
    public void onDialogButtonClick(int id) {
        super.onDialogButtonClick(id);
        switch (id) {
            case AlertDialog.BUTTON_POSITIVE:
                Intent your_browser_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.googlequicksearchbox"));
                startActivity(your_browser_intent);
                break;
        }
    }

    @Override
    public void onInit(int status) {

        createLangs(langs);

        Language sourceLang = (Language) sourceSpinner.getSelectedItem();

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(sourceLang.getCode());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                showLongToast("This Language is not supported");
                Log.e("TTS", "This Language is not supported");
            } else {
                btnSpeak.setEnabled(true);
                hideLoading();
                showLongToast(getString(R.string.init_complete_message));
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
            recreate();
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareUserInfo();
    }

    private void speakOut(String text) {
        Language targetLang = (Language) targetSpinner.getSelectedItem();

        int result = tts.setLanguage(targetLang.getCode());
        if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            showLongToast("This Language is not supported");
            TranslateService.ttsOnline(text, targetLang.getCode().getLanguage());
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private List<Language> removeUnsupportLang(List<Language> langs) {
        List<Language> temp = new ArrayList<>();
        for (int index = 0; index < langs.size(); index++) {
            if (tts.isLanguageAvailable(langs.get(index).getCode()) != TextToSpeech.LANG_NOT_SUPPORTED) {
                temp.add(langs.get(index));
            }
        }
        return temp;
    }


    private Locale getLocale(String code) {
        switch (code) {
            case "en-US":
                return Language.EN;
            case "vi-VN":
                return Language.VN;
            case "ja-JP":
                return Language.JP;
        }
        return Locale.getDefault();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_message:
                startActivity(new Intent(this, MessageActivity.class));
                return true;
            case R.id.invite_menu:
                sendInvitation();
                return true;
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                mPhotoUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case R.id.sign_in_menu:
                startActivity(new Intent(this, SignInActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkNow() {
        checkUpdate(5000);
    }

    private void checkUpdate(final long time) {

        UpdateWrapper.Builder builder = new UpdateWrapper.Builder(getApplicationContext())
                .setTime(time)
                .setNotificationIcon(R.drawable.ic_launcher)
                .setUrl("https://code4w.com/app/talk2e1_update.json")
                .setIsShowToast(false)
                .setCallback(new CheckUpdateTask.Callback() {
                    @Override
                    public void callBack(VersionModel model) {
                        L.d(TAG, "new version :" + model.getVersionName());
                    }
                });

        builder.setCustomsActivity(CustomsUpdateActivity.class);

        builder.build().start();

    }


    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
}
