package nl.tue.ddss.bimsparql.geometry;

import javax.vecmath.Vector3d;

public class Ray {
	
	Point point;
	Vector3d direction;
	
	public Ray(Point p,Vector3d v){
		this.point=p;
		this.direction=v;
	}
	
	public boolean is3D(){
		return point.is3D();
	}


}
