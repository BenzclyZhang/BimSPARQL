package nl.tue.ddss.bimsparql.geometry.algorithm;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryCollection;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;

public class Volume {
	
	public double volume(Geometry g ) throws GeometryException
	{
	    if ( g.isEmpty() ) {
	        return 0;
	    }
	    switch ( g.geometryTypeId() ) {
	    case TYPE_POINT:
	    case TYPE_LINESTRING:
	    case TYPE_POLYGON:
	    case TYPE_TRIANGLE:
	    case TYPE_MULTIPOINT:
	    case TYPE_MULTILINESTRING:
	    case TYPE_MULTIPOLYGON:
	    	 return 0;
	    case TYPE_TRIANGULATEDSURFACE:
	    	return volume((TriangulatedSurface)g);
	    case TYPE_POLYHEDRALSURFACE:
	        return volume((PolyhedralSurface)g);
	    case TYPE_GEOMETRYCOLLECTION:
	        double v=0;
	        GeometryCollection c = (GeometryCollection)g;

	        for ( int i=0; i<c.numGeometries(); i++ ) {
	                v = v + volume( c.geometryN( i ));
	        }

	        return v;
	    }
	    throw new GeometryException(String.format("volume( %s ) is not defined", g.geometryType()));
	}
	
	
	
	
    public double volume(PolyhedralSurface ps){
    	TriangulatedSurface ts=new TriangulatedSurface();
    	Triangulation.triangulate(ps, ts);
    	return volume(ts);
    }
	
	public double volume(TriangulatedSurface ts) {
		double volume=0;
		 for ( int j=0; j<ts.numTriangles(); j++ ) {
	            Triangle tri = ts.triangleN( j );
	            volume = volume + signedVolumeOfTriangle( tri);
	        }
	    return Math.abs(volume);
	}
	
	
	public double signedVolumeOfTriangle(Triangle t){
		return signedVolumeOfTriangle(t.p0.asPoint3d(),t.p1.asPoint3d(),t.p2.asPoint3d());		
	}

	public double signedVolumeOfTriangle(Point3d p1, Point3d p2, Point3d p3) {
	    double v321 = p3.x*p2.y*p1.z;
	    double v231 = p2.x*p3.y*p1.z;
	    double v312 = p3.x*p1.y*p2.z;
	    double v132 = p1.x*p3.y*p2.z;
	    double v213 = p2.x*p1.y*p3.z;
	    double v123 = p1.x*p2.y*p3.z;
	    return (1.0f/6.0f)*(-v321 + v231 + v312 - v132 - v213 + v123);
	}



}
