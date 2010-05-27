package net.itsuha.flickr_twicca.activity;

import static net.itsuha.flickr_twicca.util.LogConfig.DEBUG;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import net.itsuha.flickr_twicca.R;
import net.itsuha.flickr_twicca.util.AppProperties;
import net.itsuha.flickr_twicca.util.Messages;
import net.itsuha.flickr_twicca.util.SharedPreferenceManager;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.util.AuthStore;
import com.aetrion.flickr.util.FileAuthStore;

public class AuthActivity extends Activity {
	private Flickr mFlickr;
	private String mToken = "";
	private AuthInterface mAuthInterface;
	private String mFrob = "";
	private static final String LOGTAG = "AuthActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);

		try {
			auth();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Button authButton = (Button) findViewById(R.id.btn_complete);
		authButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveAuthResult();
				finish();
			}

		});
	}

	private void auth() throws IOException, SAXException,
			ParserConfigurationException {
		AppProperties prop = AppProperties.getInstance();
		String apiKey = prop.getApiKey();
		String secret = prop.getSecret();
		mFlickr = new Flickr(apiKey, secret, new REST());
		Flickr.debugStream = false;
		RequestContext requestContext;
		requestContext = RequestContext.getRequestContext();
		mAuthInterface = mFlickr.getAuthInterface();
		try {
			mFrob = mAuthInterface.getFrob();
		} catch (FlickrException e) {
			e.printStackTrace();
		}
		if (DEBUG)
			Log.d(LOGTAG, "frob: " + mFrob); //$NON-NLS-1$
		URL authUrl = mAuthInterface.buildAuthenticationUrl(Permission.WRITE,
				mFrob);
		TextView authUrlText = (TextView) findViewById(R.id.auth_url);
		authUrlText.setText(authUrl.toString());
		if (DEBUG) {
			Log.d(LOGTAG, Messages.getString("SettingActivity.0")); //$NON-NLS-1$
			Log.d(LOGTAG, authUrl.toExternalForm()); //$NON-NLS-1$
		}
		/*
		 * BufferedReader infile = new BufferedReader(new InputStreamReader(
		 * System.in)); String line = infile.readLine();
		 */

	}

	private void saveAuthResult() {
		try {
			Auth auth = mAuthInterface.getToken(mFrob);
			
			/* store authentication token */
			AuthStore store = null;
			try {
				store = new FileAuthStore(getFilesDir());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			store.store(auth);
			
			mToken = auth.getToken();
			if (DEBUG) {
				Log.d(LOGTAG, "Authentication success");
				// This token can be used until the user revokes it.
				Log.d(LOGTAG, "Token: " + mToken);//$NON-NLS-1$
				Log.d(LOGTAG, "nsid: " + auth.getUser().getId()); //$NON-NLS-1$
				Log.d(LOGTAG, "Realname: " + auth.getUser().getRealName()); //$NON-NLS-1$
				Log.d(LOGTAG, "Username: " + auth.getUser().getUsername()); //$NON-NLS-1$
				Log.d(LOGTAG, "Permission: " + auth.getPermission().getType()); //$NON-NLS-1$
			}
		} catch (FlickrException e) {
			if (DEBUG)
				Log.d(LOGTAG, "Authentication failed"); //$NON-NLS-1$
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}