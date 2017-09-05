package nl.tue.ddss.bimsparql.geometry.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import nl.tue.ddss.bimsparql.geometry.convert.BoundingBoxConverter;

public class BoundingBoxConvertTest {
	
	public static void main(String[] args) throws FileNotFoundException{
		BoundingBoxConverter bbc=new BoundingBoxConverter();
		InputStream in=new FileInputStream("D:/Final_Test/IFC/091210Med_Dent_Clinic_Arch_geometry.ttl");
		OutputStream out=new FileOutputStream("D:/Final_Test/IFC/091210Med_Dent_Clinic_Arch_bb.ttl");
		String baseUri="http://www.tue.nl/test/";
		bbc.convertToBoundingBox(in, out, baseUri, true);
	}

}
