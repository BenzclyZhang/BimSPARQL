package nl.tue.ddss.bimsparql.ifcdoc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.topbraid.spin.arq.ARQ2SPIN;
import org.topbraid.spin.model.Query;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.vocabulary.SP;
import org.topbraid.spin.vocabulary.SPIN;
import org.topbraid.spin.vocabulary.SPL;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import nl.tue.ddss.convert.Namespace;



public class QuantityParser {
	
	HashMap<Long,IfcdocVO> linemap;
	Model model;
	Model ifcSchema;
	Property groups;
	Property isGroupedBy;

	
	static final String IFCDOC="http://www.buildingsmart-tech.org/ifc/IFC4/final#";
	static final String QTO="http://bimsparql.org/pset#";
	static final String EXPRESS=Namespace.EXPRESS;
	static final String IFCOWL=Namespace.IFC2X3_TC1;
	static final String SCHM="http://bimsparql.org/schema#";
	static final String VOIR="http://www.voir.com#";
	
	public QuantityParser(){
		this.linemap=new HashMap<Long,IfcdocVO>();
		this.model=ModelFactory.createDefaultModel();
		model.setNsPrefix("ifcdoc",IFCDOC );
		model.setNsPrefix("qto",QTO);
		Resource base=model.createResource(QTO);
		base.addProperty(RDF.type, OWL.Ontology);
		base.addLiteral(DC.creator, "Chi Zhang");
		base.addLiteral(DC.date, "29-03-2016");
		base.addLiteral(DC.description, "This is a vocabulary for the official property sets from IFC 4 documentation. ");
        		
		model.setNsPrefix("ifcowl", IFCOWL);
		model.setNsPrefix("express",EXPRESS);
		model.setNsPrefix("rdfs",RDFS.getURI());
		model.setNsPrefix("rdf",RDF.getURI());
		model.setNsPrefix("xsd", XSD.getURI());
		model.setNsPrefix("schm", SCHM);
		model.setNsPrefix("sp", SP.BASE_URI+"#");
		model.setNsPrefix("spin", SPIN.BASE_URI+"#");
		model.setNsPrefix("spl", SPL.BASE_URI+"#");
        model.setNsPrefix("dc", DC.getURI());
        model.setNsPrefix("owl", OWL.getURI());
//	    ifcSchema.read("src/main/java/nl/tue/ddss/bimsparql/resource/IFC2X3_TC1.owl");
		groups=model.createProperty(IFCDOC,"groups");
		isGroupedBy=model.createProperty(IFCDOC,"isGroupedBy");

			InputStream in=getClass().getClassLoader().getResourceAsStream("IFC2X3_TC1.ttl");
			ifcSchema=ModelFactory.createDefaultModel();
			ifcSchema.read(in,null,"TTL");

		
	}
	

	
	public static void main(String[] args) throws FileNotFoundException{
		QuantityParser pp=new QuantityParser();
		OutputStream out=new FileOutputStream("C:\\users\\chi\\desktop\\qto.ttl");
		
		try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/IFC4_TC1_CV.ifcdoc"))) {
		    String line;
		    while ((line = br.readLine()) != null) {	      
		    	   pp.parseQuantitySet(line);
		    }
		    for(IfcdocVO ifcdocVO:pp.linemap.values()){
		    	if(ifcdocVO!=null&&ifcdocVO.getName()!=null){
		    	if(ifcdocVO.getName().equals("DOCQUANTITYSET")){
		    		pp.addQuantitySet(ifcdocVO,pp.model);
		    	}
		    	}
		    }
		    for(Resource r:JenaUtil.getAllInstances(pp.model.getResource(SPIN.MagicProperty.getURI()))){
		    	if(r.getProperty(SPIN.body)==null){
		    		pp.model.removeAll(r, null, null);
		    	}
		    }
		    pp.model.write(out,"TTL");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addQuantitySet(IfcdocVO ifcdocVO,Model model) {
		ArrayList<Object> objectList=ifcdocVO.getObjectList();
		Resource pset=model.createResource(QTO+objectList.get(0));
		pset.addProperty(RDF.type, model.createResource(IFCDOC+"QuantitySet"));
		for (String id:(ArrayList<String>)objectList.get(9)){
			IfcdocVO label=linemap.get(Long.parseLong(id.substring(1)));
			ArrayList<Object> labelValues=label.getObjectList();
			Literal literal=model.createLiteral((String)labelValues.get(2), (String)labelValues.get(0));
			pset.addLiteral(RDFS.label, literal);
		};
		for (String id:(ArrayList<String>)objectList.get(11)){
			IfcdocVO property=linemap.get(Long.parseLong(id.substring(1)));
			addQuantity(ifcdocVO,property,model);			
		};
//		objectList.get(12);
		pset.addLiteral(RDFS.comment, objectList.get(1).toString());	
	}
	
	@SuppressWarnings("unchecked")
	private void addQuantity(IfcdocVO pset,IfcdocVO ifcdocVO, Model model) {
		ArrayList<Object> objectList=ifcdocVO.getObjectList();
		String propertyName=objectList.get(0).toString();
		String psetName=(String)pset.getObjectList().get(0);
		String localName=String.valueOf(propertyName.charAt(0)).toLowerCase()+objectList.get(0).toString().substring(1);
//				+"_"+pset.getObjectList().get(0).toString().substring(5);
		Resource property=model.createResource(QTO+localName+psetName.substring(psetName.indexOf("_")));
		property.addProperty(RDF.type, RDF.Property);
		property.addProperty(RDF.type, SPIN.MagicProperty);
		String propertyType=objectList.get(10).toString().replaceAll("\\s*\\.\\s*", "");
		property.addProperty(RDF.type, model.createResource(IFCDOC+propertyType));
		property.addProperty(RDFS.subClassOf, SPIN.MagicProperties);
		model.add(model.getResource(QTO+pset.getObjectList().get(0)),groups,property);
		model.add(property,isGroupedBy,model.getResource(QTO+pset.getObjectList().get(0)));
		for (String id:(ArrayList<String>)objectList.get(9)){
			IfcdocVO label=linemap.get(Long.parseLong(id.substring(1)));
			ArrayList<Object> labelValues=label.getObjectList();
			Literal literal=model.createLiteral((String)labelValues.get(2), (String)labelValues.get(0));
			property.addLiteral(RDFS.label, literal);
		};
		property.addLiteral(RDFS.comment, objectList.get(1).toString());
	//	property.addProperty(RDFS.range, model.createResource(IFCOWL+objectList.get(10)));
		String domain=pset.getObjectList().get(10).toString();


		if(domain.contains(",")){
			String[] domains=domain.split(",\\s*");
			for (String d:domains){
				property.addProperty(RDFS.domain,model.createResource(IFCOWL+d));
			}
		}else if (domain.contains("/")){
			domain=domain.substring(0, domain.indexOf("/"));
			property.addProperty(RDFS.domain,model.createResource(IFCOWL+domain));
		}
		else property.addProperty(RDFS.domain, model.createResource(IFCOWL+domain));
		
	singleValueInference(property,propertyName,propertyType);

		
	}
	
	private void singleValueInference(Resource property,String propertyName,String propertyType){
		
		String lastTP=new String();
        if(propertyType.equals("Q_LENGTH")){
        	lastTP="?p ifcowl:lengthValue_IfcQuantityLength ?val .\n";
        }
        else if(propertyType.equals("Q_WEIGHT")){
        	lastTP="?p ifcowl:weightValue_IfcQuantityWeight ?val .\n";
        }
        else if(propertyType.equals("Q_TIME")){
        	lastTP="?p ifcowl:timeValue_IfcQuantityTime ?val .\n";
        }
        else if(propertyType.equals("Q_AREA")){
        	lastTP="?p ifcowl:areaValue_IfcQuantityArea ?val .\n";
        }
        else if(propertyType.equals("Q_COUNT")){
        	lastTP="?p ifcowl:countValue_IfcQuantityCount ?val .\n";
        }else if (propertyType.equals("Q_VOLUME")){
        	lastTP="?p ifcowl:volumeValue_IfcQuantityVolume ?val .\n";
        }

        if(lastTP.length()>0){
		String query="SELECT ?str\n"
				+	"WHERE {\n"
			  + " ?arg1 schm:hasQuantity ?p .\n"
			    +"?p ifcowl:name_IfcPhysicalQuantity ?name .\n"
			   + "?name express:hasString \""+propertyName+"\" .\n"
			   +lastTP
			   +"?val express:hasDouble ?str ."
			+"}";	
			
			if(property.getProperty(SPIN.body)==null){
				Query q=ARQ2SPIN.parseQuery(query, model);
			property.addProperty(SPIN.body, q);
			Resource bnode=model.createResource();
			bnode.addProperty(RDF.type, SPL.Argument);
			bnode.addProperty(SPL.predicate,SP.arg1);
			bnode.addProperty(SPL.valueType, RDFS.Resource);
			property.addProperty(SPIN.constraint,bnode);
			}
        }
	}
	
	

	private void parseQuantitySet(String line) {
		    IfcdocVO ifcvo = new IfcdocVO();
			int state = 0;
			StringBuffer sb = new StringBuffer();
			int cl_count = 0;
			ArrayList<Object> current = ifcvo.getObjectList();
			Stack<ArrayList<Object>> list_stack = new Stack<ArrayList<Object>>();
			for (int i = 0; i < line.length(); i++) {
				char ch = line.charAt(i);
				switch (state) {
				case 0:
					if (ch == '=') {
						ifcvo.setLine_num(Long.parseLong(sb.toString()));
						sb.setLength(0);
						state++;
						continue;
					} else if (Character.isDigit(ch))
						sb.append(ch);
					break;
				case 1: // (
					if (ch == '(') {
						ifcvo.setName(sb.toString());
						sb.setLength(0);
						state++;
						continue;
					} else if (ch == ';') {
						ifcvo.setName(sb.toString());
						sb.setLength(0);
						state = Integer.MAX_VALUE;
					} else if (!Character.isWhitespace(ch))
						sb.append(ch);
					break;
				case 2: // (... line started and doing (...
					if (ch == '\'') {
						state++;
						break;
					}
					if (ch == '(') {
						list_stack.push(current);
						ArrayList<Object> tmp = new ArrayList<Object>();
						if (sb.toString().trim().length() > 0)
							current.add(sb.toString().trim());
						sb.setLength(0);
						current.add(tmp);
						current = tmp;
						cl_count++;
						// sb.append(ch);
					} else if (ch == ')') {
						if (cl_count == 0) {
							if (sb.toString().trim().length() > 0)
								current.add(sb.toString().trim());
							sb.setLength(0);
							state = Integer.MAX_VALUE; // line is done
							continue;
						} else {
							if (sb.toString().trim().length() > 0)
								current.add(sb.toString().trim());
							sb.setLength(0);
							cl_count--;
							current = list_stack.pop();
						}
					} else if (ch == ',') {
						if (sb.toString().trim().length() > 0)
							current.add(sb.toString().trim());
				//		current.add(Character.valueOf(ch));

						sb.setLength(0);
					} else {
						sb.append(ch);
					}
					break;
				case 3: // (...
					if (ch == '\'') {
						if(sb.length()==0){
							sb.append('$');
						}
						state--;
					} else {
						sb.append(ch);
					}
					break;
				default:
					// Do nothing
				}
			}
			linemap.put(ifcvo.getLine_num(), ifcvo);
		}
	

	private class IfcdocVO{
		
		long line_num;
		String name;
		ArrayList<Object> objectList=new ArrayList<Object>();
		

		public void setLine_num(long line_num) {
			// TODO Auto-generated method stub
			this.line_num=line_num;
		}

		public long getLine_num() {
			// TODO Auto-generated method stub
			return line_num;
		}

		public void setName(String name) {
			// TODO Auto-generated method stub
			this.name=name;
		}
		
		public String getName(){
			return name;
		}

		public ArrayList<Object> getObjectList() {
			// TODO Auto-generated method stub
			return objectList;
		}
		
	}
	
	
	

}
