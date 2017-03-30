package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

public abstract class Surface implements Geometry{
	
      public Surface(){
	       super();
     }

	///
	///
	///
	int  dimension()
	{
	    return 2 ;
	}

	
	List<Triangle> triangles=new ArrayList<Triangle>();
	double area;
	public List<Triangle> getTriangles() {
		return triangles;
	}
	public void setTriangles(List<Triangle> triangles) {
		this.triangles = triangles;
	}
	public double getArea() {
		return area;
	}
	public void setArea(double area) {
		this.area = area;
	}
	
	

}
