package nl.tue.ddss.bimsparql.geometry.test;



import nl.tue.ddss.bimsparql.geometry.Line3d;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.algorithm.Topology;

public class SameLineTest {
	
	public static void main(String[] args){
		Line3d l1=new Line3d(new Point3d(53293.84375, 53031.84765625, 0.0),new Point3d(53293.84375, 53031.84765625, -0.9999999999999999));
		Point3d p=new Point3d(53293.84375, 53031.84765625, 4369.999999999999);
		boolean bool=Topology.isOnLine(p, l1);
		System.out.println(bool);
	}

}
