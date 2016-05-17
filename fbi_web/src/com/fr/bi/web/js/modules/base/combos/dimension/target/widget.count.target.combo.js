/**
 * @class BI.CountTargetCombo
 * @extend BI.Widget
 * 记录数指标下拉
 */
BI.CountTargetCombo = BI.inherit(BI.AbstractDimensionTargetCombo, {

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
                text: BI.i18nText("BI-Style_Setting"),
                value: BICst.TARGET_COMBO.STYLE_SETTING,
                cls: "style-set-h-font"
            }],
            [{
                text: BI.i18nText("BI-Filter_Number_Summary"),
                value: BICst.TARGET_COMBO.FILTER,
                cls: "filter-h-font"
            }],
            [{
                text: BI.i18nText("BI-Copy"),
                value: BICst.TARGET_COMBO.COPY,
                cls: "copy-h-font"
            }],
            [{
                text: BI.i18nText("BI-Delete_Target"),
                value: BICst.TARGET_COMBO.DELETE,
                cls: "delete-h-font"
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
        return BI.extend(BI.CountTargetCombo.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BI.CountTargetCombo.superclass._init.apply(this, arguments);
        this.field_id = BI.Utils.getFieldIDByDimensionID(this.options.dId);
    },

    _rebuildItems: function(){
        var o = this.options;
        var tableId = BI.Utils.getTableIDByDimensionID(o.dId);
        var fieldIds = BI.Utils.getStringFieldIDsOfTableID(tableId).concat(BI.Utils.getNumberFieldIDsOfTableID(tableId));
        var children = [];
        children.push({
            text: BI.i18nText("BI-Total_Row_Count"),
            value: this.field_id
        });
        BI.each(fieldIds, function(idx, fieldId){
            children.push({
                text: BI.Utils.getFieldNameByID(fieldId),
                value: fieldId
            });
        });

        var id = BI.Utils.getFieldIDByDimensionID(o.dId);
        var selectedValue = BI.Utils.getFieldTypeByDimensionID(o.dId) !== BICst.COLUMN.COUNTER ? BI.Utils.getFieldNameByID(id) : BI.i18nText("BI-Total_Row_Count");

        var dependItem = {};

        var items = this.defaultItem();

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

    _createValue: function () {
        var o = this.options;

        var result = {};

        result.group = {
            value: BICst.TARGET_COMBO.DEPEND_TYPE,
            childValue: BI.Utils.getFieldIDByDimensionID(o.dId)
        };
        return [result.group];
    }
});
$.shortcut("bi.count_target_combo", BI.CountTargetCombo);