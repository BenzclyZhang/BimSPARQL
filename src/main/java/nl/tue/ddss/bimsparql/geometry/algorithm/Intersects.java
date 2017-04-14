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

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.AxisAlignedBox;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.GeometryUtils;
import nl.tue.ddss.bimsparql.geometry.Line3d;
import nl.tue.ddss.bimsparql.geometry.Plane;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.Segment;
import nl.tue.ddss.bimsparql.geometry.Square;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;

public class Intersects {

	static final double EPS = 0.001;
	
	static final int DISJOINTS=0;
	static final int INTERSECTS=1;
	static final int TOUCHES=2;
	static final int CONTAINS=3;
	static final int EQUALS=4;	

	public static int intersects3D(Geometry gA, Geometry gB) throws GeometryException {
		
		switch (gA.geometryTypeId()) {
		case TYPE_POINT:
		case TYPE_LINESTRING:
		case TYPE_POLYGON:
		case TYPE_TRIANGLE:
			return intersectsTriangleGeometry3D((Triangle)gA,gB);
		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
		case TYPE_GEOMETRYCOLLECTION:
		case TYPE_TRIANGULATEDSURFACE:
			return intersectsTriangulatedSurfaceGeometry3D((TriangulatedSurface)gA,gB);
		case TYPE_POLYHEDRALSURFACE:
			return intersectsPolyhedralSurfaceGeometry3D((PolyhedralSurface)gA, gB);
		}
          throw new GeometryException(String.format("Geometry type is not supported: %s",gA.geometryType()));
	}
	
	public static int intersectsTriangleGeometry3D(Triangle gA, Geometry gB) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int intersectsPolyhedralSurfaceGeometry3D(PolyhedralSurface gA, Geometry gB) throws GeometryException {
		switch (gB.geometryTypeId()) {
		case TYPE_POLYHEDRALSURFACE:
			return intersectsPolyhedralSurfacePolyhedralSurface3D(gA, (PolyhedralSurface)gB);
		case TYPE_GEOMETRYCOLLECTION:
			break;
		case TYPE_LINESTRING:
			break;
		case TYPE_MULTILINESTRING:
			break;
		case TYPE_MULTIPOINT:
			break;
		case TYPE_MULTIPOLYGON:
			break;
		case TYPE_POINT:
			break;
		case TYPE_POLYGON:
			break;
		case TYPE_TRIANGLE:
			break;
		case TYPE_TRIANGULATEDSURFACE:
			break;
		default:
			break;
		}
		return 0;
	}

	private static int intersectsPolyhedralSurfacePolyhedralSurface3D(PolyhedralSurface gA, PolyhedralSurface gB) throws GeometryException {
		return trangulatedSurfaceTriangulatedSurfaceIntersection(gA.asTriangulatedSurface(),gB.asTriangulatedSurface());
	}

	public static int intersectsTriangulatedSurfaceGeometry3D(TriangulatedSurface gA, Geometry gB) throws GeometryException {
		switch (gB.geometryTypeId()) {
		case TYPE_TRIANGULATEDSURFACE:
			return trangulatedSurfaceTriangulatedSurfaceIntersection(gA, (TriangulatedSurface)gB);
		case TYPE_GEOMETRYCOLLECTION:
			break;
		case TYPE_LINESTRING:
			break;
		case TYPE_MULTILINESTRING:
			break;
		case TYPE_MULTIPOINT:
			break;
		case TYPE_MULTIPOLYGON:
			break;
		case TYPE_POINT:
			break;
		case TYPE_POLYGON:
			break;
		case TYPE_POLYHEDRALSURFACE:
			break;
		case TYPE_TRIANGLE:
			break;
		default:
			break;
		}
		return 0;
	}
	
	

