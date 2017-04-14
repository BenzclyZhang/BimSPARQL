package nl.tue.ddss.bimsparql;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import nl.tue.ddss.bimsparql.geometry.GeometryGenerator;
import nl.tue.ddss.convert.rdf2ifc.IfcWriterException;
import nl.tue.ddss.convert.rdf2ifc.StreamIfcWriter;

public class WKTTest {
	
	public static void main(String[] args) throws IOException{
		
		InputStream in=new FileInputStream("C:/Users/Chi/Desktop/lifeline.ifc");
		
	/*	PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out = new PipedOutputStream(in);
		
		Thread t=new Thread(new Runnable() {
		    public void run () {
		        try {
		        	StreamIfcWriter sw=new StreamIfcWriter("C:/Users/Chi/Desktop/lifeline.ttl", out);
		        	sw.streamConvert("C:/Users/Chi/Desktop/lifeline.ttl");
		        } catch (IOException e) {
		            // logging and exception handling should go here
		        } catch (IfcWriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
		t.start();*/

		GeometryGenerator gg=new GeometryGenerator();
	    gg.generateGeometry(in,"https://www.instance.com/");
	    OutputStream output=new FileOutputStream("C:/Users/Chi/Desktop/lifeline_geometry.ttl");
	    gg.getGeometryModel().write(output, "TTL");
	    System.out.println(gg.getGeometryModel().size());
	    output.close();
	}
	

}
