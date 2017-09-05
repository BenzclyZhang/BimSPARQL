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
package nl.tue.ddss.bimsparql.geometry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


import org.bimserver.geometry.Matrix;
import org.bimserver.plugins.renderengine.RenderEngineException;
import org.bimserver.plugins.renderengine.RenderEngineGeometry;
import org.bimserver.plugins.renderengine.RenderEngineModel;
import org.ifcopenshell.IfcOpenShellEngine;
import org.ifcopenshell.IfcOpenShellEntityInstance;
import org.ifcopenshell.IfcOpenShellModel;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import nl.tue.ddss.bimsparql.function.geom.GEOM;


public class GeometryGenerator {
	
	private Model geometryModel=ModelFactory.createDefaultModel();
	private HashMap<Integer,InstanceGeometry> geometryById=new HashMap<Integer,InstanceGeometry>();
	public HashMap<Integer, InstanceGeometry> getGeometryById() {
		return geometryById;
	}









	public void setGeometryById(HashMap<Integer, InstanceGeometry> geometryById) {
		this.geometryById = geometryById;
	}









	private HashMap<Node,Geometry> hashmap=new HashMap<Node,Geometry>();
	
	

	public GeometryGenerator(){
	}

    
    
   
    

      

    
    public HashMap<Integer, InstanceGeometry> generateGeometry(String file,String ns) throws FileNotFoundException{
    	InputStream in =new FileInputStream(file);
    	return generateGeometry(in,ns);
    }

		public HashMap<Integer, InstanceGeometry> generateGeometry(InputStream in,String ns) {	
			geometryModel.setNsPrefix("inst", ns);
			geometryModel.setNsPrefix("geom", GEOM.getURI());
			IfcOpenShellEngine ifcOpenShellEngine;
			try {
				ifcOpenShellEngine = new IfcOpenShellEngine(GeometryGenerator.class.getClassLoader().getResource("exe/64/win/IfcGeomServer.exe").getFile());
	
			RenderEngineModel renderEngineModel = ifcOpenShellEngine.openModel(in);
					renderEngineModel.generateGeneralGeometry();

					HashMap<Integer,IfcOpenShellEntityInstance> instancesById=((IfcOpenShellModel)renderEngineModel).getInstancesById();
					for (Integer id:instancesById.keySet()){
						       
								IfcOpenShellEntityInstance renderEngineInstance=instancesById.get(id);
								RenderEngineGeometry geometry=renderEngineInstance.generateGeometry();
								InstanceGeometry instanceGeometry=new InstanceGeometry();
								if (geometry != null && geometry.getNrIndices() > 0) {
                                    instanceGeometry.setId(id);
									instanceGeometry.setPointers(geometry.getIndices());
									instanceGeometry.setType(renderEngineInstance.getType());

							/*		if (geometry.getMaterialIndices() != null && geometry.getMaterialIndices().length > 0) {
										boolean hasMaterial = false;
										float[] vertex_colors = new float[geometry.getMaterialIndices().length * 4];
										int j=0;
										if (geometry.getMaterials()!=null||geometry.getMaterials().length>0){
											hasMaterial=true;
										for (int i = 0; i < geometry.getMaterialIndices().length; i++) {
											int c = geometry.getMaterialIndices()[i];
											if (c!=-1){
											vertex_colors[j]=geometry.
													getMaterials()[c*4];
											vertex_colors[j+1]=geometry.getMaterials()[c*4+1];
											vertex_colors[j+2]=geometry.getMaterials()[c*4+2];
											vertex_colors[j+3]=geometry.getMaterials()[c*4+3];
											}
											else{
												vertex_colors[j]=(float) 0.5;
												vertex_colors[j+1]=(float) 0.5;
												vertex_colors[j+2]=(float) 0.5;
												vertex_colors[j+3]=(float) 0.5;
											}
											j=j+4;
										}
										}
										if (hasMaterial) {
											instanceGeometry.setColors(vertex_colors);
										}
									}*/
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
								addGeometry(instanceGeometry);
								addGeometryTriples(instanceGeometry);
								geometryById.put(id, instanceGeometry);
					}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RenderEngineException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			return geometryById;
		}
		
    private void addGeometry(InstanceGeometry ig) {
			// TODO Auto-generated method stub
			Geometry g=toGeometry(ig);
			Node node=geometryModel.getResource(getBaseUri()+ig.getType()+"_"+ig.getId()).asNode();
			hashmap.put(node, g);
		}









	public void addGeometryTriples(InstanceGeometry ig){
    	String s=toWKT(ig);
    	geometryModel.add(geometryModel.getResource(getBaseUri()+ig.getType()+"_"+ig.getId()),GEOM.hasGeometry,geometryModel.createResource(getBaseUri()+"Geometry"+"_"+ig.getId()));
    	geometryModel.add(geometryModel.getResource(getBaseUri()+"Geometry"+"_"+ig.getId()),GEOM.asBody,geometryModel.createLiteral(s));
    	
	} 
    
    private String getBaseUri(){    	
    	return geometryModel.getNsPrefixURI("inst");
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
    
	public String toWKT(InstanceGeometry ig){
    	double[] points=ig.getPoints();
    	int[] indices=ig.getPointers();
    	String s="TIN Z (";
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
           s=s+"(("+d1+" "+d2+" "+d3+", "+d4+" "+d5+" "+d6+", "+d7+" "+d8+" "+d9+", "+d1+" "+d2+" "+d3+"))";
           if(i==indices.length-3){
        	   s=s+")";
           }else s=s+", ";    		
    	}
    	return s;
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


	public Model getGeometryModel() {
		// TODO Auto-generated method stub
		return geometryModel;
	}









	public HashMap<Node, Geometry> getHashMap() {
		// TODO Auto-generated method stub
		return hashmap;
	}
}
