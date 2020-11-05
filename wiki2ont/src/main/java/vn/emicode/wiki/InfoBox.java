package vn.emicode.wiki;

import java.util.Map;

public class InfoBox {
    private String infoBoxWikiText;
    private String template = null;
    private Map<String, String> attributes = null;

    public InfoBox(String infoBoxWikiText) {
        this.infoBoxWikiText = infoBoxWikiText;
    }

    public String dumpRaw() {
        return infoBoxWikiText;
    }

    public String getTemplate() {
    	if (template == null) {
    		template = WikiPatternMatcher.parseInfoBoxTemplate(infoBoxWikiText);
    	}
    	
    	return template;
    }
    
    public Map<String, String> getAttributes() {
    	if (attributes == null) {
    		attributes = WikiPatternMatcher.parseInfoBoxAttributes(infoBoxWikiText);
    	}
    	
    	return attributes;
    }
}