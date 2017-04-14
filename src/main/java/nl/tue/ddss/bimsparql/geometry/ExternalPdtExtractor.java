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



public class ExternalPdtExtractor {

	List<Product> products;
	AABB overall;
	List<TreeNode<AxisAlignedBox>> emptyBoxes = new ArrayList<TreeNode<AxisAlignedBox>>();
	List<TreeNode<AxisAlignedBox>> referenceBoxes = new ArrayList<TreeNode<AxisAlignedBox>>();
	List<TreeNode<AxisAlignedBox>> oneIntBoxes = new ArrayList<TreeNode<AxisAlignedBox>>();
	List<TreeNode<AxisAlignedBox>> multiIntBoxes = new ArrayList<TreeNode<AxisAlignedBox>>();
	List<TreeNode<AxisAlignedBox>> allBoxes = new ArrayList<TreeNode<AxisAlignedBox>>();
	TreeNode<AxisAlignedBox> reference;
	static final int[] TOP={4,5,6,7};
	static final int[] BOTTOM={0,1,2,3};
	static final int[] WEST={0,2,4,6};
	static final int[] EAST={1,3,5,7};
	static final int[] SOUTH={0,1,4,5};
	static final int[] NORTH={2,3,6,7};
	int[] id;
	
	Map<Product,List<TreeNode<AxisAlignedBox>>> hashMap=new HashMap<Product,List<TreeNode<AxisAlignedBox>>>();
	
	public ExternalPdtExtractor(List<Product> products,float precision) throws Exception{
		this.overall = new AABB();
		for (Product product : products) {
			AABB bb = product.getBoundingBox();
			overall.addBoundingBox(bb);
		}
		AxisAlignedBox rootBox = overall.toCube();
		buildBoxTree(new TreeNode<AxisAlignedBox>(rootBox),products,precision);
		this.id=new int[this.emptyBoxes.size()];
		for(int i=0;i<id.length;i++){
			id[i]=i;
		}
        connectAll();
		for (TreeNode<AxisAlignedBox> b:emptyBoxes){
			if (!GeometryUtils.boxBoxIntersection(b.getSelf(), overall)){
				reference=b;
				break;
			}
		}
		for (TreeNode<AxisAlignedBox> b:emptyBoxes){
			if (connected(reference,b)){
				b.isExternal=1;
				referenceBoxes.add(b);
			}
		}
	}
	
	

	public List<Product> getExternalProducts(List<Product> products)
			throws Exception {
		AABB overall = new AABB();
		for (Product product : products) {
			AABB bb = product.getBoundingBox();
			overall.addBoundingBox(bb);
		}
		AxisAlignedBox rootBox = overall.toCube();
		List<TreeNode<AxisAlignedBox>> externalBoxes = new ArrayList<TreeNode<AxisAlignedBox>>();
		List<Product> externalProducts = new ArrayList<Product>();
		externalProducts = getExternalProducts(new TreeNode<AxisAlignedBox>(rootBox),
				products, externalBoxes, externalProducts);
		return externalProducts;
	}

	private List<Product> getExternalProducts(TreeNode<AxisAlignedBox> root,
			List<Product> products, List<TreeNode<AxisAlignedBox>> externalBoxes,
			List<Product> externalProducts) throws Exception {
		AxisAlignedBox box = root.getSelf();
		AxisAlignedBox[] children = box.ocSplit();
		for (AxisAlignedBox b : children) {
			TreeNode<AxisAlignedBox> child = new TreeNode<AxisAlignedBox>(b);
			root.getChildren().add(child);
			child.setParent(root);
		}
		TreeNode<AxisAlignedBox> child0 = root.getChildren().get(0);
		for (int i = 0; i < products.size(); i++) {
			if (GeometryUtils.boxProductIntersection(child0.getSelf(),
					products.get(i))) {
				getExternalProducts(child0, products, externalBoxes,
						externalProducts);
				break;
			} else if (i == products.size() - 1) {
				externalBoxes.add(child0);
				child0.isExternal = 1;
				child0.iterated = true;
				processBoxes(child0, products, externalBoxes, externalProducts);
			}
		}
		return externalProducts;
	}

