package nl.tue.ddss.bimsparql;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Server {
	
	public List<String> getResult(InputStream in, String query){
		Model model=ModelFactory.createDefaultModel();
		model.read(in,null,"TTL");
		Query q = QueryFactory.create(query);
		QueryExecution qe = QueryExecutionFactory.create(q, model);
		ResultSet qresults = qe.execSelect();
		List<QuerySolution> solutions=ResultSetFormatter.toList(qresults);
		List<String> results=new ArrayList<String>();
		for(QuerySolution qs:solutions){
			results.add(qs.toString());
		}
		return results;
	}

}
