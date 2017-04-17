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
package nl.tue.ddss.bimsparql.pfunction.pdt;

import java.util.HashMap;
import java.util.List;

import javax.vecmath.Vector3d;

import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.Normal;
import nl.tue.ddss.bimsparql.geometry.algorithm.Stitching;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductEwktValue;

public class HasUpperSurfacePF extends FunctionBaseProductEwktValue{

	public HasUpperSurfacePF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String computeValue(Geometry geometry) {
		List<TriangulatedSurface> surfaces=null;
		try {
			surfaces = new Stitching().stitches(geometry);
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		TriangulatedSurface top=null;
		for (TriangulatedSurface surface:surfaces){
			if(top!=null){
				if(elevation(surface)>elevation(top)){
					top=surface;
				}
			}else{
				top=surface;
			}
		}
		Vector3d normal=Normal.normal(top.triangleN(0));
		double dot=normal.dot(new Vector3d(0,0,1));
		if ((dot>1-EPS&&dot<1+EPS)||(dot>-1-EPS&&dot<-1+EPS)){
		return writeEwkt(top);
		}else{
			return null;
		}
	}
	
	protected double elevation(Geometry geometry){
		if(geometry.getAABB()!=null){
			return (geometry.getAABB().min.z+geometry.getAABB().max.z)/2;
		}
		AABBVisitor visitor=new AABBVisitor();
		geometry.accept(visitor);
		geometry.setAABB(visitor.getAABB());
		return (visitor.getAABB().min.z+visitor.getAABB().max.z)/2;
	}
	

}
