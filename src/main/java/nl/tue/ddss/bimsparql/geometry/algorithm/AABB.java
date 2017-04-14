package nl.tue.ddss.bimsparql.geometry.algorithm;

import nl.tue.ddss.bimsparql.geometry.AxisAlignedBox;
import nl.tue.ddss.bimsparql.geometry.Point3d;

public class AABB extends AxisAlignedBox {

	public AABB(Point3d max, Point3d min) throws Exception {
		super(max, min);
		// TODO Auto-generated constructor stub
	}

	public AABB() {
        
	}


	public void addPoint3d(Point3d point) {
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
			if (point.z > max.z) {
				max.z = point.z;
			}
			if (point.x < min.x) {
				min.x = point.x;
			}
			if (point.y < min.y) {
				min.y = point.y;
			}
			if (point.z < min.z) {
				min.z = point.z;
			}
		}
	}
	
	public void addBoundingBox(AABB bb){
		addPoint3d(bb.min);
		addPoint3d(bb.max);
	}

	


}
