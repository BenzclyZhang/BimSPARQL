package nl.tue.ddss.bimsparql.geometry;

import java.util.List;

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;


public class ProductGeometryCalculator {
	
	Product product;
	
	
	public ProductGeometryCalculator(Product product){
		product=new Product();
	}
	
	public List<List<Triangle>> groupCoplanarTriangles(){
		return null;		
	}

	public double getVolume() {
		double volume=0;
        return volume;
	}
	
	 public double getHeight() throws GeometryException{
		AABB bb=product.getBoundingBox();
		return bb.getHeight();
	 }
	 
	 public double getThickness(){
		 double thickness=0;
		 return thickness;
	 }

}
