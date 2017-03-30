package nl.tue.ddss.bimsparql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryType;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.ConvexHull;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;

public class ConvexHullTest {
	
	public static void main(String[] args) throws IOException, WktParseException{
		InputStream in=ConvexHullTest.class.getClassLoader().getResourceAsStream("lifeline_geometry.ttl");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line ;
		while ((line= br.readLine()) != null) {
		if (line.contains("geom:asWKT")){
			String ewkt=line.substring(line.indexOf("\"")+1,line.lastIndexOf("\""));
			EwktReader reader=new EwktReader(ewkt);
			Geometry geometry=reader.readGeometry();
			if (geometry.geometryTypeId()==GeometryType.TYPE_POLYHEDRALSURFACE){
			@SuppressWarnings("unused")
			PolyhedralSurface ps=new ConvexHull().buildConvexHull((PolyhedralSurface)geometry);
		}
		
	}
	}
}
}
