package vn.emicode.wiki;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

public class WikiPatternMatcher {

	/**
	 * Strip wiki formatting characters from the given wiki text.
	 *
	 * @return
	 * @throws IOException
	 */
    public static String getPlainText(String wikiText) {
//    	TODO: không ổn khi có {{ }} 
        String text = wikiText.replaceAll("&gt;", ">");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&nbsp;", " ");
        text = text.replaceAll("<ref>.*?</ref>", " ");
        text = text.replaceAll("</?.*?>", " ");
        text = text.replaceAll("\\{\\{.*?\\}\\}", " ");
        text = text.replaceAll("\\[\\[.*?:.*?\\]\\]", " ");
        text = text.replaceAll("\\[\\[(.*?)\\]\\]", "$1");
        text = text.replaceAll("\\s(.+?)\\|(\\w+\\s)", " $2");
        text = text.replaceAll("\\[.*?\\]", " ");
        text = text.replaceAll("\\'+", "");
        return text;
    }

//	public static String getPlainText(String wikiText) throws IOException {
//		WikiModel wikiModel = new WikiModel("https://www.mywiki.com/wiki/${image}",
//				"https://www.mywiki.com/wiki/${title}");
//		return wikiModel.render(new PlainTextConverter(false), wikiText);
//	}

	/**
	 * Parse the Infobox template (i.e. parsing a string starting with
	 * &quot;{{Infobox&quot; and ending with &quot;}}&quot;)
	 *
	 * @return <code>null</code> if the Infobox template wasn't found.
	 */
	public static InfoBox parseInfoBox(String wikiText) {
		String INFOBOX_CONST_STR = "{{Infobox";
		int startPos = wikiText.indexOf(INFOBOX_CONST_STR);
		if (startPos < 0)
			return null;
		int bracketCount = 2;
		int endPos = startPos + INFOBOX_CONST_STR.length();

		if (endPos >= wikiText.length()) {
			return null;
		}
		for (; endPos < wikiText.length(); endPos++) {
			switch (wikiText.charAt(endPos)) {
			case '}':
				bracketCount--;
				break;
			case '{':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0)
				break;
		}
		String infoBoxText;
		if (endPos >= wikiText.length()) {
			infoBoxText = wikiText.substring(startPos);
		} else {
			infoBoxText = wikiText.substring(startPos, endPos + 1);
		}
		infoBoxText = stripCite(infoBoxText); // strip clumsy {{cite}} tags

		// strip any html formatting
		infoBoxText = infoBoxText.replaceAll("&gt;", ">");
		infoBoxText = infoBoxText.replaceAll("&lt;", "<");
		infoBoxText = infoBoxText.replaceAll("<ref.*?>.*?</ref>", " ");
		infoBoxText = infoBoxText.replaceAll("</?.*?>", " ");

		return new InfoBox(infoBoxText);
	}

	public static String stripCite(String text) {
		String CITE_CONST_STR = "{{cite";
		int startPos = text.indexOf(CITE_CONST_STR);
		if (startPos < 0)
			return text;
		
		int bracketCount = 2;
		int endPos = startPos + CITE_CONST_STR.length();
		for (; endPos < text.length(); endPos++) {
			switch (text.charAt(endPos)) {
			case '}':
				bracketCount--;
				break;
			case '{':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0)
				break;
		}
		text = text.substring(0, startPos - 1) + text.substring(endPos);
		return stripCite(text);
	}
	
	public static String parseInfoBoxTemplate(String infoBoxText) {
		Scanner scanner = new Scanner(infoBoxText);
		String line = scanner.nextLine(); // first line
		
		line = line.replace("{{", "");
		scanner.close();
		
		return line;
	}
	
	public static Map<String, String> parseInfoBoxAttributes(String infoBoxText) {
		infoBoxText = getPlainText(infoBoxText);
		
		Scanner scanner = new Scanner(infoBoxText);
		Map<String, String> attributes = new HashMap<>();
		String attribute = null;
		String value = null;
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			
			if (line.isBlank()) {
				continue;
			}
			
			if (line.equals("}}")) {
				break;
			}
			
			if (line.startsWith("|")) { // new 
				int index = line.indexOf('=');
				attribute = line.substring(1, index).trim();
				value = line.substring(index+1).trim();
				attributes.put(attribute, value);
				
				System.out.println(attribute + "=" + value);
			} else if (attribute != null) { // multiple line attribute values
				value += ", " + line;
				attributes.put(attribute, value);
				
				System.out.println(attribute + "=" + value);
			}
		}
		
		scanner.close();
		return attributes;
	}
}
