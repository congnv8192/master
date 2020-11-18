package wiki2ont.wiki;

import java.nio.charset.StandardCharsets;

import info.bliki.wiki.filter.Encoder;

public class Utils {
	public static String toWikiUrl(String name) {
		// TODO: escape URL , (...
		
		return name.replaceAll(" ", "_");
//		return Encoder.encodeTitleToUrl(name, false);
	}
	
	public static String paramToUTF8(String s) {
		byte[] bytes = s.getBytes(StandardCharsets.ISO_8859_1);
		return new String(bytes, StandardCharsets.UTF_8);
	}
}
