package nl.tue.ddss.bimsparql;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import nl.tue.ddss.bimsparql.geometry.GeometryGenerator;

public class GeometryGeneratorTest {
	
	public static void main(String[] args) throws FileNotFoundException{
		GeometryGenerator gg=new GeometryGenerator();
		gg.generateGeometry("C:/users/chi/desktop/wall1.ifc","https://www.instance.com/");
		FileOutputStream fos=new FileOutputStream("C:/users/chi/desktop/wall1_geometry.ttl");
		gg.getGeometryModel().write(fos,"TTL");		
	}

}
