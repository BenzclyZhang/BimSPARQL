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
package nl.tue.ddss.bimsparql.geometry.convert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.writer.WriterStreamRDFBlocks;

import org.bimserver.geometry.Matrix;
import org.bimserver.plugins.renderengine.RenderEngineException;
import org.bimserver.plugins.renderengine.RenderEngineGeometry;
import org.bimserver.plugins.renderengine.RenderEngineModel;
import org.ifcopenshell.IfcOpenShellEngine;
import org.ifcopenshell.IfcOpenShellEntityInstance;
import org.ifcopenshell.IfcOpenShellModel;

import com.hp.hpl.jena.graph.Node;

import fi.ni.rdf.Namespace;
import nl.tue.ddss.bimsparql.function.geom.GEOM;
import nl.tue.ddss.bimsparql.geometry.Box;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.InstanceGeometry;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktWriter;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktWriteException;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;
import nl.tue.ddss.bimsparql.geometry.visitor.MVBBVisitor;
import nl.tue.ddss.convert.IfcVersion;


public class GeometryConverter {
	
	StreamRDF rdfWriter;
	String baseUri;
	int i;

	public GeometryConverter(String baseUri){
		this.baseUri=baseUri;
	}
	
	public void parseModel2GeometryStream(InputStream in,OutputStream out, IfcVersion ifcVersion,boolean boundingbox)
			throws IOException, WktWriteException{
		IfcVersion.initDefaultIfcNsMap();
       String ontNs= IfcVersion.IfcNSMap.get(ifcVersion);
		setRdfWriter(new WriterStreamRDFBlocks(out));
		getRdfWriter().base(baseUri);
		getRdfWriter().prefix("ifcowl", ontNs);
		getRdfWriter().prefix("inst", baseUri);
		getRdfWriter().prefix("rdf", Namespace.RDF);
		getRdfWriter().prefix("xsd", Namespace.XSD);
		getRdfWriter().prefix("owl", Namespace.OWL);
		getRdfWriter().prefix("geom", GEOM.getURI());
		getRdfWriter().start();
		i=0;
		generateGeometry(in,boundingbox);
		System.out.println("Model parsed!");	
		System.out.println("Finished!");
		getRdfWriter().finish();
	}
	
	public void parseModel2MaterialStream(InputStream in,OutputStream out, IfcVersion ifcVersion)
			throws IOException, WktWriteException{
		IfcVersion.initDefaultIfcNsMap();
       String ontNs= IfcVersion.IfcNSMap.get(ifcVersion);
		setRdfWriter(new WriterStreamRDFBlocks(out));
		getRdfWriter().base(baseUri);
		getRdfWriter().prefix("ifcowl", ontNs);
		getRdfWriter().prefix("inst", baseUri);
		getRdfWriter().prefix("rdf", Namespace.RDF);
		getRdfWriter().prefix("xsd", Namespace.XSD);
		getRdfWriter().prefix("owl", Namespace.OWL);
		getRdfWriter().prefix("geom", GEOM.getURI());
		getRdfWriter().start();
		i=0;
		generateMaterials(in);
		System.out.println("Model parsed!");	
		System.out.println("Finished!");
		getRdfWriter().finish();
	}

		public StreamRDF getRdfWriter() {
		return rdfWriter;
	}


	public void setRdfWriter(StreamRDF rdfWriter) {
		this.rdfWriter = rdfWriter;
	}


