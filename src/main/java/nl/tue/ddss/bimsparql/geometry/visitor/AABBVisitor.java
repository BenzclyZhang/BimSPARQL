package nl.tue.ddss.bimsparql.geometry.visitor;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;

public class AABBVisitor extends GeometryVisitor {
    
	private AABB box;
	

	public AABBVisitor() {
		box=new AABB();
	}

	@Override
	public void visit(Geometry geometry) {
		PointsVisitor pv=new PointsVisitor();
		geometry.accept(pv);
		for(Point point:pv.getPoints()){
			box.addPoint3d(point.asPoint3d());			
		}
		
	}

	public AABB getAABB() {
		// TODO Auto-generated method stub
		return box;
	}
	
	

}
