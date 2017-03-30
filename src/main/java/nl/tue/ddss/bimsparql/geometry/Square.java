package nl.tue.ddss.bimsparql.geometry;



public class Square {
	
	protected Point3d max;
	protected Point3d min;

	public Square(Point3d min, Point3d max) throws Exception {
		if (max.x >= min.x && max.y >= min.y && max.z == min.z) {
			this.max = max;
			this.min = min;
		} else
			throw new Exception(
					"coordinates of max point are smaller than min point");
	}

	public Square() {
		max = null;
		min = null;
	}

	public Triangle[] toTriangles() {
		Point3d[] vertexes = getVertexes();
		return new Triangle[] {
				new Triangle(vertexes[0], vertexes[1], vertexes[2]),
				new Triangle(vertexes[0], vertexes[1], vertexes[3]) };
	}

	public Point3d[] getVertexes() {
		Point3d[] vertexes = new Point3d[8];
		vertexes[0] = max;
		vertexes[1] = min;
		vertexes[2] = new Point3d(min.x, max.y, max.z);
		vertexes[3] = new Point3d(max.x, min.y, max.z);
		return vertexes;
	}

	public Segment[] getEdges() {
		Segment[] edges = new Segment[4];
		Point3d p1 = new Point3d(min.x, max.y, min.z);
		Point3d p2 = new Point3d(max.x, min.y, min.z);
		edges[0] = new Segment(min, p1);
		edges[1] = new Segment(min, p2);
		edges[2] = new Segment(max, p1);
		edges[3] = new Segment(max, p2);
		return edges;
	}

	public Square[] quadSplit() throws Exception {
		Square[] subSquares = new Square[4];
		double hx = (max.x - min.x) / 2;
		double hy = (max.y - min.y) / 2;
		subSquares[0] = new Square(min, new Point3d(min.x + hx, min.y + hy, min.z
				));
		subSquares[1] = new Square(new Point3d(min.x + hx, min.y, min.z),
				new Point3d(max.x, min.y + hy, min.z));
		subSquares[2] = new Square(new Point3d(min.x, min.y + hy, min.z),
				new Point3d(min.x + hx, max.y, min.z ));
		subSquares[3] = new Square(new Point3d(min.x + hx, min.y + hy, min.z),
				new Point3d(max.x, max.y, min.z));
		return subSquares;
	}

	public Square toSquare() throws Exception {
		double edge = getLength();
		return new Square(new Point3d(min.x - edge / 2, min.y - edge / 2, min.z), new Point3d(min.x + edge, min.y + edge, min.z
				));
	}

	public double getLength() {
		double hx = max.x - min.x;
		double hy = max.y - min.y;
		double length = Math.max(hx, hy);
		return length;
	}

	public boolean hasPoint(Point3d p) {
		if (p.x <= max.x && p.x >= min.x && p.y <= max.y && p.y >= min.y
				&& p.z == max.z && p.z ==min.z) {
			return true;
		}
		return false;
	}

	public Point3d getMax() {
		return max;
	}

	public void setMax(Point3d max) {
		this.max = max;
	}

	public Point3d getMin() {
		return min;
	}

	public void setMin(Point3d min) {
		this.min = min;
	}

}
