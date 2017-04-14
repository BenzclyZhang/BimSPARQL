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

public class Box {
	
	public Point3d max;
	public Point3d min;
    public BoxOrientation orientation;
    
    
    
    public Box(){
    	
    }
    
    public Box(Point3d min,Point3d max){
			this(min,max,new BoxOrientation());
    }
    
    public Box(Point3d min,Point3d max,BoxOrientation orientation){
			this.max = max;
			this.min = min;
			this.orientation=new BoxOrientation();
    }
    
	public Point3d getMax() {
		return max;
	}

	public void setMax(Point3d max) {
		this.max = max;
	}

	public Point3d getMin() {
		return min;
	}

	public void setMin(Point3d min) {
		this.min = min;
	}


	public void setOrientation(BoxOrientation bo) {
		this.orientation=bo;
		
	}
	
	public BoxOrientation getOrientation() {
		return orientation;		
	}
	
	public double getLargestSurface() {
		Vector3d v=GeometryUtils.vectorSubtract(max, min);
		double l=v.dot(orientation.getN1());
		double w=v.dot(orientation.getN2());
		double h=v.dot(orientation.getN3());
		return (Math.max(l*w, Math.max(l*h, w*h)));
	}
	
	public double getVolume(){
		Vector3d v=GeometryUtils.vectorSubtract(max, min);
		double l=v.dot(orientation.getN1());
		double w=v.dot(orientation.getN2());
		double h=v.dot(orientation.getN3());
		return Math.abs(l*w*h);
	}
	
	public PolyhedralSurface toPolyhedralSurface(){
		PolyhedralSurface ps=new PolyhedralSurface();
		Vector3d v=GeometryUtils.vectorSubtract(max, min);
		Vector3d n1=orientation.getN1();
		Vector3d n2=orientation.getN2();
		Vector3d n3=orientation.getN3();
		double l=v.dot(orientation.getN1());
		double w=v.dot(orientation.getN2());
		double h=v.dot(orientation.getN3());
		double a=Math.sqrt(l*l/(n1.x*n1.x+n1.y*n1.y+n1.z*n1.z));
		double b=Math.sqrt(w*w/(n2.x*n2.x+n2.y*n2.y+n2.z*n2.z));
		double c=Math.sqrt(h*h/(n3.x*n3.x+n3.y*n3.y+n3.z*n3.z));
		Vector3d b1=GeometryUtils.vectorMul(n1, a);
		Vector3d b2=GeometryUtils.vectorMul(n2, b);
		Vector3d b3=GeometryUtils.vectorMul(n3, c);
		Point3d p0=min;
		Point3d p1=new Point3d(p0.x+b1.x,p0.y+b1.y,p0.z+b1.z);
		Point3d p2=new Point3d(p1.x+b2.x,p1.y+b2.y,p1.z+b2.z);
		Point3d p3=new Point3d(p0.x+b2.x,p0.y+b2.y,p0.z+b2.z);
		Point3d p4=new Point3d(p0.x+b3.x,p0.y+b3.y,p0.z+b3.z);
		Point3d p5=new Point3d(p4.x+b1.x,p4.y+b1.y,p4.z+b1.z);
		Point3d p6=max;
		Point3d p7=new Point3d(p4.x+b2.x,p4.y+b2.y,p4.z+b2.z);
		
		LineString ls0=new LineString();
		ls0.addPoint(p0);
		ls0.addPoint(p3);
		ls0.addPoint(p2);
		ls0.addPoint(p1);
		ls0.addPoint(p0);
		Polygon polygon0=new Polygon();
		polygon0.rings.add(ls0);
		
		LineString ls1=new LineString();
		ls1.addPoint(p0);
		ls1.addPoint(p4);
		ls1.addPoint(p7);
		ls1.addPoint(p3);
		ls1.addPoint(p0);
		Polygon polygon1=new Polygon();
		polygon1.rings.add(ls1);
		
		LineString ls2=new LineString();
		ls2.addPoint(p0);
		ls2.addPoint(p1);
		ls2.addPoint(p5);
		ls2.addPoint(p4);
		ls2.addPoint(p0);
		Polygon polygon2=new Polygon();
		polygon2.rings.add(ls2);
		
		LineString ls3=new LineString();
		ls3.addPoint(p1);
		ls3.addPoint(p2);
		ls3.addPoint(p6);
		ls3.addPoint(p5);
		ls3.addPoint(p1);
		Polygon polygon3=new Polygon();
		polygon3.rings.add(ls3);
		
		LineString ls4=new LineString();
		ls4.addPoint(p2);
		ls4.addPoint(p3);
		ls4.addPoint(p7);
		ls4.addPoint(p6);
		ls4.addPoint(p2);
		Polygon polygon4=new Polygon();
		polygon4.rings.add(ls4);
		
		LineString ls5=new LineString();
		ls5.addPoint(p4);
		ls5.addPoint(p5);
		ls5.addPoint(p6);
		ls5.addPoint(p7);
		ls5.addPoint(p4);
		Polygon polygon5=new Polygon();
		polygon5.rings.add(ls5);
		
		ps.addPolygon(polygon0);
		ps.addPolygon(polygon1);
		ps.addPolygon(polygon2);
		ps.addPolygon(polygon3);
		ps.addPolygon(polygon4);
		ps.addPolygon(polygon5);
		
		return ps;
	}
    
    

}
