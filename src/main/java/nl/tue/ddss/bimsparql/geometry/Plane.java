package nl.tue.ddss.bimsparql.geometry;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.algorithm.Projection;

public class Plane {
	
	public Point3d p0;
	public Point3d p1;
	public Point3d p2;
	public double a;
	public double b;
	public double c;
	public double d;
	public Vector3d normal;
	
	public Plane(Point3d p0,Point3d p1,Point3d p2){
		this.p0=p0;
		this.p1=p1;
		this.p2=p2;
		normal=getNormal();
		findEquation();
		
	}
	
	public Plane(Point3d p0, Vector3d normal) {
		this.p0=p0;
		this.normal=normal;
		findEquation();
	}
	
	public Plane(double a, double b,double c,double d){
		this.a=a;
		this.b=b;
		this.c=c;
		this.d=d;
	}

	public void findEquation(){
		a=normal.x;
		b=normal.y;
		c=normal.z;
		d=-a*p0.x-b*p0.y-c*p0.z;	
	}
	
	
	
	public Vector3d getNormal(){
		Vector3d v1=GeometryUtils.vectorSubtract(p1, p0);
		Vector3d v2=GeometryUtils.vectorSubtract(p2, p1);
		Vector3d u=new Vector3d();
		u.cross(v1, v2);
		u.normalize();
		this.normal=u;
		return normal;
	}
	
	public boolean hasPoint(Point3d p){
		return a*p.x+b*p.y+c*p.z+d==0;
	}
	
	public Point3d projects(Point3d p){
           return Projection.projectPointToPlane3D(p, this);
	}
	


}
