/**
 * @class BI.DatabaseTable
 * @extend BI.Widget
 * 表（数据库表、etl表、excel）
 */
BI.DatabaseTable = BI.inherit(BI.BasicButton, {

    constants: {
        ICON_WIDTH: 16,
        ICON_HEIGHT: 16,
        BUTTON_HEIGHT: 30
    },

    _defaultConfig: function () {
        var conf = BI.DatabaseTable.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-database-table",
            connName: "",
            linkNames: [],     //所有连接名称
            needMark: false    //是否需要标记
        })
    },

    _init: function () {
        BI.DatabaseTable.superclass._init.apply(this, arguments);
        var o = this.options;
        var linkNames = o.linkNames,
            connName = o.connName,
            needMark = o.needMark;
        var linkIndex = linkNames.indexOf(connName);
        this.iconButton = BI.createWidget({
            type: "bi.icon_button",
            cls: this._getIconCls(o.connName) + " table-font",
            iconHeight: this.constants.ICON_HEIGHT,
            iconWidth: this.constants.ICON_WIDTH
        });
        this.tableNameText = BI.createWidget({
            type: "bi.text_button",
            text: o.text,
            title: o.text,
            value: o.value
        });
        var items = [this.iconButton, this.tableNameText];
        var hgap = 5, rgap = 0;
        if (BI.isNotNull(linkIndex > -1) && needMark === true) {
            items.splice(0, 0, {
                type: "bi.default",
                cls: "table-conn-label" + (linkIndex % 10 + 1),
                width: 6,
                height: 30
            });
            hgap = 0;
            rgap = 5;
        }
        BI.createWidget({
            type: "bi.vertical_adapt",
            element: this.element,
            cls: "table-selected" + (linkIndex % 10 + 1),
            items: items,
            height: this.constants.BUTTON_HEIGHT,
            rgap: rgap,
            hgap: hgap
        });
    },

    _getIconCls: function (connction) {
        switch (connction) {
            case BICst.CONNECTION.ETL_CONNECTION:
                return "etl-table-font";
            case BICst.CONNECTION.SQL_CONNECTION:
                return "sql-table-font";
            case BICst.CONNECTION.EXCEL_CONNECTION:
                return "excel-table-font";
            default :
                return "data-source-table-font";
        }
    },

    isSelected: function () {
        return this.options.selected;
    },

    setSelected: function () {
        BI.DatabaseTable.superclass.setSelected.apply(this, arguments);
        if (this.isSelected()) {
            this.iconButton.setSelected(true);
        } else {
            this.iconButton.setSelected(false);
        }
    },

    doRedMark: function (keyword) {
        var o = this.options;
        this.tableNameText.element.__textKeywordMarked__(o.text || o.value, keyword, o.py);
    }
});
$.shortcut("bi.database_table", BI.DatabaseTable);