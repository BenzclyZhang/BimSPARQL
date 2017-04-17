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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.util.IterLib;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import nl.tue.ddss.bimsparql.geometry.Box;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.Plane;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.Area;
import nl.tue.ddss.bimsparql.geometry.algorithm.Projection;
import nl.tue.ddss.bimsparql.geometry.algorithm.Stitching;
import nl.tue.ddss.bimsparql.geometry.visitor.MVBBVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductOnGraph;
import nl.tue.ddss.convert.Namespace;

public class HasGrossWallAreaPF extends FunctionBaseProductOnGraph{


	public HasGrossWallAreaPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}
	



	@Override
	protected QueryIterator verifyValue(Binding binding, Graph graph, Node product, Geometry geometry, Node object,
			ExecutionContext execCxt) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected QueryIterator getValue(Binding binding, Graph graph, Node product, Geometry geometry, Var alloc,
			ExecutionContext execCxt) {
	    double a=computeValueByMVBB(geometry);
	    double b=computeValueBySurfaceArea(product,geometry,execCxt);
	    double area=Double.min(a, b);
	    if(area!=Double.NaN){
	        Node node=NodeFactory.createLiteral(Double.toString(area),null,XSDDatatype.XSDdouble);
			return IterLib.oneResult(binding, alloc, node, execCxt);
			}else{
				return IterLib.noResults(execCxt);
			}
	}
	
	protected double computeValueByMVBB(Geometry geometry){
		MVBBVisitor mv=new MVBBVisitor();
		geometry.accept(mv);
		Box mvbb=mv.getMVBB();
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
		Plane p=largest.getPlane();
		Geometry projection;
		try {
			projection = Projection.projectToPlane(geometry, p);
			return Area.area(projection);
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Double.NaN;
		}
	}
	
	protected double computeValueBySurfaceArea(Node product,Geometry geometry,ExecutionContext execCxt){
		double area=largestSurfaceArea(geometry);
		for(Geometry g:getOpenings(product,execCxt)){
			area=area+largestSurfaceArea(g);
		}
	    return area;
	}
	
	protected double largestSurfaceArea(Geometry geometry){
		double area=0;
		List<TriangulatedSurface> surfaces=null;
		try {
			surfaces = new Stitching().stitches(geometry,0.1);
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Double.NaN;
		}
		TriangulatedSurface largest=null;
		for (TriangulatedSurface surface:surfaces){
			if(largest!=null){
				double sArea=Area.area(surface);
				if(sArea>area){
					largest=surface;
					area=sArea;
				}
			}else{
				largest=surface;
				area=Area.area(surface);
			}
		}
		return area;
	}
	
	protected List<Geometry> getOpenings(Node product, ExecutionContext execCxt){
                 List<Geometry> openings=new ArrayList<Geometry>();
                Graph graph=execCxt.getActiveGraph();				
				ExtendedIterator<Triple> iterator2=graph.find(null,NodeFactory.createURI(Namespace.IFC2X3_TC1+"relatingBuildingElement_IfcRelVoidsElement") , product);
				while (iterator2.hasNext()){
					Node rel=iterator2.next().getSubject();
					ExtendedIterator<Triple> iter=graph.find(rel,NodeFactory.createURI(Namespace.IFC2X3_TC1+"relatedOpeningElement_IfcRelVoidsElement"),null);
					while(iter.hasNext()){
						Node object=iter.next().getObject();
						Geometry child= getGeometry(object,graph);
						openings.add(child);
}
				}
				return openings;
}
}