package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.algorithm.Triangulation;
import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class PolyhedralSurface extends Surface{
	
	List<Polygon> polygons=new ArrayList<Polygon>();
	
	Box aabb;
	Box mvbb;
	
	public PolyhedralSurface(){
            super();
	}

	public PolyhedralSurface(List< Polygon > polygons ){
super();
	   this.polygons=polygons;
	}



	public String  geometryType(){
	    return "PolyhedralSurface" ;
	}


	public GeometryType  geometryTypeId()
	{
	    return GeometryType.TYPE_POLYHEDRALSURFACE ;
	}


	public int  dimension()
	{
	    return 2 ;
	}

	///
	///
	///
	public int  coordinateDimension()
	{
	    if ( isEmpty() ) {
	        return 0 ;
	    }
	    else {
	        return polygons.get(0).coordinateDimension() ;
	    }
	}


	public boolean isEmpty()
	{
	    return polygons.size()==0;
	}

	public boolean  is3D(){
	
	    if ( isEmpty() ) {
	        return false ;
	    }
	    else {
	        return polygons.get(0).is3D() ;
	    }
	}

	///
	///
	///
	public boolean  isMeasured()
	{
	    if ( isEmpty() ) {
	        return false ;
	    }
	    else {
	        return polygons.get(0).isMeasured() ;
	    }
	}


	TriangulatedSurface  toTriangulatedSurface()
	{
	    TriangulatedSurface result = new TriangulatedSurface() ;
	    GeometryUtils.triangulatePolygon3D(this, result );
	    return result ;
	}


	public void  addPolygon(Polygon polygon )
	{
	    polygons.add(polygon);
	}

	public void  addPolygons(PolyhedralSurface other)
	{
	    polygons.addAll(other.polygons);
	}


	public int   numPolygons()
	{
	    return polygons.size() ;
	}


	public void accept( GeometryVisitor visitor )
	{
	    visitor.visit( this );
	}

	public List<Polygon> getPolygons() {
		// TODO Auto-generated method stub
		return polygons;
	}

	public Polygon polygonN(int i) {
		// TODO Auto-generated method stub
		return polygons.get(i);
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
		return numPolygons();
	}

	@Override
	public Geometry geometryN(int i) {
		// TODO Auto-generated method stub
		return polygonN(i);
	}

	@Override
	public TriangulatedSurface asTriangulatedSurface() {
		TriangulatedSurface t=new TriangulatedSurface();
		Triangulation.triangulate(this, t);
		return t;
	}

	@Override
	public Box getAABB() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public void setAABB(Box aabb) {
		// TODO Auto-generated method stub
		this.aabb=aabb;
	}

	@Override
	public void setMVBB(Box mvbb) {
		// TODO Auto-generated method stub
		this.mvbb=mvbb;
	}

	@Override
	public Box getMVBB() {
		// TODO Auto-generated method stub
		return null;
	}






}
