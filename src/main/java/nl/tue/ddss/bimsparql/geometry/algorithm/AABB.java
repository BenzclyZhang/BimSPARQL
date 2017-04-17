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
package nl.tue.ddss.bimsparql.geometry.algorithm;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.AxisAlignedBox;
import nl.tue.ddss.bimsparql.geometry.BoxOrientation;
import nl.tue.ddss.bimsparql.geometry.Point3d;

public class AABB extends AxisAlignedBox {

	public AABB(Point3d max, Point3d min) throws Exception {
		super(max, min);
		// TODO Auto-generated constructor stub
		super.orientation=new BoxOrientation();
	       orientation.setN1(new Vector3d(1,0,0));
	       orientation.setN2(new Vector3d(0,1,0));
	       orientation.setN3(new Vector3d(0,0,1));
	}

	public AABB() {
       super.orientation=new BoxOrientation();
       orientation.setN1(new Vector3d(1,0,0));
       orientation.setN2(new Vector3d(0,1,0));
       orientation.setN3(new Vector3d(0,0,1));
	}


	public void addPoint3d(Point3d point) {
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
			if (point.z > max.z) {
				max.z = point.z;
			}
			if (point.x < min.x) {
				min.x = point.x;
			}
			if (point.y < min.y) {
				min.y = point.y;
			}
			if (point.z < min.z) {
				min.z = point.z;
			}
		}
	}
	
	public void addBoundingBox(AABB bb){
		addPoint3d(bb.min);
		addPoint3d(bb.max);
	}

	


}
