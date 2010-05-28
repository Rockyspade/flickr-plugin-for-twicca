package net.itsuha.flickr_twicca.upload;

import static net.itsuha.flickr_twicca.util.LogConfig.DEBUG;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UploadActivity extends Activity {
	private static final String LOGTAG = "UploadActivity";
	/** Label for receiving photo URL */
	public static final String URL = "URL";
	private ProgressDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.upload);

		showProgressDialog();

		Intent callIntent = getIntent();
		Uri fileUri = callIntent.getData();
		String tweet = callIntent.getStringExtra(Intent.EXTRA_TEXT);
		UploadThread upThread = new UploadThread(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				Uri photoUrl = bundle.getParcelable(URL);
				if (DEBUG)
					Log.d(LOGTAG, "result: " + photoUrl);
				if (photoUrl != null) {
					Intent returnIntent = new Intent();
					returnIntent.setData(photoUrl);
					setResult(Activity.RESULT_OK, returnIntent);
				}
				dismissProgressDialog();
				finish();
			}

		}, this, fileUri, tweet);
		upThread.start();
	}

	private void showProgressDialog() {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle("アップロードしています");
		mDialog.setMessage("しばらくお待ちください");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.show();
	}

	private void dismissProgressDialog() {
		mDialog.dismiss();
	}
}
