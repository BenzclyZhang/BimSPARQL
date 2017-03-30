package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

public class MultiLineString extends GeometryCollection{
	
	
	private List<LineString> lineStrings=new ArrayList<LineString>();

	public LineString lineStringN(int i) {
		// TODO Auto-generated method stub
		return lineStrings.get(i);
	}

}
