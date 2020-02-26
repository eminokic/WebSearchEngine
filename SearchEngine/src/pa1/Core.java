package pa1;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import api.TaggedVertex;
import pa1.Graph.MyGraph;


public class Core {

	public final static String base_url = "http://web.cs.iastate.edu/~smkautz/cs311f19/temp/a.html";
	
	public static void main(String[] args) throws IOException, InterruptedException {
        // Crawler crawler = new Crawler("https://en.wikipedia.org", 10, 500);
		Crawler crawler = new Crawler(base_url, 10, 500);
        MyGraph<String> graph = (MyGraph<String>) crawler.crawl();
        List<TaggedVertex<String>> lstToIndex = Utility.getLstForIndexing(graph);
        Index i = new Index(lstToIndex);
        i.makeIndex();
        // test_ordering(i.index.keySet(), i);
    }
	
	static void test_ordering(Set<String> words, Index i) {
		for (String word : words) {
			System.out.println("\n\n" + word);
			List<TaggedVertex<String>> results_ordered = i.search(word);
			for (TaggedVertex<String> url : results_ordered)
	    			System.out.println("  " + url.getVertexData() + ":   "+ url.getTagValue());
		}
	}
}
