package net.itsuha.flickr_twicca.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

public class PreferenceManager {
    private static PreferenceManager sInstance = null;
    private Context mContext = null;
    private SharedPreferences mPreference;
    @SuppressWarnings("unused")
    private static final String LOGTAG = PreferenceManager.class.getSimpleName();
    private static final String DEFAULT_SETS_ID = "default_sets_id";
    public static final String BLANK_SETS_ID = "0";

    private static final String FLICKR_TOKEN = "flickr_token";
    private static final String FLICKR_SECRET_TOKEN = "flickr_secret_token";
    private static final String FLICKR_USER_ID = "flickr_user_id";
    private static final String FLICKR_USER_NAME = "flickr_user_name";
    private static final String AUTHENTICATING = "authenticating";


    private PreferenceManager(Context ctx) {
        mContext = ctx;
        mPreference = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static synchronized PreferenceManager getInstance() {
        if (sInstance == null) {
            sInstance = new PreferenceManager(MyApp.getInstance());
        }
        return sInstance;
    }

    public void saveDefaultSetsId(String sid) {
        Editor editor = mPreference.edit();
        editor.putString(DEFAULT_SETS_ID, sid);
        editor.commit();
    }

    public String getDefaultSetsId() {
        return mPreference.getString(DEFAULT_SETS_ID, BLANK_SETS_ID);
    }

    public String getFlickrToken() {
        String token = mPreference.getString(FLICKR_TOKEN, null);
        return token;
    }

    public OAuth loadSavedOAuth() {
        String userId = getUserId();
        String userName = getUserName();
        String token = getFlickrToken();
        String tokenSecret = getFlickrTokenSecret();
        if (userId == null || token == null || tokenSecret == null) {
            return null;
        }
        OAuth oauth = new OAuth();
        oauth.setToken(new OAuthToken(token, tokenSecret));
        User user = new User();
        user.setId(userId);
        user.setRealName(userName);
        oauth.setUser(user);
        RequestContext.getRequestContext().setOAuth(oauth);
        return oauth;
    }

    public void saveFlickrAuthToken(OAuth oauth) {
        String oauthToken = null;
        String tokenSecret = null;
        String userId = null;
        String userName = null;
        if (oauth != null) {
            oauthToken = oauth.getToken().getOauthToken();
            tokenSecret = oauth.getToken().getOauthTokenSecret();
            userId = oauth.getUser().getId();
            userName = oauth.getUser().getUsername();
        }
        Editor editor = mPreference.edit();
        editor.putString(FLICKR_TOKEN, oauthToken);
        editor.putString(FLICKR_SECRET_TOKEN, tokenSecret);
        editor.putString(FLICKR_USER_ID, userId);
        editor.putString(FLICKR_USER_NAME, userName);
        editor.commit();
    }

    public void saveFlickrTokenSecret(String tokenSecret) {
        Editor editor = mPreference.edit();
        editor.putString(FLICKR_SECRET_TOKEN, tokenSecret);
        editor.commit();
    }

    public String getFlickrTokenSecret() {
        return mPreference.getString(FLICKR_SECRET_TOKEN, null);
    }

    public String getUserName() {
        return mPreference.getString(FLICKR_USER_NAME, null);
    }

    public String getUserId() {
        return mPreference.getString(FLICKR_USER_ID, null);
    }

    public boolean isAuthenticating() {
        return mPreference.getBoolean(AUTHENTICATING, false);
    }

    public void setAuthenticating(boolean authenticating) {
        Editor editor = mPreference.edit();
        editor.putBoolean(AUTHENTICATING, authenticating);
        editor.commit();
    }


}