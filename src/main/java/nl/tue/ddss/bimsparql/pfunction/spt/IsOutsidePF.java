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


import nl.tue.ddss.bimsparql.geometry.ExternalPdtIdentifier;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductOnGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.util.IterLib;


public class IsOutsidePF extends FunctionBaseProductOnGraph{
	
	public IsOutsidePF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
	}


	@Override
	protected QueryIterator verifyValue(Binding binding, Graph graph, Node product, Geometry geometry,Node object,
			ExecutionContext execCxt) {
		boolean b=false;
		try {
			
			List<Geometry> allGeometries=new ArrayList<Geometry>();
					for(Geometry pdt:hashmap.values()){
						allGeometries.add(pdt);
					}
			b = new ExternalPdtIdentifier(geometry,allGeometries).getIsExternal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node node=NodeFactory.createLiteral(Boolean.toString(b),null,XSDDatatype.XSDboolean);
		if (node.equals(object)){
			return IterLib.result(binding, execCxt);
		}
		else{
			return IterLib.noResults(execCxt);
		}
	}


	@Override
	protected QueryIterator getValue(Binding binding, Graph graph, Node product, Geometry geometry,Var alloc, ExecutionContext execCxt) {
	    boolean b=false;
			try {
				List<Geometry> allGeometries=new ArrayList<Geometry>();
				for(Geometry pdt:hashmap.values()){
					allGeometries.add(pdt);
				}
		b = new ExternalPdtIdentifier(geometry,allGeometries).getIsExternal();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Node node=NodeFactory.createLiteral(Boolean.toString(b),null,XSDDatatype.XSDboolean);
			return IterLib.oneResult(binding, alloc, node, execCxt);
	}

}
