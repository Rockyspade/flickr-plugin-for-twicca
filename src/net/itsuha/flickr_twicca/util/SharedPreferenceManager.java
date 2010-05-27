package net.itsuha.flickr_twicca.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPreferenceManager {
	private static SharedPreferenceManager singleton = new SharedPreferenceManager();
	private Context mCtx; 
	private SharedPreferences mPref;
	
	
	private static final String TOKEN = "TOKEN";
	private static final String USERNAME = "USERNAME";
	private SharedPreferenceManager(){}
	
	public void initialize(Context ctx){
		mCtx = ctx;
		mPref= PreferenceManager.getDefaultSharedPreferences(mCtx);
	}
	public static SharedPreferenceManager getInstance(){
		return singleton;
	}

	public String getToken(){
		return mPref.getString(TOKEN, "");
	}
	
	public String getUserName(){
		return mPref.getString(USERNAME, "");
	}
	public void setData(String token, String username){
		Editor edit = mPref.edit();
		edit.putString(TOKEN, token);
		edit.putString(USERNAME, username);
		edit.commit();
	}
}
