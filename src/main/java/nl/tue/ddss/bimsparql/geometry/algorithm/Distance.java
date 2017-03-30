package nl.tue.ddss.bimsparql.geometry.algorithm;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.GeometryUtils;
import nl.tue.ddss.bimsparql.geometry.LineString;
import nl.tue.ddss.bimsparql.geometry.Plane;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.Segment;
import nl.tue.ddss.bimsparql.geometry.Solid;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.Sphere;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;
import nl.tue.ddss.bimsparql.geometry.visitor.PointsVisitor;


public class Distance {

	private static final double EPS = Geometry.EPS;


//----------------------
//  distance 2D
	
//----------------------

    public static double distance2D(Geometry gA,Geometry gB){
    	return 5;
    }
	

	public double distanceSegmentSegment2D(Segment gA, Segment gB) {
		return 0;
	}


//----------------------
//  distance 3D
	
//----------------------

	public static double distance3D(Geometry gA, Geometry gB) throws GeometryException {

		switch (gA.geometryTypeId()) {
		case TYPE_POINT:
			return distancePointGeometry3D(((Point) gA).asPoint3d(), gB);

		case TYPE_LINESTRING:
			return distanceLineStringGeometry3D((LineString) gA, gB);

		case TYPE_POLYGON:
			return distancePolygonGeometry3D((Polygon) gA, gB);

		case TYPE_TRIANGLE:
			return distanceTriangleGeometry3D((Triangle) gA, gB);

		case TYPE_SOLID:
			return distanceSolidGeometry3D((Solid) gA, gB);

		// collection dispatch
		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
		case TYPE_MULTISOLID:
		case TYPE_GEOMETRYCOLLECTION:
		case TYPE_TRIANGULATEDSURFACE:
		case TYPE_POLYHEDRALSURFACE:
			return distanceGeometryCollectionToGeometry3D(gB, gA);
		}

		throw new GeometryException(
				String.format("distance3D(%s,%s) is not implemented", gA.geometryType(), gB.geometryType()));
	}

	public static double distancePointGeometry3D(Point3d gA, Geometry gB) throws GeometryException {
		switch (gB.geometryTypeId()) {
		case TYPE_POINT:
			return distancePointPoint3D(gA, ((Point) gB).asPoint3d());

		case TYPE_LINESTRING:
			return distancePointLineString3D(gA, (LineString) gB);

		case TYPE_TRIANGLE:
			return distancePointTriangle3D(gA, (Triangle) gB);

		case TYPE_POLYGON:
			return distancePointPolygon3D(gA, (Polygon) gB);
		case TYPE_SOLID:
			return distancePointSolid3D(gA, (Solid) gB);

		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
		case TYPE_MULTISOLID:
		case TYPE_GEOMETRYCOLLECTION:
		case TYPE_TRIANGULATEDSURFACE:
		case TYPE_POLYHEDRALSURFACE:
			return distanceGeometryCollectionToGeometry3D(gB, gA);
		}

		throw new GeometryException(
				String.format("distance3D(%s,%s) is not implemented", gA.geometryType(), gB.geometryType()));
	}

	public static double distancePointPoint3D(Point gA, Point gB) throws GeometryException {
		Segment s = distancePointPoint3DAsGeometry(gA.asPoint3d(), gB.asPoint3d());
		if (s == null) {
			return Double.POSITIVE_INFINITY;
		}
		return s.getLength();
	}

	public static Segment distancePointPoint3DAsGeometry(Point3d gA, Point3d gB) {
		if (gA.isEmpty() || gB.isEmpty()) {
			return null;
		}
		return new Segment(gA, gB);
	}

	public static double distancePointLineString3D(Point3d gA, LineString gB) throws GeometryException {
		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		double dMin = Double.POSITIVE_INFINITY;

		for (int i = 0; i < gB.numSegments(); i++) {
			dMin = Math.min(dMin,
					distancePointSegment3D(gA, new Segment(gB.pointN(i).asPoint3d(), gB.pointN(i + 1).asPoint3d())));
		}

		return dMin;
	}

