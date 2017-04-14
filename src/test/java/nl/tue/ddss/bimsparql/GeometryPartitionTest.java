package nl.tue.ddss.bimsparql;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import nl.tue.ddss.bimsparql.geometry.GeometryGenerator;
import nl.tue.ddss.bimsparql.geometry.partition.GeometryTripleExtractor;

public class GeometryPartitionTest {
	
	public static void main(String[] args) throws FileNotFoundException{
	//	   test("Duplex_A_20110505.ifc.ttl","Duplex_A_20110505.ifc");
	//      test("091210Med_Dent_Clinic_Arch.ifc.ttl","091210Med_Dent_Clinic_Arch.ifc");
	      test("161210Med_Dent_Clinic_Combined.ttl","161210Med_Dent_Clinic_Combined.ifc");
	}
	
	public static void test(String ttlfile,String ifcfile) throws FileNotFoundException{
/*		Model model=ModelFactory.createDefaultModel();
		InputStream in=GeometryPartitionTest.class.getClassLoader().getResourceAsStream(ttlfile);
		model.read(in,null,"TTL");
		System.out.println("Model size: "+(model.size()-10));
		Model schema=ModelFactory.createDefaultModel();
		InputStream input=GeometryPartitionTest.class.getClassLoader().getResourceAsStream("IFC2X3_TC1.ttl");
		schema.read(input,null,"TTL");
		GeometryTripleExtractor gp=new GeometryTripleExtractor(model,schema);
		List<Statement> stmts=gp.listAllGeometricStatements();
		System.out.println("Geometry triples: "+stmts.size());
		model.close();*/
		GeometryGenerator gg=new GeometryGenerator();
		InputStream inputStream=GeometryPartitionTest.class.getClassLoader().getResourceAsStream(ifcfile);
		gg.generateGeometry(inputStream, "");
		Model geometryModel=gg.getGeometryModel();
		System.out.println("WKT triples: "+geometryModel.size());
	}

}
