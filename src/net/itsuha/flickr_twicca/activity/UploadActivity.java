package net.itsuha.flickr_twicca.activity;

import static net.itsuha.flickr_twicca.util.LogConfig.DEBUG;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.itsuha.flickr_twicca.R;
import net.itsuha.flickr_twicca.util.AppProperties;
import net.itsuha.flickr_twicca.util.FlickrBaseEncoder;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.uploader.UploadMetaData;
import com.aetrion.flickr.uploader.Uploader;
import com.aetrion.flickr.util.AuthStore;
import com.aetrion.flickr.util.FileAuthStore;

public class UploadActivity extends Activity {
	private static final String LOGTAG = "UploadActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);

		Intent callIntent = getIntent();
		Uri fileUri = callIntent.getData();
		String tweet = callIntent.getStringExtra(Intent.EXTRA_TEXT);
		Uri result = upload(fileUri, tweet);
		if(DEBUG)
			Log.d(LOGTAG, "result: "+result.toString());

		if (result != null) {
			Intent returnIntent = new Intent();
			returnIntent.setData(result);
			setResult(Activity.RESULT_OK, returnIntent);
		}
		finish();
	}

	private Uri upload(Uri fileUri, String tweet) {
		String apiKey = AppProperties.getInstance().getApiKey();
		String secret = AppProperties.getInstance().getSecret();

		Auth auth = retrieveToken();
		RequestContext.getRequestContext().setAuth(auth);

		Flickr flickr = new Flickr(apiKey, secret, new Flickr(apiKey)
				.getTransport());
		UploadMetaData metadata = new UploadMetaData();
		metadata.setTitle(tweet);
		metadata.setPublicFlag(true);
		Uploader uploader = flickr.getUploader();

		/*
		 * content://media/external/images/media/52
		 */
		if (DEBUG) {
			Log.d(LOGTAG, "fileUri: " + fileUri.toString());
		}
		InputStream ins = null;
		try {
			ins = this.getContentResolver().openInputStream(fileUri);

		} catch (FileNotFoundException e) {
			if (DEBUG) {
				Log.d(LOGTAG, "file not found.");
			}
		}

		String photoId = "";
		try {
			photoId = uploader.upload(ins, metadata);
		} catch (IOException e) {
			return null;
		} catch (FlickrException e) {
			return null;
		} catch (SAXException e) {
			return null;
		}
		String shortId = FlickrBaseEncoder.encode(Long.valueOf(photoId));
		String shortUrl = "http://flic.kr/p/" + shortId;
		if(DEBUG){
			Log.d(LOGTAG,"upload completed");
			Log.d(LOGTAG,"photo id: "+photoId);
			Log.d(LOGTAG,"short id: "+shortUrl);
		}
		return Uri.parse(shortUrl);
	}

	/**
	 *  retrieve login information from store
	 */
	private Auth retrieveToken() {
		AuthStore store = null;
		try {
			store = new FileAuthStore(getFilesDir());
		} catch (IOException e1) {
			return null;
		}
		Auth[] tokens = store.retrieveAll();
		if(tokens.length == 1)
			return tokens[0];
		else
			return null;
	}
}
