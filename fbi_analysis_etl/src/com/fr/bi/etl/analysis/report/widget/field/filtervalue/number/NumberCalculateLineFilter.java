/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.field.filtervalue.number.NumberFilterValue;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.index.NumberIndexCreater;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.line.AvgLine;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.line.CalLineGetter;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.utils.BIServerUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;


/**
 * @author Daniel
 *
 */
public abstract class NumberCalculateLineFilter implements NumberFilterValue{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5289564327012309298L;

	private Operator t;
	
	protected CalLineGetter getter = AvgLine.INSTANCE;
	
	private BIKey[] dimension;
	
	private BIKey key;
	
	
	
	NumberCalculateLineFilter(Operator t){
		this.t = t;
	}


	@Override
	public boolean canCreateFilterIndex() {
		return true;
	}

	@Override
	public GroupValueIndex createFilterIndex(DimensionCalculator dimension, Table target, ICubeDataLoader loader,
                                             long userId) {
		ICubeTableService ti = loader.getTableIndex(target);
		GroupValueIndex gvi = ti.getAllShowIndex();
		NumberIndexCreater creater = new NumberIndexCreater(ti, t, key, getter);
		FilterIndexCalculator dealer = new FilterIndexCalculator(creater);
		ResultDealer dimensionDealer = BIServerUtils.createDimensonDealer(this.dimension, dealer);
		dimensionDealer.dealWith(ti, gvi, 0);
		return dealer.getResult();
	}
	

	@Override
	public void parseJSON(JSONObject jo, long userId) throws Exception {
		if(jo.has("field")){
			this.key = new IndexKey(jo.getString("field"));
		}
		if(jo.has("dimension")){
			JSONArray ja = jo.getJSONArray("dimension");
			this.dimension = new BIKey[ja.length()];
			for(int i = 0; i < jo.length(); i++){
				this.dimension[i] = new IndexKey(ja.getString(i));
			}
		}
	}

	@Override
	public JSONObject createJSON() throws Exception {
		return null;
	}

	@Override
	public void readXML(XMLableReader reader) {
		
	}

	@Override
	public void writeXML(XMLPrintWriter writer) {
	}

	@Override
	public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMatchValue(Number value) {
		return false;
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

}