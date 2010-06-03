package net.itsuha.flickr_twicca.upload;

import static net.itsuha.flickr_twicca.util.LogConfig.DEBUG;

import com.aetrion.flickr.auth.Auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import net.itsuha.flickr_twicca.R;
import net.itsuha.flickr_twicca.setting.AuthActivity;
import net.itsuha.flickr_twicca.util.SettingManager;

public class UploadActivity extends Activity {
	private static final String LOGTAG = "UploadActivity";
	/** Label for receiving photo URL */
	public static final String URL = "URL";
	/** Label for upload status */
	public static final String STATUS = "STATUS";
	/** Value for upload status */
	public static final int SUCCESS = 0;
	/** Value for upload status */
	public static final int FAILURE = 1;
	private ProgressDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SettingManager setting = SettingManager.getInstance();
		setting.initialize(this);
		Auth auth = setting.getAuth();
		if (auth == null) {
			Intent intent = new Intent(this, AuthActivity.class);
			startActivityForResult(intent, 0);
		} else {
			showProgressDialog();
			startUploadThread();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			showProgressDialog();
			startUploadThread();
		} else {
			finish();
		}
	}

	private void startUploadThread() {
		Intent callIntent = getIntent();
		Uri fileUri = callIntent.getData();
		String tweet = callIntent.getStringExtra(Intent.EXTRA_TEXT);
		UploadThread upThread = new UploadThread(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				int status = bundle.getInt(STATUS);
				switch (status) {
				case SUCCESS:
					Uri photoUrl = bundle.getParcelable(URL);
					if (DEBUG)
						Log.d(LOGTAG, "result: " + photoUrl);
					Intent returnIntent = new Intent();
					returnIntent.setData(photoUrl);
					setResult(Activity.RESULT_OK, returnIntent);
					dismissProgressDialog();
					finish();
					break;
				case FAILURE:
					dismissProgressDialog();
					finish();
				default:
					break;
				}
			}

		}, this, fileUri, tweet);
		upThread.start();
	}

	private void showProgressDialog() {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(getString(R.string.titile_uploading));
		mDialog.setMessage(getString(R.string.msg_wait));
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.show();
	}

	private void dismissProgressDialog() {
		mDialog.dismiss();
	}

}
