package com.fr.bi.etl.analysis.data;

import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.conf.data.source.operator.add.ETLFormularOperator;
import com.fr.bi.conf.data.source.operator.add.SingleValueOperator;
import com.fr.bi.conf.data.source.operator.add.ValueConverOperator;
import com.fr.bi.conf.data.source.operator.add.date.GetValueFromDateOperator;
import com.fr.bi.conf.data.source.operator.add.datediff.DateDiffOperator;
import com.fr.bi.conf.data.source.operator.add.express.ExpressionValueOperator;
import com.fr.bi.conf.data.source.operator.add.rowcal.accumulate.AccumulateRowCalculatorOperator;
import com.fr.bi.conf.data.source.operator.add.rowcal.alldata.AllDataRowCalculatorOperator;
import com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiod.CorrespondMonthPeriodRowCalculatorOperator;
import com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiod.CorrespondPriodRowCalculatorOperator;
import com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiodpercentage.CorrespondMonthPeriodPercentRowCalculatorOperator;
import com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiodpercentage.CorrespondPriodPercentRowCalculatorOperator;
import com.fr.bi.conf.data.source.operator.add.rowcal.rank.RankRowCalculatorOperator;
import com.fr.bi.conf.data.source.operator.create.TableColumnFieldsFilterOperator;
import com.fr.bi.conf.data.source.operator.create.TableColumnFilterOperator;
import com.fr.bi.conf.data.source.operator.create.TableFilterOperator;
import com.fr.bi.conf.data.source.operator.create.TableSumByGroupOperator;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/4/8.
 */
public class AnalysisETLOperatorFactory {

    public static List<IETLOperator> createOperatorsByJSON(JSONObject jo, long userId) throws Exception {
        List<IETLOperator> list = new ArrayList<IETLOperator>();
        int etlType = jo.getInt("etlType");
        switch (etlType){
            case Constants.ETL_TYPE.USE_PART_FIELDS :{
                IETLOperator op = new TableFilterOperator();
                op.parseJSON(jo.getJSONObject("operator"));
                list.add(op);
                break;
            }
            case Constants.ETL_TYPE.ADD_COLUMN :{
                JSONObject operators = jo.getJSONObject("operator");
                JSONArray columns = operators.getJSONArray("columns");
                for (int i = 0; i < columns.length(); i ++){
                    list.add(createAddColumnOperator(columns.getJSONObject(i), userId));
                }
                break;
            }
            case Constants.ETL_TYPE.GROUP_SUMMARY :{
                IETLOperator op = new TableSumByGroupOperator();
                op.parseJSON(jo.getJSONObject("operator"));
                list.add(op);
                break;
            }
            case Constants.ETL_TYPE.FILTER :{
                IETLOperator op = new TableColumnFieldsFilterOperator(userId);
                op.parseJSON(jo.getJSONObject("operator"));
                list.add(op);
                break;
            }
            case Constants.ETL_TYPE.MERGE_SHEET :{
                IETLOperator op = new TableColumnFilterOperator();
                op.parseJSON(jo.getJSONObject("operator"));
                list.add(op);
                break;
            }
        }
        return list;
    }

    private static IETLOperator createAddColumnOperator(JSONObject jo, long userId) throws Exception {
        String type = jo.getString("add_column_type");
        IETLOperator op = null;
        if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.FORMULA)){
            op = new ETLFormularOperator(userId);
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.DATE_DIFF)){
            op = new DateDiffOperator(userId);
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.DATE_YEAR)
                || ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.DATE_SEASON)
                || ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.DATE_MONTH)){
            op = new GetValueFromDateOperator(userId);
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.GROUP)){
            op = new ExpressionValueOperator(userId);
        }  else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.SINGLE_VALUE)){
            op = new SingleValueOperator(userId);
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT)){
            op = new ValueConverOperator(userId);
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_ACC)){
            op = new AccumulateRowCalculatorOperator();
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_RANK)){
            op = new RankRowCalculatorOperator();
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_SUM)){
            op = new AllDataRowCalculatorOperator();
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_LP)){
            op = new CorrespondPriodRowCalculatorOperator();
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_LP_PERCENT)){
            op = new CorrespondPriodPercentRowCalculatorOperator();
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_CPP)){
            op = new CorrespondMonthPeriodRowCalculatorOperator();
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_CPP_PERCENT)){
            op = new CorrespondMonthPeriodPercentRowCalculatorOperator();
        }
        op.parseJSON(jo);
        return op;
    }

}
