package vn.emicode.wiki;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import info.bliki.wiki.dump.InfoBox;
import info.bliki.wiki.dump.WikiPatternMatcher;

public class PatternMatcher {

	private WikiPatternMatcher matcher;
	private InfoBox infoBox;
	private String summary;

	public PatternMatcher(String text) {
		this.matcher = new WikiPatternMatcher(text);
	}

	public boolean isRedirect() {
		return this.matcher.isRedirect();
	}

	public String getRedirectText() {
		return this.matcher.getRedirectText();
	}

	public InfoBox getInfoBox() {
		if (infoBox == null) {
			infoBox = this.matcher.getInfoBox();
		}

		return this.infoBox;
	}
	
	public String getSummary() {
		if (summary == null) {
			summary = trim(getContent(), 200);
		}
		
	    return this.summary;
	}
	
	public String trim(String src, int size) {
	    if (src.length() <= size) return src;
	    int pos = src.lastIndexOf(" ", size - 3);
	    if (pos < 0) return src.substring(0, size);
	    return src.substring(0, pos) + "...";
	} 

	public String getContent() {
//		Matcher m = Pattern.compile("/\\{\\{(.*)\\}\\}/gmsu").matcher(this.matcher.getText());
//		while (m.find()) {
//			System.out.println(m.group());
//		}

		StringBuilder sb = new StringBuilder(this.matcher.getPlainText());

		String WIKI_TAG_CONST_STR = "{{";
		int startPos = sb.indexOf(WIKI_TAG_CONST_STR);
		
		while (startPos >= 0) {
			int bracketCount = 2;
	        int endPos = startPos + WIKI_TAG_CONST_STR.length();

	        for (; endPos < sb.length(); endPos++) {
	            switch (sb.charAt(endPos)) {
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
	        
	        sb.replace(startPos, endPos+1, "");
	        
	        // update startPos
	        startPos = sb.indexOf(WIKI_TAG_CONST_STR);
		}
		
		String contentText = sb.toString().trim();
		// strip any html formatting
        contentText = contentText.replaceAll("&gt;", ">");
        contentText = contentText.replaceAll("&lt;", "<");
        contentText = contentText.replaceAll("<ref.*?>.*?</ref>", " ");
        contentText = contentText.replaceAll("</?.*?>", " ");

        return contentText;
	}

	/**
	 * @requires this.infobox neq null
	 */
	public String getInfoBoxTemplate() {
		Scanner scanner = new Scanner(this.infoBox.dumpRaw().trim());// remove empty lines
		String line = scanner.nextLine(); // first line
		
		line = line.replace("{{", "");
		
		// in case of invalid infobox format
		if (line.indexOf('|') > 0) {
			line = line.substring(0, line.indexOf('|'));
		}
		
		scanner.close();

		return line;
	}

	public Map<String, String> getInfoBoxAttributes() {
		Scanner scanner = new Scanner(this.infoBox.dumpRaw());
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
				value = line.substring(index + 1).trim();
				attributes.put(attribute, value);

//				System.out.println(attribute + "=" + value);
			} else if (attribute != null) { // multiple line attribute values
				value += ", " + line;
				attributes.put(attribute, value);

//				System.out.println(attribute + "=" + value);
			}
		}

		scanner.close();
		return attributes;
	}
}
