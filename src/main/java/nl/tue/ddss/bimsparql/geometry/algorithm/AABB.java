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
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;

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


	public void addPoint3d(javax.vecmath.Point3d point3d) {
		if (max == null && min == null) {
			max=new Point3d(point3d.x,point3d.y,point3d.z);
			min=new Point3d(point3d.x,point3d.y,point3d.z);
		}  else {
			if (point3d.x > max.x) {
				max.x = point3d.x;
			}
			if (point3d.y > max.y) {
				max.y = point3d.y;
			}
			if (point3d.z > max.z) {
				max.z = point3d.z;
			}
			if (point3d.x < min.x) {
				min.x = point3d.x;
			}
			if (point3d.y < min.y) {
				min.y = point3d.y;
			}
			if (point3d.z < min.z) {
				min.z = point3d.z;
			}
		}
	}
	
	public void addBoundingBox(AABB bb){
		if(bb!=null){
		addPoint3d(bb.min);
		addPoint3d(bb.max);
		}
	}

	public static AABB getAABB(Geometry geometry) {
		AABBVisitor visitor=new AABBVisitor();
		if(geometry!=null&&!geometry.isEmpty()){
		geometry.accept(visitor);
		return visitor.getAABB();
		}return null;
	}

	


}
