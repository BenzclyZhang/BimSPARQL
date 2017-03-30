package nl.tue.ddss.bimsparql.geometry;



public class Sphere {
	double radius;
	Point3d center;
	boolean empty;

	public Sphere(double r, Point3d c) {
		radius = r;
		center = c;
		empty = false;
	}

	public Sphere() {
		empty = true;
	}

	public boolean isEmpty() {
		return empty;
	}

	public double getRadius() {
		return radius;
	}

	public Point3d getCenter() {
		return center;
	}
};