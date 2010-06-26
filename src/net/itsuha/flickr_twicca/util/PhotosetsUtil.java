package net.itsuha.flickr_twicca.util;

import static net.itsuha.flickr_twicca.util.LogConfig.DEBUG;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.photosets.PhotosetsInterface;

public class PhotosetsUtil {
	private static Auth mAuth = null;
	private static PhotosetsInterface mPhotosetsIf = null;
	private Context mCtx;
	private static final String LOGTAG = "PhotosetsUtil";
	private static final String SAVE_FILE_NAME = "sets.dat";

	public PhotosetsUtil(Context ctx) {
		mCtx = ctx;
	}

	@SuppressWarnings("unchecked")
	public Collection<Photoset> getPhotosetsFromFlickr() {
		String usrId;
		if (mAuth == null) {
			Auth auth = SettingManager.getInstance().getAuth();
			if (auth == null)
				return null;
			else
				usrId = auth.getUser().getId();
		} else {
			usrId = mAuth.getUser().getId();
		}
		if (mPhotosetsIf == null) {
			AppProperties prop = AppProperties.getInstance();
			Flickr flickr;
			try {
				flickr = new Flickr(prop.getApiKey(), prop.getSecret(),
						new REST());
			} catch (ParserConfigurationException e) {
				if (DEBUG)
					Log.d(LOGTAG, "ParserConfigurationException");
				return null;
			}
			mPhotosetsIf = flickr.getPhotosetsInterface();
		}
		try {
			return mPhotosetsIf.getList(usrId).getPhotosets();
		} catch (IOException e) {
			if (DEBUG)
				Log.d(LOGTAG, "IOException");
			return null;
		} catch (SAXException e) {
			if (DEBUG)
				Log.d(LOGTAG, "SAXException");
			return null;
		} catch (FlickrException e) {
			if (DEBUG)
				Log.d(LOGTAG, "FlickrException");
			return null;
		}
	}

	public void savePhotosets(TreeMap<String, String> setsMap) {
		try {
			FileOutputStream fos = mCtx.openFileOutput(SAVE_FILE_NAME,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(setsMap);
			oos.close();
			fos.close();
		} catch (IOException e) {
			if (DEBUG)
				Log.d(LOGTAG, "savePhotosets(): IOException");
		}
	}

	@SuppressWarnings( { "unchecked", "finally" })
	public TreeMap<String, String> getPhotosetsFromCache() {
		TreeMap<String, String> setsMap = null;
		try {
			FileInputStream fis = mCtx.openFileInput(SAVE_FILE_NAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			setsMap = (TreeMap<String, String>) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException e) {
			if (DEBUG)
				Log.d(LOGTAG, "savePhotosets(): IOException");
		} catch (ClassNotFoundException e) {
			if (DEBUG)
				Log.d(LOGTAG, "savePhotosets(): ClassNotFoundException");
		} finally {
			return setsMap;
		}

	}
}
