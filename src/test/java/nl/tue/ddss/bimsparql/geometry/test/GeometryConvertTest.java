package nl.tue.ddss.bimsparql.geometry.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.tue.ddss.bimsparql.geometry.convert.GeometryConverter;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktWriteException;
import nl.tue.ddss.convert.IfcVersion;

public class GeometryConvertTest {
	
	public static void main(String[] args) throws IOException, WktWriteException{
		InputStream in=new FileInputStream("D:/Final_Test/IFC/IFC_Schependomlaan.ifc");
		OutputStream out=new FileOutputStream("D:/Final_Test/IFC/IFC_Schependomlaan_geometry.ttl");
		GeometryConverter converter=new GeometryConverter("http://www.tue.nl/test/");
		converter.parseModel2GeometryStream(in, out, IfcVersion.IFC2X3_TC1, true);
	}

}
