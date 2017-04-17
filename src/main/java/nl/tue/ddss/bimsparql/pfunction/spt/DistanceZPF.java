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
package nl.tue.ddss.bimsparql.pfunction.spt;

import java.util.HashMap;

import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseGroupProduct2;

public class DistanceZPF extends FunctionBaseGroupProduct2{

	public DistanceZPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Object computeValue(Geometry g1, Geometry g2) {
		 AABBVisitor visitor1=new AABBVisitor();
			g1.accept(visitor1);
			AABB aabb1=visitor1.getAABB();
			 AABBVisitor visitor2=new AABBVisitor();
				g2.accept(visitor2);
				AABB aabb2=visitor2.getAABB();
				if(aabb1.max.z<aabb2.min.z){
					return aabb2.min.z-aabb1.max.z;
				}else if(aabb2.max.z<aabb1.min.z){
					return aabb1.min.z-aabb2.max.z;
				}	else{
						return 0;
					}			
	}


}
