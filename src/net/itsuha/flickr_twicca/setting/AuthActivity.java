package net.itsuha.flickr_twicca.setting;

import static net.itsuha.flickr_twicca.util.LogConfig.DEBUG;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import javax.xml.parsers.ParserConfigurationException;

import net.itsuha.flickr_twicca.R;
import net.itsuha.flickr_twicca.util.AppProperties;
import net.itsuha.flickr_twicca.util.SettingManager;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class AuthActivity extends Activity {
	private AuthInterface mAuthInterface;
	private String mFrob = "";
	private Auth mAuth;
	private static final String LOGTAG = "AuthActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);

		try {
			authenticate();
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
				try {
					saveAuthResult();
					showAuthResultDialog();
				} catch (Exception e) {
					showAuthErrorDialog();
				}
			}
		});
	}

	private void authenticate() throws IOException, SAXException,
			ParserConfigurationException {
		AppProperties prop = AppProperties.getInstance();
		String apiKey = prop.getApiKey();
		String secret = prop.getSecret();
		Flickr flickr = new Flickr(apiKey, secret, new REST());
		Flickr.debugStream = false;
		RequestContext requestContext;
		requestContext = RequestContext.getRequestContext();
		mAuthInterface = flickr.getAuthInterface();
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
			Log.d(LOGTAG, "Authentication Sucess");
			Log.d(LOGTAG, authUrl.toString()); //$NON-NLS-1$
		}
	}

	private void saveAuthResult() throws IOException, SAXException,
			FlickrException {
		Auth auth = mAuthInterface.getToken(mFrob);
		mAuth = auth;

		/* store authentication token */
		SettingManager.getInstance().saveAuth(auth);
	}

	private void showAuthResultDialog() {
		Auth auth = mAuth;
		String title = getString(R.string.title_login_success);
		String message = MessageFormat.format(getString(R.string.msg_logined_as) , auth.getUser().getUsername());
		showDialog(title, message);
	}

	private void showAuthErrorDialog() {
		String title = getString(R.string.title_error);
		String message = getString(R.string.msg_auth_failure);
		showDialog(title, message);
	}
	
	private void showDialog(String title, String message){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alertDialogBuilder.setCancelable(false);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		
	}
}