package nl.tue.ddss.bimsparql.geometry;

import java.io.Serializable;


public class InstanceGeometry implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6667317210022534099L;
	private double[] points;
	private int[] pointers;
	private float[] colors;
	private double[] reflection;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String id;
	private String type;
	private int[] materialIndices;
	
	public double[] getPoints() {
		return points;
	}

	public void setPoints(double[] points) {
		this.points = points;
	}

	public int[] getPointers() {
		return pointers;
	}

	public void setPointers(int[] pointers) {
		this.pointers = pointers;
	}

	public float[] getColors() {
		return colors;
	}

	public void setColors(float[] colors) {
		this.colors = colors;
	}

	public double[] getReflection() {
		return reflection;
	}

	public void setReflection(double[] reflection) {
		this.reflection = reflection;
	}

	public InstanceGeometry(){
		
	}

	public void setType(String type) {
		// TODO Auto-generated method stub
		this.type=type;
	}
	
	public String getType(){
		return type;
	}

	public void setMaterialIndices(int[] materialIndices) {
		// TODO Auto-generated method stub
		this.materialIndices=materialIndices;
	}
	
	public int[] getMaterialIndices(){
		return materialIndices;
	}

}
