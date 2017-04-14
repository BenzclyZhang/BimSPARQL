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
package nl.tue.ddss.bimsparql.geometry.algorithm;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.GeometryUtils;
import nl.tue.ddss.bimsparql.geometry.LineString;
import nl.tue.ddss.bimsparql.geometry.Plane;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;


public class Projection {
   
	
	
   public static Geometry projectToXY(Geometry geometry) throws GeometryException{
	   switch (geometry.geometryTypeId()){
		case TYPE_POINT:
			return projectPointToXY((Point)geometry);
		case TYPE_LINESTRING:
			return projectLineStringToXY((LineString)geometry);
		case TYPE_POLYGON:
			return projectPolygonToXY((Polygon)geometry);
		case TYPE_TRIANGLE:
			return projectTriangleToXY((Triangle)geometry);
		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
		case TYPE_GEOMETRYCOLLECTION:
		case TYPE_TRIANGULATEDSURFACE:
			return projectToXY((TriangulatedSurface)geometry);
		case TYPE_POLYHEDRALSURFACE:
			return projectToXY((PolyhedralSurface)geometry);
		}

		throw new GeometryException(
				String.format("stitch(%s) is not implemented", geometry.geometryType()));
		
   }
   
   public static Geometry projectToPlane(Geometry geometry,Plane p) throws GeometryException{
	   switch (geometry.geometryTypeId()){
		case TYPE_POINT:
			return projectToPlane((Point)geometry,p);
		case TYPE_LINESTRING:
			return projectToPlane((LineString)geometry,p);
		case TYPE_POLYGON:
			return projectToPlane((Polygon)geometry,p);
		case TYPE_TRIANGLE:
			return projectToPlane((Triangle)geometry,p);
		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
		case TYPE_GEOMETRYCOLLECTION:
		case TYPE_TRIANGULATEDSURFACE:
			return projectToPlane((TriangulatedSurface)geometry,p);
		case TYPE_POLYHEDRALSURFACE:
			return projectToPlane((PolyhedralSurface)geometry,p);
		}
		throw new GeometryException(
				String.format("stitch(%s) is not implemented", geometry.geometryType()));
		
   }
   
   
   
   public static Polygon projectToXY(TriangulatedSurface ts){
	   TriangulatedSurface triangulatedSurface=new TriangulatedSurface();
	   for(int i=0;i<ts.numTriangles();i++){
		   triangulatedSurface.addTriangle(projectTriangleToXY(ts.triangleN(i)));
	   }
	   Polyhedron polyhedron=new Polyhedron(triangulatedSurface);
		 return outline(polyhedron,new Vector3d(1,0,0));
   }
   
