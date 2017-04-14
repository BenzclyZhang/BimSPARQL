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

import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import nl.tue.ddss.bimsparql.function.geom.GEOM;
import nl.tue.ddss.bimsparql.geometry.Box;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryType;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.ConvexHull;
import nl.tue.ddss.bimsparql.geometry.algorithm.MVBB;
import nl.tue.ddss.bimsparql.geometry.algorithm.Polyhedron;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;

public class OBBTest {
	
	public static void main(String[] args) throws IOException, WktParseException{
		InputStream in=ConvexHullTest.class.getClassLoader().getResourceAsStream("lifeline_geometry.ttl");
		Model model=ModelFactory.createDefaultModel();
		model.read(in,null,"TTL");
		StmtIterator stmts=model.listStatements(null, GEOM.hasGeometry, (RDFNode)null);
		while(stmts.hasNext()){
			Statement stmt=stmts.next();
			System.out.print(stmt.getSubject().getLocalName()+ " : ");
			String ewkt=stmt.getObject().asResource().getProperty(GEOM.asBody).getObject().asLiteral().getString();
			EwktReader reader=new EwktReader(ewkt);
			Geometry geometry=reader.readGeometry();
			if (geometry.geometryTypeId()==GeometryType.TYPE_POLYHEDRALSURFACE){
			PolyhedralSurface ps=new ConvexHull().buildConvexHull((PolyhedralSurface)geometry);
			Polyhedron ph=new Polyhedron(ps);
			MVBB obb=new MVBB(ph);
			obb.computeMinBB();
			Box box=obb.getBox();
			System.out.println(box.getVolume()/1000000000);
			System.out.print(box.getOrientation().getN1());
			System.out.print(box.getOrientation().getN2());
			System.out.println(box.getOrientation().getN3());
//			System.out.println(box.getMin());
//			System.out.println(box.getMax());
		}
		}
		
	}

}
