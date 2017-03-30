package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class TriangulatedSurface extends Surface{
	
	List<Triangle> triangles=new ArrayList<Triangle>();
	
	public TriangulatedSurface(){
	    super();
	}


	public TriangulatedSurface(List< Triangle > triangles){
	    super();
	    this.triangles=triangles;
	}



	public String  geometryType()
	{
	    return "TriangulatedSurface" ;
	}


	public GeometryType geometryTypeId(){
	    return GeometryType.TYPE_TRIANGULATEDSURFACE ;
	}


	public int dimension()
	{
	    //surface
	    return 2 ;
	}

	///
	///
	///
	public int coordinateDimension()
	{
	    if ( triangles.size()==0 ) {
	        return 0 ;
	    }
	    else {
	        return triangles.get(0).coordinateDimension() ;
	    }
	}

	///
	///
	///
	public boolean isEmpty()
	{
	    return triangles.size()==0 ;
	}

	public boolean is3D()
	{
	    return triangles.size()!=0 && triangles.get(0).is3D() ;
	}

	///
	///
	///
	public boolean isMeasured()
	{
	    return triangles.size()!=0 && triangles.get(0).isMeasured() ;
	}


	///
	///
	///
	public void  addTriangles(TriangulatedSurface other )
	{
	    triangles.addAll(other.triangles);
	}



	public int  numGeometries()
	{
	    return triangles.size();
	}


	public void accept( GeometryVisitor visitor )
	{
	    visitor.visit( this );
	}



	public void addTriangle(Triangle triangle) {
		// TODO Auto-generated method stub
		triangles.add(triangle);
	}


	public Triangle geometryN(int i) {
		// TODO Auto-generated method stub
		return triangles.get(i);
	}


	public int numTriangles() {
		// TODO Auto-generated method stub
		return triangles.size();
	}


	public Triangle triangleN(int i) {
		// TODO Auto-generated method stub
		return triangles.get(i);
	}


	@Override
	public AABB boundingBox() {
		// TODO Auto-generated method stub
		return null;
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
	public TriangulatedSurface asTriangulatedSurface() {
		// TODO Auto-generated method stub
		return null;
	}



}
