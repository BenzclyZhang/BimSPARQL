package nl.tue.ddss.bimsparql.geometry.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.Polygon;

public class Face {
	
	List<HalfEdge> halfEdges=new LinkedList<HalfEdge>();
	List<Vertex> vertices=new LinkedList<Vertex>();
	Polygon polygon;
	int index;
	
	public Face(Polygon polygon, int index) {
		this.polygon=polygon;
		this.index=index;
		
	}

	public Vector3d getNormal() {
		return polygon.getNormal();
	}


	public Edge getEdge(int i) {
		// TODO Auto-generated method stub
		return halfEdges.get(i).getEdge();
	}
	
	public List<Face> getNeighbors(){
		List<Face> faces=new ArrayList<Face>();
		for(HalfEdge he:halfEdges){
			Edge e=he.getEdge();
			List<Face> fs=e.getNFaces();
			for (Face f:fs){
				if(f!=this){
					faces.add(f);
				}
			}
		}
		return faces;
	}

	public void addHalfEdge(HalfEdge hf) {
		halfEdges.add(hf);
		
	}

	public void addVertex(Vertex v) {
		vertices.add(v);
		
	}

}
