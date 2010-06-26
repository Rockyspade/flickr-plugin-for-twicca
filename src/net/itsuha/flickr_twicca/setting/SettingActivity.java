package net.itsuha.flickr_twicca.setting;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.photosets.Photosets;

import net.itsuha.flickr_twicca.R;
import net.itsuha.flickr_twicca.util.DownloadIconTask;
import net.itsuha.flickr_twicca.util.PhotosetsUtil;
import net.itsuha.flickr_twicca.util.SettingManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import static net.itsuha.flickr_twicca.util.LogConfig.DEBUG;

public class SettingActivity extends Activity {
	private TreeMap<String, String> mSetsMap = null;
	private static final String LOGTAG = "SettingActivity";
	public static final String ICON_NAME = "icon.dat";
	public static final String ICON = "icon";
	private ImageView mIconView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
//		mIconView = (ImageView) findViewById(R.id.img_icon);

		SettingManager.getInstance().initialize(this);
		prepareUserAccount();
		prepareSetsPart();
	}

	public ImageView getIconView() {
		return mIconView;
	}

	/**
	 * Displays the icon and the user name and sets event listener.
	 */
	private void prepareUserAccount() {
		Button button = (Button) findViewById(R.id.new_account_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						AuthActivity.class);
				startActivity(intent);
			}
		});

		prepareUserInfomation();

	}

	private void prepareUserInfomation() {
		Auth auth = SettingManager.getInstance().getAuth();
		if (auth == null)
			return;
		User user = auth.getUser();
		TextView tv = (TextView) findViewById(R.id.label_user_name);
		tv.setText(user.getUsername());

		/*
		// set icon
		if(DEBUG){
			Log.d(LOGTAG, "buddyIconURL: "+user.getBuddyIconUrl());
			Log.d(LOGTAG, "getIconFarm: "+user.getIconFarm());
			Log.d(LOGTAG, "getIconServer: "+user.getIconServer());
		}
		try {
			FileInputStream fis = openFileInput(ICON_NAME);
			Drawable icon = Drawable.createFromStream(fis, ICON);
			mIconView.setImageDrawable(icon);
		} catch (FileNotFoundException e) {
			DownloadIconTask task = new DownloadIconTask(this);
			task.execute(user.getBuddyIconUrl());
		}
		*/
	}

	private void prepareSetsPart() {
		Spinner setsSpinner = (Spinner) findViewById(R.id.sets_spinner);
		PhotosetsUtil util = new PhotosetsUtil(this);
		TreeMap<String, String> setsMap = util.getPhotosetsFromCache();
		if (setsMap != null)
			updateSpinnerWithMap(setsSpinner, setsMap);
		setsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			/**
			 * save selected item
			 */
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (DEBUG) {
					;
					Log.d(LOGTAG, "selected: "
							+ parent.getAdapter().getItem(position));
				}
				String sid = resolveIdByTitle((String) parent.getAdapter()
						.getItem(position), mSetsMap);
				SettingManager.getInstance().saveDefaultSetsId(sid);
				if (DEBUG) {
					Log.d(LOGTAG, "saved value: "
							+ SettingManager.getInstance().getDefaultSetsId());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		Button updateButton = (Button) findViewById(R.id.update_button);
		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateSetsSpinnerWithFlickr();
			}
		});
	}

	private String[] updateSpinnerWithMap(Spinner spinner,
			final TreeMap<String, String> setsMap) {
		mSetsMap = setsMap;
		final String[] setsArray = setsMap.values().toArray(new String[0]);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, setsArray);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		// TODO: make speficied sets selected
		String defaultSetId = SettingManager.getInstance().getDefaultSetsId();
		int defaultPosition = 0;
		for (String setsId : setsMap.keySet()) {
			if (setsId.equals(defaultSetId))
				break;
			defaultPosition++;
		}
		spinner.setSelection(defaultPosition);
		return setsArray;
	}

	private void updateSetsSpinnerWithFlickr() {
		Spinner setsSpinner = (Spinner) findViewById(R.id.sets_spinner);
		PhotosetsUtil util = new PhotosetsUtil(this);
		Collection<Photoset> sets = util.getPhotosetsFromFlickr();
		if (sets == null)
			return;
		TreeMap<String, String> setsMap = new TreeMap<String, String>();
		if (sets != null) {
			for (Photoset set : sets) {
				setsMap.put(set.getId(), set.getTitle());
			}
		}
		// add blank for no setting
		setsMap.put(SettingManager.BLANK_SETS_ID, "blank");
		updateSpinnerWithMap(setsSpinner, setsMap);
		util.savePhotosets(setsMap);
	}

	private String resolveIdByTitle(String title,
			TreeMap<String, String> setsMap) {
		for (String id : setsMap.keySet()) {
			if (title.equals(setsMap.get(id)))
				return id;
		}
		return null;
	}
}
