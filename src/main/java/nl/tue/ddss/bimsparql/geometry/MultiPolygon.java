
package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

public class MultiPolygon extends GeometryCollection{
	
	private List<Polygon> polygons=new ArrayList<Polygon>();

	public Polygon polygonN(int i) {
		// TODO Auto-generated method stub
		return polygons.get(i);
	}

}
