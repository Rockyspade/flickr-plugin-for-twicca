/**
 * 
 */
package net.itsuha.flickr_twicca.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;


import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import net.itsuha.flickr_twicca.R;
import net.itsuha.flickr_twicca.setting.AuthDialogFragment;
import net.itsuha.flickr_twicca.utils.FlickrHelper;
import net.itsuha.flickr_twicca.utils.PreferenceManager;

import java.net.URL;

/**
 * Represents the task to start the oauth process.
 * 
 * @author charles
 * 
 */
public class OAuthTask extends AsyncTask<Void, Integer, String> {

	private static final Uri OAUTH_CALLBACK_URI = Uri.parse(AuthDialogFragment.SCHEME + "://oauth");

	/**
	 * The context.
	 */
	private Context mContext;

	/**
	 * The progress dialog before going to the browser.
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * Constructor.
	 * 
	 * @param context
	 */
	public OAuthTask(Context context) {
		super();
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		mProgressDialog = ProgressDialog.show(mContext,
//				"", mContext.getString(R.string.auth_gen_req_token)); //$NON-NLS-1$
//		mProgressDialog.setCanceledOnTouchOutside(true);
//		mProgressDialog.setCancelable(true);
//		mProgressDialog.setOnCancelListener(new OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dlg) {
//				OAuthTask.this.cancel(true);
//			}
//		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Void... params) {
		try {
			Flickr f = FlickrHelper.getFlickr();
			OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(
					OAUTH_CALLBACK_URI.toString());
            PreferenceManager.getInstance().saveFlickrTokenSecret(oauthToken.getOauthTokenSecret());
			URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(
					Permission.WRITE, oauthToken);
			return oauthUrl.toString();
		} catch (Exception e) {
			return "error:" + e.getMessage(); //$NON-NLS-1$
		}
	}

	@Override
	protected void onPostExecute(String result) {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		if (result != null && !result.startsWith("error") ) { //$NON-NLS-1$
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(result)));
		} else {
			Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
		}
	}

}
