package net.itsuha.flickr_twicca.utils;


import android.net.Uri;
import android.util.Log;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.galleries.GalleriesInterface;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

import javax.xml.parsers.ParserConfigurationException;

public final class FlickrHelper {

	private static final String API_KEY = "9221b3ce58d5ce9b7dfefd9e00c028d7";
	public static final String API_SEC = "92922591579884f2";

	public static Flickr getFlickr() {
		try {
			Flickr f = new Flickr(API_KEY, API_SEC, new REST());
			return f;
		} catch (ParserConfigurationException e) {
			return null;
		}
	}

	public static Flickr getFlickr(String token, String secret) {
		Flickr f = getFlickr();
		RequestContext requestContext = RequestContext.getRequestContext();
		OAuth auth = new OAuth();
		auth.setToken(new OAuthToken(token, secret));
		requestContext.setOAuth(auth);
		return f;
	}

	public static InterestingnessInterface getInterestingInterface() {
		Flickr f = getFlickr();
		if (f != null) {
			return f.getInterestingnessInterface();
		} else {
			return null;
		}
	}

	public static PhotosInterface getPhotosInterface() {
		Flickr f = getFlickr();
		if (f != null) {
			return f.getPhotosInterface();
		} else {
			return null;
		}
	}

	public static GalleriesInterface getGalleryInterface() {
		Flickr f = getFlickr();
		if (f != null) {
			return f.getGalleriesInterface();
		} else {
			return null;
		}
	}

    public static Uri getShortUrl(String photoId){

        String shortId = FlickrBaseEncoder.encode(Long.valueOf(photoId));
        String shortUrl = "http://flic.kr/p/" + shortId;
        return Uri.parse(shortUrl);
    }

}
