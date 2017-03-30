package nl.tue.ddss.convert.rdf2ifc;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.util.ModelUtils;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;



public class IfcRDFUtils extends ModelUtils{


	public static List<Resource> getObjects(Model model){
		return null;
	}

    public static Resource getClass(Resource ifcObject,Model schema){
    	Resource clazz=ifcObject.getPropertyResourceValue(RDF.type);
		return clazz;
    	
    }
    
    public static boolean isSubClassOf(Resource subclass, Resource superclass,Model schema){
    	OntModel model=ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, schema);
    	OntClass superClass=model.getOntClass(superclass.getURI());
    	return superClass.hasSubClass(subclass);
    }
    
    public static boolean isDirectSubClassOf(Resource subclass, Resource superclass,Model schema){
    	return schema.contains(subclass,RDFS.subClassOf,superclass);
    }
    public static List<Resource> listSubClasses(Resource clazz,Model schema,List<Resource> subClasses){
    
    	return subClasses;
    }
    
    public static HashMap<Resource,List<Property>> getProperties4Class(Model schema){
    	HashMap<Resource,List<Property>> hashMap=new HashMap<Resource,List<Property>>();
    	List<Resource> classes=new ArrayList<Resource>();
    	Property index=schema.getProperty(SupplementNamespace.getURI()+"index");
    	Property isIfcEntity=schema.getProperty(SupplementNamespace.getURI()+"isIfcEntity");
    	Property isTopLevelIfcEntity=schema.getProperty(SupplementNamespace.getURI()+"isTopLevelIfcEntity");
    	StmtIterator stmt=schema.listLiteralStatements(null, isIfcEntity, true);
    	while(stmt.hasNext()){
    		classes.add(stmt.next().getSubject());
    	}
    	for (Resource clazz:classes){
    		if(clazz.hasLiteral(isTopLevelIfcEntity, true)){
    			List<Property> properties=getPropList(schema,clazz,index);
    			hashMap.put(clazz, properties);
    			List<Resource> subClasses=getSubClasses(clazz,schema,RDFS.subClassOf);
    			if(subClasses.size()>0){
    			for(Resource subClass:subClasses){
    				recurGetProperties(hashMap,subClass,schema,index,RDFS.subClassOf,properties);
    			}			   			
    		}
    	}
		  	
    }return hashMap;  
    } 	
    private static void recurGetProperties(HashMap<Resource,List<Property>> hashMap,Resource clazz,Model schema,Property index,Property ifcSubClassOf,List<Property> inherited){
		List<Property> properties=new ArrayList<Property>();
		properties.addAll(inherited);
		properties.addAll(getPropList(schema,clazz,index));
		hashMap.put(clazz,properties);
    	List<Resource> subClasses=getSubClasses(clazz,schema,ifcSubClassOf);
			if(subClasses.size()>0){
			for(Resource subClass:subClasses){
				recurGetProperties(hashMap,subClass,schema,index,ifcSubClassOf,properties);				
			}
    }
    }  
    private static List<Property> getPropList(Model schema,Resource clazz,Property index){
    	List<Property> properties=new ArrayList<Property>();
		StmtIterator propIter=schema.listStatements(null,RDFS.domain,clazz);
		while(propIter.hasNext()){
			Resource p=propIter.next().getSubject();
			if(schema.contains(p,index)){
			properties.add(schema.getProperty(p.getURI()));
			}
		}
		properties=reArrangePropList(properties,index);
		return properties;
    }
    
    private static List<Property> reArrangePropList(List<Property> props,Property index){
    	List<Property> properties=new ArrayList<Property>();
    	Property[] array=new Property[props.size()];
    	for(Property prop:props){
    		int i=prop.listProperties(index).next().getInt();
    		array[i]=prop;
    	}
    	for(Property p:array){
    		properties.add(p);
    	}
    	return properties;
    }
    
    private static List<Resource> getSubClasses(Resource superClass,Model model,Property ifcSubClassOf){
    	List<Resource> classes=new ArrayList<Resource>();
    	StmtIterator stmt=model.listStatements(null, ifcSubClassOf, superClass);
    	while(stmt.hasNext()){
    		classes.add(stmt.next().getSubject());
    	} 
		return classes;
    	
    }
    

    


}
