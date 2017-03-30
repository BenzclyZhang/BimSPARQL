package nl.tue.ddss.bimsparql.geometry;

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;

public class BoundingSquare extends Square {

	public BoundingSquare(Point3d max, Point3d min) throws Exception {
		super(max, min);
		// TODO Auto-generated constructor stub
	}

	public BoundingSquare() {
	}

	public void addPoint(Point3d point) {
		if (max == null && min == null) {
			max=new Point3d(point.x,point.y,point.z);
			min=new Point3d(point.x,point.y,point.z);
		}  else {
			if (point.x > max.x) {
				max.x = point.x;
			}
			if (point.y > max.y) {
				max.y = point.y;
			}
			if (point.x < min.x) {
				min.x = point.x;
			}
			if (point.y < min.y) {
				min.y = point.y;
			}
		}
	}
	
	public void addBoundingSquare(BoundingSquare bs){
		addPoint(bs.min);
		addPoint(bs.max);
	}
	
	public void addBoundingBox(AABB bb){
		addPoint(bb.getMin());
		addPoint(bb.getMax());
	}
	


}
