package nl.tue.ddss.bimsparql.geometry.algorithm;

import java.util.ArrayList;
import java.util.List;

public class Edge {
	
	List<Face> faces=new ArrayList<Face>();
	List<HalfEdge> halfEdges=new ArrayList<HalfEdge>();

	
	

	public List<Face> getNFaces() {
		// TODO Auto-generated method stub
		return faces;
	}
	
	public boolean hasVertex(Vertex vertex){
		return halfEdges.get(0).start==vertex||halfEdges.get(0).end==vertex;
	}



	public Vertex getVertex(int i) {
		// TODO Auto-generated method stub
		if(i==0){
			return halfEdges.get(0).start;
		}else if(i==1){
			return halfEdges.get(0).end;
		}
		return null;
	}
	
	public Vertex anotherVertex(Vertex vertex){
		if (halfEdges.get(0).start==vertex) return halfEdges.get(0).end;
		else if(halfEdges.get(0).end==vertex) return halfEdges.get(0).start;
		else return null;
	}



	
	

}
