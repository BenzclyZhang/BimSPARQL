package nl.tue.ddss.bimsparql.geometry;



import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.algorithm.Area;
import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class Triangle implements Geometry{
	
	public Point p0;
	public Point p1;
	public Point p2; 
	final private Point[] vertices={p0,p1,p2};
	private Segment[] edges=new Segment[3];
	
    
	public double area;
	
    public Triangle(){
    	super();
    }

	public Triangle(Point p0, Point p1, Point p2) {
		super();
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		edges[0]=new Segment(p0,p1);
		edges[1]=new Segment(p1,p2);
		edges[2]=new Segment(p2,p0);
		vertices[0]=p0;
		vertices[1]=p1;
		vertices[2]=p2;
	}

  

    public String    geometryType()
    {
        return "Triangle" ;
    }


    ///
    ///
    ///
    public GeometryType   geometryTypeId()
    {
        return GeometryType.TYPE_TRIANGLE ;
    }


    ///
    ///
    ///
    int  coordinateDimension()
    {
        return vertices[0].coordinateDimension() ;
    }


    ///
    ///
    ///
  public  boolean  isEmpty()
    {
        return vertices[0].isEmpty();
    }


    ///
    ///
    ///
    public boolean  is3D()
    {
        return vertices[0].is3D() ;
    }

    ///
    ///
    ///
    public boolean  isMeasured()
    {
        return vertices[0].isMeasured() ;
    }

   public Polygon  toPolygon()
    {
        if ( isEmpty() ) {
            return new Polygon() ;
        }

        List< Point > points =new ArrayList<Point>();

        for ( int i = 0; i < 4; i++ ) {
            points.add( vertex( i ) );
        }

        return new Polygon( new LineString( points ) );
    }

    ///
    ///
    ///
    public void accept(GeometryVisitor visitor)
    {visitor.visit( this );
      
    }

    public Point vertex(int i ){
        return vertices[ i % 3 ];
    }
    
    public Point[] getVertices(){
    	return vertices;
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
	
public double area(){
	return Area.area(this);
}

public boolean hasPoint(Point3d p){
      Vector3d u =GeometryUtils.vectorSubtract(this.p1.asPoint3d(), this.p0.asPoint3d());
      Vector3d v =GeometryUtils.vectorSubtract(this.p2.asPoint3d(), this.p0.asPoint3d());
      Vector3d n=new Vector3d();
      n.cross(u, v);
      Vector3d w=GeometryUtils.vectorSubtract(p, this.p0.asPoint3d());
      double r=Math.abs(GeometryUtils.vectorCross(u,w).dot(n))/n.lengthSquared();
      double b=Math.abs(GeometryUtils.vectorCross(w,v).dot(n))/n.lengthSquared();
      double a=1-r-b;
      if(a>=0&&a<=1&&b>=0&&b<=1&&r>=0&&r<=1){
    	  return true;
      }
      return false;
}

public Segment[] getEdges() {
	// TODO Auto-generated method stub
	return edges;
}
	

    
    
}