	public static int trangulatedSurfaceTriangulatedSurfaceIntersection(TriangulatedSurface ts0, TriangulatedSurface ts1) throws GeometryException {
		AABBVisitor bv0=new AABBVisitor();
		ts0.accept(bv0);
		AABB box0 = bv0.getAABB();
		AABBVisitor bv1=new AABBVisitor();
		ts1.accept(bv1);
		AABB box1 = bv1.getAABB();
		int intersection = 0;
		if (box0 == null || box1 == null) {
			return 0;
		} else if (!boxBoxIntersection(box0, box1)) {
			return 0;
		}
		for (Triangle t0 : ts0.getTriangles()) {
			for (Triangle t1 : ts0.getTriangles()) {
				if (intersectsTriangleTriangle3D(t0, t1) == 1) {
					return 1;
				} else if (intersectsTriangleTriangle3D(t0, t1) == 2) {
					intersection = 2;
				}
			}
		}
		return intersection;
	}



	public static int intersectsSegmentTriangle3D(Segment l, Triangle t) throws GeometryException {
		Vector3d u, v, n; // triangle vectors
		Vector3d dir, w0, w; // ray vectors
		double r, a, b; // params to calc ray-plane intersect

		// get triangle edge vectors and plane normal
		u = GeometryUtils.vectorSubtract(t.p1.asPoint3d(), t.p0.asPoint3d());
		v = GeometryUtils.vectorSubtract(t.p2.asPoint3d(), t.p0.asPoint3d());
		n = new Vector3d();
		n.cross(u, v); // cross product
		// if (Math.abs(n.length())< EPS) // triangle is degenerate
		// return -1; // do not deal with this case

		dir = GeometryUtils.vectorSubtract(l.p1.asPoint3d(), l.p0.asPoint3d()); // ray
																				// direction
																				// vector
		w0 = GeometryUtils.vectorSubtract(l.p0.asPoint3d(), t.p0.asPoint3d());
		a = -n.dot(w0);
		b = n.dot(dir);
		if (Math.abs(b) < EPS) { // ray is parallel to triangle plane
			if (Math.abs(a) < EPS) // ray lies in triangle plane
				return 1; //
			else
				return 0; // ray disjoint from plane
		}

		// get intersect point of ray with triangle plane
		r = a / b;
		if (r < 0.0 || r > 1.0) // ray goes away from triangle
			return 0; // => no intersect
		// for a segment, also test if (r > 1.0) => no intersect

		Point3d i = new Point3d(); // intersect point of ray and plane
		dir.scale(r);
		i.add(l.p0.asPoint3d(), dir);
		// is I inside T?
		double uu, uv, vv, wu, wv, d;
		uu = u.dot(u);
		uv = u.dot(v);
		vv = v.dot(v);
		w = GeometryUtils.vectorSubtract(i, t.p0.asPoint3d());
		wu = w.dot(u);
		wv = w.dot(v);
		d = uv * uv - uu * vv;

		// get and test parametric coords
		double s, o;
		s = (uv * wv - vv * wu) / d;
		if (s < 0.0 || s > 1.0) // I is outside T
			return 0;
		o = (uv * wu - uu * wv) / d;
		if (o < 0.0 || (s + o) > 1.0) // I is outside T
			return 0;
		// is I in l?
		if (!(i.x <= Math.max(l.p0.x(), l.p1.x()) + EPS && i.x >= Math.min(l.p0.x(), l.p1.x()) - EPS
				&& i.y <= Math.max(l.p0.y(), l.p1.y()) + EPS && i.y >= Math.min(l.p0.y(), l.p1.y()) - EPS
				&& i.z <= Math.max(l.p0.z(), l.p1.z()) + EPS && i.z >= Math.min(l.p0.z(), l.p1.z()) - EPS)) {
			return 0;
		} else if ((Math.abs(i.x - l.p0.x()) < EPS && Math.abs(i.y() - l.p0.y()) < EPS
				&& Math.abs(i.z() - l.p0.z()) < EPS)
				|| (Math.abs(i.x - l.p1.x()) < EPS && Math.abs(i.y() - l.p1.y()) < EPS
						&& Math.abs(i.z() - l.p1.z()) < EPS)) {
			return 2;
		}

		return 3;// I is in T and in l;
	}

	private static boolean approEqual(double d0, double d1) {
		if (d0 - d1 <= EPS || d1 - d0 <= EPS) {
			return true;
		}
		return false;
	}

