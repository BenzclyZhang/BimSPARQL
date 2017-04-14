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
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;

public class Polyhedron {

	final static double EPS = Geometry.EPS;

	List<Face> faces = new ArrayList<Face>();
	List<Edge> edges = new ArrayList<Edge>();
	List<Vertex> vertices = new ArrayList<Vertex>();
	Vertex lastVertex;

	public Polyhedron(PolyhedralSurface ps) {
		for (int i = 0; i < ps.numPolygons(); i++) {
			Polygon p = ps.polygonN(i);
			Face f = new Face(p, i);
			for (int j = 0; j < p.exteriorRing().numPoints(); j++) {
				Point pt = p.exteriorRing().pointN(j);
				Vertex v = addPoint(pt);
				f.addVertex(v);
				if (lastVertex != null) {
					HalfEdge hf = new HalfEdge(lastVertex, v);
					f.addHalfEdge(hf);
					
					Edge e = addEdge(hf);
					e.faces.add(f);
					v.edges.add(e);
				}
				if (j != p.exteriorRing().numPoints() - 1) {
					lastVertex = v;
				} else {
					lastVertex = null;
				}

			}
			faces.add(f);
		}
	}
	
	public Polyhedron(TriangulatedSurface ps) {
		for (int i = 0; i < ps.numTriangles(); i++) {
			Triangle p = ps.triangleN(i);
			Polygon polygon=p.toPolygon();
			Face f = new Face(polygon, i);
			for (int j = 0; j < polygon.exteriorRing().numPoints(); j++) {
				Point pt = polygon.exteriorRing().pointN(j);
				Vertex v = addPoint(pt);
				f.addVertex(v);
				if (lastVertex != null) {
					HalfEdge hf = new HalfEdge(lastVertex, v);
					f.addHalfEdge(hf);
					Edge e = addEdge(hf);
					e.faces.add(f);
					v.edges.add(e);
				}
				if (j != polygon.exteriorRing().numPoints() - 1) {
					lastVertex = v;
				} else {
					lastVertex = null;
				}

			}
			faces.add(f);
		}
	}
	
	public List<Vertex> neighborVertices(Vertex vertex){
		List<Vertex> neighbors=new ArrayList<Vertex>();
		for (Edge edge:edges){
			if (edge.hasVertex(vertex)){
				neighbors.add(edge.anotherVertex(vertex));
			}
		}
		return neighbors;
	}
	

	private Vertex addPoint(Point pt) {
		for (int i = 0; i < vertices.size(); i++) {
			if (vertices.get(i).pnt.epsilonEquals(pt.asPoint3d(), EPS)) {
				return vertices.get(i);
			}
		}
		vertices.add(new Vertex(pt.asPoint3d(), vertices.size()));
		return vertices.get(vertices.size() - 1);

	}

	private Edge addEdge(HalfEdge h) {
		Edge e = null;
		for (int i = 0; i < edges.size(); i++) {
			if (edges.get(i).halfEdges.size() == 2) {
				continue;
			} else if (edges.get(i).halfEdges.size() == 1) {
				HalfEdge inverse = edges.get(i).halfEdges.get(0);
				if (inverse.start == h.end && inverse.end == h.start) {
					edges.get(i).halfEdges.add(h);
					h.setEdge(edges.get(i));
					e = edges.get(i);
					return e;
				}
			}

		}
		e = new Edge();
		e.halfEdges.add(h);
		h.setEdge(e);
		edges.add(e);
		return e;
	}


	public List<Face> getFaces() {
		// TODO Auto-generated method stub
		return faces;
	}

	public List<Edge> getEdges() {
		// TODO Auto-generated method stub
		return edges;
	}

	public List<Vertex> getVertices() {
		// TODO Auto-generated method stub
		return vertices;
	}

}
