package vn.emicode.wiki;

/**
 * https://www.mediawiki.org/wiki/API:Client_code
 *
 */
import java.io.File;
import java.io.IOException;
import java.util.Map;

import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;

/**
 * Demo application which reads a compressed or uncompressed Wikipedia XML dump
 * file (depending on the given file extension <i>.gz</i>, <i>.bz2</i> or
 * <i>.xml</i>) and prints the title and wiki text.
 *
 */
public class Demo {

	/**
	 * Print all titles of the wiki pages which have &quot;Real&quot; content (i.e.
	 * the title has no namespace prefix) (key == 0).
	 */
	static class ArticleFilter implements IArticleFilter {

		@Override
		public void process(WikiArticle page, Siteinfo siteinfo) throws IOException {
			if (page.isMain()) {
				InfoBox box = WikiPatternMatcher.parseInfoBox(page.getText());
				if (box != null) {
					Map<String, String> attributes = box.getAttributes();
//					for (String attribute : attributes.keySet()) {
//						System.out.println(attribute +" = "+ attributes.get(attribute));
//					}
					
//					System.out.println(box.dumpRaw());
//					System.out.println("=======");
//					System.out.println(WikiPatternMatcher.getPlainText(box.dumpRaw()));
//					System.out.println("----------------------");
				}
			}
		}
	}

	/**
	 * @param args filename, e.g. dewikiversity-20100401-pages-articles.xml.bz2
	 */
	public static void main(String[] args) throws Exception {
		new WikiXMLParser(new File("data/viwiki-20201020-pages-articles-multistream4.xml-p4565247p6065246.bz2"),
				new ArticleFilter()).parse();

	}
}
