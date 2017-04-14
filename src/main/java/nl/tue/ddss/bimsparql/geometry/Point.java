package nl.tue.ddss.bimsparql.geometry;



import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public interface Point extends Geometry{
    




public default String geometryType()
{
    return "Point";
}

///
///
///
public default GeometryType geometryTypeId()
{
    return GeometryType.TYPE_POINT ;
}

///
///
///
public default int dimension()
{
    return 0 ;
}

///
///
///
public int coordinateDimension();



public boolean isEmpty();

///
///
///
public boolean is3D();


public boolean  isMeasured();

///
///
///
public default void accept( GeometryVisitor visitor ){
	visitor.visit( this );
    return ;
}

public void setM(double measure);



public Point3d asPoint3d();

public double[] getCoordinates();

public double m();

@Override
public default Box getAABB() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public default Box getMVBB() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public default void setAABB(Box aabb){
	
}

@Override
public default void setMVBB(Box mvbb) {
	// TODO Auto-generated method stub
}

@Override
public default Geometry boundary() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public double distance(Geometry other);

@Override
public double distance3D(Geometry other);

@Override
public default int numGeometries() {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public default Geometry geometryN(int i) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public default TriangulatedSurface asTriangulatedSurface() {
	// TODO Auto-generated method stub
	return null;
}

public boolean equals(Point p);

public double x();

public double y();

public double z();



public Point2d asPoint2d();



}