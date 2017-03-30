package nl.tue.ddss.bimsparql.geometry.algorithm;



public class HalfEdge {
	
	Vertex start;
	Vertex end;
	int index;
	Edge edge;
	
	public HalfEdge(Vertex start, Vertex end,int index){
		this.start=start;
		this.end=end;
		this.index=index;
	}

	public HalfEdge(Vertex start, Vertex end) {
		this.start=start;
		this.end=end;
	}
	
	public Edge getEdge(){
		return edge;
	}
	
	public void setEdge(Edge edge){
		this.edge=edge;
	}
	


}