	public static double distancePointTriangle3D(Point gA, Triangle gB) throws GeometryException {
		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		return distancePointTriangle3D(gA, gB.vertex(0), gB.vertex(1), gB.vertex(2));
	}

	static double distancePointPolygon3D(Point gA, Polygon gB) throws GeometryException {
		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		TriangulatedSurface triangulateSurfaceB = new TriangulatedSurface();
		Triangulation.triangulate(gB, triangulateSurfaceB);
		return distanceGeometryCollectionToGeometry3D(triangulateSurfaceB, gA);
	}

	static double distancePointSolid3D(Point gA, Solid gB) throws GeometryException {
		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		if (Intersects.intersectsPointSolid3D(gA, gB)!=0) {
			return 0.0;
		}

		double dMin = Double.POSITIVE_INFINITY;

		for (int i = 0; i < gB.numShells(); i++) {
			dMin = Math.min(dMin, distanceGeometryCollectionToGeometry3D(gB.shellN(i), gA));
		}

		return dMin;
	}

	static double distanceLineStringGeometry3D(LineString gA, Geometry gB) throws GeometryException{
		// SFCGAL_DEBUG( boost::format("dispatch
		// distanceLineStringGeometry3D(%s,%s)") % gA.asText() % gB.asText() );

		switch (gB.geometryTypeId()) {
		case TYPE_POINT:
			return distancePointLineString3D(((Point) gB).asPoint3d(), gA); // symetric

		case TYPE_LINESTRING:
			return distanceLineStringLineString3D(gA, (LineString) gB);

		case TYPE_TRIANGLE:
			return distanceLineStringTriangle3D(gA, (Triangle) gB);

		case TYPE_POLYGON:
			return distanceLineStringPolygon3D(gA, (Polygon) gB);

		case TYPE_SOLID:
			return distanceLineStringSolid3D(gA, (Solid) gB);

		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
		case TYPE_MULTISOLID:
		case TYPE_GEOMETRYCOLLECTION:
		case TYPE_TRIANGULATEDSURFACE:
		case TYPE_POLYHEDRALSURFACE:
			return distanceGeometryCollectionToGeometry3D(gB, gA);
		}

		throw new GeometryException(
				String.format("distance3D(%s,%s) is not implemented", gA.geometryType(), gB.geometryType()));

	}

	static double distanceLineStringLineString3D(LineString gA, LineString gB) {
		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		int nsA = gA.numSegments();
		int nsB = gB.numSegments();

		double dMin = Double.POSITIVE_INFINITY;

		for (int i = 0; i < nsA; i++) {
			for (int j = 0; j < nsB; j++) {
				dMin = Math.min(dMin,
						distanceSegmentSegment3D(new Segment(gA.pointN(i).asPoint3d(), gA.pointN(i + 1).asPoint3d()),
								new Segment(gB.pointN(j).asPoint3d(), gB.pointN(j + 1).asPoint3d())));
			}
		}

		return dMin;
	}

	public static double distanceLineStringTriangle3D(LineString gA, Triangle gB) throws GeometryException {
		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		double dMin = Double.POSITIVE_INFINITY;

		Point tA = gB.vertex(0);
		Point tB = gB.vertex(1);
		Point tC = gB.vertex(2);

		for (int i = 0; i < gA.numSegments(); i++) {
			dMin = Math.min(dMin, distanceSegmentTriangle3D(gA.pointN(i), gA.pointN(i + 1), tA, tB, tC));
		}

		return dMin;
	}

	public static double distanceLineStringPolygon3D(LineString gA, Polygon gB) throws GeometryException {
		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		TriangulatedSurface triangulateSurfaceB = new TriangulatedSurface();
		Triangulation.triangulate(gB, triangulateSurfaceB);
		return distanceGeometryCollectionToGeometry3D(triangulateSurfaceB, gA);
	}

