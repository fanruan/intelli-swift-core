/**
 * @class BI.ETLFlowChartButton
 * @extend BI.Widget
 * 选择表界面的流程图按钮
 */
BI.ETLFlowChartButton = BI.inherit(BI.Widget, {

    constants: {
        WRAPPER_WIDTH: 180,
        WRAPPER_HEIGHT: 100,
        BUTTON_WIDTH: 170,
        BUTTON_HEIGHT: 30,
        WRAPPER_GAP: 5,
        BUTTON_BOTTOM_GAP: 10,
        OPERATOR_TOP_GAP: 20,
        OPERATOR_LEFT_GAP: 20
    },

    _defaultConfig: function(){
        return BI.extend(BI.ETLFlowChartButton.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-table-etl-flow-chart",
            height: this.constants.WRAPPER_HEIGHT,
            width: this.constants.WRAPPER_WIDTH
        })
    },

    _init: function(){
        BI.ETLFlowChartButton.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tableInfo = o.tableInfo;
        var operator = BI.createWidget();
        if(BI.isNotNull(tableInfo.etl_type)){
            operator =  BI.createWidget({
                type: "bi.left",
                items: [{
                    type: "bi.default",
                    cls: "operator-left-triangle",
                    width: 0,
                    height: 0
                }, {
                    type: "bi.label",
                    text: this._getETLTypeName(tableInfo.etl_type),
                    cls: "operator-center",
                    width: 110,
                    height: 25
                }, {
                    type: "bi.default",
                    cls: "operator-right-triangle",
                    width: 0,
                    height: 0
                }]
            });
        }
        this.table = BI.createWidget({
            type: "bi.text_button",
            cls: "etl-table-button",
            width: this.constants.BUTTON_WIDTH,
            height: self.constants.BUTTON_HEIGHT,
            text: tableInfo.table_name,
            title: tableInfo.table_name
        });
        this.table.on(BI.TextButton.EVENT_CHANGE, function(){
            self.fireEvent(BI.ETLFlowChartButton.EVENT_CHANGE, self.table.isSelected());
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: operator,
                top: this.constants.OPERATOR_TOP_GAP,
                left: this.constants.OPERATOR_LEFT_GAP
            }, {
                el: self.table,
                bottom: self.constants.BUTTON_BOTTOM_GAP,
                left: self.constants.WRAPPER_GAP
            }]
        });
    },

    _getETLTypeName: function(etlType) {
        switch (etlType) {
            case BICst.ETL_OPERATOR.JOIN:
                return "JOIN";
            case BICst.ETL_OPERATOR.UNION:
                return "UNION";
            case BICst.ETL_OPERATOR.CIRCLE:
                return BI.i18nText("BI-Create_Self_cycle_Column");
            case BICst.ETL_OPERATOR.CONVERT:
                return BI.i18nText("BI-Row_Column_Transformation");
            case BICst.ETL_OPERATOR.FILTER:
                return BI.i18nText("BI-Filter");
            case BICst.ETL_OPERATOR.FORMULA:
                return BI.i18nText("BI-Add_Formula_Column");
            case BICst.ETL_OPERATOR.GROUP:
                return BI.i18nText("BI-Grouping_Count");
            case BICst.ETL_OPERATOR.PARTIAL:
                return BI.i18nText("BI-Use_Part_Of_Fields");
            case BICst.ETL_OPERATOR.NEW_GROUP:
                return BI.i18nText("BI-Add_Grouping_Column");
        }
    },

    setEnable: function(v){
        this.table.setEnable(v);
    },

    getValue: function(){
        return this.options.tableInfo;
    }
});
BI.ETLFlowChartButton.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_flow_chart_button", BI.ETLFlowChartButton);