	/*
	 * Calculate the spatial relationship between two triangles return 0:
	 * disjointed return 1: intersected return 2: touched
	 */

	public static int intersectsTriangleTriangle3D(Triangle t0, Triangle t1) throws GeometryException {
		Plane pl0 = new Plane(t0.p0.asPoint3d(), t0.p1.asPoint3d(), t0.p2.asPoint3d());
		Plane pl1 = new Plane(t1.p0.asPoint3d(), t1.p1.asPoint3d(), t1.p2.asPoint3d());
		double distance0 = pointPlaneDistance(t0.p0.asPoint3d(), pl1);
		double distance1 = pointPlaneDistance(t0.p1.asPoint3d(), pl1);
		double distance2 = pointPlaneDistance(t0.p2.asPoint3d(), pl1);
		if (approEqual(distance0, distance1) && approEqual(distance1, distance2)) {
			if (distance0 < EPS) {
				if (outsideTriangle(t0.p0.asPoint3d(), t1) && outsideTriangle(t0.p1.asPoint3d(), t1)
						&& outsideTriangle(t0.p2.asPoint3d(), t1) && outsideTriangle(t1.p0.asPoint3d(), t0)
						&& outsideTriangle(t1.p1.asPoint3d(), t0) && outsideTriangle(t1.p2.asPoint3d(), t0))
					return 0;
				else
					return 2;
			} else {
				return 0; // triangles are disjoint and parallel but not on the
							// same plane
			}
		} else {
			Line3d l = intersect3D_2Planes(pl0, pl1);
			Segment s0 = intersectedSegment(l, t0);
			Segment s1 = intersectedSegment(l, t1);
			if (s0 != null && s1 != null) {
				if (s0.getLength() < EPS) {
					Point3d ip = s0.p0.asPoint3d();
					int relation = onSegment(ip, s1);
					if (relation == 1 || relation == 2)
						return 2;
				} else if (s1.getLength() < EPS) {
					Point3d ip = s1.p0.asPoint3d();
					int relation = onSegment(ip, s0);
					if (relation == 1 || relation == 2)
						return 2;
				} else {
					int relation0 = onSegment(s0.p0.asPoint3d(), s1);
					int relation1 = onSegment(s0.p1.asPoint3d(), s1);
					if (relation0 == 1 || relation1 == 1) {
						return 1;
					}
					if (relation0 == 2 || relation1 == 2) {
						return 2;
					}
					return 0;
				}
			} else {
				return 0;
			}
			return 0;
		}
	}

	private static boolean outsideTriangle(Point3d p, Triangle t) {
		double area = t.area();
		double a1 = new Triangle(t.p0, t.p1, p).area();
		double a2 = new Triangle(t.p0, t.p2, p).area();
		double a3 = new Triangle(t.p1, t.p2, p).area();
		return !(Math.abs(a1 + a2 + a3 - area) < EPS);
	}

	/*
	 * Calculate the relationship between a point and a segment, based on the
	 * assumption that they are on the same line. return 0: not on Segment
	 * return 1: at one of the points of segment return 2: inside segment
	 */

	public static int onSegment(Point3d p, Segment s) {
		if (!(p.x <= Math.max(s.p0.x(), s.p1.x()) + EPS && p.x >= Math.min(s.p0.x(), s.p1.x()) - EPS
				&& p.y <= Math.max(s.p0.y(), s.p1.y()) + EPS && p.y >= Math.min(s.p0.y(), s.p1.y()) - EPS
				&& p.z <= Math.max(s.p0.z(), s.p1.z()) + EPS && p.z >= Math.min(s.p0.z(), s.p1.z()) - EPS))
			return 0;
		else {
			if (samePoint(p, s.p0.asPoint3d()) || samePoint(p, s.p1.asPoint3d())) {
				return 2;
			}
			return 1;
		}

	}

