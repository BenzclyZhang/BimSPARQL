/*******************************************************************************
 * Copyright (C) 2017 Chi Zhang
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
		if(this.normal!=null){
			return normal;
		}
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
