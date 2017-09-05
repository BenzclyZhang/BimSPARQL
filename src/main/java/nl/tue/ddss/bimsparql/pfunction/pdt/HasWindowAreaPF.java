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

import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Box;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.Plane;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.Area;
import nl.tue.ddss.bimsparql.geometry.algorithm.Projection;
import nl.tue.ddss.bimsparql.geometry.visitor.MVBBVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductNumericalValue;

public class HasWindowAreaPF extends FunctionBaseProductNumericalValue{


	public HasWindowAreaPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double computeValue(Geometry geometry) {
		Box mvbb=geometry.getMVBB();
		if(mvbb==null){
		MVBBVisitor mv=new MVBBVisitor();
		geometry.accept(mv);
		mvbb=mv.getMVBB();
		}
		PolyhedralSurface ps=mvbb.toPolyhedralSurface();
		Polygon largest=null;
		double area=0;
		for(Polygon p:ps.getPolygons()){
			double pArea=0;
			try {
				pArea = Area.area(p);
			} catch (GeometryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Double.NaN;
				
			}
			if(pArea>area){
				largest=p;
				area=pArea;
			}
		}
/*		Plane p=largest.getPlane();
		
		Geometry projection;
		try {
			projection = Projection.projectToPlane(geometry, p);
			return Area.area(projection);
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Double.NaN;
		}*/
		try {
			return Area.area(largest);
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Double.NaN;
		}
	}

}
