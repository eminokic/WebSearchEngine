package pa1.Graph;

public class Edge {

	public String from;
	public String to;
	
	public Edge(String from, String to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public String toString() {
		return "\nFrom: " + this.from + "\nTo: " + this.to + "\n";
	}
}