		public void generateGeometry(InputStream in,boolean boundingbox) throws WktWriteException {	
			IfcOpenShellEngine ifcOpenShellEngine;
			try {
				Path path = Paths.get(GeometryConverter.class.getProtectionDomain().getCodeSource().getLocation().toURI()); 
				Path parent = path.getParent();
				String parentDirName = parent.toString();
				ifcOpenShellEngine = new IfcOpenShellEngine(parentDirName+getEngineForOS());
	
			RenderEngineModel renderEngineModel = ifcOpenShellEngine.openModel(in);
					renderEngineModel.generateGeneralGeometry();
					HashMap<Integer,IfcOpenShellEntityInstance> instancesById=((IfcOpenShellModel)renderEngineModel).getInstancesById();
					for (Integer id:instancesById.keySet()){	
						i++;
								IfcOpenShellEntityInstance renderEngineInstance=instancesById.get(id);
								RenderEngineGeometry geometry=renderEngineInstance.generateGeometry();
								InstanceGeometry instanceGeometry=new InstanceGeometry();
								if (geometry != null && geometry.getNrIndices() > 0) {
                                    instanceGeometry.setId(id);
									instanceGeometry.setPointers(geometry.getIndices());
									instanceGeometry.setType(renderEngineInstance.getType());
									instanceGeometry.setColors(geometry.getMaterials());
									instanceGeometry.setMaterialIndices(geometry.getMaterialIndices());
									
									double[] tranformationMatrix = new double[16];
									Matrix.setIdentityM(tranformationMatrix, 0);
									if (renderEngineInstance.getTransformationMatrix() != null) {
										tranformationMatrix = renderEngineInstance.getTransformationMatrix();
									}
                                    double[] points=new double[geometry.getVertices().length];
									for (int i = 0; i < instanceGeometry.getPointers().length; i++) {
										processExtends(tranformationMatrix, geometry.getVertices(), instanceGeometry.getPointers()[i] * 3,points);
									}
									instanceGeometry.setPoints(points);
								}
								if(i%500==0){
									System.out.println("");
								}
								addGeometryTriples(instanceGeometry,boundingbox);
					}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RenderEngineException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		private String getEngineForOS() throws RenderEngineException{
			String os=System.getProperty("os.name").toLowerCase();
			String arch=System.getProperty("os.arch");
			String result="/exe";
			if(arch.contains("64")){
				result=result+"/64";
				if(os.indexOf("win") >= 0){
					return result+"/win/IfcGeomServer.exe";
				}else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 ){
					return result+"/linux/IfcGeomServer";
				}else if(os.indexOf("mac") >= 0){
					return result+"/osx/IfcGeomServer";
				}
			}else if(arch.contains("32")){
				result=result+"/32";
				if(os.indexOf("win") >= 0){
					return result+"/win/IfcGeomServer.exe";
				}else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 ){
					return result+"/linux/IfcGeomServer";
				}
			}
			throw new RenderEngineException("not supported operation system : "+os+" "+arch);

		}
		
