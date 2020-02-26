package pa1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;

import api.TaggedVertex;
import api.Util;
import pa1.Graph.MyGraph;

public class Utility {

	public static List<Integer> getImportance(MyGraph<String> graph) {
		int index;
		List<Integer> result;
		
		result = new ArrayList<>();
		for (index = 0; index < graph.V.size(); index++)
			result.add(graph.reverse_adjacency_list.get(graph.V.get(index)).size());
		return result;
	}
	
	// Close derivative of above method
	public static List<TaggedVertex<String>> getLstForIndexing(MyGraph<String> graph) {
		int index, indegree;
		TaggedVertex<String> current;
		List<TaggedVertex<String>> result;
		
		result = new ArrayList<>();
		for (index = 0; index < graph.V.size(); index++) {
			indegree = graph.reverse_adjacency_list.get(graph.V.get(index)).size();
			if (index == 0) indegree = 1;
			current = new TaggedVertex<String>(graph.V.get(index), indegree);
			result.add(current);
		}
		return result;
	}
	
	public static int getOccurences(String w, TaggedVertex<String> url) {
		int amount = 0;
		String doc_txt = "";
		String[] words;
		
		try { doc_txt = Jsoup.connect(url.getVertexData()).get().body().text(); }
			catch (HttpStatusException e3) { e3.printStackTrace(); } 
			catch (UnsupportedMimeTypeException e2) { e2.printStackTrace(); } 
			catch (IOException e1) { e1.printStackTrace(); }
		words = doc_txt.split(" ");
		if (words.length != 0) {
			for (String word : words) {
				word = Util.stripPunctuation(word);
				if (w.equals(word)) amount++;
			}
		}
		return amount;
	}
	
	
	static List<TaggedVertex<String>> lst;
	List<TaggedVertex<String>> out;
	
	
	// largest to smallest
	public static List<TaggedVertex<String>> putInOrder(List<TaggedVertex<String>> in) {
		lst = in;
		mergeSort(0, in.size() - 1);
		return lst;
	}
	
	private static void mergeSort(int start, int end) {
		int mid = (start + end) / 2;
		if (start < end) {
			mergeSort(start, mid);
			mergeSort(mid + 1, end);
			merge(start, mid, end);
		}

	}
	
	private static void merge(int start, int mid, int end) {
		List<TaggedVertex<String>> tempLst = new ArrayList<TaggedVertex<String>>();
		for (int i = 0; i < end + 1; i++) {
			tempLst.add(new TaggedVertex<String>("PLACEHOLDER", 0));
		}
		int tempLstIndex = start;
		int startIndex = start;
		int midIndex = mid + 1;
		while (startIndex <= mid && midIndex <= end) {
			if (lst.get(startIndex).getTagValue() > lst.get(midIndex).getTagValue())
				tempLst.set(tempLstIndex++, lst.get(startIndex++));
			else tempLst.set(tempLstIndex++, lst.get(midIndex++));
		}
		while (startIndex <= mid)
			tempLst.set(tempLstIndex++, lst.get(startIndex++));
		while (midIndex <= end)
			tempLst.set(tempLstIndex++, lst.get(midIndex++));
		for (int i = start; i <= end; i++)
			lst.set(i, tempLst.get(i));
	}
}







































