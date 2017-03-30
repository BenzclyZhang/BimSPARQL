package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

public class MultiSolid extends GeometryCollection{
	
	List<Solid> solids=new ArrayList<Solid>();

	public Solid solidN(int i) {
		// TODO Auto-generated method stub
		return solids.get(i);
	}



}
