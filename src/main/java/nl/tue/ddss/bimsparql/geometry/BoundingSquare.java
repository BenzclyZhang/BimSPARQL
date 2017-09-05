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

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;

public class BoundingSquare extends Square {

	public BoundingSquare(Point3d min, Point3d max) throws GeometryException {
		super(min, max);
		// TODO Auto-generated constructor stub
	}

	public BoundingSquare() {
	}

	public void addPoint(Point3d point) {
		if (max == null && min == null) {
			max=new Point3d(point.x,point.y,point.z);
			min=new Point3d(point.x,point.y,point.z);
		}  else {
			if (point.x > max.x) {
				max.x = point.x;
			}
			if (point.y > max.y) {
				max.y = point.y;
			}
			if (point.x < min.x) {
				min.x = point.x;
			}
			if (point.y < min.y) {
				min.y = point.y;
			}
		}
	}
	
	public void addBoundingSquare(BoundingSquare bs){
		addPoint(bs.min);
		addPoint(bs.max);
	}
	
	public void addBoundingBox(AABB bb){
		addPoint(bb.getMin());
		addPoint(bb.getMax());
	}

	public static BoundingSquare getBoundingSquare(Geometry geometry) {
		AABB aabb=AABB.getAABB(geometry);
	
		try {
			return new BoundingSquare(new Point3d(aabb.min.x,aabb.min.y,(aabb.max.z+aabb.min.z)/2),new Point3d(aabb.max.x,aabb.max.y,(aabb.max.z+aabb.min.z)/2));
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	


}
