package nl.tue.ddss.bimsparql.geometry.ewkt;

import java.io.IOException;
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
import nl.tue.ddss.bimsparql.geometry.Point2d;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;

public class EwktReader{
	   
	   String s;
	   boolean is3D       = false;
	   boolean isMeasured = false;
	   int pointer=0;
	   String temp=null;
	
	public EwktReader(String s){
		this.s=s;
	}

    public int readSRID() throws IOException, WktParseException
	{
	    int srid = 0;

	    if ( imatch( "SRID=" ) ) {
	    	try{
	          srid=Integer.parseInt(bufferedRead());
	    	}catch (NumberFormatException e){
	    		throw new WktParseException("");
	    	}
	        if (! match( ';' ) ) {
	            throw new WktParseException( parseErrorMessage() );
	        }
	    }

	    return srid;
	}

	public Geometry  readGeometry() throws WktParseException, IOException{
	   GeometryType   geometryType   = readGeometryType() ;
	   is3D       = imatch( 'Z' );
	   isMeasured = imatch( 'M' );

	    switch ( geometryType ) {
	    case TYPE_POINT : {
	        Point g=null;
	        readInnerPoint(g);
	        return g ;
	    }

	    case TYPE_LINESTRING: {
	        LineString g=new LineString();
	        readInnerLineString( g );
	        return g ;
	    }

	    case TYPE_TRIANGLE: {
	        Triangle g= new Triangle() ;
	        readInnerTriangle( g );
	        return g ;
	    }

	    case TYPE_POLYGON: {
	        Polygon g=new Polygon() ;
	        readInnerPolygon( g );
	        return g ;
	    }

	    case TYPE_MULTIPOINT : {
	        MultiPoint g = new MultiPoint();
	        readInnerMultiPoint( g );
	        return g ;
	    }

	    case TYPE_MULTILINESTRING : {
	        MultiLineString g=new MultiLineString();
	        readInnerMultiLineString( g );
	        return g ;
	    }

	    case TYPE_MULTIPOLYGON : {
	        MultiPolygon g=new MultiPolygon() ;
	        readInnerMultiPolygon( g );
	        return g;
	    }

	    case TYPE_GEOMETRYCOLLECTION : {
	        GeometryCollection g= new GeometryCollection();
	        readInnerGeometryCollection( g );
	        return g ;
	    }

	    case TYPE_TRIANGULATEDSURFACE : {
	        TriangulatedSurface  g= new TriangulatedSurface() ;
	        readInnerTriangulatedSurface( g );
	        return g ;
	    }

	    case TYPE_POLYHEDRALSURFACE : {
	        PolyhedralSurface g=new PolyhedralSurface() ;
	        readInnerPolyhedralSurface( g );
	        return g ;
	    }
	    }

	    throw new WktParseException( "unexpected geometry" );
	}


	public GeometryType readGeometryType() throws WktParseException{
		temp=bufferedRead();
	    if ( imatch( "POINT" ) ) {
	        return GeometryType.TYPE_POINT ;
	    }
	    else if ( imatch( "LINESTRING" ) ) {
	        return GeometryType.TYPE_LINESTRING ;
	    }
	    else if ( imatch( "POLYGON" ) ) {
	        return GeometryType.TYPE_POLYGON ;
	    }
	    else if ( imatch( "TRIANGLE" ) ) {
	        //not official
	        return GeometryType.TYPE_TRIANGLE ;
	    }
	    else if ( imatch( "MULTIPOINT" ) ) {
	        return GeometryType.TYPE_MULTIPOINT ;
	    }
	    else if ( imatch( "MULTILINESTRING" ) ) {
	        return GeometryType.TYPE_MULTILINESTRING ;
	    }
	    else if ( imatch( "MULTIPOLYGON" ) ) {
	        return GeometryType.TYPE_MULTIPOLYGON ;
	    }
	    else if ( imatch( "GEOMETRYCOLLECTION" ) ) {
	        return GeometryType.TYPE_GEOMETRYCOLLECTION ;
	    }
	    else if ( imatch( "TIN" ) ) {
	        return GeometryType.TYPE_TRIANGULATEDSURFACE ;
	    }
	    else if ( imatch( "POLYHEDRALSURFACE" ) ) {
	        return GeometryType.TYPE_POLYHEDRALSURFACE ;
	    }
       
	    throw new WktParseException( "can't parse WKT geometry type (" + context() + ")"  );
	}



	void   readInnerPoint( Point g ) throws WktParseException{
		temp=bufferedRead();
	    if ( imatch( "EMPTY" ) ) {
	        return;
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException(parseErrorMessage() );
	    }

	    g=readPointCoordinate();

	    if ( ! match( ')' ) ) {
	        throw new WktParseException (parseErrorMessage()  );
	    }
	}


