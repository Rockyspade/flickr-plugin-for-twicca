package net.itsuha.flickr_twicca.upload;

import static net.itsuha.flickr_twicca.util.LogConfig.DEBUG;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.itsuha.flickr_twicca.util.AppProperties;
import net.itsuha.flickr_twicca.util.FlickrBaseEncoder;
import net.itsuha.flickr_twicca.util.SettingManager;

import org.xml.sax.SAXException;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.photosets.PhotosetsInterface;
import com.aetrion.flickr.uploader.UploadMetaData;
import com.aetrion.flickr.uploader.Uploader;

public class UploadThread extends Thread {
	private static final String LOGTAG = "UploadThread";
	private Handler mHandler;
	private Context mCtx;
	private Uri mFileUri;
	private String mTweet;
	private boolean mCancelFlag = false;
	private String mPhotoId;
	private Flickr mFlickr;

	public UploadThread(Handler handler, Context ctx, Uri fileUri, String tweet) {
		mHandler = handler;
		mCtx = ctx;
		mFileUri = fileUri;
		mTweet = tweet;
	}

	@Override
	public void run() {
		Uri result = upload(mFileUri, mTweet);
		if(mCancelFlag){
			return;
		}
		Bundle bundle = new Bundle();
		Message msg = new Message();
		if (result != null) {
			bundle.putParcelable(UploadActivity.URL, result);
			bundle.putInt(UploadActivity.STATUS, UploadActivity.SUCCESS);
		} else {
			bundle.putInt(UploadActivity.STATUS, UploadActivity.FAILURE);
		}
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}


	@SuppressWarnings("deprecation")
	private Uri upload(Uri fileUri, String tweet) {
		String apiKey = AppProperties.getInstance().getApiKey();
		String secret = AppProperties.getInstance().getSecret();

		Auth auth = SettingManager.getInstance().getAuth();
		RequestContext.getRequestContext().setAuth(auth);

		Flickr flickr = new Flickr(apiKey, secret, new Flickr(apiKey)
				.getTransport());
		mFlickr = flickr;
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
			ins = mCtx.getContentResolver().openInputStream(fileUri);

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
		mPhotoId = photoId;
		if(mCancelFlag)return null;
		
		//add the photo to the specified sets
		addPhoto2Sets();
		
		String shortId = FlickrBaseEncoder.encode(Long.valueOf(photoId));
		String shortUrl = "http://flic.kr/p/" + shortId;
		if (DEBUG) {
			Log.d(LOGTAG, "upload completed");
			Log.d(LOGTAG, "photo id: " + photoId);
			Log.d(LOGTAG, "short id: " + shortUrl);
		}
		return Uri.parse(shortUrl);
	}

	@SuppressWarnings("deprecation")
	private void addPhoto2Sets() {
		String setsId = SettingManager.getInstance().getDefaultSetsId();
		if(!setsId.equals(SettingManager.BLANK_SETS_ID)){
			PhotosetsInterface psif = mFlickr.getPhotosetsInterface();
			try {
				psif.addPhoto(setsId, mPhotoId);
			} catch (IOException e) {
			} catch (SAXException e) {
			} catch (FlickrException e) {
			}
		}
	}

	public void cancel() {
		mCancelFlag = true;
	}
}
