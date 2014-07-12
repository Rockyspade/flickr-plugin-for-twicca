package net.itsuha.flickr_twicca.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.people.User;

import net.itsuha.flickr_twicca.R;
import net.itsuha.flickr_twicca.setting.SettingActivity;
import net.itsuha.flickr_twicca.utils.FlickrHelper;
import net.itsuha.flickr_twicca.utils.PreferenceManager;

import static net.itsuha.flickr_twicca.BuildConfig.DEBUG;

public class UploadActivity extends Activity implements  UploadTask.Callback{
	private static final String LOGTAG = "UploadActivity";
	/** Label for receiving photo URL */
	public static final String URL = "URL";
	/** Label for upload status */
	public static final String STATUS = "STATUS";
	/** Value for upload status */
	public static final int SUCCESS = 0;
	/** Value for upload status */
	public static final int FAILURE = 1;
	private ProgressDialog mDialog = null;
	private UploadTask mTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload);

        OAuth oauth = PreferenceManager.getInstance().loadSavedOAuth();
		if (isAuthenticated(oauth)) {
            showProgressDialog();
            startUploadThread();
		} else {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
		}
	}

    private boolean isAuthenticated(OAuth oauth){
        if(oauth == null){
            if(DEBUG){
            Log.d(LOGTAG, "OAuth: null");
            }
            return false;
        }else{
            final User user = oauth.getUser();
            if(user == null){
                if(DEBUG) {
                    Log.d(LOGTAG, "OAuth: User null");
                }
                return false;
            }else{
                if(DEBUG) {
                    Log.d(LOGTAG, "OAuth: User " + user.getUsername());
                }
                return true;
            }
        }
    }

	private void startUploadThread() {
		Intent callIntent = getIntent();
		Uri fileUri = callIntent.getData();
		String tweet = callIntent.getStringExtra(Intent.EXTRA_TEXT);
		mTask = new UploadTask(this);
        mTask.execute(new UploadTask.Param(fileUri, tweet));
	}

	private void showProgressDialog() {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(getString(R.string.title_uploading));
		mDialog.setMessage(getString(R.string.msg_wait));
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
				getText(android.R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mTask.cancel();
						finish();
					}
				});
		mDialog.show();
	}

	private void dismissProgressDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

    @Override
    public void onUploadSuccess(String photoId) {
        Uri photoUrl = FlickrHelper.getShortUrl(photoId);
        if (DEBUG)
            Log.d(LOGTAG, "result: " + photoUrl);
        Intent returnIntent = new Intent();
        returnIntent.setData(photoUrl);
        setResult(Activity.RESULT_OK, returnIntent);
        dismissProgressDialog();
        finish();
    }

    @Override
    public void onUploadFail() {
        dismissProgressDialog();
        finish();
    }
}
