package vn.emicode.wiki;

public class WikiArticle extends info.bliki.wiki.dump.WikiArticle {
	private InfoBox infoBox;
	
	public InfoBox getInfoBox() {
		if (infoBox == null) {
			parseInfoBox();
		}
		
		return this.infoBox;
	}
	
	private void parseInfoBox() {
		WikiPatternMatcher.parseInfoBox(this.getText());
	}
}
