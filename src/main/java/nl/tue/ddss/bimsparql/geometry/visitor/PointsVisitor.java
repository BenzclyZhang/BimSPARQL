package nl.tue.ddss.bimsparql.geometry.visitor;

import java.util.ArrayList;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryCollection;
import nl.tue.ddss.bimsparql.geometry.GeometryType;
import nl.tue.ddss.bimsparql.geometry.LineString;
import nl.tue.ddss.bimsparql.geometry.MultiLineString;
import nl.tue.ddss.bimsparql.geometry.MultiPoint;
import nl.tue.ddss.bimsparql.geometry.MultiPolygon;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;

public class PointsVisitor extends GeometryVisitor{
	
	List<Point> points=new ArrayList<Point>();

	@Override
	public void visit(Geometry geometry) {
		GeometryType geom=geometry.geometryTypeId();
		switch(geom){
		case TYPE_POINT:
			visitPoint((Point)geometry);
		case TYPE_LINESTRING:
			visitLineString((LineString)geometry);

		case TYPE_TRIANGLE:
			visitTriangle((Triangle)geometry);
		case TYPE_POLYGON : visitPolygon((Polygon)geometry);
		case TYPE_MULTIPOINT:
			visitMultiPoint((MultiPoint)geometry);
		case TYPE_MULTILINESTRING:
			visitMultiLineString((MultiLineString)geometry);
		case TYPE_MULTIPOLYGON:
			visitMultiPolygon((MultiPolygon)geometry);
		case TYPE_GEOMETRYCOLLECTION:
			visitGeometryCollection((GeometryCollection)geometry);
		case TYPE_POLYHEDRALSURFACE:{
			visitPolyhedralSurface((PolyhedralSurface)geometry);
		}case TYPE_TRIANGULATEDSURFACE:{
			visitTriangulatedSurface((TriangulatedSurface)geometry);
		}

		}		
	}
	
	public void visitPoint(Point g )
	{
	    points.add( g );
	}


	public void visitLineString(LineString g )
	{
	    for ( int i = 0; i < g.numPoints(); i++ ) {
	        visitPoint( g.pointN(i));
	    }
	}


	public void visitPolygon(Polygon g )
	{
	    for ( int i = 0; i < g.numRings(); i++ ) {
	        visitLineString( g.ringN( i ) );
	    }
	}


	public void visitTriangle(Triangle g )
	{
	    visitPoint( g.p0 );
	    visitPoint( g.p1);
	    visitPoint( g.p2 );
	}


	public void visitMultiPoint(MultiPoint g )
	{
	    for ( int i = 0; i < g.numGeometries(); i++ ) {
	        visitPoint( g.pointN( i ) );
	    }
	}


	public void visitMultiLineString(MultiLineString g )
	{
	    for ( int i = 0; i < g.numGeometries(); i++ ) {
	        visitLineString( g.lineStringN( i ) );
	    }
	}

	public void visitMultiPolygon(MultiPolygon g )
	{
	    for ( int i = 0; i < g.numGeometries(); i++ ) {
	        visitPolygon( g.polygonN( i ) );
	    }
	}


	public void visitGeometryCollection(GeometryCollection g )
	{
	    for ( int i = 0; i < g.numGeometries(); i++ ) {
	        g.geometryN( i ).accept( this );
	    }
	}


	public void visitPolyhedralSurface(PolyhedralSurface g )
	{
	    for ( int i = 0; i < g.numPolygons(); i++ ) {
	        visitPolygon( g.polygonN( i ) );
	    }
	}


	public void visitTriangulatedSurface(TriangulatedSurface g )
	{
	    for ( int i = 0; i < g.numGeometries(); i++ ) {
	        visitTriangle( g.geometryN( i ) );
	    }
	}
	
	public List<Point> getPoints(){
		return points;
	}


}
