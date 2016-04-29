/**
 * @class BI.ETLTableOperator
 * @extend BI.Widget
 * etl 操作
 */
BI.ETLTableOperator = BI.inherit(BI.BasicButton, {
    _defaultConfig: function(){
        var conf = BI.ETLTableOperator.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "")+ " bi-join-type-button",
            height: 25,
            width: 150
        })
    },

    _init: function(){
        BI.ETLTableOperator.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tableInfo = o.tableInfo;
        BI.createWidget({
            type: "bi.left",
            element: this.element,
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
        })
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

    doClick: function(){
        BI.ETLTableOperator.superclass.doClick.apply(this, arguments);
        var tableInfo = this.options.tableInfo;
        if(this.isValid()) {
            this.fireEvent(BI.ETLTableOperator.EVENT_CHANGE, tableInfo);
        }
    }
});
BI.ETLTableOperator.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl-table-operator", BI.ETLTableOperator);