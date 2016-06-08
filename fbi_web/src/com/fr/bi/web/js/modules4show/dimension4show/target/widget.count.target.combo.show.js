/**
 * @class BI.CountTargetCombo
 * @extend BI.Widget
 * 记录数指标下拉
 */
BI.CountTargetComboShow = BI.inherit(BI.AbstractDimensionTargetComboShow, {

    constants: {
        CHART_TYPE_POSITION: 1
    },

    defaultItem: function(){
        var o = this.options;
        var fieldName = BI.Utils.getFieldNameByID(this.field_id);
        var tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(this.field_id));
        var fromText = BI.i18nText("BI-This_Target_From") + ": " + tableName + "."  + fieldName;
        return [
            [{
                el: {
                    text: BI.i18nText("BI-Count_Depend"),
                    value: BICst.TARGET_COMBO.DEPEND_TYPE,
                    iconCls1: ""
                },
                children: []
            }],
            [{
                text: fromText,
                title: fromText,
                tipType: "warning",
                value: BICst.TARGET_COMBO.INFO,
                cls: "dimension-from-font",
                disabled: true
            }]
        ]
    },

    _defaultConfig: function(){
        return BI.extend(BI.CountTargetComboShow.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BI.CountTargetComboShow.superclass._init.apply(this, arguments);
        this.field_id = BI.Utils.getFieldIDByDimensionID(this.options.dId);
    },

    _rebuildItems: function(){
        var o = this.options;
        var tableId = BI.Utils.getTableIDByDimensionID(o.dId);
        var fieldIds = BI.Utils.getStringFieldIDsOfTableID(tableId).concat(BI.Utils.getNumberFieldIDsOfTableID(tableId));
        var children = [];
        children.push({
            text: BI.i18nText("BI-Total_Row_Count"),
            value: this.field_id,
            cls: "dot-e-font"
        });
        BI.each(fieldIds, function(idx, fieldId){
            children.push({
                text: BI.Utils.getFieldNameByID(fieldId),
                value: fieldId,
                cls: "dot-e-font"
            });
        });

        var id = BI.Utils.getFieldIDByDimensionID(o.dId);
        var selectedValue = BI.Utils.getFieldTypeByDimensionID(o.dId) !== BICst.COLUMN.COUNTER ? BI.Utils.getFieldNameByID(id) : BI.i18nText("BI-Total_Row_Count");

        var dependItem = {};

        var items = this.defaultItem();
        var wType = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(this.options.dId));

        switch (wType) {
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                items[this.constants.CHART_TYPE_POSITION][0].disabled = false;
                break;
            default:
                items[this.constants.CHART_TYPE_POSITION][0].disabled = true;
                break;
        }

        BI.find(items, function(idx, item){
            dependItem = BI.find(item, function(id, it){
                var itE = BI.stripEL(it);
                return itE.value === BICst.TARGET_COMBO.DEPEND_TYPE;
            });
            return BI.isNotNull(dependItem);
        });

        dependItem.el.text = BI.i18nText("BI-Count_Depend") + "(" + selectedValue +")";
        dependItem.children = children;

        return items;
    },

    _assertChartType: function (val) {
        val || (val = {});
        if(BI.isNull(val.type)){
            val.type = BICst.WIDGET.AXIS;
        }
        return val;
    },

    _createValue: function () {
        var o = this.options;
        var chartType = BI.Utils.getDimensionStyleOfChartByID(o.dId);
        chartType = this._assertChartType(chartType);

        var result = {};

        result.chartType = {
            value: BICst.TARGET_COMBO.CHART_TYPE,
            childValue: chartType.type
        };
        result.group = {
            value: BICst.TARGET_COMBO.DEPEND_TYPE,
            childValue: BI.Utils.getFieldIDByDimensionID(o.dId)
        };
        return [result.chartType, result.group];
    }
});
$.shortcut("bi.count_target_combo_show", BI.CountTargetComboShow);