package net.itsuha.flickr_twicca.setting;

import android.os.Handler;


public class FetchFrobThread extends Thread {
	private static final String LOGTAG = "UploadThread";
	private Handler mHandler;
/*
	public FetchFrobThread(Handler handler) {
		mHandler = handler;
	}

	@Override
	public void run() {
		URL url = buildAuthUrl();
		Bundle bundle = new Bundle();
		Message msg = new Message();
		if (url != null) {
			bundle.putString(AuthActivity.URL, url.toString());
		} else {
			bundle.putString(AuthActivity.URL, null);
		}
			msg.setData(bundle);
			mHandler.sendMessage(msg);
	}

	private URL buildAuthUrl() {
		AppProperties prop = AppProperties.getInstance();
		String apiKey = prop.getApiKey();
		String secret = prop.getSecret();
		Flickr flickr;
		try {
			flickr = new Flickr(apiKey, secret, new REST());
		} catch (ParserConfigurationException e1) {
			return null;
		}
		Flickr.debugStream = false;
		RequestContext requestContext;
		requestContext = RequestContext.getRequestContext();
		AuthInterface authInterface = flickr.getAuthInterface();
		String frob = "";
		try {
			frob = authInterface.getFrob();
		} catch (FlickrException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (SAXException e) {
			return null;
		}
		if (DEBUG)
			Log.d(LOGTAG, "frob: " + frob); //$NON-NLS-1$
		URL authUrl;
		try {
			authUrl = authInterface.buildAuthenticationUrl(Permission.WRITE,
					frob);
		} catch (MalformedURLException e) {
			return null;
		}
		return authUrl;
	}
	*/

}
