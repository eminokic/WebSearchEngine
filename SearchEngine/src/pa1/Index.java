package pa1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;

import api.TaggedVertex;
import api.Util;

/**
 * Implementation of an inverted index for a web graph.
 * 
 * @author Emin Okic
 */
public class Index {
	
	private List<TaggedVertex<String>> urls;
	private HashMap<String, List<TaggedVertex<String>>> index; 
	
	
	/**
	 * Constructs an index from the given list of urls. The tag value for each url
	 * is the indegree of the corresponding node in the graph to be indexed.
	 * 
	 * @param urls
	 *            information about graph to be indexed
	 */
	public Index(List<TaggedVertex<String>> urls) {
		this.urls = urls;
		this.index = new HashMap<String, List<TaggedVertex<String>>>();
	}

	/**
	 * Creates the index.
	 */
	public void makeIndex() {
		String doc_txt = "";
		String[] words;
		int connectionCount = 0;
		
		for (TaggedVertex<String> url : urls) {
			connectionCount++;
			try{
				if (connectionCount >= 50) {
					Thread.sleep(3000);
					connectionCount = 0;
				}
			}
			catch (InterruptedException ignore) {}
			 try{ doc_txt = Jsoup.connect(url.getVertexData()).get().body().text(); }
			 	catch (HttpStatusException e3) { e3.printStackTrace(); }
			 	catch (UnsupportedMimeTypeException e2) { e2.printStackTrace(); }
			 	catch (IOException e1) { e1.printStackTrace(); }
			 words = doc_txt.split(" ");
			 if (words.length != 0) {
				 for (String word : words) {
					 word = Util.stripPunctuation(word);
					 if (!index.containsKey(word) && !Util.isStopWord(word))
						 index.put(word, new ArrayList<TaggedVertex<String>>());
					 if (!Util.isStopWord(word)) 
						 index.get(word).add(url);
				 }
			 }
		}
	}

	/**
	 * Searches the index for pages containing keyword w. Returns a list of urls
	 * ordered by ranking (largest to smallest). The tag value associated with each
	 * url is its ranking. The ranking for a given page is the number of occurrences
	 * of the keyword multiplied by the indegree of its url in the associated graph.
	 * No pages with rank zero are included.
	 * 
	 * @param w
	 *            keyword to search for
	 * @return ranked list of urls
	 */
	public List<TaggedVertex<String>> search(String w) {
		List<TaggedVertex<String>> base, result;
		int rank;
		
		if (!index.containsKey(w)) return null;
		base = index.get(w);
		result = new ArrayList<TaggedVertex<String>>();
		for (TaggedVertex<String> url : base) {
			rank = url.getTagValue() * Utility.getOccurences(w, url);
			if (rank != 0) result.add(new TaggedVertex<String>(url.getVertexData(), rank));
		}
		return Utility.putInOrder(result);
	}

	/**
	 * Searches the index for pages containing both of the keywords w1 and w2.
	 * Returns a list of qualifying urls ordered by ranking (largest to smallest).
	 * The tag value associated with each url is its ranking. The ranking for a
	 * given page is the number of occurrences of w1 plus number of occurrences of
	 * w2, all multiplied by the indegree of its url in the associated graph. No
	 * pages with rank zero are included.
	 * 
	 * @param w1
	 *            first keyword to search for
	 * @param w2
	 *            second keyword to search for
	 * @return ranked list of urls
	 */
	public List<TaggedVertex<String>> searchWithAnd(String w1, String w2) {
		List<TaggedVertex<String>> base1, base2, hasBoth, result;
		int rank;
		
		if (!index.containsKey(w1) || !index.containsKey(w2)) return null;
		base1 = index.get(w1);
		base2 = index.get(w2);
		hasBoth = new ArrayList<TaggedVertex<String>>();
		result = new ArrayList<TaggedVertex<String>>();
		for (TaggedVertex<String> url : base1) {
			if (base2.contains(url)) hasBoth.add(url);
		}
		for (TaggedVertex<String> url : hasBoth) {
			rank = (Utility.getOccurences(w1, url) + Utility.getOccurences(w2, url)) * url.getTagValue();
			if (rank != 0) result.add(new TaggedVertex<String>(url.getVertexData(), rank));
		}
		return Utility.putInOrder(result);
	}

	/**
	 * Searches the index for pages containing at least one of the keywords w1 and
	 * w2. Returns a list of qualifying urls ordered by ranking (largest to
	 * smallest). The tag value associated with each url is its ranking. The ranking
	 * for a given page is the number of occurrences of w1 plus number of
	 * occurrences of w2, all multiplied by the indegree of its url in the
	 * associated graph. No pages with rank zero are included.
	 * 
	 * @param w1
	 *            first keyword to search for
	 * @param w2
	 *            second keyword to search for
	 * @return ranked list of urls
	 */
	public List<TaggedVertex<String>> searchWithOr(String w1, String w2) {
		List<TaggedVertex<String>> hasOneOf, result;
		int rank;
		
		if (!index.containsKey(w1) || !index.containsKey(w2)) return null;
		hasOneOf = index.get(w1);
		result = new ArrayList<TaggedVertex<String>>();
		for (TaggedVertex<String> url : index.get(w2)) {
			if (!hasOneOf.contains(url)) hasOneOf.add(url);
		}
		for (TaggedVertex<String> url : hasOneOf) {
			rank = (Utility.getOccurences(w1, url) + Utility.getOccurences(w2, url)) * url.getTagValue();
			if (rank != 0) result.add(new TaggedVertex<String>(url.getVertexData(), rank));
		}
		return Utility.putInOrder(result);
	}

	/**
	 * Searches the index for pages containing keyword w1 but NOT w2. Returns a list
	 * of qualifying urls ordered by ranking (largest to smallest). The tag value
	 * associated with each url is its ranking. The ranking for a given page is the
	 * number of occurrences of w1, multiplied by the indegree of its url in the
	 * associated graph. No pages with rank zero are included.
	 * 
	 * @param w1
	 *            first keyword to search for
	 * @param w2
	 *            second keyword to search for
	 * @return ranked list of urls
	 */
	public List<TaggedVertex<String>> searchAndNot(String w1, String w2) {
		List<TaggedVertex<String>> hasOneNot, result;
		int rank;
		
		if (!index.containsKey(w1) || !index.containsKey(w2)) return null;
		hasOneNot = index.get(w1);
		result = new ArrayList<TaggedVertex<String>>();
		for (TaggedVertex<String> url : index.get(w2)) {
			if (hasOneNot.contains(url)) hasOneNot.remove(url);
		}
		for (TaggedVertex<String> url : hasOneNot) {
			rank = Utility.getOccurences(w1, url) * url.getTagValue();
			if (rank != 0) result.add(new TaggedVertex<String>(url.getVertexData(), rank));
		}
		return Utility.putInOrder(result);
	}
}