   public static Polygon projectToXY(PolyhedralSurface ps){
	   PolyhedralSurface polyhedralSurface=new PolyhedralSurface();
	   for(int i=0;i<ps.numPolygons();i++){
		  polyhedralSurface.addPolygon(projectPolygonToXY(ps.polygonN(i)));
	   }
	   Polyhedron polyhedron=new Polyhedron(polyhedralSurface);
		  return outline(polyhedron,new Vector3d(1,0,0));
   }
   
   
   public static Polygon projectToPlane(PolyhedralSurface ps,Plane p){
	   PolyhedralSurface polyhedralSurface=new PolyhedralSurface();
	   for(int i=0;i<ps.numPolygons();i++){
		  polyhedralSurface.addPolygon(projectToPlane(ps.polygonN(i),p));
	   }
	   Polyhedron polyhedron=new Polyhedron(polyhedralSurface);
	  return outline(polyhedron,p);
   }
   
   
   public static Polygon projectToPlane(TriangulatedSurface ts,Plane p){
	   TriangulatedSurface triangulatedSurface=new TriangulatedSurface();
	   for(int i=0;i<ts.numTriangles();i++){
		   triangulatedSurface.addTriangle(projectToPlane(triangulatedSurface.triangleN(i),p));
	   }
	   Polyhedron polyhedron=new Polyhedron(triangulatedSurface);
	   return outline(polyhedron,p);
   }
   
   
   private static Polygon outline(Polyhedron polyhedron,Plane p){
	   Vector3d random=GeometryUtils.vectorSubtract(p.p0, p.p1);
	   return outline(polyhedron,random);
   }
   
   
   private static Polygon outline(Polyhedron polyhedron,Vector3d random){
	   
	   random.normalize();
	   double dot=Double.NEGATIVE_INFINITY;
	   Vertex start=null;
	   for(Vertex vertex:polyhedron.vertices){
		   double e=random.dot(new Vector3d(vertex.pnt.x,vertex.pnt.y,vertex.pnt.z));
		   if (e>dot) {
			   dot=e;
		   start=vertex;
		   }
	   }
	   Vertex current=start;
	   Vertex next=new Vertex(null, 0);
	   LineString exterior=new LineString();
	   exterior.addPoint(start.pnt);
	   while (next!=start){
		   dot=Double.NEGATIVE_INFINITY;
		   
	  for(Edge edge:current.edges){
		  Vertex another=edge.anotherVertex(current);
		  Vector3d v=GeometryUtils.vectorSubtract(another.pnt, current.pnt);
		  v.normalize();
		  double d=v.dot(random);
		  if(d>dot){
			  dot=d;
			  next=another;
		  }
	  }
	  exterior.addPoint(next.pnt);
	  current=next;
	 }
	   Polygon polygon=new Polygon(exterior);
	   return polygon;	   
   }
   
   

   
   
   public static Polygon projectToPlane(Polygon poly,Plane p){
	   LineString ls=poly.exteriorRing();
	   return new Polygon(projectToPlane(ls,p));
   }
   
   
   public static Triangle projectToPlane(Triangle t,Plane p){
	   Triangle triangle=new Triangle();
	   triangle.p0=projectToPlane(t.p0,p);
	   triangle.p1=projectToPlane(t.p1,p);
	   triangle.p2=projectToPlane(t.p2,p);
	   return triangle;
   }
   
   public static LineString projectToPlane(LineString ls,Plane p){
	   LineString lineString=new LineString();
	   for(int i=0;i<ls.numPoints();i++){
		   lineString.addPoint(projectToPlane(ls.pointN(i),p));
	   }
	   return lineString;
   }
   
   
   public static Point3d projectToPlane(Point pt,Plane p){
	   Vector3d vector=GeometryUtils.vectorSubtract(pt.asPoint3d(), p.p0);
	   Vector3d n=p.getNormal();
	   double dist=vector.dot(n);
	   return new Point3d(pt.asPoint3d().x-dist*n.x,pt.asPoint3d().y-dist*n.y,pt.asPoint3d().z-dist*n.z);
   }
   

	
   public static Point projectPointToXY (Point p){
	   return p.asPoint2d();
   }
   
   public static LineString projectLineStringToXY (LineString l){
	   for (int i=0;i<l.numPoints();i++){
		   l.getPoints().set(i, projectPointToXY(l.pointN(i)));
	   }
	   return l;
   }
   
   public static Triangle projectTriangleToXY (Triangle t){
	   t.p0=projectPointToXY(t.p0);
	   t.p1=projectPointToXY(t.p1);
	   t.p2=projectPointToXY(t.p2);
	return t;
 }
   
   public static Polygon projectPolygonToXY (Polygon p){
	   LineString ls=p.exteriorRing();
	   return new Polygon(projectLineStringToXY(ls));
   }
   
   
   public static Point3d projectPointToPlane3D (Point3d p,Plane pl){
		Vector3d normal=pl.getNormal();
		Vector3d diff=GeometryUtils.vectorSubtract(p, pl.p0);
		diff.scale(diff.dot(normal));
		Vector3d v=GeometryUtils.vectorSubtract(p,diff);
		return new Point3d(v.x,v.y,v.z);
   }
   
   

}
