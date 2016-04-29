package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.result.NewDiskBaseRootNode;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.stable.report.result.LightNode;

public class ComboSingleDimensionGroup implements ISingleDimensionGroup {

	
	private ISingleDimensionGroup[] iSingleDimensionGroup;
	
	
    protected volatile NewDiskBaseRootNode root;
	
	@Override
	public int getChildIndexByValue(Object value) {
		// TODO Auto-generated method stub
		return root.getIndexByValue(value);
	}

	@Override
	public NoneDimensionGroup getChildDimensionGroup(int row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getChildData(int row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getChildShowName(int row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getChildNode(int row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LightNode getRoot() {
		// TODO Auto-generated method stub
		return root;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCurrentTotalRow() {
		// TODO Auto-generated method stub
		return 0;
	}

}