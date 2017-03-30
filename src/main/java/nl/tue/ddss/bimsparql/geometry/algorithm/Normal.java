package nl.tue.ddss.bimsparql.geometry.algorithm;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.LineString;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.Triangle;


public class Normal {
	
	public static Vector3d normal(LineString ls){
	    // Newell's formula
	    double nx, ny, nz;
	    nx = ny = nz = 0.0;

	    for ( int i = 0; i < ls.numPoints(); ++i ) {
	        Point3d pi = ls.pointN( i ).asPoint3d();
	        Point3d pj = ls.pointN( ( i+1 ) % ls.numPoints() ).asPoint3d();
	        double zi = pi.z ;
	        double zj = pj.z ;
	        nx += ( pi.y - pj.y ) * ( zi + zj );
	        ny += ( zi - zj ) * ( pi.x + pj.x );
	        nz += ( pi.x - pj.x ) * ( pi.y + pj.y );
	    }
        Vector3d normal=new Vector3d(nx, ny, nz );
        normal.normalize();
	        return normal;
	}
	
	public static Vector3d normal(Triangle t){		
         return normal(t.toPolygon());
	}
	
	public static Vector3d normal(Polygon p){
		return normal( p.exteriorRing());
	}

}
