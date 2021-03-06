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

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import nl.tue.ddss.bimsparql.geometry.partition.GeometryTripleExtractor;

public class GeometryPartitionTest {
	
	public static void main(String[] args) throws FileNotFoundException{
	//	   test("Duplex_A_20110505.ttl","Duplex_A_20110505.ifc");
	  //   test("091210Med_Dent_Clinic_Arch.ttl","091210Med_Dent_Clinic_Arch.ifc");
	      test("Duplex_A_20110505.ttl");
	      test("Office_A_S_20110811_combined.ttl");
	      test("IFC_Schependomlaan.ttl");
	      test("161210Med_Dent_Clinic_Combined.ttl");
	}
	
	public static void test(String ttlfile) throws FileNotFoundException{
		String directory = "D:/tdb" ;
		Dataset dataset = TDBFactory.createDataset(directory) ;
		dataset.begin(ReadWrite.READ) ;
		Model model = dataset.getNamedModel(ttlfile) ;		  
		System.out.println("Model size: "+(model.size()));
		Model schema=ModelFactory.createDefaultModel();
		InputStream input=GeometryPartitionTest.class.getClassLoader().getResourceAsStream("IFC2X3_TC1.ttl");
		schema.read(input,null,"TTL");
		GeometryTripleExtractor gp=new GeometryTripleExtractor(model,schema);
		long size=gp.geometryTripleCount();
		System.out.println("Geometry triples: "+size);
//		gp.removeGeometryTriples();
//		OutputStream out=new FileOutputStream("C:/Users/Chi/Desktop/Duplex_noGeometry.ttl");
//		model.write(out, "TTL");
//		model.close();
		dataset.end();
		
	}

}
