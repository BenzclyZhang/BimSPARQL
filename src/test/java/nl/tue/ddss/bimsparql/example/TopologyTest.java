package nl.tue.ddss.bimsparql.example;

public class TopologyTest extends QueryFunctionTest{
	
	public static void main(String[] args){
 //       String query="SELECT ?wall ?w ?d\n" + "WHERE{\n" +"?wall a ifcowl:IfcWall .\n"+"?wall schm:isContainedIn ?storey .\n"+"?storey a ifcowl:IfcBuildingStorey .\n"+"?w schm:isContainedIn ?storey .\n"+"?w a ifcowl:IfcDoor .\n"+"(?wall ?w) spt:distance ?d .\n"+ "}";
 //       executeQuery(query);
 //       String query1="SELECT ?s ?storey\n" + "WHERE{\n" +"?s a ifcowl:IfcBuildingStorey .\n"+"?s spt:hasUpperFloor ?storey .\n"+"?storey a ifcowl:IfcBuildingStorey .\n"+ "}";
 //       executeQuery(query1);
          String query="SELECT ?wall ?w \n" + "WHERE{\n" +"?wall a ifcowl:IfcWall .\n"+"?w a ifcowl:IfcDoor .\n"+"?wall spt:touches ?w .\n"+ "}";
          executeQuery(query);

	}

}
