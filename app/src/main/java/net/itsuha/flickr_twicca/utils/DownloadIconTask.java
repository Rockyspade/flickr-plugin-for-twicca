package net.itsuha.flickr_twicca.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import net.itsuha.flickr_twicca.setting.SettingActivity;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadIconTask extends AsyncTask<String, Integer, Boolean> {
	private SettingActivity mCaller;

	public DownloadIconTask(SettingActivity caller) {
		mCaller = caller;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		HttpClient c = new DefaultHttpClient();
		HttpGet g = new HttpGet(params[0]);
		try {
			HttpResponse response = c.execute(g);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != HttpURLConnection.HTTP_OK) {
				Log.e("Connection.download", "download failed");
			} else {
				InputStream is = response.getEntity().getContent();
				FileOutputStream fos = mCaller.openFileOutput(
						SettingActivity.ICON_NAME, Context.MODE_PRIVATE);
				byte[] buffer = new byte[512];
				int length = 0;
				while ((length = is.read(buffer)) > 0) {
					fos.write(buffer, 0, length);
				}
				is.close();
				fos.close();
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		ImageView iconView = mCaller.getIconView();
		FileInputStream fis;
		try {
			fis = mCaller.openFileInput(SettingActivity.ICON_NAME);
		} catch (FileNotFoundException e) {
			return;
		}
		Drawable icon = Drawable.createFromStream(fis, SettingActivity.ICON);
		iconView.setImageDrawable(icon);
	}

}
