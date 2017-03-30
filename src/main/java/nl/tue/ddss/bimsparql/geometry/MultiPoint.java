package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

public class MultiPoint extends GeometryCollection{
    
	List<Point> points=new ArrayList<Point>();
	
	public Point pointN(int i) {
		// TODO Auto-generated method stub
		return points.get(i);
	}

}
