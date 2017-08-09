package com.finebi.cube.location.convert;

import com.finebi.cube.utils.BILocationUtils;
import com.fr.stable.StringUtils;

import java.util.List;

/**
 * Created by wang on 2017/7/18.
 */
public class BILocationTree {
    private BILocationTreeNode<String> root;
    public BILocationTree(String root){
        this.root = new BILocationTreeNode<String>(root,false);
    }

    public void insertTree(String treePath){
        String[] treeStrings = treePath.split(BILocationUtils.LOCATION_SEPERATOR);
        String parentPath;
        String currentPath  ;
        for(int i =1;i<treeStrings.length-1;i++){
            parentPath = StringUtils.EMPTY;
            for(int j=1;j<i;j++) {
                parentPath = parentPath+BILocationUtils.LOCATION_SEPERATOR+treeStrings[j];
            }
            currentPath = parentPath+BILocationUtils.LOCATION_SEPERATOR+treeStrings[i];
            BILocationTreeNode<String> tmpNode = new BILocationTreeNode<String>(currentPath, false);
            tmpNode.setParentPath(parentPath);
            this.insertNode(tmpNode);
        }
        parentPath = StringUtils.EMPTY;
        for(int i=1;i<treeStrings.length-1;i++) {
            parentPath = parentPath+BILocationUtils.LOCATION_SEPERATOR+treeStrings[i];
        }
        currentPath = parentPath+BILocationUtils.LOCATION_SEPERATOR+treeStrings[treeStrings.length-1];
        BILocationTreeNode<String> leaf = new BILocationTreeNode<String>(currentPath, true);
        leaf.setParentPath(parentPath);
        this.insertNode(leaf);
    }

    public void removeLeaf(String treePath){
        root.removeChild(treePath);
    }

    public List<BILocationTreeNode<String>> getLeafs(String parentPath){
        return root.getAllLeafs(parentPath);
    }

    private void insertNode(BILocationTreeNode node){
        root.insertJuniorNode(node);
    }
    public static void main(String[] args) {

    }
}