	public static double distanceLineStringSolid3D(LineString gA, Solid gB) {
		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		if (Intersects.intersectsLineStringSolid3D(gA, gB)!=0) {
			return 0.0;
		}

		double dMin = Double.POSITIVE_INFINITY;

		for (int i = 0; i < gB.numShells(); i++) {
			dMin = Math.min(dMin, gB.shellN(i).distance3D(gA));
		}

		return dMin;
	}

	public static double distanceTriangleGeometry3D(Triangle gA, Geometry gB) throws GeometryException {
		// SFCGAL_DEBUG( boost::format("dispatch
		// distanceTriangleGeometry3D(%s,%s)") % gA.asText() % gB.asText() );

		switch (gB.geometryTypeId()) {
		case TYPE_POINT:
			return distancePointTriangle3D((Point) gB, gA); // symetric

		case TYPE_LINESTRING:
			return distanceLineStringTriangle3D((LineString) gB, gA); // symetric

		case TYPE_TRIANGLE:
			return distanceTriangleTriangle3D(gA, (Triangle) gB);

		case TYPE_POLYGON:
			return distancePolygonGeometry3D((Polygon) gB, gA);

		case TYPE_SOLID:
			return distanceTriangleSolid3D(gA, (Solid) gB);

		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
		case TYPE_MULTISOLID:
		case TYPE_GEOMETRYCOLLECTION:
		case TYPE_TRIANGULATEDSURFACE:
		case TYPE_POLYHEDRALSURFACE:
			return distanceGeometryCollectionToGeometry3D(gB, gA);
		}

		throw new GeometryException(
				String.format("distance3D(%s,%s) is not implemented", gA.geometryType(), gB.geometryType()));
	}

	public static double distanceTriangleSolid3D(Triangle gA, Solid gB) {
		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		if (Intersects.intersectsTriangleGeometry3D(gA, gB)==2) {
			return 0.0;
		}

		double dMin = Double.POSITIVE_INFINITY;

		for (int i = 0; i < gB.numShells(); i++) {
			dMin = Math.min(dMin, gB.shellN(i).distance3D(gA));
		}

		return dMin;
	}

	public static double distancePolygonGeometry3D(Polygon gA, Geometry gB) throws GeometryException {
		// SFCGAL_DEBUG( boost::format("dispatch
		// distancePolygonGeometry3D(%s,%s)") % gA.asText() % gB.asText() );

		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		TriangulatedSurface triangulateSurfaceA = new TriangulatedSurface();
		Triangulation.triangulate(gA, triangulateSurfaceA);
		return distanceGeometryCollectionToGeometry3D(triangulateSurfaceA, gB);
	}

	public static double distanceSolidGeometry3D(Solid gA, Geometry gB) throws GeometryException {
		// SFCGAL_DEBUG( boost::format("dispatch
		// distanceSolidGeometry3D(%s,%s)") % gA.asText() % gB.asText() );

		switch (gB.geometryTypeId()) {
		case TYPE_POINT:
			return distancePointSolid3D((Point) gB, gA); // symetric

		case TYPE_LINESTRING:
			return distanceLineStringSolid3D((LineString) gB, gA); // symetric

		case TYPE_TRIANGLE:
			return distanceTriangleSolid3D((Triangle) gB, gA); // symetric

		case TYPE_POLYGON:
			return distancePolygonGeometry3D((Polygon) gB, gA); // symetric

		case TYPE_SOLID:
			return distanceSolidSolid3D(gA, (Solid) gB);

		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
		case TYPE_MULTISOLID:
		case TYPE_GEOMETRYCOLLECTION:
		case TYPE_TRIANGULATEDSURFACE:
		case TYPE_POLYHEDRALSURFACE:
			return distanceGeometryCollectionToGeometry3D(gB, gA);
		}

		throw new GeometryException(
				String.format("distance3D(%s,%s) is not implemented", gA.geometryType(), gB.geometryType()));
	}

