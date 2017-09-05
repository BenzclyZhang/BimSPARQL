package nl.tue.ddss.bimsparql;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class VocabularyExtract {
	
	public static void main(String[] args){
		Model schm=ModelFactory.createDefaultModel();
		Model pset=ModelFactory.createDefaultModel();
		Model qto=ModelFactory.createDefaultModel();
		InputStream in=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/schm.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		schm.read(in, null,"TTL");
		InputStream in2=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/pset_1.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		pset.read(in2, null,"TTL");
		InputStream in4=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/qto.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		qto.read(in4, null,"TTL");
		extract(schm,"schm");
		extract(pset,"pset");
		extract(qto,"qto");
	}
	
	
	public static void extract(Model m,String name){
		Property groups=m.createProperty("http://www.buildingsmart-tech.org/ifc/IFC4/final#groups");
		Property isGroupedBy=m.createProperty("http://www.buildingsmart-tech.org/ifc/IFC4/final#isGroupedBy");
		Resource SingleValue=m.createResource("http://www.buildingsmart-tech.org/ifc/IFC4/final#P_SINGLEVALUE");
		Model vocabulary=ModelFactory.createDefaultModel();
		StmtIterator stmt1=m.listStatements(null, RDFS.comment,(String)null);
		StmtIterator stmt2=m.listStatements(null,RDFS.domain,(RDFNode)null);
		StmtIterator stmt3=m.listStatements(null,RDFS.range,(RDFNode)null);
		StmtIterator stmt4=m.listStatements(null, RDF.type, RDF.Property);
		StmtIterator stmt6=m.listStatements(null, RDFS.label, (String)null);
		
		StmtIterator stmt8=m.listStatements(null, groups, (RDFNode)null);
		StmtIterator stmt9=m.listStatements(null, isGroupedBy, (RDFNode)null);

		vocabulary.add(stmt1);
		vocabulary.add(stmt2);
		vocabulary.add(stmt3);
		vocabulary.add(stmt4);
		vocabulary.add(stmt6);
		vocabulary.add(stmt8);
		vocabulary.add(stmt9);
		m.remove(vocabulary);
		StmtIterator stmt7=m.listStatements(null, RDF.type, (RDFNode)null);
		List<Statement> ss=new ArrayList<Statement>();
		while(stmt7.hasNext()){
			Statement s=stmt7.next();
			if(s.getObject().asResource().getURI().startsWith("http://www.buildingsmart-tech.org/ifc/IFC4/final")){
				ss.add(s);
			}
		}
		m.remove(ss);
		try {
			OutputStream voc=new FileOutputStream("C:\\users\\chi\\desktop\\output\\"+name+".ttl");
			OutputStream rule=new FileOutputStream("C:\\users\\chi\\desktop\\output\\"+name+"_rule.ttl");
			vocabulary.setNsPrefixes(m.getNsPrefixMap());
			vocabulary.write(voc,"TTL");
			m.write(rule,"TTL");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