	private void processBoxes(TreeNode<AxisAlignedBox> start, List<Product> products,
			List<TreeNode<AxisAlignedBox>> externalBoxes, List<Product> externalProducts)
			throws Exception {
		if (!start.iterated) {
			for (Product product : products) {
				if (GeometryUtils.boxProductIntersection(start.getSelf(),
						product)) {
					start.intersectedPdt.add(product);
					start.intersects = start.intersects + 1;
				}
				if (start.intersects > 1) {
					break;
				}
			}
			if (start.intersects == 0) {
				for (TreeNode<AxisAlignedBox> exb : externalBoxes) {
					if (GeometryUtils.boxBoxIntersection(start.getSelf(),
							exb.getSelf())) {
						start.isExternal = 1;
						externalBoxes.add(start);
						break;
					}
				}

			} else if (start.intersects == 1) {
				for (TreeNode<AxisAlignedBox> exb : externalBoxes) {
					if (GeometryUtils.boxBoxIntersection(start.getSelf(),
							exb.getSelf())) {
						if (!externalProducts.contains(start.intersectedPdt)) {
							externalProducts.addAll(start.intersectedPdt);
						}
						break;
					}
				}
			}
			if (start.intersects > 1 && start.getSelf().getLength() > 0.1) {
				AxisAlignedBox[] children = start.getSelf().ocSplit();
				for (AxisAlignedBox child : children) {
					TreeNode<AxisAlignedBox> b = new TreeNode<AxisAlignedBox>(child);
					b.setParent(start);
					start.getChildren().add(b);
				}
				for (TreeNode<AxisAlignedBox> n : start.getChildren()) {
					processBoxes(n, products, externalBoxes, externalProducts);
				}
			}
			start.iterated = true;
		}
		if (start.getParent() != null) {
			List<TreeNode<AxisAlignedBox>> siblings = start.getSiblings();
			for (TreeNode<AxisAlignedBox> node : siblings) {
				if (!node.iterated) {
					for (Product product : products) {
						if (GeometryUtils.boxProductIntersection(node.getSelf(),
								product)) {
							node.intersectedPdt.add(product);
							node.intersects = node.intersects + 1;
						}
						if (node.intersects > 1) {
							break;
						}
					}
					if (node.intersects == 0) {
						if (start.isExternal == 1) {
							node.isExternal = 1;
							externalBoxes.add(node);
						} else {
							for (TreeNode<AxisAlignedBox> exb : externalBoxes) {
								if (GeometryUtils.boxBoxIntersection(
										node.getSelf(), exb.getSelf())) {
									node.isExternal = 1;
									externalBoxes.add(node);
									break;
								}
							}
						}
					} else if (node.intersects == 1) {
						if (start.isExternal == 1) {
							if (!externalProducts.contains(node.intersectedPdt)) {
								externalProducts.addAll(node.intersectedPdt);
							}
						} else {
							for (TreeNode<AxisAlignedBox> exb : externalBoxes) {
								if (GeometryUtils.boxBoxIntersection(
										node.getSelf(), exb.getSelf())) {
									if (!externalProducts
											.contains(node.intersectedPdt)) {
										externalProducts
												.addAll(node.intersectedPdt);
									}
									break;
								}
							}
						}
					}
					if (node.intersects > 1 && node.getSelf().getLength() > 0.1) {
						AxisAlignedBox[] children = node.getSelf().ocSplit();
						for (AxisAlignedBox child : children) {
							TreeNode<AxisAlignedBox> b = new TreeNode<AxisAlignedBox>(child);
							b.setParent(node);
							node.getChildren().add(b);
						}
						for (TreeNode<AxisAlignedBox> n : node.getChildren()) {
							processBoxes(n, products, externalBoxes,
									externalProducts);
						}
					}
					node.iterated = true;
				}
			}
			TreeNode<AxisAlignedBox> parent = start.getParent();
			parent.iterated = true;
			processBoxes(parent, products, externalBoxes, externalProducts);
		}
	}

