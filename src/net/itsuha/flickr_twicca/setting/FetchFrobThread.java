package net.itsuha.flickr_twicca.setting;

import static net.itsuha.flickr_twicca.util.LogConfig.DEBUG;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import net.itsuha.flickr_twicca.util.AppProperties;

import org.xml.sax.SAXException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;


public class FetchFrobThread extends Thread {
	private static final String LOGTAG = "UploadThread";
	private Handler mHandler;
/*
	public FetchFrobThread(Handler handler) {
		mHandler = handler;
	}

	@Override
	public void run() {
		URL url = buildAuthUrl();
		Bundle bundle = new Bundle();
		Message msg = new Message();
		if (url != null) {
			bundle.putString(AuthActivity.URL, url.toString());
		} else {
			bundle.putString(AuthActivity.URL, null);
		}
			msg.setData(bundle);
			mHandler.sendMessage(msg);
	}

	private URL buildAuthUrl() {
		AppProperties prop = AppProperties.getInstance();
		String apiKey = prop.getApiKey();
		String secret = prop.getSecret();
		Flickr flickr;
		try {
			flickr = new Flickr(apiKey, secret, new REST());
		} catch (ParserConfigurationException e1) {
			return null;
		}
		Flickr.debugStream = false;
		RequestContext requestContext;
		requestContext = RequestContext.getRequestContext();
		AuthInterface authInterface = flickr.getAuthInterface();
		String frob = "";
		try {
			frob = authInterface.getFrob();
		} catch (FlickrException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (SAXException e) {
			return null;
		}
		if (DEBUG)
			Log.d(LOGTAG, "frob: " + frob); //$NON-NLS-1$
		URL authUrl;
		try {
			authUrl = authInterface.buildAuthenticationUrl(Permission.WRITE,
					frob);
		} catch (MalformedURLException e) {
			return null;
		}
		return authUrl;
	}
	*/

}
