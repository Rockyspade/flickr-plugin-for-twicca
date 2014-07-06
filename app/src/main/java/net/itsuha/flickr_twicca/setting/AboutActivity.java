package net.itsuha.flickr_twicca.setting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.itsuha.flickr_twicca.R;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		TextView notice = (TextView) findViewById(R.id.label_notice);
		notice.setText(readAsset("NOTICE"));
		TextView version = (TextView) findViewById(R.id.label_version);
		version.setText("Version: " + readVersion());
		Button showLicenseBtn = (Button) findViewById(R.id.btn_show_license);
		showLicenseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView license = (TextView) findViewById(R.id.label_license);
				license.setVisibility(View.VISIBLE);
				Handler handler = new Handler();
				Runnable scrollTask = new Runnable() {
					@Override
					public void run() {
						ScrollView scroll = (ScrollView) findViewById(R.id.about_scroll_view);
						scroll.smoothScrollBy(0, 160);
					}
				};
				handler.postDelayed(scrollTask, 100);
			}
		});
		ReadLicenseTask task = new ReadLicenseTask();
		task.execute(null, null);
	}

	private String readVersion() {
		String versionName = null;
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = null;
			info = pm.getPackageInfo("net.itsuha.flickr_twicca", 0);
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	private String readAsset(String fileName) {
		AssetManager manager = getAssets();
		InputStream is = null;
		try {
			is = manager.open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String notice = "";
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				notice += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return notice;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return notice;
	}

	private class ReadLicenseTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			TextView license = (TextView) findViewById(R.id.label_license);
			license.setText(readAsset("LICENSE"));
			return null;
		}

	}

}
