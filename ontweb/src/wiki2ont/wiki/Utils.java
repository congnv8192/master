package wiki2ont.wiki;

import info.bliki.wiki.filter.Encoder;

public class Utils {
	public static String toWikiUrl(String name) {
		// TODO: escape URL , (...
		
//		return name.replaceAll(" ", "_");
		return Encoder.encodeTitleToUrl(name, false);
	}
}
