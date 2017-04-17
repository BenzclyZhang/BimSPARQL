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


import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.algorithm.Distance;
import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class Point3d extends javax.vecmath.Point3d implements Point{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double[] coordinates=new double[3];
	private double measure;
	
    public Point3d(){
    	super();
    }
    
    public Point3d(double x,double y,double z){
    	super(x,y,z);
    	coordinates[0]=x;
    	coordinates[1]=y;
    	coordinates[2]=z;
    }

	public int dimension()
	{
	    return 0 ;
	}

	public int coordinateDimension()
	{
	    return coordinates.length + ( isMeasured() ? 1 : 0 ) ;
	}



	public boolean isEmpty()
	{
	    return coordinates==null||coordinates.length==0 ;
	}

	public boolean is3D()
	{
	    return true ;
	}


	public boolean  isMeasured(){
	    return measure!=0.0 ;
	}



	public void setM(double measure) {
		this.measure=measure;	
	}



	public double[] getCoordinates(){
		return coordinates;
	}

	public double m() {
		// TODO Auto-generated method stub
		return measure;
	}


	@Override
	public double distance(Geometry other) {
		return 0;
	}

	@Override
	public double distance3D(Geometry other) {
			try {
				return Distance.distance3D(this,other);
			} catch (Exception e) {
				e.printStackTrace();
				return 0.0;
			}

	}


	public Point2d asPoint2d() {
		return new Point2d(x,y);
	}
	
	public Point3d asPoint3d(){
		return this;
	}


	@Override
	public void accept(GeometryVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Geometry boundary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numGeometries() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GeometryType geometryTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String geometryType() {
		// TODO Auto-generated method stub
		return null;
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
	public double x() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public double y() {
		// TODO Auto-generated method stub
		return y;
	}

	@Override
	public double z() {
		// TODO Auto-generated method stub
		return z;
	}

	@Override
	public boolean equals(Point p) {
		if (p instanceof Point3d){
			return this.epsilonEquals(p.asPoint3d(), EPS);
		}
		return false;
	}
	
	public Vector3d asVector3d(){
		return new Vector3d(x,y,z);
	}











}
