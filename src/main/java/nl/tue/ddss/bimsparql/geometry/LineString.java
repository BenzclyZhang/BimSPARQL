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

import java.util.ArrayList;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class LineString implements Geometry{
	
   List<Point> points=new ArrayList<Point>();
   AABB aabb;
   Box mvbb;


	public LineString( List<Point> points ){
	    super();
	 this.points=points;
	}


	LineString(Point startPoint, Point endPoint){
	    points.add( startPoint);
	    points.add( endPoint);
	}
	


	public LineString() {
	}


	public GeometryType geometryTypeId()
	{
	    return GeometryType.TYPE_LINESTRING ;
	}


	public String geometryType()
	{
	    return "LineString" ;
	}


	public int dimension()
	{
	    return 1 ;
	}


	public int   coordinateDimension()
	{
	    return isEmpty() ? 0 : points.get(0).coordinateDimension() ;
	}


	public boolean   isEmpty()
	{
	    return points.size()==0 ? true:false;
	}


	public boolean  is3D()
	{
	    return ! isEmpty() && startPoint().is3D() ;
	}

	private Point startPoint() {
		if(points.size()>0){
		return points.get(0);
		}return null;
	}
	
	private Point endPoint() {
		if(points.size()>0){
		return points.get(points.size()-1);
		}return null;
	}


	public boolean  isMeasured()
	{
	    return ! isEmpty() && startPoint().isMeasured() ;
	}


	public void clear()
	{
	    points.clear();
	}


	public void reverse()
	{
	    List<Point> pointList=points;
	    points=new ArrayList<Point>();
	    for (int i=pointList.size()-1;i>=0;i--){
	    	points.add(pointList.get(i));
	    }
	}


	public int numSegments()
	{
	    if ( points.size()==0 ) {
	        return 0 ;
	    }
	    else {
	        return points.size() - 1 ;
	    }
	}
	
	public Segment segmentN(int i){
		return new Segment(points.get(i),points.get(i+1));
	}


	boolean isClosed()
	{
	    return ( ! isEmpty() ) && ( startPoint().equals(endPoint() )) ;
	}


    

	public void accept( GeometryVisitor visitor )
	{ visitor.visit( this );
	    return;
	}

	public void addPoint(Point p) {
		// TODO Auto-generated method stub
		points.add(p);
	}

	public int numPoints() {
		// TODO Auto-generated method stub
		return points.size();
	}


	public Point pointN(int i) {
		// TODO Auto-generated method stub
		return points.get(i);
	}
	
	public List<Point> getPoints(){
		return points;
	}


	@Override
	public Geometry boundary() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public double distance(Geometry other) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double distance3D(Geometry other) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int numGeometries() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Geometry geometryN(int i) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public TriangulatedSurface asTriangulatedSurface() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public AABB getAABB() {
		// TODO Auto-generated method stub
		return aabb;
	}


	@Override
	public Box getMVBB() {
		// TODO Auto-generated method stub
		return mvbb;
	}



	@Override
	public void setAABB(AABB aabb) {
		// TODO Auto-generated method stub
		this.aabb=aabb;
	}


	@Override
	public void setMVBB(Box mvbb) {
		// TODO Auto-generated method stub
		this.mvbb=mvbb;
	}



}
