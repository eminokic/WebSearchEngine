package pa1;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import api.Graph;
import api.Util;
import pa1.Graph.MyGraph;

/**
 * Implementation of a basic web crawler that creates a graph of some portion of
 * the world wide web.
 *
 * @author Emin Okic
 */
public class Crawler {

	// need to work w/ maxDepth and maxPages (cont adding edges after max vertex is reached)
	private int maxDepth, maxPages, connectionCount = 0;
	protected String seedUrl;
	private MyGraph<String> graph;
	private Queue<String> toProcessQueue = new ArrayDeque<>();
    private Set<String> visitedSet = new HashSet<>();
	
    
    
	/**
	 * Constructs a Crawler that will start with the given seed url, including only
	 * up to maxPages pages at distance up to maxDepth from the seed url.
	 */
	public Crawler(String seedUrl, int maxDepth, int maxPages) {
		graph = new MyGraph<String>();
		this.seedUrl = seedUrl;
		this.maxDepth = maxDepth;
		this.maxPages = maxPages;
	}

	
	
	/**
	 * Creates a web graph for the portion of the web obtained by a BFS of the web
	 * starting with the seed url for this object, subject to the restrictions
	 * implied by maxDepth and maxPages.
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public Graph<String> crawl() {
		String currentUrl, currentLink;
		int i, depth = 0;
		boolean isFull, doneLoading, valid;
		Elements elmts;
		List<String> links;
		Document doc = null;
		currentUrl = this.seedUrl;
		isFull = false;
		toProcessQueue.add(currentUrl);
		toProcessQueue.add("!");
		while(toProcessQueue.size() > 1 && depth < maxDepth) {
			links = new ArrayList<>();
			visitedSet.add(currentUrl);
			graph.addVertex(currentUrl);
			connectionCount++;
			try{
				if (connectionCount >= 50) {
					Thread.sleep(3000);
					connectionCount = 0;
				}
			}
			catch (InterruptedException ignore) {}
			try { doc = Jsoup.connect(currentUrl).get(); } 
				catch (HttpStatusException e3) { e3.printStackTrace(); }
				catch (UnsupportedMimeTypeException e2) { e2.printStackTrace(); }
				catch (IOException e1) { e1.printStackTrace(); }
	        elmts = doc.select("a[href]");
	        for (Element e : elmts) {
	        		// TODO: sloppy fix here
	        		if (!links.contains(e.attr("abs:href"))) links.add(e.attr("abs:href"));
	        }
			i = 0;
			doneLoading = false;
			while ((graph.V.size() < maxPages) && i < links.size()) {
				if (!Util.ignoreLink(currentUrl, links.get(i))) {
					
					valid = true;
					currentLink = links.get(i);
					connectionCount++;
					try{
						if (connectionCount >= 50) {
							Thread.sleep(3000);
							connectionCount = 0;
						}
					}
					catch (InterruptedException ignore) {}
					try { @SuppressWarnings("unused")
					Document tmp = Jsoup.connect(currentLink).get(); }
						catch (HttpStatusException e3) {
							e3.printStackTrace();
							valid = false;
						}
						catch (UnsupportedMimeTypeException e2) {
							e2.printStackTrace();
							valid = false;
						}
						catch (IOException e1) {
							e1.printStackTrace();
							valid = false;
						}
					if (valid) {
						graph.addVertex(currentLink);
						if (!visitedSet.contains(currentLink) && !toProcessQueue.contains(currentLink))
							toProcessQueue.add(currentLink);
						graph.addEdge(currentUrl, currentLink);
						if (graph.V.size() >= maxPages) doneLoading = true;// check if this is trig
					}
				}
				i++;				
			}
			
			if (graph.V.size() >= maxPages) isFull = true;
			if (doneLoading == true) {
				while (i < links.size()) {
					if (toProcessQueue.contains(links.get(i)))
						graph.addEdge(currentUrl, links.get(i));
					i++;
				}
				isFull = false;
			}
			if (isFull)
				graph.addAllEdges(currentUrl, links);
			toProcessQueue.remove(currentUrl);
			currentUrl = toProcessQueue.peek();
			if (currentUrl.equals("!")) {
				depth ++;
				toProcessQueue.remove(currentUrl);
				currentUrl = toProcessQueue.peek();
				toProcessQueue.add("!");
			}
		}
		return graph;
	}
}
