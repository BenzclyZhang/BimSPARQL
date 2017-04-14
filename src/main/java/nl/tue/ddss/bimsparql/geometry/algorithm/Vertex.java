package nl.tue.ddss.bimsparql.geometry.algorithm;


import java.util.ArrayList;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.Point3d;

public class Vertex {
	
	public Point3d pnt;
	public int index;
	public List<Edge> edges;
	
	public Vertex(Point3d pt, int index) {
		this.pnt=pt;
		this.index=index;
		this.edges=new ArrayList<Edge>();
	}



}
