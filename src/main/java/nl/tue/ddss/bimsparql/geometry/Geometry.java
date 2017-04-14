package nl.tue.ddss.bimsparql.geometry;


import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public interface Geometry {
	
	public static double EPS=0.00000001;
 
    public boolean isMeasured();
    
    
    
	public Box  getAABB();
	
	public Box getMVBB();
	
	public void setAABB(Box aabb);
	
	public void setMVBB(Box mvbb);
	
	
	
	public void accept(GeometryVisitor visitor);

	///
	///
	///
	public Geometry boundary();


	///
	///
	///
	public double distance( Geometry other );

	///
	///
	///
	public double distance3D( Geometry other );


	///
	///
	///
	public int numGeometries();
	
	



	
/*	boolean equals(Geometry g){
		if ( geometryTypeId() != g.geometryTypeId() ) {
	        return false;
	    }

	    detail::GetPointsVisitor get_points_a, get_points_b;
	    ga.accept( get_points_a );
	    gb.accept( get_points_b );

	    if ( get_points_a.points.size() != get_points_b.points.size() ) {
	        return false;
	    }

	    for ( size_t i = 0; i < get_points_a.points.size(); ++i ) {
	        bool found = false;

	        for ( size_t j = 0; j < get_points_b.points.size(); ++j ) {
	            Point pta = ( get_points_a.points[i] );
	            Point ptb = ( get_points_b.points[j] );

	            if ( pta == ptb ) {
	                found = true;
	                break;
	            }
	        }

	        if ( ! found ) {
	            return false;
	        }
	    }

	    return true;
	}*/
	

	public GeometryType geometryTypeId();

	public boolean isEmpty();

	public boolean is3D();

	public String geometryType();

	public Geometry geometryN(int i);

	public TriangulatedSurface asTriangulatedSurface();


}
