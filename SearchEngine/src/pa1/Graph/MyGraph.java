package pa1.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import api.Graph;
import api.TaggedVertex;
import pa1.Graph.Edge;

public class MyGraph<E> implements Graph<E> {

	public List<Edge> E;
	public List<String> V;
	public HashMap<String, List<Edge>> adjacency_list;	// outgoing edges stored
	public HashMap<String, List<Edge>> reverse_adjacency_list; // incoming edges stored, to rev just swap to and from
	

	public MyGraph() {
		V = new ArrayList<String>();
		E = new ArrayList<Edge>();
		adjacency_list = new HashMap<>();
		reverse_adjacency_list = new HashMap<>();
	}
	
	public void addVertex(String v) {
		if (V.contains(v)) return;
		V.add(v);
		adjacency_list.put(v, new ArrayList<Edge>());
		reverse_adjacency_list.put(v, new ArrayList<Edge>());
	}

	public void addEdge(String from, String to) {
		if (!from.equals(to)) {
			Edge e = new Edge(from, to);
			E.add(e);
			if (!V.contains(to)) V.add(to);
			adjacency_list.get(from).add(e);
			reverse_adjacency_list.get(to).add(e);
		}
	}
	
	public void addAllEdges(String from, List<String> lst_to) {
		for (String to : lst_to) {
			if (!from.equals(to)) {
				Edge e = new Edge(from, to);
				E.add(e);
				if (!V.contains(to)) V.add(to);
				adjacency_list.get(from).add(e);
				reverse_adjacency_list.get(to).add(e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<E> vertexData() {
		ArrayList<E> result = new ArrayList<E>();
		for (String v : V)
			result.add((E) v);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<TaggedVertex<E>> vertexDataWithIncomingCounts() {
		int index;
		ArrayList<TaggedVertex<E>> result = new ArrayList<TaggedVertex<E>>();
		for (index = 0; index < V.size(); index++) {
			//if (index == 0) result.add(new TaggedVertex<E>((E) V.get(index), 1));
			result.add(new TaggedVertex<E>((E) V.get(index), reverse_adjacency_list.get(V.get(index)).size()));
		}
		return result;
	}

	@Override
	public List<Integer> getIncoming(int index) {
		List<Integer> result = new ArrayList<Integer>();
		try {
			for (Edge e : reverse_adjacency_list.get(V.get(index)))
				result.add(V.indexOf(e.from));
		} catch (ArrayIndexOutOfBoundsException e) {
			e.getMessage();
		}
		return result;
	}

	@Override
	public List<Integer> getNeighbors(int index) {
		List<Integer> result = new ArrayList<Integer>();
		try {
			for (Edge e : adjacency_list.get(V.get(index)))
				result.add(V.indexOf(e.to));
		} catch (ArrayIndexOutOfBoundsException e) {
			e.getMessage();
		}
		return result;
	}
}