	public static Segment intersectedSegment(Line3d l, Triangle t) throws GeometryException {
		ArrayList<Point3d> points = new ArrayList<Point3d>();
		for (Segment edge : t.getEdges()) {
			if (onLine(edge, l)) {
				return edge;
			} else {
				Point3d pt = intersect3D_LineSegment(l, edge);
				if (pt != null)
					points.add(pt);
			}
		}
		if (points.size() == 0) {
			return null;
		} else if (points.size() == 1) {
			return new Segment(points.get(0), points.get(0));
		} else if (points.size() == 2) {
			return new Segment(points.get(0), points.get(1));
		} else if (points.size() == 3) {
			if (samePoint(points.get(0), points.get(1))) {
				return new Segment(points.get(0), points.get(2));
			} else if (samePoint(points.get(0), points.get(2))) {
				return new Segment(points.get(0), points.get(1));
			} else if (samePoint(points.get(1), points.get(2))) {
				return new Segment(points.get(0), points.get(1));
			} else
				throw new GeometryException("A line has three intersects with a triangle");
		} else
			throw new GeometryException("A triangle has more than three edges");
	}

	private static boolean onLine(Segment s, Line3d l) {
		if (sameLine(l, new Line3d(s.p0.asPoint3d(), s.p1.asPoint3d()))) {
			return true;
		}
		return false;
	}

	private static boolean sameLine(Line3d l1, Line3d l2) {
		Point3d p1 = l1.p0;
		Point3d p2 = l1.p1;
		Point3d p3 = l2.p0;
		Point3d p4 = l2.p1;
		Point3d p13 = new Point3d(), p43 = new Point3d(), p21 = new Point3d();
		double d4321, d1321, d4343, d2121;
		double denom;

		p13.x = p1.x - p3.x;
		p13.y = p1.y - p3.y;
		p13.z = p1.z - p3.z;
		p43.x = p4.x - p3.x;
		p43.y = p4.y - p3.y;
		p43.z = p4.z - p3.z;
		p21.x = p2.x - p1.x;
		p21.y = p2.y - p1.y;
		p21.z = p2.z - p1.z;

		d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
		d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
		d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
		d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;

		denom = d2121 * d4343 - d4321 * d4321;
		if (Math.abs(denom) < EPS) {
			if (samePoint(p1, p3) || samePoint(p1, p4)) {
				return true;
			} else {
				double d1313 = p13.x * p13.x + p13.y * p13.y + p13.z * p13.z;
				double deon = d2121 * d1313 - d1321 * d1321;
				if (Math.abs(deon) < EPS) {
					return true;
				}
			}

		}
		return false;
	}

	/*
	 * private static Segment overlap3d_Segments(Segment s1, Segment s2) { //
	 * TODO Auto-generated method stub return null; }
	 */

	private static Point3d intersect3D_LineSegment(Line3d l1, Segment segment) throws GeometryException {
		Line3d l = new Line3d(segment.p0.asPoint3d(), segment.p1.asPoint3d());
		Point3d ip = intersect3D_Lines(l, l1);
		if (ip != null) {
			if (ip.x <= Math.max(l.p0.x, l.p1.x) + EPS && ip.x >= Math.min(l.p0.x, l.p1.x) - EPS
					&& ip.y <= Math.max(l.p0.y, l.p1.y) + EPS && ip.y >= Math.min(l.p0.y, l.p1.y) - EPS
					&& ip.z <= Math.max(l.p0.z, l.p1.z) + EPS && ip.z >= Math.min(l.p0.z, l.p1.z) - EPS)
				return ip;
		}
		return null;
	}

	private static Point3d intersect3D_Lines(Line3d l1, Line3d l2) throws GeometryException {

		Segment distance = distance3D_Lines(l1, l2);
		if (distance.getLength() < EPS) {
			return distance.p0.asPoint3d();
		}
		return null;
	}

