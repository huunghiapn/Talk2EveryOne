package com.nghianguyenit.talk2everyone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.nghianguyenit.talk2everyone.CodelabPreferences;
import com.nghianguyenit.talk2everyone.R;
import com.nghianguyenit.talk2everyone.models.History;
import com.nghianguyenit.talk2everyone.utils.Helper;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by NghiaNH on 4/24/2017.
 */

public class MessageActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    private static final String TAG = MessageActivity.class.getSimpleName();
    public static final String HISTORYS_CHILD = "user-historys";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "http://talk2e1.nghianguyenit.net/message/";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<History, MessageViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<History, MessageViewHolder>(
                History.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(HISTORYS_CHILD).child(mFirebaseUser.getUid())) {

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              History history, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (history.getTarget() != null) {
                    viewHolder.messageTextView.setText(history.getTarget());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                } else {
                }


                viewHolder.messengerTextView.setText(history.source);
                viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(MessageActivity.this,
                        R.drawable.ic_account_circle_black_36dp));
                /*if (history.getPhotoUrl() == null) {;
                } else {
                    Glide.with(MessageActivity.this)
                            .load(history.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }*/

                if (history.getTarget() != null) {
                    // write this message to the on-device index
                    FirebaseAppIndex.getInstance().update(getMessageIndexable(history));
                }

                // log a view action on it
                FirebaseUserActions.getInstance().end(getMessageViewAction(history));
            }

            @Override
            protected void onCancelled(DatabaseError error) {
                showLongToast("Failed: " + error.getMessage());
                super.onCancelled(error);
            }

            @Override
            public boolean onFailedToRecycleView(MessageViewHolder holder) {
                showLongToast("Failed on bind view");
                return super.onFailedToRecycleView(holder);
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                showLongToast("Failed on bind view");
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(CodelabPreferences.FRIENDLY_MSG_LENGTH, 10L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();

        // Initialize and request AdMob ad.
        mAdView = (AdView) findViewById(R.id.adView);
        Helper.admobLoader(this, getResources(), mAdView);
    }

    private Action getMessageViewAction(History history) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(history.getSource(), MESSAGE_URL.concat(history.getUid()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    private Indexable getMessageIndexable(History history) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername.equals(history.getSource()))
                .setName(history.getSource())
                .setUrl(MESSAGE_URL.concat(history.getUid() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(MESSAGE_URL.concat(history.getUid() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(history.getTarget())
                .setUrl(MESSAGE_URL.concat(history.getUid()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                mPhotoUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            case R.id.fresh_config_menu:
                showLoading();
                fetchConfig();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                        hideLoading();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(TAG, "Error fetching config", e);
                        applyRetrievedLengthLimit();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
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
        }
    }

    /**
     * Apply retrieved length limit to edit text field. This result may be fresh from the server or it may be from
     * cached values.
     */
    private void applyRetrievedLengthLimit() {
        Long friendly_msg_length = mFirebaseRemoteConfig.getLong(CodelabPreferences.FRIENDLY_MSG_LENGTH);
        //mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        Log.d(TAG, "FML is: " + friendly_msg_length);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
