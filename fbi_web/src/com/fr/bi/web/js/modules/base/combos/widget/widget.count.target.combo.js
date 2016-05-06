/**
 * @class BI.CountTargetCombo
 * @extend BI.Widget
 * 记录数指标下拉
 */
BI.CountTargetCombo = BI.inherit(BI.Widget, {

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
                el: {
                    text: BI.i18nText("BI-Chart_Type"),
                    value: BICst.TARGET_COMBO.CHART_TYPE,
                    iconCls1: "",
                    disabled: true
                },
                children: [{
                    text: BI.i18nText("BI-Column_Chart"),
                    value: BICst.CHART_VIEW_STYLE_BAR
                }, {
                    text: BI.i18nText("BI-Stacked_Chart"),
                    value: BICst.CHART_VIEW_STYLE_ACCUMULATED_BAR
                }, {
                    text: BI.i18nText("BI-Line_Chart"),
                    value: BICst.CHART_VIEW_STYLE_LINE
                }, {
                    text: BI.i18nText("BI-Area_Chart"),
                    value: BICst.CHART_VIEW_STYLE_SQUARE
                }]
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
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font"
        });

        this.field_id = BI.Utils.getFieldIDByDimensionID(o.dId);

        this.combo.on(BI.DownListCombo.EVENT_SON_VALUE_CHANGE,function(v, father){
            self.fireEvent(BI.CountTargetCombo.EVENT_CHANGE, father, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.CountTargetCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW,function(){
            this.populate(self._rebuildItems());
            var selectedValue = BI.Utils.getFieldIDByDimensionID(o.dId);
            this.setValue([{
                value: BICst.TARGET_COMBO.DEPEND_TYPE,
                childValue: selectedValue
            }, self._createValueByChartType()]);
        });
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

    //Todo
    _createValueByChartType: function(){
        return {
            value: "",
            childValue: ""
        };
    },

    getValue: function(){
        return this.combo.getValue();
    }
});
BI.CountTargetCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.count_target_combo", BI.CountTargetCombo);