	/*
	 * Calculate the line segment PaPb that is the shortest route between two
	 * lines P1P2 and P3P4. Calculate also the values of mua and mub where Pa =
	 * P1 + mua (P2 - P1) Pb = P3 + mub (P4 - P3) Return FALSE if no solution
	 * exists.
	 */
	private static Segment distance3D_Lines(Line3d l1, Line3d l2) {
		Point3d p1 = l1.p0;
		Point3d p2 = l1.p1;
		Point3d p3 = l2.p0;
		Point3d p4 = l2.p1;
		Point3d p13 = new Point3d(), p43 = new Point3d(), p21 = new Point3d();
		double d1343, d4321, d1321, d4343, d2121;
		double numer, denom;

		p13.x = p1.x - p3.x;
		p13.y = p1.y - p3.y;
		p13.z = p1.z - p3.z;
		p43.x = p4.x - p3.x;
		p43.y = p4.y - p3.y;
		p43.z = p4.z - p3.z;
		if (Math.abs(p43.x) < EPS && Math.abs(p43.y) < EPS && Math.abs(p43.z) < EPS)
			return null; // two points are the same
		p21.x = p2.x - p1.x;
		p21.y = p2.y - p1.y;
		p21.z = p2.z - p1.z;
		if (Math.abs(p21.x) < EPS && Math.abs(p21.y) < EPS && Math.abs(p21.z) < EPS)
			return null; // two points are the same

		d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z;
		d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
		d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
		d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
		d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;

		denom = d2121 * d4343 - d4321 * d4321;
		if (Math.abs(denom) < EPS) {

			return null; // lines parallel to each other
		}
		numer = d1343 * d4321 - d1321 * d4343;

		double mua = numer / denom;
		double mub = (d1343 + d4321 * (mua)) / d4343;

		Point3d a = new Point3d();
		Point3d b = new Point3d();
		a.x = p1.x + mua * p21.x;
		a.y = p1.y + mua * p21.y;
		a.z = p1.z + mua * p21.z;
		b.x = p3.x + mub * p43.x;
		b.y = p3.y + mub * p43.y;
		b.z = p3.z + mub * p43.z;
		Segment shortest = new Segment(a, b);
		return (shortest);
	}

	private static boolean samePoint(Point3d p1, Point3d p2) {
		return (approEqual(p1.x, p2.x) && approEqual(p1.y, p2.y) && approEqual(p1.z, p2.z));
	}

	public static Line3d intersect3D_2Planes(Plane pn1, Plane pn2) {
		Vector3d u = new Vector3d();
		u.cross(pn1.getNormal(), pn2.getNormal()); // cross product
		double ax = (u.x >= 0 ? u.x : -u.x);
		double ay = (u.y >= 0 ? u.y : -u.y);
		double az = (u.z >= 0 ? u.z : -u.z);

		// Pn1 and Pn2 intersect in a line
		// first determine max abs coordinate of cross product
		int maxc; // max coordinate
		if (ax > ay) {
			if (ax > az)
				maxc = 1;
			else
				maxc = 3;
		} else {
			if (ay > az)
				maxc = 2;
			else
				maxc = 3;
		}

		// next, to get a point on the intersect line
		// zero the max coord, and solve for the other two
		Point3d iP = new Point3d(); // intersect point
		double d1, d2; // the constants in the 2 plane equations
		d1 = -pn1.normal.dot(new Vector3d(pn1.p0.x, pn1.p0.y, pn1.p0.z)); // note:
																			// could
																			// be
																			// pre-stored
																			// with
																			// plane
		d2 = -pn2.normal.dot(new Vector3d(pn2.p0.x, pn2.p0.y, pn2.p0.z)); // ditto

		switch (maxc) { // select max coordinate
		case 1: // intersect with x=0
			iP.x = 0;
			iP.y = (d2 * pn1.normal.z - d1 * pn2.normal.z) / u.x;
			iP.z = (d1 * pn2.normal.y - d2 * pn1.normal.y) / u.x;
			break;
		case 2: // intersect with y=0
			iP.x = (d1 * pn2.normal.z - d2 * pn1.normal.z) / u.y;
			iP.y = 0;
			iP.z = (d2 * pn1.normal.x - d1 * pn2.normal.x) / u.y;
			break;
		case 3: // intersect with z=0
			iP.x = (d2 * pn1.normal.y - d1 * pn2.normal.y) / u.z;
			iP.y = (d1 * pn2.normal.x - d2 * pn1.normal.x) / u.z;
			iP.z = 0;
		}
		Line3d l = new Line3d(iP, new Point3d(iP.x + u.x, iP.y + u.y, iP.z + u.z));
		return l;
	}

