package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.algorithm.Normal;
import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class Polygon implements Geometry{
	
	List<LineString> rings=new ArrayList<LineString>();



	public Polygon () {
		super();
		rings.add(new LineString());
	}

	public LineString exteriorRing() {
		// TODO Auto-generated method stub
		return rings.get(0);
	}

	public void addRing(LineString interiorRing) {
		rings.add(interiorRing);
		
	}
	

    


	public Polygon (LineString exteriorRing ){
		super();
	    rings.add( exteriorRing );
	}


     public Polygon(Triangle triangle ){
	    
	super();
	    rings.add( new LineString() );

	    if ( ! triangle.isEmpty() ) {
	        for ( int i = 0; i < 4; i++ ) {
	            exteriorRing().addPoint( triangle.vertex( i ) );
	        }
	    }
	}






	public int coordinateDimension(){
	    return rings.get(0).coordinateDimension() ;
	}



	public String geometryType(){
	    return "Polygon" ;
	}


	public GeometryType geometryTypeId(){
	    return GeometryType.TYPE_POLYGON ;
	}




	public boolean isEmpty()
	{
	    return exteriorRing().isEmpty() ;
	}


	public boolean  is3D()
	{
	    return exteriorRing().is3D() ;
	}

	///
	///
	///
	public boolean  isMeasured()
	{
	    return exteriorRing().isMeasured() ;
	}


     public void reverse(){
	    for ( int i = 0; i < numRings(); i++ ) {
        rings.get(i).reverse();
       }
    }

     public int numRings(){
	    return rings.size();
     }



	public void accept( GeometryVisitor visitor )
	{
	    visitor.visit( this );
	}


	public boolean isCounterClockWiseOriented(){
	    return GeometryUtils.isCounterClockWiseOriented( this );
	}

	public LineString ringN(int i) {
		// TODO Auto-generated method stub
		return rings.get(i);
	}

	public int numInteriorRings() {
		// TODO Auto-generated method stub
		return rings.size()>0 ? rings.size()-1 : 0;
	}
	
	public LineString interiorRingN(int i){
		return rings.get(i+1);
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

	public Vector3d getNormal() {
		// TODO Auto-generated method stub
		return Normal.normal(this);
	}




}
