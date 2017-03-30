package nl.tue.ddss.bimsparql.geometry.algorithm;

import java.util.ArrayList;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
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

	public Polyhedron(TriangulatedSurface ps) {
		for (int i = 0; i < ps.numTriangles(); i++) {
			Polygon p = ps.triangleN(i).toPolygon();
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
