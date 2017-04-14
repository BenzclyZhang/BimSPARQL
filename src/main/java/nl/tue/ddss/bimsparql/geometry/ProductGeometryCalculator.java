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
