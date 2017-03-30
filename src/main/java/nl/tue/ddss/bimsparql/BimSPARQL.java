package nl.tue.ddss.bimsparql;


import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.topbraid.spin.model.Function;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.vocabulary.SP;
import org.topbraid.spin.vocabulary.SPIN;
import org.xml.sax.SAXException;

import nl.tue.ddss.bimsparql.function.geom.GEOM;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;
import nl.tue.ddss.bimsparql.pfunction.pdt.HasGrossWallAreaPF;
import nl.tue.ddss.bimsparql.pfunction.pdt.HasOverallHeightPF;
import nl.tue.ddss.bimsparql.pfunction.pdt.HasOverallLengthPF;
import nl.tue.ddss.bimsparql.pfunction.pdt.HasOverallWidthPF;
import nl.tue.ddss.bimsparql.pfunction.pdt.HasWindowAreaPF;
import nl.tue.ddss.bimsparql.pfunction.spt.ContainsPF;
import nl.tue.ddss.bimsparql.pfunction.spt.DisjointsPF;
import nl.tue.ddss.bimsparql.pfunction.spt.DistanceXY;
import nl.tue.ddss.bimsparql.pfunction.spt.IntersectsPF;
import nl.tue.ddss.bimsparql.pfunction.spt.IsOutsidePF;
import nl.tue.ddss.bimsparql.pfunction.spt.TouchesPF;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionFactory;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;




public class BimSPARQL {
	
	
	static HashMap<Node,Geometry> hashmap=new HashMap<Node,Geometry>();
	static Model schema;

	
	public static void init(Model model,Model geometryModel) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException, URISyntaxException{
		schema=loadSchema();
		getSimpleGeometry(geometryModel);
        PropertyFunctionRegistry.get().put("http://bimsparql.org/spatial#intersects", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new IntersectsPF(hashmap) ;
            }
        });
        PropertyFunctionRegistry.get().put("http://bimsparql.org/spatial#contains", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new ContainsPF(hashmap) ;
            }
        });
        PropertyFunctionRegistry.get().put("http://bimsparql.org/spatial#disjoints", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new DisjointsPF(hashmap) ;
            }
        });
        PropertyFunctionRegistry.get().put("http://bimsparql.org/spatial#touches", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new TouchesPF(hashmap) ;
            }
        });
        
        PropertyFunctionRegistry.get().put("http://bimsparql.org/spatial#distanceXY", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new DistanceXY(hashmap) ;
            }
        });

        PropertyFunctionRegistry.get().put("http://bimsparql.org/product#hasOverallHeight", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new HasOverallHeightPF(hashmap) ;
            }
        });
        PropertyFunctionRegistry.get().put("http://bimsparql.org/product#hasOverallLength", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new HasOverallLengthPF(hashmap) ;
            }
        });
        PropertyFunctionRegistry.get().put("http://bimsparql.org/product#hasWidthLength", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new HasOverallWidthPF(hashmap) ;
            }
        });
        PropertyFunctionRegistry.get().put("http://bimsparql.org/product#hasWindowArea", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new HasWindowAreaPF(hashmap) ;
            }
        });
        PropertyFunctionRegistry.get().put("http://bimsparql.org/product#hasGrossWallArea", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new HasGrossWallAreaPF(hashmap) ;
            }
        });
       PropertyFunctionRegistry.get().put("http://bimsparql.org/spatial#isOutside", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new IsOutsidePF(hashmap) ;
            }
        });
      
        Model spinRules=loadSpinRules();
        
		SPINModuleRegistry.get().init();
		for (Function f:SPINModuleRegistry.get().getFunctions())
		{
			System.out.println(f.getURI());
		}
        System.out.println("-------------------------------");
		SPINModuleRegistry.get().registerAll(spinRules, null);

        
	}
	
	private static void getSimpleGeometry(Model geometryModel) {
		Graph graph=geometryModel.getGraph();
		ExtendedIterator<Triple> iterator=graph.find(null, GEOM.hasGeometry.asNode(), null);
		while(iterator.hasNext()){
			Triple t=iterator.next();
			Node subject=t.getSubject();
			Node object=t.getObject();
			ExtendedIterator<Triple> iter=graph.find(object, GEOM.asWKT.asNode(), null);
			if(iter.hasNext()){
				String s=iter.next().getObject().getLiteralValue().toString();
				try {
					Geometry geometry=new EwktReader(s).readGeometry();
					hashmap.put(subject, geometry);
				} catch (WktParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
	}

	private static Model loadSpinRules() {
		Model spinRules=ModelFactory.createDefaultModel();
		spinRules.add(SPIN.getModel());
		InputStream in=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/schm.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		spinRules.read(in, null,"TTL");
		InputStream in2=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/pset.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		spinRules.read(in2, null,"TTL");
		InputStream in3=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/pdt.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		spinRules.read(in3, null,"TTL");
		Model spModel = SP.getModel();
		Model spinModel = SPIN.getModel();
		MultiUnion multiUnion = JenaUtil.createMultiUnion(new Graph[] {
			spModel.getGraph(),
			spinModel.getGraph(),
			spinRules.getGraph()
		});
		Model unionModel = ModelFactory.createModelForGraph(multiUnion);
		return unionModel;		
	}

	private static Model loadSchema() throws IOException{
		Model schema=ModelFactory.createDefaultModel();
		InputStream in=BimSPARQL.class.getClassLoader().getResourceAsStream("IFC2X3_TC1.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		return schema.read(in, null,"TTL");
	}
	
	public static String getOSVersion(){
		String os=System.getProperty("os.name");
		return os;
	}
	
	

}
