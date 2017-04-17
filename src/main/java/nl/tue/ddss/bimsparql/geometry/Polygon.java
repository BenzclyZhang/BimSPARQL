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

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.algorithm.Normal;
import nl.tue.ddss.bimsparql.geometry.algorithm.Triangulation;
import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class Polygon implements Geometry{
	
	List<LineString> rings=new ArrayList<LineString>();

    AABB aabb;
    Box mvbb;

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
		TriangulatedSurface ts=new TriangulatedSurface();
		Triangulation.triangulate(this, ts);
		return ts;
	}

	public Vector3d getNormal() {
		// TODO Auto-generated method stub
		return Normal.normal(this);
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
		this.aabb=aabb;
		
	}

	@Override
	public void setMVBB(Box mvbb) {
		// TODO Auto-generated method stub
		this.mvbb=mvbb;
	}

	public Plane getPlane() {
		LineString ls=this.exteriorRing();
		return new Plane(ls.pointN(0).asPoint3d(),ls.pointN(1).asPoint3d(),ls.pointN(2).asPoint3d());
	}




}
