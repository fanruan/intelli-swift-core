/**
 * @class BI.PackageTablesListPane
 * @extend BI.Widget
 * 单个业务包界面所有表
 */
BI.PackageTablesListPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.PackageTablesListPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-package-tables-list-pane"
        })
    },

    _init: function () {
        BI.PackageTablesListPane.superclass._init.apply(this, arguments);
        this.tablesPane = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            behaviors: {
                redmark: function () {
                    return true;
                }
            },
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_NONE,
            layouts: [{
                type: "bi.left",
                scrollable: true,
                hgap: 10,
                vgap: 10
            }]
        })
    },

    _getAllDatabaseLinkNames: function(items) {
        var linkNames = [];
        BI.each(items, function (i, item) {
            var connName = item.connName;
            if (BI.isNotNull(connName) && !linkNames.contains(connName) &&
                connName !== BICst.CONNECTION.ETL_CONNECTION &&
                connName !== BICst.CONNECTION.EXCEL_CONNECTION &&
                connName !== BICst.CONNECTION.SQL_CONNECTION &&
                connName !== BICst.CONNECTION.SERVER_CONNECTION) {
                linkNames.push(connName);
            }
        });
        return linkNames;
    },

    populate: function (items) {
        var self = this;
        this.options.items = items;
        var tables = [];
        var linkNames = this._getAllDatabaseLinkNames(items);

        BI.each(items, function (i, item) {
            var dbTable = BI.createWidget({
                type: "bi.database_table",
                forceNotSelected: true,
                text: item.text,
                connName: item.connName,
                linkNames: linkNames,
                needMark: true
            });
            dbTable.on(BI.Controller.EVENT_CHANGE, function () {
                arguments[1] = item.id;
                self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
                self.fireEvent(BI.PackageTablesListPane.EVENT_CLICK_TABLE, item.id);
            });
            tables.push(dbTable);
        });
        arguments[0] = tables;
        this.tablesPane.populate.apply(this.tablesPane, arguments);
    },

    getValue: function () {

    },

    setValue: function () {

    }
});
BI.PackageTablesListPane.EVENT_CLICK_TABLE = "EVENT_CLICK_TABLE";
$.shortcut("bi.package_tables_list_pane", BI.PackageTablesListPane);