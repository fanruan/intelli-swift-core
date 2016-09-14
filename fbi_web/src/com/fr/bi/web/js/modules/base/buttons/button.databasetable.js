/**
 * @class BI.DatabaseTable
 * @extend BI.Widget
 * 表（数据库表、etl表、excel）
 */
BI.DatabaseTable = BI.inherit(BI.BasicButton, {

    constants: {
        ICON_WIDTH: 20,
        ICON_HEIGHT: 16,
        BUTTON_HEIGHT: 30
    },

    _defaultConfig: function(){
        var conf = BI.DatabaseTable.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-database-table",
            connName: ""
        })
    },

    _init: function(){
        BI.DatabaseTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.iconButton = BI.createWidget({
            type: "bi.icon_button",
            cls: this._getIconCls(o.connName) + " table-font",
            height: this.constants.ICON_HEIGHT,
            width: this.constants.ICON_WIDTH
        });
        this.tableNameText = BI.createWidget({
            type: "bi.text_button",
            text: o.text,
            title: o.text,
            value: o.value
        });
        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [this.iconButton, this.tableNameText],
            height: this.constants.BUTTON_HEIGHT,
            hgap: 5
        });
    },

    _getIconCls: function(connction){
        switch (connction){
            case BICst.CONNECTION.ETL_CONNECTION:
                return "etl-table-font";
            case BICst.TABLE_TYPE_SQL:
                return "sql-table-font";
            case BICst.TABLE_TYPE_EXCEL:
                return "excel-table-font";
            default :
                return "data-source-table-font";
        }
    },

    isSelected: function () {
        return this.options.selected;
    },

    setSelected: function(){
        BI.DatabaseTable.superclass.setSelected.apply(this, arguments);
        if (this.isSelected()) {
            this.iconButton.setSelected(true);
        } else {
            this.iconButton.setSelected(false);
        }
    },

    doRedMark: function(keyword){
        var o = this.options;
        this.tableNameText.element.__textKeywordMarked__(o.text || o.value, keyword, o.py);
    }
});
$.shortcut("bi.database_table", BI.DatabaseTable);