package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.visitor.GeometryVisitor;

public class Solid implements Geometry{
	
	List<PolyhedralSurface> shells=new ArrayList<PolyhedralSurface>();

	public PolyhedralSurface exteriorShell() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addInteriorShell(PolyhedralSurface shell) {
		// TODO Auto-generated method stub
		
	}

	public int numShells() {
		// TODO Auto-generated method stub
		return shells.size();
	}

	public PolyhedralSurface shellN(int i) {
		// TODO Auto-generated method stub
		return shells.get(i);
	}

	public int numInteriorShells() {
		
		return shells.size()-1>0 ? shells.size()-1 : 0;
	}

	public PolyhedralSurface interiorShellN(int i) {
		// TODO Auto-generated method stub
		return shells.get(i+1);
	}

	@Override
	public boolean isMeasured() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AABB boundingBox() {
		// TODO Auto-generated method stub
		return null;
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
	public int numGeometries() {
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
	public Geometry geometryN(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TriangulatedSurface asTriangulatedSurface() {
		// TODO Auto-generated method stub
		return null;
	}

}
