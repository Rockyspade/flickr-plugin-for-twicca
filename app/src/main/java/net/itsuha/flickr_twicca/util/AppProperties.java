package net.itsuha.flickr_twicca.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class AppProperties {
	private static AppProperties singleton = new AppProperties();

	private String apiKey = "";
	private String secret = "";

	private static final String BUNDLE_NAME = "net.itsuha.flickr_twicca.application"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private AppProperties() {
	}

	private static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static AppProperties getInstance() {
		if (singleton.apiKey == "") {
			singleton.apiKey = getString("apiKey");
			singleton.secret = getString("secret");
		}
		return singleton;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getSecret() {
		return secret;
	}

}
