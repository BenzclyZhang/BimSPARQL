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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;

public class ExternalPdtIdentifier {

	Product p;
	List<Product> products;
	AABB overall=new AABB();
	TreeNode<Square> root;
	List<TreeNode<Square>> emptySquares = new ArrayList<TreeNode<Square>>();
	List<TreeNode<Square>> referenceSquares = new ArrayList<TreeNode<Square>>();
	List<TreeNode<Square>> oneIntSquares = new ArrayList<TreeNode<Square>>();
	List<TreeNode<Square>> multiIntSquares = new ArrayList<TreeNode<Square>>();
	List<TreeNode<Square>> allSquares = new ArrayList<TreeNode<Square>>();
	TreeNode<Square> reference;
	static final int[] WEST={0,2};
	static final int[] EAST={1,3};
	static final int[] SOUTH={0,1};
	static final int[] NORTH={2,3};
	int[] id;
	
	Map<Product,List<TreeNode<Square>>> hashMap=new HashMap<Product,List<TreeNode<Square>>>();
	
	
	public ExternalPdtIdentifier(Product subject,List<Product> pdts) throws Exception{
	/*	Node relatedObjects=NodeFactory.createURI("http://rdfrepo.ddss.nl/schema/IFC2X3#relatedObjects_of_IfcRelDecomposes");
		Node relatingObject=NodeFactory.createURI("http://rdfrepo.ddss.nl/schema/IFC2X3#relatingObject");
		ExtendedIterator<Node> stmt=GraphUtil.listSubjects(graph,hasGeometry,null);
		List<Product> pdts=new ArrayList<Product>();
		Product subject=new Product();
		HashMap<Node,List<Triangle>> composedProductGeometry=new HashMap<Node,List<Triangle>>();
		while(stmt.hasNext()){
			Node obj=stmt.next();
			if(!GraphUtil.listObjects(graph, obj, RDF.type.asNode()).next().getLocalName().equals("IfcFurnishingElement")){
			List<Triangle> ts=new ArrayList<Triangle>();
			Node geometry=GraphUtil.listObjects(graph,obj, hasGeometry).next();
			ExtendedIterator<Node> st=GraphUtil.listObjects(graph,geometry,hasTriangle);
			while(st.hasNext()){
				List<Point3d> ps=new ArrayList<Point3d>();
				List<Node> ss=GraphUtil.listObjects(graph,st.next(),hasPoint).toList();
				for(Node s:ss){
					double x=NodeFactoryExtra.nodeToDouble(GraphUtil.listObjects(graph, s, hasX).next());
					double y=NodeFactoryExtra.nodeToDouble(GraphUtil.listObjects(graph, s, hasY).next());
					double z=NodeFactoryExtra.nodeToDouble(GraphUtil.listObjects(graph, s, hasZ).next());
					Point3d p=new Point3d(x,y,z);
					ps.add(p);
				}
				Triangle t=new Triangle(ps.get(0),ps.get(1),ps.get(2));
				ts.add(t);
			}
			ExtendedIterator<Node> compositions=GraphUtil.listSubjects(graph,relatedObjects,obj);
			if(compositions.hasNext()){
				Node father=GraphUtil.listObjects(graph,compositions.next(),relatingObject).next();
				if (composedProductGeometry.containsKey(father)){
					composedProductGeometry.get(father).addAll(ts);
				}
				else{
					composedProductGeometry.put(father, ts);
				}
			}
			else{
			Product pdt=new Product(ts);
			if(obj.equals(product)){
				subject=pdt;
			}
			pdts.add(pdt);
			}
			}			
		}
		for (Node f:composedProductGeometry.keySet()){
			Product fp=new Product(composedProductGeometry.get(f));
			if(f.equals(product)){
				subject=fp;
			}
			pdts.add(fp);
		}*/
		init(subject,pdts,(float)0.05);
	}
	
	public ExternalPdtIdentifier(Product p,List<Product> products,float precision) throws Exception{
                 init(p,products,precision);
	}
	
	public ExternalPdtIdentifier(Geometry geometry, List<Geometry> allGeometries) {
		List<Product> products=new ArrayList<Product>();
		Product product=new Product();
	}