	private void buildBoxTree(TreeNode<AxisAlignedBox> root, List<Product> products,
			float precision) throws Exception {
		AxisAlignedBox box = root.getSelf();
		for (Product product : products) {
			if (GeometryUtils.boxProductIntersection(box, product)) {
				root.intersectedPdt.add(product);
				root.intersects = root.intersects + 1;
			}
		}
		if (root.intersects == 0) {
			emptyBoxes.add(root);
			root.id=emptyBoxes.size()-1;

		} else if (root.intersects == 1) {
			oneIntBoxes.add(root);
			if(hashMap.containsKey(root.intersectedPdt)){
				hashMap.get(root.intersectedPdt.get(0)).add(root);
			}
			else{
				List<TreeNode<AxisAlignedBox>> list=new ArrayList<TreeNode<AxisAlignedBox>>();
				list.add(root);
				hashMap.put(root.intersectedPdt.get(0), list);
			}
		} else if (root.intersects > 1) {
			if (root.getSelf().getLength() > precision) {
				AxisAlignedBox[] children = box.ocSplit();
				for (int i=0;i<8;i++) {
					TreeNode<AxisAlignedBox> b = new TreeNode<AxisAlignedBox>(children[i]);
					b.setParent(root);
					root.getChildren().add(b);
					b.branch=i;
					buildBoxTree(b, root.intersectedPdt, precision);
				}
			} else
				multiIntBoxes.add(root);
		}
	}
	
