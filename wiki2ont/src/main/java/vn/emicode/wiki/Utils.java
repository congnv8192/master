package vn.emicode.wiki;

public class Utils {
	public static String toWikiUrl(String name) {
		return name.replaceAll(" ", "_");
	}
}
