package wiki2ont.wiki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.bliki.wiki.dump.WikiPatternMatcher;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;
import wiki2ont.AppConfig;

public class PatternMatcher {
	private WikiPatternMatcher matcher;
	private InfoBox infoBox;
	private String summary;

	private boolean redirect = false;
	private String redirectString = null;
	private List<String> pageCats = null;

	private final static Pattern REDIRECT_PATTERN = Pattern.compile("#đổi\\s+\\[\\[(.*?)\\]\\]");
	private final static Pattern CATEGORY_PATTERN = Pattern.compile("\\[\\[Thể loại:(.*?)\\]\\]", Pattern.MULTILINE);

	public PatternMatcher(String text) {
		this.matcher = new WikiPatternMatcher(text);

		// redirect
		Matcher matcher = REDIRECT_PATTERN.matcher(text);
		if (matcher.find()) {
			redirect = true;
			if (matcher.groupCount() == 1)
				redirectString = matcher.group(1);
		}
	}

	public boolean isRedirect() {
		// en || vi
		return this.matcher.isRedirect() || this.redirect;
	}

	public String getRedirectText() {
		// en
		if (this.matcher.isRedirect()) {
			return this.matcher.getRedirectText();
		}

		// vi
		return this.redirectString;
	}

	public InfoBox getInfoBox() {
		if (infoBox == null) {
			info.bliki.wiki.dump.InfoBox en = this.matcher.getInfoBox();
			if (en != null) {
				infoBox = InfoBox.create(this.matcher.getInfoBox());
			} else {
				infoBox = parseInfoBox();
			}
		}

		return this.infoBox;
	}

	private InfoBox parseInfoBox() {
		final String[] INFOBOX_CONST_STRS = { "{{Hộp thông tin", "{{Thông tin" };
		String wikiText = this.matcher.getText();

		for (String INFOBOX_CONST_STR : INFOBOX_CONST_STRS) {
			int startPos = wikiText.indexOf(INFOBOX_CONST_STR);
			if (startPos < 0)
				continue;
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
			infoBoxText = Utils.decodeHtmlEntities(infoBoxText);
			
			infoBoxText = infoBoxText.replaceAll("<ref.*?>.*?</ref>", " ");
			infoBoxText = infoBoxText.replaceAll("</?.*?>", " ");

			return new InfoBox(infoBoxText);
		}

		return null;
	}

	private String stripCite(String text) {
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

	public List<String> getCategories() {
		if (pageCats == null) {
			parseCategories();
		}

		return pageCats;
	}

	private void parseCategories() {
		pageCats = new ArrayList<>();

		// en
		pageCats.addAll(this.matcher.getCategories());

		// vi
		Matcher matcher = CATEGORY_PATTERN.matcher(this.matcher.getText());

		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");
			pageCats.add(temp[0]);
		}
	}

	public String getSummary() {
		if (summary == null) {
			Scanner scanner = new Scanner(getContent());
			try {
				String line = scanner.nextLine();
				scanner.close();
				
				summary = trim(line, AppConfig.SUMMARY_LENGTH);

			} catch (Exception e) {
				e.printStackTrace();
				
				System.out.println(getPlainText(this.matcher.getText()));
			}

		}

		return this.summary;
	}

	public String trim(String src, int size) {
		if (src.length() <= size)
			return src;
		int pos = src.lastIndexOf(" ", size - 3);
		if (pos < 0)
			return src.substring(0, size);
		return src.substring(0, pos) + "...";
	}

	public String getContent() {
		StringBuilder sb = new StringBuilder(getPlainText(this.matcher.getText()));

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

			sb.replace(startPos, endPos + 1, "");

			// update startPos
			startPos = sb.indexOf(WIKI_TAG_CONST_STR);
		}
		
		String contentText = sb.toString().trim();
		// strip any html formatting
//		contentText = Utils.decodeHtmlEntities(contentText);
//		
//		contentText = contentText.replaceAll("<ref.*?>.*?</ref>", " ");
//		contentText = contentText.replaceAll("</?.*?>", " ");

		return contentText;
	}
	
	public String getPlainText(String text) {
		text = Utils.decodeHtmlEntities(text);
		
        text = text.replaceAll("<ref>.*?</ref>", " ");
        text = text.replaceAll("</?.*?>", " ");
//        text = text.replaceAll("\\{\\{.*?\\}\\}", " ");
        text = text.replaceAll("\\[\\[.*?:.*?\\]\\]", " ");
        text = text.replaceAll("\\[\\[(.*?)\\]\\]", "$1");
//        text = text.replaceAll("\\s(.*?)\\|(\\w+\\s)", " $2");
        text = text.replaceAll("\\[.*?\\]", " ");
        text = text.replaceAll("\\'+", "");
        return text;
    }

	/**
	 * @requires this.infobox neq null
	 */
	public String getInfoBoxTemplate() {
		Scanner scanner = new Scanner(this.infoBox.getText().trim());// remove empty lines
		String line = scanner.nextLine(); // first line

		line = line.replace("{{", "");

		// in case of 1-line infobox
		if (line.indexOf('|') > 0) {
			line = line.substring(0, line.indexOf('|'));
		}

		scanner.close();

		return line.trim();
	}

	// TODO: improve in case of 1-line infobox
	public Map<String, String> getInfoBoxAttributes() {
		Scanner scanner = new Scanner(this.infoBox.getText());
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
			} else if (attribute != null) { // multiple line attribute values
				value += ", " + line;
				attributes.put(attribute, value);
			}
		}

		scanner.close();
		return attributes;
	}
}
