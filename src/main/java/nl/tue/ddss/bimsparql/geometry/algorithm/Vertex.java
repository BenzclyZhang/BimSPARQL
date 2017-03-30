package nl.tue.ddss.bimsparql.geometry.algorithm;


import nl.tue.ddss.bimsparql.geometry.Point3d;

public class Vertex {
	
	public Point3d pnt;
	public int index;
	
	public Vertex(Point3d pt, int index) {
		this.pnt=pt;
		this.index=index;
	}



}
