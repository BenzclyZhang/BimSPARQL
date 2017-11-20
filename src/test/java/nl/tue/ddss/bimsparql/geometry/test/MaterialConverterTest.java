package nl.tue.ddss.bimsparql.geometry.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.tue.ddss.bimsparql.geometry.convert.GeometryConverter;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktWriteException;
import nl.tue.ddss.convert.IfcVersion;

public class MaterialConverterTest {
	
	public static void main(String[] args) throws IOException, WktWriteException{
		InputStream in=new FileInputStream("D:/Final_Test/Office_A_20110811_optimized.ifc");
//		OutputStream out1=new FileOutputStream("D:/Final_Test/IFC/Touches_geometry.ttl");
		OutputStream out2=new FileOutputStream("D:/Final_Test/Office_A_20110811_optimized_material.ttl");
		GeometryConverter converter=new GeometryConverter("http://www.tue.nl/test/");
//		converter.parseModel2GeometryStream(in, out1, IfcVersion.IFC2X3_TC1, false);
		converter.parseModel2MaterialStream(in, out2, IfcVersion.IFC2X3_TC1);
	}

}
