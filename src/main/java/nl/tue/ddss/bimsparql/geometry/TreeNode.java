package nl.tue.ddss.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
	T self;
    TreeNode<T> parent;
    List<TreeNode<T>> children;
    int intersects;
    int isExternal;
    List<Product> intersectedPdt=new ArrayList<Product>();
    boolean iterated;
    int[] position;
    int branch;
    int id;
    List<TreeNode<T>> neighbors;
    
    public TreeNode(T self){
    	this.self=self;
    	intersects=0;
    	iterated=false;
    	children=new ArrayList<TreeNode<T>>();
    }

	public TreeNode<T> getParent() {
		return parent;
	}
	

	public void setParent(TreeNode<T> parent) {
		this.parent = parent;
	}
	
	public void transPosition(){
		
	}
	
	public void connect(TreeNode<AxisAlignedBox> e){
		
	}
	
	public List<TreeNode<T>> getSiblings(){
		List<TreeNode<T>> list=getParent().getChildren();
		List<TreeNode<T>> siblings=new ArrayList<TreeNode<T>>();
		for(TreeNode<T> node:list){
			if (node!=this){
				siblings.add(node);
			}
		}
		return siblings;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode<T>> children) {
		this.children = children;
	}

	public T getSelf() {
		return self;
	}

	public void setSelf(T self) {
		this.self = self;
	}
    
    
}