	public void   readInnerLineString( LineString g ) throws WktParseException
	{
	    if ( imatch( "EMPTY" ) ) {
	        return ;
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    while ( !eof() ) {

            Point p=null;
	        if ( (p=readPointCoordinate())!=null ) {
	            g.addPoint( p);
	        }
	        else {
	            throw new WktParseException( parseErrorMessage() );
	        }

	        if ( match( ',' ) ) {
	            continue ;
	        }

	        break;
	    }


	    if ( g.numPoints() < 2 ) {
	        throw new WktParseException( "WKT parse error, LineString should have at least 2 points" );
	    }


	    if ( ! match( ')' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }
	}


	public void   readInnerPolygon( Polygon g ) throws WktParseException
	{
	    if ( imatch( "EMPTY" ) ) {
	        return ;
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    for( int i = 0; ! eof() ; i++ ) {
	        if ( i == 0 ) {
	            readInnerLineString( g.exteriorRing() ) ;
	        }
	        else {
	            LineString interiorRing=new LineString();
	            readInnerLineString( interiorRing ) ;
	            g.addRing( interiorRing ) ;
	        }

	        if ( ! match( ',' ) ) {
	            break ;
	        }
	    }

	    if ( ! match( ')' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }
	}



	void   readInnerTriangle( Triangle g ) throws WktParseException{
	    if ( imatch( "EMPTY" ) ) {
	        return ;
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    // 4 points to read
	    List< Point > points =new ArrayList<Point>();

	    while ( ! eof() ) {
	    	Point p=readPointCoordinate();
	        points.add( p ) ;


	        if (!match( ',') ) {
	            break;
	        }
	    }

	    if ( points.size() != 4 ) {
	        throw new WktParseException( "WKT parse error, expected 4 points for triangle" );
	    }

	    if (! (points.get(points.size()-1)).equals(points.get(0)) ) {
	        throw new WktParseException( "WKT parse error, first point different of the last point for triangle" );
	    }
	    
	    g.p0=points.get(0);
	    g.p1=points.get(1);
	    g.p2=points.get(2);

	    g = new Triangle( points.get(0), points.get(1), points.get(2) );

	    if ( ! match( ')' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    if ( ! match( ')' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }
	}


private void    readInnerMultiPoint( MultiPoint g ) throws WktParseException, IOException{
	    if ( imatch( "EMPTY" ) ) {
	        return ;
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    while( ! eof() ) {
	        Point p=null ;

	        if ( !imatch( "EMPTY" ) ) {
	            // optional open/close parenthesis
	            boolean parenthesisOpen = false ;

	            if ( match( '(' ) ) {
	                parenthesisOpen = true ;
	            }

	           p= readPointCoordinate();

	            if ( parenthesisOpen && ! match( ')' ) ) {
	                throw new WktParseException( parseErrorMessage() );
	            }
	        }

	        if ( !p.isEmpty() ) g.addGeometry( p );

	        //break if not followed by another points
	        if ( ! match( ',' ) ) {
	            break ;
	        }
	    }

	    if ( ! match( ')' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	}


	private void   readInnerMultiLineString( MultiLineString g ) throws WktParseException, IOException
	{
	    if ( imatch( "EMPTY" ) ) {
	        return ;
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    while( ! eof() ) {

	        LineString lineString= new LineString() ;
	        readInnerLineString( lineString );
	        if ( !lineString.isEmpty() ) g.addGeometry( lineString);
	        if ( ! match( ',' ) ) {
	            break ;
	        }
	    }

	    if ( ! match( ')' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }
	}

	///
	///
	///
	void   readInnerMultiPolygon( MultiPolygon g ) throws WktParseException, IOException
	{
	    if ( imatch( "EMPTY" ) ) {
	        return ;
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    while( ! eof() ) {

	        Polygon polygon=new Polygon() ;
	        readInnerPolygon( polygon );
	        if ( !polygon.isEmpty() ) g.addGeometry( polygon );

	        //break if not followed by another points
	        if ( ! match( ',' ) ) {
	            break ;
	        }
	    }

	    if ( ! match( ')' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }
	}



	///
	///
	///
	void   readInnerGeometryCollection( GeometryCollection g ) throws WktParseException, IOException
	{
	    if ( imatch( "EMPTY" ) ) {
	        return ;
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    while( ! eof() ) {

	        Geometry gg = readGeometry();
	        if ( !gg.isEmpty() )
	            g.addGeometry( gg );

	        if ( ! match( ',' ) ) {
	            break ;
	        }
	    }

	    if ( ! match( ')' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }
	}


	void  readInnerTriangulatedSurface( TriangulatedSurface g ) throws WktParseException, IOException
	{
	    if ( imatch( "EMPTY" ) ) {
	        return ;
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    while( ! eof() ) {
	        Triangle triangle= new Triangle() ;
	        readInnerTriangle( triangle );
	        g.addTriangle( triangle);
	        if ( ! match( ',' ) ) {
	            break ;
	        }
	    }


	    if ( ! match( ')' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }
	}

	private boolean eof() {
		// TODO Auto-generated method stub
		return false;
	}




	///
	///
	///





	void readInnerPolyhedralSurface( PolyhedralSurface g ) throws WktParseException, IOException
	{
	    if ( imatch( "EMPTY" ) ) {
	        return ;
	    }

	    if ( ! match( '(' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }

	    while( ! eof() ) {
	         Polygon polygon=new Polygon();
	        readInnerPolygon( polygon );
	        g.addPolygon( polygon);

	        //break if not followed by another points
	        if ( ! match( ',' ) ) {
	            break ;
	        }
	    }

	    if ( ! match( ')' ) ) {
	        throw new WktParseException( parseErrorMessage() );
	    }
	}



	private Point readPointCoordinate() throws WktParseException{
	    List<Double> coordinates=new ArrayList<Double>() ;
        Point p=null;
	    if ( imatch( "EMPTY" ) ) {
	        return p;
	    }

	    while (!(match(')')||match(',') )) {
	        coordinates.add(Double.parseDouble(bufferedRead()) );
	    }
        pointer--;
	    if ( coordinates.size() < 2 ) {
	        throw new WktParseException(
	                                    "WKT parse error, Coordinate dimension < 2 ");
	    }

	    if ( coordinates.size() > 4 ) {
	        throw new WktParseException( "WKT parse error, Coordinate dimension > 4"  );
	    }

	    if ( isMeasured && is3D ) {
	        // XYZM
	        if ( coordinates.size() != 4 ) {
	            throw new WktParseException( "bad coordinate dimension" );
	        }

	        p = new Point3d( coordinates.get(0), coordinates.get(1), coordinates.get(2));
	        p.setM( coordinates.get(3) );
	    }
	    else if ( isMeasured && ! is3D ) {
	        // XYM
	        if ( coordinates.size() != 3 ) {
	            throw new WktParseException( "bad coordinate dimension (expecting XYM coordinates)" );
	        }

	        p = new Point2d( coordinates.get(0), coordinates.get(1) );
	        p.setM( coordinates.get(2));
	    }
	    else if ( coordinates.size() == 3 ) {
	        // XYZ
	        p = new Point3d( coordinates.get(0), coordinates.get(1), coordinates.get(2) );
	    }
	    else {
	        // XY
	        p = new Point2d( coordinates.get(0), coordinates.get(1) );
	    }

	    return p ;
	}



	String parseErrorMessage()
	{
	    String s;
	    s ="WKT parse error (" +context() +")" ;
	    return s;
	}
	
	private String context() {
		// TODO Auto-generated method stub
		return null;
	}


    private boolean match(char c){
    	char c1=s.charAt(pointer);
		while (c1==' '){
			pointer=pointer+1;
			c1=s.charAt(pointer);
		}
    	boolean result=c1==c;
    	if(result) pointer++;
    	return result;
    }
    

	private boolean imatch(String s){
		int p=pointer;
if (temp==null){
	boolean result=bufferedRead().equalsIgnoreCase(s);
  if(!result) pointer=p;
		return result;
}else {
	boolean result=temp.equalsIgnoreCase(s);
	if (result) temp=null;
	return result;
}
	}
	
	private boolean imatch(char c){
		char c1=s.charAt(pointer);
		while (c1==' '){
			pointer=pointer+1;
			c1=s.charAt(pointer);
		}
		boolean result=String.valueOf(c1).equalsIgnoreCase(String.valueOf(c));
		if(result) pointer++;
		return result;
	}
	

	
	public String bufferedRead(){
		StringBuilder sb=new StringBuilder();
		char c;
		while(pointer<s.length()){
			c=s.charAt(pointer);
			switch (c){
				case ' ':{
				pointer++;
				if(sb.length()>0)
				return sb.toString();
				continue;
				}
				case '(': {	
					return sb.toString();
				}
				case ',': {
					return sb.toString();
					
				}
				case ')':{
					return sb.toString();
		        }
				case ';':{
					return sb.toString();
				}
				default:

			   sb.append(c);
				pointer++;
		}
		}
		return sb.toString();
	}

    

}
