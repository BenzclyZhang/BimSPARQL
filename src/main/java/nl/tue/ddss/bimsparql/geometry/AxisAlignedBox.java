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

public class AxisAlignedBox extends Box{


	
	public AxisAlignedBox(Point3d min, Point3d max){
		super(min,max);
		this.orientation=new BoxOrientation(new Vector3d(1,0,0),new Vector3d(0,1,0),new Vector3d(0,0,1));
	}
			

	public AxisAlignedBox() {
		super();
	}

	public Triangle[] toTriangles() throws GeometryException {
		Point3d[] vertexes = getVertexes();
		return new Triangle[] {
				new Triangle(vertexes[1], vertexes[2], vertexes[6]),
				new Triangle(vertexes[2], vertexes[7], vertexes[6]),
				new Triangle(vertexes[1], vertexes[5], vertexes[6]),
				new Triangle(vertexes[1], vertexes[5], vertexes[4]),
				new Triangle(vertexes[1], vertexes[2], vertexes[3]),
				new Triangle(vertexes[1], vertexes[3], vertexes[4]),
				new Triangle(vertexes[2], vertexes[3], vertexes[0]),
				new Triangle(vertexes[2], vertexes[7], vertexes[0]),
				new Triangle(vertexes[3], vertexes[4], vertexes[5]),
				new Triangle(vertexes[3], vertexes[0], vertexes[5]),
				new Triangle(vertexes[0], vertexes[7], vertexes[6]),
				new Triangle(vertexes[0], vertexes[6], vertexes[5]), };
	}

	public Point3d[] getVertexes() throws GeometryException {
		Point3d[] vertexes = new Point3d[8];
		vertexes[0] = max;
		vertexes[1] = min;
		vertexes[2] = new Point3d(min.x, min.y, max.z);
		vertexes[3] = new Point3d(min.x, max.y, max.z);
		vertexes[4] = new Point3d(min.x, max.y, min.z);
		vertexes[5] = new Point3d(max.x, max.y, min.z);
		vertexes[6] = new Point3d(max.x, min.y, min.z);
		vertexes[7] = new Point3d(max.x, min.y, max.z);
		return vertexes;
	}

	public Segment[] getEdges() throws GeometryException {
		Segment[] edges = new Segment[12];
		Point3d p1 = new Point3d(min.x, min.y, max.z);
		Point3d p2 = new Point3d(min.x, max.y, max.z);
		Point3d p3 = new Point3d(min.x, max.y, min.z);
		Point3d p4 = new Point3d(max.x, max.y, min.z);
		Point3d p5 = new Point3d(max.x, min.y, min.z);
		Point3d p6 = new Point3d(max.x, min.y, max.z);
		edges[0] = new Segment(min, p1);
		edges[1] = new Segment(min, p3);
		edges[2] = new Segment(min, p5);
		edges[3] = new Segment(max, p2);
		edges[4] = new Segment(max, p4);
		edges[5] = new Segment(max, p6);
		edges[6] = new Segment(p1, p2);
		edges[7] = new Segment(p1, p6);
		edges[8] = new Segment(p3, p2);
		edges[9] = new Segment(p3, p4);
		edges[10] = new Segment(p5, p4);
		edges[11] = new Segment(p5, p6);
		return edges;
	}

	public AxisAlignedBox[] ocSplit() throws Exception {
		AxisAlignedBox[] subBoxes = new AxisAlignedBox[8];
		double hx = (max.x - min.x) / 2;
		double hy = (max.y - min.y) / 2;
		double hz = (max.z - min.z) / 2;
		subBoxes[0] = new AxisAlignedBox(min, new Point3d(min.x + hx, min.y + hy, min.z
				+ hz));
		subBoxes[1] = new AxisAlignedBox(new Point3d(min.x + hx, min.y, min.z),
				new Point3d(max.x, min.y + hy, min.z + hz));
		subBoxes[2] = new AxisAlignedBox(new Point3d(min.x, min.y + hy, min.z),
				new Point3d(min.x + hx, max.y, min.z + hz));
		subBoxes[3] = new AxisAlignedBox(new Point3d(min.x + hx, min.y + hy, min.z),
				new Point3d(max.x, max.y, min.z + hz));
		subBoxes[4] = new AxisAlignedBox(new Point3d(min.x, min.y, min.z + hz),
				new Point3d(min.x + hx, min.y + hy, max.z));
		subBoxes[5] = new AxisAlignedBox(new Point3d(min.x + hx, min.y, min.z + hz),
				new Point3d(max.x, min.y + hy, max.z));
		subBoxes[6] = new AxisAlignedBox(new Point3d(min.x, min.y + hy, min.z + hz),
				new Point3d(min.x + hx, max.y, max.z));
		subBoxes[7] = new AxisAlignedBox(new Point3d(min.x + hx, min.y + hy, min.z + hz),
				max);
		return subBoxes;
	}

	public AxisAlignedBox toCube() throws Exception {
		double edge = getLength();
		return new AxisAlignedBox(new Point3d(min.x - edge / 2, min.y - edge / 2, min.z
				- edge / 2), new Point3d(min.x + edge, min.y + edge, min.z
				+ edge));
	}

	public double getLength() throws GeometryException {
		double hx = max.x - min.x;
		double hy = max.y - min.y;
		double hz = max.z - min.z;
		double length = Math.max(Math.max(hx, hy), hz);
		return length;
	}
	
	public double getXYLength(){
		double hx = max.x - min.x;
		double hy = max.y - min.y;
		double length = Math.max(hx, hy);
		return length;
	}
	
	public double getXYWidth(){
		double hx = max.x - min.x;
		double hy = max.y - min.y;
		double width = Math.min(hx, hy);
		return width;
	}
	
	
	public double getXLength(){
		return max.x-min.x;
	}
	
	public double getYLength(){
		return max.y-min.y;
	}
	
	public double getHeight(){
		return max.z-min.z;
	}

	public boolean hasPoint3d(Point3d p) throws GeometryException {
		if (p.x <= max.x && p.x >= min.x && p.y <= max.y && p.y >= min.y
				&& p.z <= max.z && p.z >= min.z) {
			return true;
		}
		return false;
	}
	
	public double getVolume(){
		return getXLength()*getYLength()*getHeight();
	}


	


}
