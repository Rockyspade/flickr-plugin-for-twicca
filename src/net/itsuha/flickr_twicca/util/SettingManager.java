package net.itsuha.flickr_twicca.util;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.util.AuthStore;
import com.aetrion.flickr.util.FileAuthStore;

public class SettingManager {
	private static SettingManager singleton = new SettingManager();
	private Context mCtx = null;
	private SharedPreferences mPref;
	private Auth mAuth;
	private static final String LOGTAG = "SettingManager";
	private static final String DEFAULT_SETS_ID = "default_sets_id";
	public static final String BLANK_SETS_ID = "0";

	private SettingManager() {
	}

	public void initialize(Context ctx) {
		mCtx = ctx;
		mPref = PreferenceManager.getDefaultSharedPreferences(mCtx);
	}

	public static SettingManager getInstance() {
		return singleton;
	}

	/**
	 * retrieve login information from store
	 * 
	 * @return
	 */
	public Auth getAuth() {
		if (mAuth != null)
			return mAuth;
		AuthStore store = null;
		try {
			store = new FileAuthStore(mCtx.getFilesDir());
		} catch (IOException e1) {
			return null;
		}
		Auth[] tokens = store.retrieveAll();
		if (tokens.length == 1) {
			mAuth = tokens[0];
			return tokens[0];
		} else {
			return null;
		}
	}

	/**
	 * store authentication token
	 */
	public boolean saveAuth(Auth auth) {
		AuthStore store = null;
		try {
			store = new FileAuthStore(mCtx.getFilesDir());
			store.clearAll();
			store.store(auth);
		} catch (IOException e1) {
			return false;
		}
		return true;
	}

	public void saveDefaultSetsId(String sid) {
		Editor editor = mPref.edit();
		editor.putString(DEFAULT_SETS_ID, sid);
		editor.commit();
	}
	
	public String getDefaultSetsId(){
		return mPref.getString(DEFAULT_SETS_ID, BLANK_SETS_ID);
	}


}