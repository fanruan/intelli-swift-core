package com.fr.bi.conf.data.source.operator.add.express;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.AbstractAddColumnOperator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/3/5.
 */
public class ExpressionValueOperator extends AbstractAddColumnOperator {

    public static final String XML_TAG = "FieldFormulaOperator";
    private static final long serialVersionUID = -1675716963282566541L;
    @BICoreField
    private Expression expression;

    public ExpressionValueOperator(long userId) {
        super(userId);
    }

    public ExpressionValueOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        if(expression != null){
        	jo.put("item", expression.createJSON());
        }
        jo.put("add_column_type", BIJSONConstant.ETL_ADD_COLUMN_TYPE.GROUP);
        return jo;
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jsonObject json对象
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        super.parseJSON(jsonObject);
        if (jsonObject.has("item")){
            JSONObject item = jsonObject.getJSONObject("item");
            expression = new GeneralExpression();
            expression.parseJSON(item);
        }
    }



    @Override
    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
        int rowCount = ti.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            try {
            	Object value = expression == null ? null : expression.get(ti, row, columnType);
                travel.actionPerformed(new BIDataValue(row, startCol, value));
            } catch (Exception e) {
                BILogger.getLogger().error("incorrect formular");
                travel.actionPerformed(new BIDataValue(row, startCol, null));
            }
        }
        return rowCount;
    }

}