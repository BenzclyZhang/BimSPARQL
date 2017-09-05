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
package nl.tue.ddss.bimsparql.geometry.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.AxisAlignedBox;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.Product;

public class TreeNode<T> {
	
	T self;
    TreeNode<T> parent;
    List<TreeNode<T>> children;
    int intersects;
    int isExternal;
    List<Product> intersectedPdt=new ArrayList<Product>();
    HashSet<Geometry> intersectedGeometry=new HashSet<Geometry>();
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
