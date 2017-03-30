package nl.tue.ddss.bimsparql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionFactory;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import nl.tue.ddss.bimsparql.geometry.Product;


public class HasPathTest {
	
	public static void main(String args[]) throws FileNotFoundException{
		Model model=ModelFactory.createDefaultModel();
		InputStream in=new FileInputStream("C:\\Users\\Chi\\Desktop\\example.owl");
		model.read(in,null,"TTL");
  /*      PropertyFunctionRegistry.get().put("http://www.semanticweb.org/chi#hasPath", new PropertyFunctionFactory() {
            public PropertyFunction create(String uri) {
                return new HasPathPF(new HashMap<Node,Product>()) ;
            }
        });
        String s="SELECT \nWHERE{\n?d1 a <http://www.semanticweb.org/chi#Door>.\n?d2 a <http://www.semanticweb.org/chi#Door>.\n(?d1 ?d2) <http://www.semanticweb.org/chi#hasPath> ?y.\n?y <http://www.semanticweb.org/chi#length> ?l.\n}";
        Query q=QueryFactory.create(s);
        QueryExecution qe=QueryExecutionFactory.create(q,model);
        ResultSet r=qe.execSelect();
        ResultSetFormatter.out(r);
        OutputStream out=new FileOutputStream("C:\\Users\\Chi\\Desktop\\out.ttl");
        model.write(out,"TTL");*/
        
	}

}
