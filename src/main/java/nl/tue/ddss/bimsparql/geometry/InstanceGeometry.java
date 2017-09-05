/*******************************************************************************
 * Copyright (C) 2017 Chi Zhang
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int id;
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
