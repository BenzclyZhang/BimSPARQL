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
 * This file is the java version of the ewkt in http://www.sfcgal.org/.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package nl.tue.ddss.bimsparql.geometry.ewkt;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryCollection;
import nl.tue.ddss.bimsparql.geometry.LineString;
import nl.tue.ddss.bimsparql.geometry.MultiLineString;
import nl.tue.ddss.bimsparql.geometry.MultiPoint;
import nl.tue.ddss.bimsparql.geometry.MultiPolygon;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;

public class EwktWriter {
	
   String s;
   boolean exactWrite;
	
	public EwktWriter(String s){
	    this.s=s;
	    this.exactWrite= false;

	}

	public void writeRec(Geometry g ) throws WktWriteException
	{
	    switch( g.geometryTypeId() ) {
	    case TYPE_POINT:
	        write((Point)g);
	        return ;

	    case TYPE_LINESTRING:
	        write( (LineString)g);
	        return ;

	    case TYPE_POLYGON:
	        write( (Polygon)g );
	        return ;

	    case TYPE_GEOMETRYCOLLECTION:
	        write((GeometryCollection)g );
	        return ;

	    case TYPE_MULTIPOINT:
	        write((MultiPoint) g);
	        return ;

	    case TYPE_MULTILINESTRING:
	        write( (MultiLineString)g);
	        return ;

	    case TYPE_MULTIPOLYGON:
	        write( (MultiPolygon)g );
	        return ;

	    case TYPE_TRIANGLE:
	        write( (Triangle)g);
	        return ;

	    case TYPE_TRIANGULATEDSURFACE:
	        write( (TriangulatedSurface)g);
	        return ;

	    case TYPE_POLYHEDRALSURFACE:
	        write( (PolyhedralSurface)g);
	        return ;
	    }

	   throw new WktWriteException("WktWriter : '" + g.geometryType() + "' is not supported");
	}


	void write(Geometry g) throws WktWriteException
	{
	    writeRec( g );
	}


	void writeCoordinateType(Geometry g )
	{
	    if ( g.is3D() && g.isMeasured() ) {
	        s=s+ " ZM";
	    }
	    else if( g.is3D()){
	    	s=s+" Z";
	    }
	    else if ( ! g.is3D() && g.isMeasured() ) {
	        s=s+ " M";
	    }
	}


	void writeCoordinate(Point g ) throws WktWriteException
	{
	    
	        s=s+ g.getCoordinates()[0] + " " + g.getCoordinates()[1];

	        if ( g.is3D() ) {
					s=s+ " " + g.getCoordinates()[2] ;
	        }
	    
	    if ( g.isMeasured() ) {
	        s=s+ " " + g.m();
	    }
}


	void write(Point g ) throws WktWriteException
	{
	    s=s+ "POINT" ;
	    writeCoordinateType( g );

	    if ( g.isEmpty() ) {
	        s=s+ " EMPTY" ;
	        return ;
	    }

	    writeInner( g );
	}

	void writeInner(Point g ) throws WktWriteException
	{
	    if ( g.isEmpty() ) {
	        s=s+ "EMPTY" ;
	        return ;
	    }

	    s=s+ "(";
	    writeCoordinate( g );
	    s=s+ ")";
	}


	void write(LineString g ) throws WktWriteException
	{
	    s=s+ "LINESTRING" ;
	    writeCoordinateType( g );

	    if ( g.isEmpty() ) {
	        s=s+ " EMPTY" ;
	        return ;
	    }

	    writeInner( g );
	}

	void writeInner(LineString g ) throws WktWriteException
	{
	    s=s+ "(";

	    for ( int i = 0; i < g.numPoints(); i++ ) {
	        if ( i != 0 ) {
	            s=s+ ",";
	        }

	        writeCoordinate((Point) g.pointN( i ) );
	    }

	    s=s+ ")";
	}


	void write(Polygon g ) throws WktWriteException
	{
	    s=s+ "POLYGON" ;
	    writeCoordinateType( g );

	    if ( g.isEmpty() ) {
	        s=s+ " EMPTY" ;
	        return ;
	    }

	    writeInner( g );
	}


	void writeInner(Polygon g ) throws WktWriteException
	{
	    s=s+ "(";
	    writeInner( g.exteriorRing() );

	    for ( int i = 0; i < g.numInteriorRings(); i++ ) {
	        s=s+ ",";
	        writeInner( g.interiorRingN( i ) );
	    }

	    s=s+ ")";
	}