	public void init(Product p,List<Product> products,float precision) throws Exception{
		this.p=p;
		BoundingSquare rootSquare = p.getBoundingSquare();
		for (Product product : products) {
			AABB bs = product.getBoundingBox();
			overall.addBoundingBox(bs);
			rootSquare.addBoundingBox(bs);
		}
        root=new TreeNode<Square>(rootSquare.toSquare());
		buildSquareTree(root,products,precision);
		this.id=new int[this.emptySquares.size()];
		for(int i=0;i<id.length;i++){
			id[i]=i;
		}
        connectAll();
		for (TreeNode<Square> b:emptySquares){
			if (!GeometryUtils.squareBoxIntersection(b.getSelf(), overall)){
				reference=b;
				break;
			}
		}
		for (TreeNode<Square> b:emptySquares){
			if (connected(reference,b)){
				b.isExternal=1;
				referenceSquares.add(b);
			}
		}
	}
	
	public boolean getIsExternal(){
		List<TreeNode<Square>> squares=hashMap.get(p);
//		if(squares!=null){
				for(int i=0;i<squares.size();i++){
					if(squares.get(i).intersects==1){
						List<TreeNode<Square>> neighbors=getNeighborSquares(squares.get(i));
						for (int j=0;j<neighbors.size();j++){
							if (neighbors.get(j).intersects==0&&referenceSquares.contains(neighbors.get(j))){
								return true;
							}
							else if(i==squares.size()-1&&j==neighbors.size()-1){
								return false;
							}
						}
					}
				}return false;
//				}
//		 return false;
			}



	private void buildSquareTree(TreeNode<Square> root, List<Product> products,
			float precision) throws Exception {
		Square square = root.getSelf();
		for (Product product : products) {
			if (GeometryUtils.squareProductIntersection(square, product)) {
				root.intersectedPdt.add(product);
				root.intersects = root.intersects + 1;
			}
//			if (root.intersects > 1) {
//					break;
//			}
		}
		if (root.intersects == 0) {
			emptySquares.add(root);
			root.id=emptySquares.size()-1;

		} else if (root.intersects == 1) {
			oneIntSquares.add(root);
			if(hashMap.containsKey(root.intersectedPdt.get(0))){
				hashMap.get(root.intersectedPdt.get(0)).add(root);
			}
			else{
				List<TreeNode<Square>> list=new ArrayList<TreeNode<Square>>();
				list.add(root);
				hashMap.put(root.intersectedPdt.get(0), list);
			}
		} else if (root.intersects > 1) {
			if (root.getSelf().getLength() > precision) {
				Square[] children = square.quadSplit();
				for (int i=0;i<4;i++) {
					TreeNode<Square> b = new TreeNode<Square>(children[i]);
					b.setParent(root);
					root.getChildren().add(b);
					b.branch=i;
					buildSquareTree(b, root.intersectedPdt, precision);
				}
			} else
				multiIntSquares.add(root);
		}
	}
	
	public List<TreeNode<Square>> buildNodeMatrix(TreeNode<Square> root,
			float precision,List<TreeNode<Square>> nodes) throws Exception {
			Square[] children = root.getSelf().quadSplit();
			for (int i=0;i<8;i++) {
				TreeNode<Square> b = new TreeNode<Square>(children[i]);
				b.branch=i;
				if (b.getSelf().getLength()>precision){
					buildNodeMatrix(b, precision,nodes);
				}
				else{
					b.transPosition();
					nodes.add(b);
				}			
			}
			return nodes;
	}
	
	private void connect(TreeNode<Square> n1,TreeNode<Square> n2){
		connect(emptySquares.indexOf(n1),emptySquares.indexOf(n2));
	}
	
	private void connect(int i,int j){
		id[root(i)]=root(j);
	}
	
	private boolean connected(int i,int j){
		return root(i)==root(j);
	}
	
	public boolean connected(TreeNode<Square> n1,TreeNode<Square> n2){
		if (n1.intersects==0&&n2.intersects==0){
			return connected(emptySquares.indexOf(n1),emptySquares.indexOf(n2));
		}
		else return false;
	}
	
	private int root(int i){
		while (i!=id[i]){
			id[i]=id[id[i]];
			i=id[i];
		}
		return i;
	}
	
	private void connectAll(){
		 for (int i=0;i<emptySquares.size();i++){
	        	List<TreeNode<Square>> list=getNeighborSquares(emptySquares.get(i));
	        	emptySquares.get(i).neighbors=list;
	        	for(TreeNode<Square> square:list){
	        		if (square.intersects==0&&!connected(emptySquares.get(i),square)){
	        		connect(emptySquares.get(i),square);
	        	}
	        }
		 }
	}
	
	
	public List<TreeNode<Square>> getNeighborSquares(TreeNode<Square> node){
		List<TreeNode<Square>> squares=new ArrayList<TreeNode<Square>>();
		squares.addAll(getEastNodes(node));
		squares.addAll(getWestNodes(node));
		squares.addAll(getNorthNodes(node));
		squares.addAll(getSouthNodes(node));
		return squares;
	}
	
