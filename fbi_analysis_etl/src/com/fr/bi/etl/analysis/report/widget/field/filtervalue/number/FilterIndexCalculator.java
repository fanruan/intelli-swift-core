package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.index.NumberIndexCreater;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;

public class FilterIndexCalculator implements ResultDealer {

	NumberIndexCreater creater;
	
	private GroupValueIndex res = GVIFactory.createAllEmptyIndexGVI();
	
	FilterIndexCalculator(NumberIndexCreater creater){
		this.creater = creater;
	}
	@Override
	public void dealWith(ICubeTableService ti, GroupValueIndex currentIndex) {
		res = res.or(creater.createFilterGvi(currentIndex));
	}
	
	GroupValueIndex getResult() {
		return res;
	}
	
}