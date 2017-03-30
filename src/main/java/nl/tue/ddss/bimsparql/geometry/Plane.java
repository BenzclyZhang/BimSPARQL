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
		findEquation(p0,p1,p2);
		normal=getNormal();
	}
	
	public Plane(Point3d p0, Vector3d normal) {
		this.p0=p0;
		this.normal=normal;
	}
	
	public Plane(double a, double b,double c,double d){
		this.a=a;
		this.b=b;
		this.c=c;
		this.d=d;
	}

	public void findEquation(Point3d p0,Point3d p1,Point3d p2){
		Vector3d v1=new Vector3d(p0.x-p2.x,p0.y-p2.y,p0.z-p2.z);
		Vector3d v2=new Vector3d(p1.x-p2.x,p1.y-p2.y,p1.z-p2.z);
		a=v1.y*v1.z-v2.y*v2.z;
		b=v1.x*v1.z-v2.x*v2.z;
		c=v1.x*v1.y-v2.x*v2.y;
		d=-a*p0.x-b*p0.y-c*p0.z;	
	}
	
	
	
	public Vector3d getNormal(){
		double nx = (p1.y - p0.y)*(p2.z - p0.z) - (p1.z - p0.z)*(p2.y - p0.y);
		double ny = (p1.z - p0.z)*(p2.x - p0.x) - (p1.x - p0.x)*(p2.z - p0.z);
		double nz = (p1.x - p0.x)*(p2.y - p0.y) - (p1.y - p0.y)*(p2.x - p0.x);
		Vector3d normal=new Vector3d(nx,ny,nz);
		normal.normalize();
		return normal;
	}
	
	public boolean hasPoint(Point3d p){
		return a*p.x+b*p.y+c*p.z+d==0;
	}
	
	public Point3d projects(Point3d p){
           return Projection.projectPointToPlane3D(p, this);
	}
	


}