	public static double distanceSolidSolid3D(Solid gA, Solid gB) throws GeometryException {
		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		if (Intersects.intersects3D(gA, gB)==2) {
			return 0.0;
		}

		double dMin = Double.POSITIVE_INFINITY;

		for (int i = 0; i < gA.numShells(); i++) {
			for (int j = 0; j < gB.numShells(); j++) {
				dMin = Math.min(dMin, gA.shellN(i).distance3D(gB.shellN(j)));
			}
		}

		return dMin;
	}



	public static Sphere boundingSphere(Geometry geom) {
		if (geom.isEmpty()) {
			return new Sphere();
		}

		PointsVisitor v = new PointsVisitor();
		geom.accept(v);

		if (v.getPoints().size() == 0) {
			return new Sphere();
		}

		// centroid
		Vector3d c = new Vector3d(0, 0, 0);

		int numPoint = 0;
		while (numPoint < v.getPoints().size()) {
			c.add(v.getPoints().get(numPoint).asPoint3d());
			numPoint++;
		}

		c.scale(1/numPoint);

		// farthest point from centroid
//		Point3d f = c;
		double maxDistanceSq = 0;

		for (int i = 0; i < v.getPoints().size(); i++) {
			Point3d x = v.getPoints().get(i).asPoint3d();
			Vector3d cx = new Vector3d(x.x-c.x,x.y-c.y,x.z-c.z);
			double dSq = cx.length();
			if (dSq > maxDistanceSq) {
//				f = x;
				maxDistanceSq = dSq;
			}
		}

		return new Sphere(Math.sqrt(maxDistanceSq), new Point3d(c.x,c.y,c.z));
	}

