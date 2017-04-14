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

import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class Point2d extends javax.vecmath.Point2d implements Point{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[] coordinates=new double[2];
	private double measure;
	
	
	public Point2d(){
		
	}
	
	public Point2d(double x, double y) {
		super(x,y);
		coordinates[0]=x;
		coordinates[1]=y;
	}

	@Override
	public int coordinateDimension() {
		// TODO Auto-generated method stub
		return coordinates.length + ( isMeasured() ? 1 : 0 ) ;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return coordinates==null||coordinates.length==0 ;
	}

	@Override
	public boolean is3D() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMeasured() {
		// TODO Auto-generated method stub
		return measure!=0.0;
	}


	@Override
	public void setM(double measure) {
		this.measure=measure;
		
	}

	@Override
	public Point3d asPoint3d() {
		// TODO Auto-generated method stub
		return new Point3d(x,y,0);
	}

	@Override
	public double[] getCoordinates() {
		// TODO Auto-generated method stub
		return coordinates;
	}

	@Override
	public double m() {
		// TODO Auto-generated method stub
		return measure;
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
	public Point2d asPoint2d() {
		// TODO Auto-generated method stub
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
		return 0;
	}

	@Override
	public boolean equals(Point p) {
		if(p instanceof Point2d){
			return this.epsilonEquals(p.asPoint2d(), EPS);
		}
		return false;
	}


    

}
