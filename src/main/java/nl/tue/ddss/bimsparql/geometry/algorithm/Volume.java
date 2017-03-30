package nl.tue.ddss.bimsparql.geometry.algorithm;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryCollection;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Solid;
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
	    case TYPE_TRIANGULATEDSURFACE:
	    case TYPE_POLYHEDRALSURFACE:
	        return 0;

	    case TYPE_SOLID:
	        return volume( (Solid)g);

	    case TYPE_MULTISOLID:
	    case TYPE_GEOMETRYCOLLECTION:
	        double v=0;
	        GeometryCollection c = (GeometryCollection)g;

	        for ( int i=0; i<c.numGeometries(); i++ ) {
	            if ( c.geometryN( i ) instanceof Solid) {
	                v = v + volume( (Solid)c.geometryN( i ));
	            }
	        }

	        return v;
	    }
	    throw new GeometryException(String.format("volume( %s ) is not defined", g.geometryType()));
	}
	
	public double volume(Solid solid)
	{
	    double vol = 0;
	    Point3d origin=new Point3d( 0,0,0 );
	    int numShells = solid.numShells();

	    for ( int i=0; i<numShells; i++ ) {
	        Geometry t= solid.shellN( i );
	        TriangulatedSurface tin =t.asTriangulatedSurface();
	       int numTriangles = tin.numTriangles();

	        for ( int j=0; j<numTriangles; j++ ) {
	            Triangle tri = tin.triangleN( j );
	            vol = vol + volume( origin, tri.p0.asPoint3d(),
	                                      tri.p1.asPoint3d(),
	                                      tri.p2.asPoint3d());
	        }
	    }

	    return vol;
	}

	private double volume(Point3d origin, Point3d p0, Point3d p1, Point3d p2) {
		// TODO Auto-generated method stub
		return 0;
	}



}