	static double distanceGeometryCollectionToGeometry3D(Geometry gA, Geometry gB) throws GeometryException {
		// SFCGAL_DEBUG( boost::format("dispatch
		// distanceGeometryCollectionToGeometry3D(%s,%s)") % gA.asText() %
		// gB.asText() );

		if (gA.isEmpty() || gB.isEmpty()) {
			return Double.MAX_VALUE;
		}

		// if bounding spheres (BS) of gB and gAi don't intersect and
		// if the closest point of BS(gAj) is further than the farest
		// point of BS(gAi) there is no need to compute the distance(gAj, gB)
		// since it will be greater than distance(gAi, gB)
		//
		// The aim is not to find the minimal bounding sphere, but a good
		// enough sphere than
		// encloses all points
		List<Integer> noTest = new ArrayList<Integer>();

		List<Sphere> bsA = new ArrayList<Sphere>();

		for (int i = 0; i < gA.numGeometries(); i++) {
			bsA.add(boundingSphere(gA.geometryN(i)));
		}

		Sphere bsB = boundingSphere(gB);

		if (bsB.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		List<Integer> noIntersect = new ArrayList<Integer>();

		for (int i = 0; i < gA.numGeometries(); i++) {
			if (bsA.get(i).isEmpty()) {
				continue;
			}

			double l2 = Math.pow(GeometryUtils.vectorSubtract(bsB.getCenter(), bsA.get(i).getCenter()).length(), 2);

			if (Math.pow(bsB.getRadius() + bsA.get(i).getRadius(), 2) < l2) {
				noIntersect.add(i);
			}
		}

		for (int i = 0; i < noIntersect.size(); i++) {
			Vector3d vi=GeometryUtils.vectorSubtract(bsA.get(noIntersect.get(i)).getCenter(), bsB.getCenter());
			double li = vi.length();

			for (int j = i; j < noIntersect.size(); j++) {
				Vector3d vj=GeometryUtils.vectorSubtract(bsA.get(noIntersect.get(j)).getCenter(), bsB.getCenter());
				double lj = vj.length();

				if (li + bsA.get(noIntersect.get(i)).getRadius() < lj - bsA.get(noIntersect.get(j)).getRadius()) {
					noTest.add(noIntersect.get(j));
				} else if (lj + bsA.get(noIntersect.get(j)).getRadius() < li
						- bsA.get(noIntersect.get(i)).getRadius()) {
					noTest.add(noIntersect.get(i));
				}
			}
		}

		// if (!noTest.empty()) std::cout << "pruning " << noTest.size() << "/"
		// << gA.numGeometries() << "\n";

		double dMin = Double.POSITIVE_INFINITY;

		for (int i = 0; i < gA.numGeometries(); i++) {
			if (noTest.size() - 1 != noTest.indexOf(i)) {
				continue;
			}

			dMin = Math.min(dMin, distance3D(gA.geometryN(i), gB));
		}

		return dMin;
	}

	static double distancePointSegment3D(Point3d pt, Segment seg) throws GeometryException {
		Vector3d diff = GeometryUtils.vectorSubtract(pt, seg.p0.asPoint3d());
		Vector3d segvec = GeometryUtils.vectorSubtract(seg.p1.asPoint3d(), seg.p0.asPoint3d());
		double d = diff.dot(segvec);
		if (d <= 0)
			return (diff.length());
		if ((d * diff.length()) > (segvec.length()))
			return distancePointPoint3D(pt, seg.p1.asPoint3d());

		Vector3d cr = new Vector3d();
				cr.cross(segvec,diff);
		return cr.length() / (segvec.length());
	}

	static double distancePointTriangle3D(Point3d p, Triangle abc) throws GeometryException {
		Point3d a = abc.p0.asPoint3d();
		Point3d b = abc.p1.asPoint3d();
		Point3d c = abc.p2.asPoint3d();

		/*
		 * project P on ABC plane as projP.
		 */
		Point3d projP = new Plane(a, b, c).projects(p);

		double dMin;

		if (abc.hasPoint(projP)) {
			dMin = distancePointPoint3D(p, projP);
		} else {
			// Else, the distance is the minimum from P to triangle sides
			dMin = distancePointSegment3D(p, new Segment(a, b));
			dMin = Math.min(dMin, distancePointSegment3D(p, new Segment(b, c)));
			dMin = Math.min(dMin, distancePointSegment3D(p, new Segment(c, a)));
		}

		return dMin;
	}

	///
	///
	///
	static double distancePointTriangle3D(Point p_, Point a_, Point b_, Point c_) throws GeometryException {

		Point3d p = p_.asPoint3d();
		Triangle abc = new Triangle(a_.asPoint3d(), b_.asPoint3d(), c_.asPoint3d());

		double dMin = distancePointTriangle3D(p, abc);
		return dMin;
	}

	static double distanceSegmentTriangle3D(Segment sAB, Triangle tABC) throws GeometryException {
		if (Intersects.intersectsSegmentTriangle3D(sAB, tABC)==2) {
			return 0.0;
		}

		/*
		 * else, distance is the min of the following values : - distance from
		 * sA to the Triangle - distance from sB to the Triangle - distance from
		 * sAB to the side of the Triangles
		 */
		double dMin = distancePointTriangle3D(sAB.p0, tABC);
		dMin = Math.min(dMin, distancePointTriangle3D(sAB.p1, tABC));

		for (int i = 0; i < 3; i++) {
			dMin = Math.min(dMin, distanceSegmentSegment3D(sAB, new Segment(tABC.vertex(i), tABC.vertex(i + 1))));
		}

		return dMin;
	}

	private static double distanceSegmentSegment3D(Segment s1, Segment s2) {
		Vector3d u = GeometryUtils.vectorSubtract(s1.p1.asPoint3d(),s1.p0.asPoint3d());
		Vector3d v = GeometryUtils.vectorSubtract(s2.p1.asPoint3d(),s2.p0.asPoint3d());
		Vector3d w = GeometryUtils.vectorSubtract(s1.p0.asPoint3d(),s2.p0.asPoint3d());
		double a = u.dot(u); 
		double b = u.dot(v);
		double c = v.dot(v); 
		double d = u.dot(w);
		double e = v.dot(w);
		double D = a * c - b * b; 
		double sc, sN, sD = D; 
		double tc, tN, tD = D; 

		// compute the line parameters of the two closest points
		if (D < EPS) { // the lines are almost parallel
			sN = 0.0; // force using point P0 on segment S1
			sD = 1.0; // to prevent possible division by 0.0 later
			tN = e;
			tD = c;
		} else { // get the closest points on the infinite lines
			sN = (b * e - c * d);
			tN = (a * e - b * d);
			if (sN < 0.0) { // sc < 0 => the s=0 edge is visible
				sN = 0.0;
				tN = e;
				tD = c;
			} else if (sN > sD) { // sc > 1 => the s=1 edge is visible
				sN = sD;
				tN = e + b;
				tD = c;
			}
		}

		if (tN < 0.0) { // tc < 0 => the t=0 edge is visible
			tN = 0.0;
			// recompute sc for this edge
			if (-d < 0.0)
				sN = 0.0;
			else if (-d > a)
				sN = sD;
			else {
				sN = -d;
				sD = a;
			}
		} else if (tN > tD) { // tc > 1 => the t=1 edge is visible
			tN = tD;
			// recompute sc for this edge
			if ((-d + b) < 0.0)
				sN = 0;
			else if ((-d + b) > a)
				sN = sD;
			else {
				sN = (-d + b);
				sD = a;
			}
		}
		// finally do the division to get sc and tc
		sc = (Math.abs(sN) < EPS ? 0.0 : sN / sD);
		tc = (Math.abs(tN) < EPS ? 0.0 : tN / tD);

		u.scaleAdd(sc, w);
		v.scale(tc);
		Vector3d dP = GeometryUtils.vectorSubtract(u, v);

		return dP.length(); // return the closest distance
	}

	static double distanceSegmentTriangle3D(Point sA_, Point sB_, Point tA_, Point tB_, Point tC_) throws GeometryException {

		Point3d sA = sA_.asPoint3d();
		Point3d sB = sB_.asPoint3d();
		Segment sAB = new Segment(sA, sB);

		Point3d tA = tA_.asPoint3d();
		Point3d tB = tB_.asPoint3d();
		Point3d tC = tC_.asPoint3d();
		Triangle tABC = new Triangle(tA, tB, tC);

		double dMin = distanceSegmentTriangle3D(sAB, tABC);
		return dMin;
	}


	public static double distanceTriangleTriangle3D(Triangle triangleA, Triangle triangleB) throws GeometryException {
		if (Intersects.intersectsTriangleTriangle3D(triangleA, triangleB)==2) {
			return 0;
		}

		double dMin = distanceSegmentTriangle3D(new Segment(triangleA.vertex(0), triangleA.vertex(1)), triangleB);
		dMin = Math.min(dMin,
				distanceSegmentTriangle3D(new Segment(triangleA.vertex(1), triangleA.vertex(2)), triangleB));
		dMin = Math.min(dMin,
				distanceSegmentTriangle3D(new Segment(triangleA.vertex(2), triangleA.vertex(0)), triangleB));

		dMin = Math.min(dMin,
				distanceSegmentTriangle3D(new Segment(triangleB.vertex(0), triangleB.vertex(1)), triangleA));
		dMin = Math.min(dMin,
				distanceSegmentTriangle3D(new Segment(triangleB.vertex(1), triangleB.vertex(2)), triangleA));
		dMin = Math.min(dMin,
				distanceSegmentTriangle3D(new Segment(triangleB.vertex(2), triangleB.vertex(0)), triangleA));

		return dMin;
	}


}