	void write(GeometryCollection g ) throws WktWriteException
	{
	    s=s+ "GEOMETRYCOLLECTION" ;
	    writeCoordinateType( g );

	    if ( g.isEmpty() ) {
	        s=s+ " EMPTY" ;
	        return ;
	    }

	    s=s+ "(" ;

	    for ( int i = 0 ; i < g.numGeometries(); i++ ) {
	        if ( i != 0 ) {
	            s=s+ ",";
	        }

	        writeRec( g.geometryN( i ) );
	    }

	    s=s+ ")" ;
	}


	void write(MultiPoint g ) throws WktWriteException
	{
	    s=s+ "MULTIPOINT" ;
	    writeCoordinateType( g );

	    if ( g.isEmpty() ) {
	        s=s+ " EMPTY" ;
	        return ;
	    }

	    s=s+ "(";

	    for (int i = 0; i < g.numGeometries(); i++ ) {
	        if ( i != 0 ) {
	            s=s+ "," ;
	        }

	        writeInner((Point) g.geometryN( i ));
	    }

	    s=s+ ")";
	}


	void write(MultiLineString g ) throws WktWriteException
	{
	    s=s+ "MULTILINESTRING" ;
	    writeCoordinateType( g );

	    if ( g.isEmpty() ) {
	        s=s+ " EMPTY" ;
	        return ;
	    }

	    s=s+ "(";

	    for ( int i = 0; i < g.numGeometries(); i++ ) {
	        if ( i != 0 ) {
	            s=s+ "," ;
	        }

	        writeInner( (LineString)g.geometryN( i ));
	    }

	    s=s+ ")";
	}


	void write(MultiPolygon g ) throws WktWriteException
	{
	    s=s+ "MULTIPOLYGON" ;
	    writeCoordinateType( g );

	    if ( g.isEmpty() ) {
	        s=s+ " EMPTY" ;
	        return ;
	    }

	    s=s+ "(";

	    for ( int i = 0; i < g.numGeometries(); i++ ) {
	        if ( i != 0 ) {
	            s=s+ "," ;
	        }

	        writeInner( (Polygon) g.geometryN( i ));
	    }

	    s=s+ ")";
	}

	void write(Triangle g ) throws WktWriteException
	{
	    s=s+ "TRIANGLE" ;
	    writeCoordinateType( g );

	    if ( g.isEmpty() ) {
	        s=s+ " EMPTY" ;
	        return ;
	    }

	    writeInner( g );
	}

	void writeInner(Triangle g ) throws WktWriteException
	{
	    s=s+ "(";
	    s=s+ "(";

	    //close triangle
	    for ( int i = 0; i < 4; i++ ) {
	        if ( i != 0 ) {
	            s=s+ "," ;
	        }

	        writeCoordinate( g.vertex( i ) );
	    }

	    s=s+ ")";
	    s=s+ ")";
	}

	void write(TriangulatedSurface g ) throws WktWriteException
	{
	    s=s+ "TIN" ;
	    writeCoordinateType( g );

	    if ( g.isEmpty() ) {
	        s=s+ " EMPTY" ;
	        return ;
	    }

	    s=s+ "(" ; //begin TIN

	    for ( int i = 0; i < g.numGeometries(); i++ ) {
	        if ( i != 0 ) {
	            s=s+ ",";
	        }

	        writeInner( (Triangle)g.geometryN( i ) );
	    }

	    s=s+ ")" ; //end TIN
	}

	void write(PolyhedralSurface g ) throws WktWriteException
	{
	    s=s+ "POLYHEDRALSURFACE" ;
	    writeCoordinateType( g );

	    if ( g.isEmpty() ) {
	        s=s+ " EMPTY" ;
	        return ;
	    }

	    writeInner( g );
	}

	void writeInner(PolyhedralSurface g ) throws WktWriteException
	{
	    s=s+ "(" ; //begin POLYHEDRALSURFACE

	    for ( int i = 0; i < g.numPolygons(); i++ ) {
	        if ( i != 0 ) {
	            s=s+ ",";
	        }

	        writeInner( g.getPolygons().get( i ) );
	    }

	    s=s+ ")" ; //end POLYHEDRALSURFACE
	}

	public String getString() {
		// TODO Auto-generated method stub
		return s;
	}

	public static String writeGeometry(Geometry g) {
		EwktWriter ew=new EwktWriter("");
		try {
			ew.write(g);
		} catch (WktWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ew.getString();
	}

}