		public void generateMaterials(InputStream in) throws WktWriteException{	
			IfcOpenShellEngine ifcOpenShellEngine;
			try {
				Path path = Paths.get(GeometryConverter.class.getProtectionDomain().getCodeSource().getLocation().toURI()); 
				Path parent = path.getParent();
				String parentDirName = parent.toString();
				ifcOpenShellEngine = new IfcOpenShellEngine(parentDirName+getEngineForOS());
	
			RenderEngineModel renderEngineModel = ifcOpenShellEngine.openModel(in);
					renderEngineModel.generateGeneralGeometry();
					HashMap<Integer,IfcOpenShellEntityInstance> instancesById=((IfcOpenShellModel)renderEngineModel).getInstancesById();
					for (Integer id:instancesById.keySet()){	
						i++;
								IfcOpenShellEntityInstance renderEngineInstance=instancesById.get(id);
								RenderEngineGeometry geometry=renderEngineInstance.generateGeometry();
								InstanceGeometry instanceGeometry=new InstanceGeometry();
								if (geometry != null && geometry.getNrIndices() > 0) {
                                    instanceGeometry.setId(id);
									instanceGeometry.setType(renderEngineInstance.getType());
									instanceGeometry.setColors(geometry.getMaterials());
									instanceGeometry.setMaterialIndices(geometry.getMaterialIndices());
								}
								if(i%500==0){
									System.out.println("");
								}
								addMaterialTriples(instanceGeometry);
					}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RenderEngineException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
    public void addMaterialTriples(InstanceGeometry ig){
    	if(ig.getColors()!=null&&ig.getColors().length>0){
        String materials=""+ig.getColors()[0];
        for(int i=1;i<ig.getColors().length;i++){
        	materials=materials+" "+ig.getColors()[i];
        }
        
        String indices=""+ig.getMaterialIndices()[0];
        for(int i=1;i<ig.getMaterialIndices().length;i++){
        	indices=indices+" "+ig.getMaterialIndices()[i];
        }
        
        Node hasMaterials=NodeFactory.createURI(GEOM.getURI()+"hasMaterials");
        Node hasMaterialIndices=NodeFactory.createURI(GEOM.getURI()+"hasMaterialIndices");
    	getRdfWriter().triple(new Triple(NodeFactory.createURI(baseUri+"Geometry"+"_"+ig.getId()),hasMaterials,NodeFactory.createLiteral(materials)));
    	getRdfWriter().triple(new Triple(NodeFactory.createURI(baseUri+"Geometry"+"_"+ig.getId()),hasMaterialIndices,NodeFactory.createLiteral((indices))));
    	}
       
    }
    
    public void processBoundingBoxes(){
    	
    }
    
	public void addGeometryTriples(InstanceGeometry ig,boolean boundingbox) throws WktWriteException{
		Geometry g=toGeometry(ig);

    	String s=toWKT(g);
    	getRdfWriter().triple(new Triple(NodeFactory.createURI(baseUri+ig.getType()+"_"+ig.getId()),GEOM.hasGeometry.asNode(),NodeFactory.createURI(baseUri+"Geometry"+"_"+ig.getId())));
    	getRdfWriter().triple(new Triple(NodeFactory.createURI(baseUri+"Geometry"+"_"+ig.getId()),GEOM.asBody.asNode(),NodeFactory.createLiteral((s))));
    	Box mvbb=null;
 /*   	if((!g.isEmpty())&&boundingbox){
    		Box aabb=toAABB(g);
    		mvbb=toMVBB(g);
    		if(aabb.getVolume()<mvbb.getVolume()){
    			System.out.println("Error: AABB: "+ aabb.getVolume()+" | MVBB: "+mvbb.getVolume());
    		}
    		getRdfWriter().triple(new Triple(NodeFactory.createURI(baseUri+"Geometry"+"_"+ig.getId()),GEOM.asAABB.asNode(),NodeFactory.createLiteral((toWKT(aabb.toPolyhedralSurface())))));
    		getRdfWriter().triple(new Triple(NodeFactory.createURI(baseUri+"Geometry"+"_"+ig.getId()),GEOM.asMVBB.asNode(),NodeFactory.createLiteral((toWKT(mvbb.toPolyhedralSurface())))));
    	}*/
	} 
	
	  public Geometry toGeometry(InstanceGeometry ig){
	    	TriangulatedSurface geometry=new TriangulatedSurface();
	    	double[] points=ig.getPoints();
	    	int[] indices=ig.getPointers();
	    	if(points!=null&&indices!=null){
	    	for (int i = 0; i < indices.length; i = i + 3){
	    		double d1=points[indices[i]*3];
	    		double d2=points[indices[i]*3+1];
	    		double d3=points[indices[i]*3+2];
	    		double d4=points[indices[i+1]*3];
	    		double d5=points[indices[i+1]*3+1];
	    		double d6=points[indices[i+1]*3+2];
	    		double d7=points[indices[i+2]*3];
	    		double d8=points[indices[i+2]*3+1];
	    		double d9=points[indices[i+2]*3+2];
	    		Triangle t=new Triangle(new Point3d(d1,d2,d3),new Point3d(d4,d5,d6),new Point3d(d7,d8,d9));
	           geometry.addTriangle(t);  		
	    	}
	    	}
	    	return geometry;
	    }
    
    
	public String toWKT(Geometry g) throws WktWriteException{
    EwktWriter ew=new EwktWriter("");
    ew.writeRec(g);
    return ew.getString();
    }
	
	public AABB toAABB(Geometry g) throws WktWriteException{
		AABBVisitor visitor=new AABBVisitor();
		g.accept(visitor);
		AABB box=visitor.getAABB();
		return box;
	}
	
	public Box toMVBB(Geometry g) throws WktWriteException{
		MVBBVisitor visitor=new MVBBVisitor();
		g.accept(visitor);
		return visitor.getMVBB();
	}
	
	private void processExtends(double[] transformationMatrix, float[] ds, int index,double[] output) {
		double x = ds[index];
		double y = ds[index + 1];
		double z = ds[index + 2];

		double[] result = new double[4];
		Matrix.multiplyMV(result, 0, transformationMatrix, 0, new double[] { x, y, z, 1 }, 0);
		output[index] = result[0];
		output[index + 1] = result[1];
		output[index + 2] = result[2];		
	}

}