	public List<TreeNode<AxisAlignedBox>> buildNodeMatrix(TreeNode<AxisAlignedBox> root,
			float precision,List<TreeNode<AxisAlignedBox>> nodes) throws Exception {
			AxisAlignedBox[] children = root.getSelf().ocSplit();
			for (int i=0;i<8;i++) {
				TreeNode<AxisAlignedBox> b = new TreeNode<AxisAlignedBox>(children[i]);
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
	
	private void connect(TreeNode<AxisAlignedBox> n1,TreeNode<AxisAlignedBox> n2){
		connect(emptyBoxes.indexOf(n1),emptyBoxes.indexOf(n2));
	}
	
	private void connect(int i,int j){
		id[root(i)]=root(j);
	}
	
	private boolean connected(int i,int j){
		return root(i)==root(j);
	}
	
	public boolean connected(TreeNode<AxisAlignedBox> n1,TreeNode<AxisAlignedBox> n2){
		if (n1.intersects==0&&n2.intersects==0){
			return connected(emptyBoxes.indexOf(n1),emptyBoxes.indexOf(n2));
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
		 for (int i=0;i<emptyBoxes.size();i++){
	        	List<TreeNode<AxisAlignedBox>> list=getNeighborBoxes(emptyBoxes.get(i));
	        	emptyBoxes.get(i).neighbors=list;
	        	for(TreeNode<AxisAlignedBox> box:list){
	        		if (box.intersects==0&&!connected(emptyBoxes.get(i),box)){
	        		connect(emptyBoxes.get(i),box);
	        	}
	        }
		 }
	}
	
	
	public List<TreeNode<AxisAlignedBox>> getNeighborBoxes(TreeNode<AxisAlignedBox> node){
		List<TreeNode<AxisAlignedBox>> boxes=new ArrayList<TreeNode<AxisAlignedBox>>();
		boxes.addAll(getTopNodes(node));
		boxes.addAll(getBottomNodes(node));
		boxes.addAll(getEastNodes(node));
		boxes.addAll(getWestNodes(node));
		boxes.addAll(getNorthNodes(node));
		boxes.addAll(getSouthNodes(node));
		return boxes;
	}
	public List<TreeNode<AxisAlignedBox>> getTopNodes(TreeNode<AxisAlignedBox> node){
		TreeNode<AxisAlignedBox> topNode=getNeighborNode(node,BOTTOM,0,"",4);
		List<TreeNode<AxisAlignedBox>> nodes=new ArrayList<TreeNode<AxisAlignedBox>>();
		if (topNode!=null){
			return getChildrenOnBranches(topNode,BOTTOM,nodes);
		}
		else{
			return nodes;
		}
	}
	
	public List<TreeNode<AxisAlignedBox>> getBottomNodes(TreeNode<AxisAlignedBox> node){
		TreeNode<AxisAlignedBox> bottomNode=getNeighborNode(node,TOP,0,"",-4);
		List<TreeNode<AxisAlignedBox>> nodes=new ArrayList<TreeNode<AxisAlignedBox>>();
		if (bottomNode!=null){
			return getChildrenOnBranches(bottomNode,TOP,nodes);
		}
		else{
			return nodes;
		}
	}
	
	public List<TreeNode<AxisAlignedBox>> getWestNodes(TreeNode<AxisAlignedBox> node){
		TreeNode<AxisAlignedBox> westNode=getNeighborNode(node,EAST,0,"",-1);
		List<TreeNode<AxisAlignedBox>> nodes=new ArrayList<TreeNode<AxisAlignedBox>>();
		if (westNode!=null){
			return getChildrenOnBranches(westNode,EAST,nodes);
		}
		else{
			return nodes;
		}
	}
	
	public List<TreeNode<AxisAlignedBox>> getEastNodes(TreeNode<AxisAlignedBox> node){
		TreeNode<AxisAlignedBox> eastNode=getNeighborNode(node,WEST,0,"",1);
		List<TreeNode<AxisAlignedBox>> nodes=new ArrayList<TreeNode<AxisAlignedBox>>();
		if (eastNode!=null){
			return getChildrenOnBranches(eastNode,WEST,nodes);
		}
		else{
			return nodes;
		}
	}
	
	public List<TreeNode<AxisAlignedBox>> getNorthNodes(TreeNode<AxisAlignedBox> node){
		TreeNode<AxisAlignedBox> northNode=getNeighborNode(node,SOUTH,0,"",2);
		List<TreeNode<AxisAlignedBox>> nodes=new ArrayList<TreeNode<AxisAlignedBox>>();
		if (northNode!=null){
			return getChildrenOnBranches(northNode,SOUTH,nodes);
		}
		else{
			return nodes;
		}
	}
	
	public List<TreeNode<AxisAlignedBox>> getSouthNodes(TreeNode<AxisAlignedBox> node){
		TreeNode<AxisAlignedBox> southNode=getNeighborNode(node,NORTH,0,"",-2);
		List<TreeNode<AxisAlignedBox>> nodes=new ArrayList<TreeNode<AxisAlignedBox>>();
		if (southNode!=null){
			return getChildrenOnBranches(southNode,NORTH,nodes);
		}
		else{
			return nodes;
		}
	}
	
	
	private TreeNode<AxisAlignedBox> getNeighborNode(TreeNode<AxisAlignedBox> node,int[] position,int i,String branch,int dif){
		if (node.getParent()!=null){
			int b=node.branch;
			if (ArrayUtils.contains(position, b)){
					TreeNode<AxisAlignedBox> topNode=node.getParent().getChildren().get(b+dif);
					TreeNode<AxisAlignedBox> n=getRelatedNode(topNode,i,branch,dif);
					return n;
			}
			else{
				TreeNode<AxisAlignedBox> parent=node.getParent();
			    i++;
			    branch=branch+b;
			    return getNeighborNode(parent,position,i,branch,dif);
			}
		}
		else {return null;}
		}


    private TreeNode<AxisAlignedBox> getRelatedNode(TreeNode<AxisAlignedBox> node,int i,String position,int dif){
    	if (i>0){
    		char c=position.charAt(i-1);
			int p=Character.getNumericValue(c);
			if (node.getChildren().size()>0){
				i--;
				TreeNode<AxisAlignedBox> n=node.getChildren().get(p-dif);
				return getRelatedNode(n,i,position,dif);
    	}
			else{return node;}
    	} else{return node;}
    }
	
	public List<TreeNode<AxisAlignedBox>> getChildrenOnBranches(TreeNode<AxisAlignedBox> node,int[] branches,List<TreeNode<AxisAlignedBox>> nodes){
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
			
	public List<TreeNode<AxisAlignedBox>> getIntersections(List<TreeNode<AxisAlignedBox>> nodes,List<Product> products,AABB overall) throws GeometryException{
		List<TreeNode<AxisAlignedBox>> outsideBoxes=new ArrayList<TreeNode<AxisAlignedBox>>();
		for (TreeNode<AxisAlignedBox> box:nodes){
		if (GeometryUtils.boxBoxIntersection(overall, box.getSelf())){
			for(Product p:products){
				if(GeometryUtils.boxProductIntersection(box.getSelf(), p)){
					box.intersectedPdt.add(p);
					box.intersects = box.intersects + 1;
					if(hashMap.get(p)!=null){
						hashMap.get(p).add(box);
					}
					else{
						List<TreeNode<AxisAlignedBox>> intNodes=new ArrayList<TreeNode<AxisAlignedBox>>();
						intNodes.add(box);
						hashMap.put(p, intNodes);
					}
				}
			}
	}
		else {
			outsideBoxes.add(box);
		}
}return outsideBoxes;
	}

}
