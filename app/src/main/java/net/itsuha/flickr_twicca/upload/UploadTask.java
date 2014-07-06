package net.itsuha.flickr_twicca.upload;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.googlecode.flickrjandroid.uploader.Uploader;

import net.itsuha.flickr_twicca.utils.FlickrHelper;
import net.itsuha.flickr_twicca.utils.MyApp;
import net.itsuha.flickr_twicca.utils.PreferenceManager;

import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import static net.itsuha.flickr_twicca.BuildConfig.DEBUG;

public class UploadTask extends AsyncTask<UploadTask.Param, Void, String> {

    public static class Param {
        public Uri file;
        public String tweet;

        public Param(Uri file, String tweet) {
            this.file = file;
            this.tweet = tweet;
        }
    }

    interface Callback {
        void onUploadSuccess(String photoId);
        void onUploadFail();
    }

	private static final String LOGTAG = "UploadTask";
	private boolean mCancelFlag = false;

    private WeakReference<Callback> mReference;

	public UploadTask(Callback callback) {
        mReference = new WeakReference<Callback>(callback);
	}

//	@SuppressWarnings("deprecation")
//	private void addPhoto2Sets() {
//		String setsId = PreferenceManager.getInstance().getDefaultSetsId();
//		if(!setsId.equals(PreferenceManager.BLANK_SETS_ID)){
//			PhotosetsInterface psif = mFlickr.getPhotosetsInterface();
//			try {
//				psif.addPhoto(setsId, mPhotoId);
//			} catch (IOException e) {
//			} catch (SAXException e) {
//			} catch (FlickrException e) {
//			}
//		}
//	}

	public void cancel() {
		mCancelFlag = true;
	}

    @Override
    protected String doInBackground(Param... uris) {
        final Uri fileUri = uris[0].file;
        final String tweet = uris[0].tweet;
        OAuth auth = PreferenceManager.getInstance().loadSavedOAuth();
		RequestContext.getRequestContext().setOAuth(auth);
		Flickr flickr = FlickrHelper.getFlickr();
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
			ins = MyApp.getInstance().getContentResolver().openInputStream(fileUri);

		} catch (FileNotFoundException e) {
			if (DEBUG) {
				Log.d(LOGTAG, "file not found.");
			}
		}

		String photoId = "";
		try {
			photoId = uploader.upload("temp", ins, metadata);
		} catch (IOException e) {
            if (DEBUG) {
                Log.w(LOGTAG, "Upload failed", e);
            }
            return null;
		} catch (FlickrException e) {
            if (DEBUG) {
                Log.w(LOGTAG, "Upload failed", e);
            }
			return null;
		} catch (SAXException e) {
            if (DEBUG) {
                Log.w(LOGTAG, "Upload failed", e);
            }
			return null;
		}
        return photoId;
    }

    @Override
    protected void onPostExecute(String photoId) {
        super.onPostExecute(photoId);
        Callback callback = mReference.get();
        if(callback != null){
            if(TextUtils.isEmpty(photoId)){
                callback.onUploadFail();
            }else{
                callback.onUploadSuccess(photoId);
            }
        }
    }
}
