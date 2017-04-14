package nl.tue.ddss.bimsparql.geometry.algorithm;

import java.util.List;

import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryCollection;
import nl.tue.ddss.bimsparql.geometry.LineString;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;

public class Triangulation {
	


	
	public static void triangulate(Geometry g, TriangulatedSurface triangulatedSurface){
		switch (g.geometryTypeId()) {
		case TYPE_POINT:
		case TYPE_LINESTRING:
		case TYPE_POLYGON:
			triangulate((Polygon)g, triangulatedSurface);;
		case TYPE_TRIANGLE:
			triangulate((Triangle)g, triangulatedSurface);
		// collection dispatch
		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
			triangulate((GeometryCollection)g, triangulatedSurface);
		case TYPE_GEOMETRYCOLLECTION:
			triangulate((GeometryCollection)g, triangulatedSurface);
		case TYPE_TRIANGULATEDSURFACE:
			triangulate((TriangulatedSurface)g, triangulatedSurface);
		case TYPE_POLYHEDRALSURFACE:
			triangulate((PolyhedralSurface)g, triangulatedSurface);
		}

	}
    

	public static void triangulate(Triangle triangle,TriangulatedSurface triangulatedSurface){
	    if ( triangle.isEmpty() ) {
	        return ;
	    }
	    triangulatedSurface.addTriangle( triangle ) ;
	}


	public static void triangulate(TriangulatedSurface g,TriangulatedSurface triangulatedSurface){
	    triangulatedSurface.addTriangles( g ) ;
	}


	public static void triangulate(GeometryCollection g,TriangulatedSurface triangulatedSurface){
	    for ( int i = 0; i < g.numGeometries(); i++ ) {
	        triangulate( g.geometryN( i ), triangulatedSurface );
	    }
	}
	

	public static void triangulate(Polygon polygon,TriangulatedSurface triangulatedSurface){
		ConstrainedMesh mesh = new ConstrainedMesh();
		try {
		for (int i=0;i<polygon.numRings();i++){
			LineString ls=polygon.ringN(i);
			for(int j=0;j<ls.numPoints()-1;j++){
				Point3d start=ls.pointN(j).asPoint3d();
				Point3d end=ls.pointN(j+1).asPoint3d();

					DEdge	ed = new DEdge(new DPoint(start.x,start.y,start.z),new DPoint(end.x,end.y,end.z));
					mesh.addConstraintEdge(ed);


			}
		}
			mesh.processDelaunay();
		} catch (DelaunayError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		List<DTriangle> dTriangles=mesh.getTriangleList();
		for(DTriangle dt:dTriangles){
			triangulatedSurface.addTriangle(toTriangle(dt));
		}
		
	}
	


	public static void triangulate(
	    PolyhedralSurface g,
	    TriangulatedSurface triangulatedSurface
	)
	{
	    for ( int i = 0; i < g.numGeometries(); i++ ) {
	        triangulate( g.polygonN( i ), triangulatedSurface );
	    }
	}


	private static Triangle toTriangle(DTriangle dt){
		return new Triangle(toPoint(dt.getPoint(0)),toPoint(dt.getPoint(1)),toPoint(dt.getPoint(2)));
	}
	
	private static Point3d toPoint(DPoint dp){
		return new Point3d(dp.getX(),dp.getY(),dp.getZ());
	}

}
