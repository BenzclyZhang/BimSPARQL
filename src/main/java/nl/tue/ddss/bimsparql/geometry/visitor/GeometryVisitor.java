package nl.tue.ddss.bimsparql.geometry.visitor;

import nl.tue.ddss.bimsparql.geometry.Geometry;

public abstract class GeometryVisitor {

	public abstract void visit(Geometry geometry);

}
