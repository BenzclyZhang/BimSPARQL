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
