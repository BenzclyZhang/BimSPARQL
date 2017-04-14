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

import java.util.HashMap;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class ProductUtil {
	
	
	public static final Node RELATED_OBJECTS=NodeFactory.createURI("http://www.buildingsmart-tech.org/ifcOWL/IFC2X3_TC1#relatedObjects_IfcRelDecomposes");
	public static final Node RELATING_OBJECT=NodeFactory.createURI("http://www.buildingsmart-tech.org/ifcOWL/IFC2X3_TC1#relatingObject_IfcRelDecomposes");
	
	public static Product getProduct(Node subject,Graph graph,HashMap<Node,Product> hashmap){
		Product product=hashmap.get(subject);
		if (product!=null){
			return product;
		}
		else{
			product=new Product();
			ExtendedIterator<Node> compositions=GraphUtil.listSubjects(graph,RELATING_OBJECT,subject);
			if(compositions.hasNext()){
				ExtendedIterator<Node> childIterator=GraphUtil.listObjects(graph,compositions.next(),RELATED_OBJECTS);
				while (childIterator.hasNext()){
					Node childNode=childIterator.next();
					Product child=hashmap.get(childNode);
					if (child!=null){
						product.getTriangles().addAll(child.getTriangles());
					}
					else{
						child=getProduct(childNode,graph,hashmap);
						product.getTriangles().addAll(child.getTriangles());
					}
				}
			}
			return product;
			}			
	}
	
}
