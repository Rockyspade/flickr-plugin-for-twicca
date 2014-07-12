/**
 *
 */

package net.itsuha.flickr_twicca.setting;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

import net.itsuha.flickr_twicca.R;
import net.itsuha.flickr_twicca.task.OAuthTask;
import net.itsuha.flickr_twicca.utils.FlickrHelper;
import net.itsuha.flickr_twicca.utils.MyApp;
import net.itsuha.flickr_twicca.utils.PreferenceManager;

import java.lang.ref.WeakReference;

import static net.itsuha.flickr_twicca.BuildConfig.DEBUG;

/**
 * Represents the auth dialog to grant this application the permission to access
 * user's flickr photos.
 *
 * @author charles
 */
public class AuthDialogFragment extends DialogFragment {
    public static final String SCHEME = "flickr-twicca";
    public static final String TAG = "auth_fragment";
    private static final String LOGTAG = AuthDialogFragment.class.getSimpleName();

    private WeakReference<Callback> mReference;

    public static AuthDialogFragment newInstance(Callback callback) {
        final AuthDialogFragment f = new AuthDialogFragment();
        f.mReference = new WeakReference<Callback>(callback);
        return f;
    }

    public interface Callback {
        void onOAuthDone(OAuth result);

        void onOAuthCancel();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.auth_dialog_message)
                .setPositiveButton(R.string.button_auth, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                OAuthTask task = new OAuthTask(getActivity());
                                task.execute();
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Callback callback = mReference.get();
                                if (callback != null) {
                                    callback.onOAuthCancel();
                                }
                                dismissAllowingStateLoss();
                            }
                        }
                );
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        String scheme = intent.getScheme();
        if (SCHEME.equals(scheme)) {
            Uri uri = intent.getData();
            String query = uri.getQuery();
            String[] data = query.split("&");
            if (data != null && data.length == 2) {
                String oauthToken = data[0].substring(data[0].indexOf("=") + 1);
                String oauthVerifier = data[1]
                        .substring(data[1].indexOf("=") + 1);
                String secret = PreferenceManager.getInstance().getFlickrTokenSecret();
                if (secret != null) {
                    GetOAuthTokenTask task = new GetOAuthTokenTask(this);
                    task.execute(oauthToken, secret, oauthVerifier);
                }
            }
        }

    }

    /**
     * Represents the task to get the oauth token and user information.
     * <p/>
     * This task should be called only after you got the request oauth request
     * token and the verifier.
     *
     * @author charles
     */
    private static class GetOAuthTokenTask extends
            AsyncTask<String, Integer, OAuth> {
        private static final String LOGTAG = GetOAuthTokenTask.class.getSimpleName();

        private final AuthDialogFragment mAuthDialog;

        GetOAuthTokenTask(AuthDialogFragment context) {
            this.mAuthDialog = context;
        }

        @Override
        protected OAuth doInBackground(String... params) {
            if (DEBUG) {
                Log.d(LOGTAG, "doInBackground");
            }
            String oauthToken = params[0];
            String oauthTokenSecret = params[1];
            String verifier = params[2];

            Flickr f = FlickrHelper.getFlickr();
            OAuthInterface oauthApi = f.getOAuthInterface();
            try {
                return oauthApi.getAccessToken(oauthToken, oauthTokenSecret,
                        verifier);
            } catch (Exception e) {
                Log.e(AuthDialogFragment.class.getName(), e.getMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(OAuth result) {
            if (DEBUG) {
                Log.d(LOGTAG, "onPostExecute");
            }
            if (mAuthDialog != null) {
                mAuthDialog.onOAuthDone(result);
            }
        }

    }

    void onOAuthDone(OAuth result) {
        if (DEBUG) {
            Log.d(LOGTAG, "onPostExecute");
        }
        if (result == null) {
            MyApp.toast("Failed");
        } else {
            User user = result.getUser();
            OAuthToken token = result.getToken();
            if (user == null || user.getId() == null || token == null
                    || token.getOauthToken() == null
                    || token.getOauthTokenSecret() == null) {
                MyApp.toast("Failed");
            } else {
                PreferenceManager.getInstance().saveFlickrAuthToken(result);
            }
        }
        Callback callback = mReference.get();
        if (callback != null) {
            callback.onOAuthDone(result);
        }
        dismissAllowingStateLoss();
    }
}
