package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class GeometryCollection implements Geometry{
	
	List<Geometry> geometries=new ArrayList<Geometry>();
	Box aabb;
	Box mvbb;

	public void addGeometry(Geometry gg) {
		// TODO Auto-generated method stub
		
	}
	
	public int numGeometries(){
		return geometries.size();
	}

	public Geometry geometryN(int i) {
		// TODO Auto-generated method stub
		return geometries.get(i);
	}

	@Override
	public boolean isMeasured() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void accept(GeometryVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Geometry boundary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double distance(Geometry other) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double distance3D(Geometry other) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GeometryType geometryTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean is3D() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String geometryType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TriangulatedSurface asTriangulatedSurface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Box getAABB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Box getMVBB() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setAABB(Box aabb) {
		this.aabb=aabb;
		
	}

	@Override
	public void setMVBB(Box mvbb) {
		this.mvbb=mvbb;
		
	}
	
	

}
