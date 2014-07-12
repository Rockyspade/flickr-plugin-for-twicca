package net.itsuha.flickr_twicca.utils;

public class FlickrBaseEncoder {
	protected static final String alphabetString = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
	protected static final char[] alphabet = alphabetString.toCharArray();
	protected static final int base_count = alphabet.length;
	
	public static String encode(long num){
		String result = "";
		long div;
		int mod = 0;
		
		while (num >= base_count) {
			div = num/base_count;
			mod = (int)(num-(base_count*(long)div));
			result = alphabet[mod] + result;
			num = (long)div;
		}
		if (num>0){
			result = alphabet[(int)num] + result;
		}
		return result;
	}
	
	public static long decode(String link){
			long result= 0;
			long multi = 1;
			while (link.length() > 0) {
				String digit = link.substring(link.length()-1);
				result = result + multi * alphabetString.lastIndexOf(digit);
				multi = multi * base_count;
				link = link.substring(0, link.length()-1);
			}
			return result;
	}
}
