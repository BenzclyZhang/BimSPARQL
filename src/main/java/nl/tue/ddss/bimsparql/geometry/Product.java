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

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;





public class Product {
	
	Geometry geometry3d;
	Geometry geometry2d;
	AABB aabb;
	AxisAlignedBox obb;
	
	
	List<Triangle> triangles=new ArrayList<Triangle>();
	AABB box;
	String type;
	String guid;
	
    public Product(){
    	
    }
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public List<Triangle> getTriangles() {
		return triangles;
	}
	
	public Product(List<Triangle> triangles){
		this.triangles=triangles;
		this.box=new AABB();
	}

	public void setTriangles(List<Triangle> triangles) {
		this.triangles = triangles;
	}
	
	public AABB getBoundingBox(){
		for(Triangle t:triangles){
			box.addPoint3d((Point3d)t.getVertices()[0]);
			box.addPoint3d((Point3d)t.getVertices()[1]);
			box.addPoint3d((Point3d)t.getVertices()[2]);
		}
		return box;
	}
	
	public void setBoundingBox(AABB box){
		this.box=box;
	}
	
	public BoundingSquare getBoundingSquare() throws Exception{
		return new BoundingSquare(new Point3d(box.min.x,box.min.y,(box.min.z+box.max.z)/2),new Point3d(box.max.x,box.max.y,(box.min.z+box.max.z)/2));
	}
	
	


}
