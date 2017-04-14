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

import java.util.List;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.LineString;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.Triangle;

public class Extrude {
	
	
	
	public static PolyhedralSurface extrude(Triangle t,double deep){
		return extrude(t.toPolygon(),deep);
	}

	public static PolyhedralSurface extrude(Polygon p, double deep) {
		PolyhedralSurface ps = new PolyhedralSurface();
		if (deep == 0) {
			return null;
		} else if (deep > 0) {
			Vector3d normal = Normal.normal(p);
			LineString ls = p.exteriorRing();
			LineString extruded = new LineString();
			for (int i = 0; i < ls.numSegments(); i++) {
				LineString s = new LineString();
				Point3d p0 = ls.segmentN(i).p0.asPoint3d();
				Point3d p1 = ls.segmentN(i).p1.asPoint3d();
				Point3d p2 = new Point3d(p1.x + deep * normal.x, p1.y + deep * normal.y, p1.z + deep * normal.z);
				Point3d p3 = new Point3d(p0.x + deep * normal.x, p0.y + deep * normal.y, p0.z + deep * normal.z);
				s.addPoint(p0);
				s.addPoint(p1);
				s.addPoint(p2);
				s.addPoint(p3);
				s.addPoint(p0);
				Polygon poly = new Polygon(s);
				ps.addPolygon(poly);
				extruded.addPoint(p3);
				if (i == ls.numSegments() - 1) {
					extruded.addPoint(p2);
				}
			}
			ps.addPolygon(new Polygon(extruded));
			ps.addPolygon(new Polygon(getInverseLineString(ls)));
			return ps;
		} else {
			Vector3d normal = Normal.normal(p);
			LineString ls = p.exteriorRing();
			LineString extruded = new LineString();
			for (int i = 0; i < ls.numSegments(); i++) {
				LineString s = new LineString();
				Point3d p0 = ls.segmentN(i).p0.asPoint3d();
				Point3d p1 = ls.segmentN(i).p1.asPoint3d();
				Point3d p2 = new Point3d(p1.x + deep * normal.x, p1.y + deep * normal.y, p1.z + deep * normal.z);
				Point3d p3 = new Point3d(p0.x + deep * normal.x, p0.y + deep * normal.y, p0.z + deep * normal.z);
				s.addPoint(p1);
				s.addPoint(p0);
				s.addPoint(p3);
				s.addPoint(p2);
				s.addPoint(p1);
				Polygon poly = new Polygon(s);
				ps.addPolygon(poly);
				extruded.addPoint(p2);
				if (i == ls.numSegments() - 1) {
					extruded.addPoint(p3);
				}
			}
			ps.addPolygon(new Polygon(extruded));
			ps.addPolygon(new Polygon(ls));
			return ps;
		}

	}

	private static LineString getInverseLineString(LineString ls) {
		LineString l = new LineString();
		List<Point> pointList = ls.getPoints();
		for (int i = pointList.size() - 1; i >= 0; i--) {
			l.addPoint(pointList.get(i));
		}
		return l;
	}

}
