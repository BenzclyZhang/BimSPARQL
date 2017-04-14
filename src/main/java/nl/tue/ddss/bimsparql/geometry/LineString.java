package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class LineString implements Geometry{
	
   List<Point> points=new ArrayList<Point>();


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
	public Box getAABB() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Box getMVBB() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setAABB(Box aabb) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setMVBB(Box mvbb) {
		// TODO Auto-generated method stub
		
	}

}
