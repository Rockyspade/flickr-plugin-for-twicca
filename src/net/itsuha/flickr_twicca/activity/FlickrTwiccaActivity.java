package net.itsuha.flickr_twicca.activity;

import net.itsuha.flickr_twicca.R;
import net.itsuha.flickr_twicca.R.layout;
import android.app.Activity;
import android.os.Bundle;

public class FlickrTwiccaActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);
    }
}