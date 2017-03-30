package nl.tue.ddss.bimsparql.geometry.algorithm;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryUtils;
import nl.tue.ddss.bimsparql.geometry.LineString;
import nl.tue.ddss.bimsparql.geometry.Plane;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.Triangle;


public class Projection {
	
	
   public static Geometry projectToXY(Geometry g){
	   return null;
   }
   
   public static Geometry projectToPlane(Geometry g){
	   return null;
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
	   return p;
   }
   
   
   public static Point3d projectPointToPlane3D (Point3d p,Plane pl){
		Vector3d normal=pl.getNormal();
		Vector3d diff=GeometryUtils.vectorSubtract(p, pl.p0);
		diff.scale(diff.dot(normal));
		Vector3d v=GeometryUtils.vectorSubtract(p,diff);
		return new Point3d(v.x,v.y,v.z);
   }
   
   

}
