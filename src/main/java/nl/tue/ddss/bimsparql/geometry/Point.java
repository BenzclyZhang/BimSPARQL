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