	public static double pointPlaneDistance(Point3d pt, Plane pl) {
		return (pl.a * pt.x + pl.b * pt.y + pl.c * pt.z + pl.d) / Math.sqrt(pl.a * pl.a + pl.b * pl.b + pl.c * pl.c);
	}


	private static boolean triangleTriangleIntersection(Triangle t0, Triangle t1) throws GeometryException {
		for (Segment l : t0.getEdges()) {
			if (intersectsSegmentTriangle3D(l, t1) == 3) {
				return true;
			}
		}
		for (Segment l : t1.getEdges()) {
			if (intersectsSegmentTriangle3D(l, t0) == 3) {
				return true;
			}
		}
		return false;
	}

	public static boolean boxBoxIntersection(AxisAlignedBox box0, AxisAlignedBox box1) throws GeometryException {
		if (box0.min.x > box1.max.x || box0.min.y > box1.max.y || box0.min.z > box1.max.z || box1.min.x > box0.max.x
				|| box1.min.y > box0.max.y || box1.min.z > box0.max.z) {
			return false;
		}
		for (Point3d vertex : box0.getVertexes()) {
			if (boxPointIntersection(box1, vertex)) {
				return true;
			}
		}
		for (Point3d vertex : box1.getVertexes()) {
			if (boxPointIntersection(box0, vertex)) {
				return true;
			}
		}
		for (Triangle t : box0.toTriangles()) {
			for (Triangle tt : box1.toTriangles()) {
				if (triangleTriangleIntersection(t, tt)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean boxPointIntersection(AxisAlignedBox box, Point3d p) {
		if ((p.x < box.max.x || Math.abs(p.x - box.max.x) < EPS) && (p.x > box.min.x || Math.abs(p.x - box.min.x) < EPS)
				&& (p.y < box.max.y || Math.abs(p.y - box.max.y) < EPS)
				&& (p.y > box.min.y || Math.abs(p.y - box.min.y) < EPS)
				&& (p.z < box.max.z || Math.abs(p.z - box.max.z) < EPS)
				&& (p.z > box.min.z || Math.abs(p.z - box.min.z) < EPS)) {
			return true;
		}
		return false;
	}

	public static boolean boxTriangleIntersection(AxisAlignedBox box, Triangle triangle) throws GeometryException {
		for (Point vertex : triangle.getVertices()) {
			if (boxPointIntersection(box, vertex.asPoint3d())) {
				return true;
			}
		}
		for (Segment edge : box.getEdges()) {
			if (intersectsSegmentTriangle3D(edge, triangle) == 3) {
				return true;
			}
		}
		for (Triangle t : box.toTriangles()) {
			if (triangleTriangleIntersection(t, triangle)) {
				return true;
			}
		}
		return false;
	}

	public static boolean squareBoxIntersection(Square square, AxisAlignedBox box) throws GeometryException {
		for (Triangle t : square.toTriangles()) {
			if (boxTriangleIntersection(box, t)) {
				return true;
			}
		}
		return false;
	}

	public static boolean squareTriangleIntersection(Square square, Triangle triangle) throws GeometryException {
		for (Triangle t : square.toTriangles()) {
			if (triangleTriangleIntersection(t, triangle)) {
				return true;
			}
		}
		return false;
	}

	public static boolean boxProductIntersection(AxisAlignedBox box, TriangulatedSurface product) throws GeometryException {
		AABBVisitor bv=new AABBVisitor();
		product.accept(bv);
		if (boxBoxIntersection(box, bv.getAABB())) {
			for (Triangle triangle : product.getTriangles()) {
				if (boxTriangleIntersection(box, triangle)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean squareProductIntersection(Square square, TriangulatedSurface product) throws GeometryException {
		AABBVisitor bv=new AABBVisitor();
		product.accept(bv);
		if (squareBoxIntersection(square, bv.getAABB())) {
			for (Triangle triangle : product.getTriangles()) {
				if (squareTriangleIntersection(square, triangle)) {
					return true;
				}
			}
		}
		return false;
	}



	

}
