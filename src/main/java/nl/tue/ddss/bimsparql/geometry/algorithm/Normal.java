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
import nl.tue.ddss.bimsparql.geometry.LineString;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.Triangle;


public class Normal {
	


	public static Vector3d normal(Geometry g) throws GeometryException {
		 switch ( g.geometryTypeId() ) {
		    case TYPE_POINT:
		    case TYPE_LINESTRING:
		        return normal((LineString)g) ;
		    case TYPE_POLYGON:
		        return normal( (Polygon)g);
		    case TYPE_TRIANGLE:
		        return normal( (Triangle)g);
		    case TYPE_MULTIPOINT:
		    case TYPE_MULTILINESTRING:
		    case TYPE_MULTIPOLYGON:
		    case TYPE_GEOMETRYCOLLECTION:
		    case TYPE_TRIANGULATEDSURFACE:
		    case TYPE_POLYHEDRALSURFACE:
		    }
		   throw new GeometryException( "missing case in SFCGAL::algorithm::area3D" );
	}
	
	
	public static Vector3d normal(LineString ls){
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
