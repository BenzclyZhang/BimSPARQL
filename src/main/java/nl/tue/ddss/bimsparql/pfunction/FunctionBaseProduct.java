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
package nl.tue.ddss.bimsparql.pfunction;

import java.io.IOException;
import java.util.HashMap;

import org.apache.jena.atlas.iterator.Iter;

import nl.tue.ddss.bimsparql.function.geom.GEOM;
import nl.tue.ddss.bimsparql.geometry.Box;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryCollection;
import nl.tue.ddss.bimsparql.geometry.GeometryType;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.algorithm.MVBB;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;
import nl.tue.ddss.convert.IfcVersion;
import nl.tue.ddss.convert.Namespace;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.pfunction.PFuncSimple;
import com.hp.hpl.jena.sparql.util.IterLib;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public abstract class FunctionBaseProduct extends PFuncSimple{
	
	protected final static double EPS=Geometry.EPS;
	
	protected HashMap<Node, Geometry> hashmap;
	protected IfcVersion ifcVersion;


	public FunctionBaseProduct(HashMap<Node, Geometry> hashmap) {
		super();
    	this.hashmap = hashmap;
	}

	public QueryIterator execEvaluated(Binding binding, Node product,
			 Node predicate, Node object,
			ExecutionContext execCxt) {
		Graph graph = execCxt.getActiveGraph();
		Geometry geometry=getGeometry(product,graph);
		if(geometry==null){
			return IterLib.noResults(execCxt);
		}
		if (Var.isVar(product))
			throw new ARQInternalErrorException(
					this.getClass().getName()+": Subject are variables without binding");
		if (Var.isVar(object))
			return getValue(binding, graph, product,geometry,
					Var.alloc(object), execCxt);
		else
			return verifyValue(binding, graph, product,geometry,object,
					execCxt);

	}


	private Geometry getGeometry(Node product, Graph graph) {
		Geometry geometry=hashmap.get(product);
		if(geometry!=null){
			return geometry;
		}
		ExtendedIterator<Triple> iterator=graph.find(product, GEOM.hasGeometry.asNode(), null);
		if(iterator.hasNext()){
			Triple t=iterator.next();
			Node object=t.getObject();
			ExtendedIterator<Triple> iter=graph.find(object, GEOM.asBody.asNode(), null);
			ExtendedIterator<Triple> iter1=graph.find(object, GEOM.asAABB.asNode(), null);
			ExtendedIterator<Triple> iter2=graph.find(object, GEOM.asMVBB.asNode(), null);
			if(iter.hasNext()){
				String s=iter.next().getObject().getLiteralValue().toString();
				try {
					geometry=new EwktReader(s).readGeometry();
					return geometry;
				} catch (WktParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(iter1.hasNext()){
				String s=iter.next().getObject().getLiteralValue().toString();
				try {
					Geometry aabbG=new EwktReader(s).readGeometry();
					if(aabbG.geometryTypeId()==GeometryType.TYPE_POLYHEDRALSURFACE){
						AABB aabb=AABB.getAABB(aabbG);
						geometry.setAABB(aabb);
					}
				} catch (WktParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}if(iter2.hasNext()){
				String s=iter.next().getObject().getLiteralValue().toString();
				try {
					Geometry mvbbG=new EwktReader(s).readGeometry();
					if(mvbbG.geometryTypeId()==GeometryType.TYPE_POLYHEDRALSURFACE){
						Box mvbb=Box.toBox((PolyhedralSurface)mvbbG);
						geometry.setMVBB(mvbb);
					}
				} catch (WktParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else{
			geometry=new GeometryCollection();
			ExtendedIterator<Triple> iterator2=graph.find(null,NodeFactory.createURI(Namespace.IFC2X3_TC1+"relatingObject_IfcRelDecomposes") , product);
			while (iterator2.hasNext()){
				Node rel=iterator2.next().getSubject();
				ExtendedIterator<Triple> iter=graph.find(rel,NodeFactory.createURI(Namespace.IFC2X3_TC1+"relatedObjects_IfcRelDecomposes"),null);
				while(iter.hasNext()){
					Node object=iter.next().getObject();
					Geometry child=getGeometry(object,graph);
					((GeometryCollection)geometry).addGeometry(child);
				}
			}
			if(geometry.numGeometries()>0){
				return geometry;
			}
		}
		return null;
	}

	protected abstract QueryIterator verifyValue(Binding binding, Graph graph,
			Node product, Geometry geometry,Node object,
			ExecutionContext execCxt) ;


	protected abstract QueryIterator getValue(Binding binding, Graph graph, Node product,Geometry geometry,
			Var alloc, ExecutionContext execCxt) ;

}
