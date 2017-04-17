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
package nl.tue.ddss.bimsparql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JenaTest {
	
	
	private static final String prefixes="PREFIX ifcowl: <http://www.buildingsmart-tech.org/ifcOWL/IFC2X3_TC1#>\n"+"PREFIX list: <http://www.co-ode.org/ontologies/list.owl#>\n"+"PREFIX expr: <http://purl.org/voc/express#>\n"
			+ "PREFIX qrw:<http://bimsparql.org/query-rewriting#>\n" + "PREFIX pset:<http://bimsparql.org/pset#>\n"+ "PREFIX spt:<http://bimsparql.org/spatial#>\n"+"PREFIX pdt:<http://bimsparql.org/product#>\n";
	public static Query getQuery(String q){
		return QueryFactory.create(q);
	}
	public static void main(String[] args) throws FileNotFoundException{
		Model model=ModelFactory.createDefaultModel();
		InputStream in=new FileInputStream("BimSPARQL_example.ttl");
		model.read(in,null,"TTL");
		String query=prefixes+"SELECT ?s\n"+ 
"WHERE{?s ?p ?o .}";
		Query q = QueryFactory.create(query);
		QueryExecution qe = QueryExecutionFactory.create(q, model);
		ResultSet qresults = qe.execSelect();
        ResultSetFormatter.out(qresults);
	}

}