	public List<TreeNode<Square>> getWestNodes(TreeNode<Square> node){
		TreeNode<Square> westNode=getNeighborNode(node,EAST,0,"",-1);
		List<TreeNode<Square>> nodes=new ArrayList<TreeNode<Square>>();
		if (westNode!=null){
			return getChildrenOnBranches(westNode,EAST,nodes);
		}
		else{
			return nodes;
		}
	}
	
	public List<TreeNode<Square>> getEastNodes(TreeNode<Square> node){
		TreeNode<Square> eastNode=getNeighborNode(node,WEST,0,"",1);
		List<TreeNode<Square>> nodes=new ArrayList<TreeNode<Square>>();
		if (eastNode!=null){
			return getChildrenOnBranches(eastNode,WEST,nodes);
		}
		else{
			return nodes;
		}
	}
	
	public List<TreeNode<Square>> getNorthNodes(TreeNode<Square> node){
		TreeNode<Square> northNode=getNeighborNode(node,SOUTH,0,"",2);
		List<TreeNode<Square>> nodes=new ArrayList<TreeNode<Square>>();
		if (northNode!=null){
			return getChildrenOnBranches(northNode,SOUTH,nodes);
		}
		else{
			return nodes;
		}
	}
	
	public List<TreeNode<Square>> getSouthNodes(TreeNode<Square> node){
		TreeNode<Square> southNode=getNeighborNode(node,NORTH,0,"",-2);
		List<TreeNode<Square>> nodes=new ArrayList<TreeNode<Square>>();
		if (southNode!=null){
			return getChildrenOnBranches(southNode,NORTH,nodes);
		}
		else{
			return nodes;
		}
	}
	
	
	private TreeNode<Square> getNeighborNode(TreeNode<Square> node,int[] position,int i,String branch,int dif){
		if (node.getParent()!=null){
			int b=node.branch;
			if (ArrayUtils.contains(position, b)){
					TreeNode<Square> topNode=node.getParent().getChildren().get(b+dif);
					TreeNode<Square> n=getRelatedNode(topNode,i,branch,dif);
					return n;
			}
			else{
				TreeNode<Square> parent=node.getParent();
			    i++;
			    branch=branch+b;
			    return getNeighborNode(parent,position,i,branch,dif);
			}
		}
		else {return null;}
		}


    private TreeNode<Square> getRelatedNode(TreeNode<Square> node,int i,String position,int dif){
    	if (i>0){
    		char c=position.charAt(i-1);
			int p=Character.getNumericValue(c);
			if (node.getChildren().size()>0){
				i--;
				TreeNode<Square> n=node.getChildren().get(p-dif);
				return getRelatedNode(n,i,position,dif);
    	}
			else{return node;}
    	} else{return node;}
    }
	
	public List<TreeNode<Square>> getChildrenOnBranches(TreeNode<Square> node,int[] branches,List<TreeNode<Square>> nodes){
		if (node.getChildren().size()==0){
			nodes.add(node);
		}
		else{
			for(int i:branches){
				getChildrenOnBranches(node.getChildren().get(i),branches,nodes);
			}
		}
		return nodes;
	}
			
	public List<TreeNode<Square>> getIntersections(List<TreeNode<Square>> nodes,List<Product> products) throws GeometryException{
		List<TreeNode<Square>> outsidesquares=new ArrayList<TreeNode<Square>>();
		for (TreeNode<Square> square:nodes){
		if (GeometryUtils.squareBoxIntersection(square.getSelf(),overall)){
			for(Product p:products){
				if(GeometryUtils.squareProductIntersection(square.getSelf(), p)){
					square.intersectedPdt.add(p);
					square.intersects = square.intersects + 1;
					if(hashMap.get(p)!=null){
						hashMap.get(p).add(square);
					}
					else{
						List<TreeNode<Square>> intNodes=new ArrayList<TreeNode<Square>>();
						intNodes.add(square);
						hashMap.put(p, intNodes);
					}
				}
			}
	}
		else {
			outsidesquares.add(square);
		}
}return outsidesquares;
	}

}
