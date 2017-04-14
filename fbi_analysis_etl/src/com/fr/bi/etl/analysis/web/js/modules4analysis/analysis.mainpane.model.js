/**
 * Created by Young's on 2017/4/10.
 */
BI.AnalysisMainPaneModel = BI.inherit(BI.OB, {
    _init: function () {
        BI.AnalysisMainPaneModel.superclass._init.apply(this, arguments);
    },

    get: function (key) {
        return this.options[key];
    },

    getId: function () {
        return this.options.id;
    },

    getItems: function () {
        var table = this.options.table;
        return BI.isNotNull(table) ? (table.items || []) : [];
    },

    getTableDefaultName: function () {
        var name = this.options.name;
        if (BI.isNull(name)) {
            var initTable = this.options.table;
            var items = initTable.items;
            if (items && items.length > 0) {
                name = items[0].tableName;
            }
        }
        return BI.Utils.createDistinctName(BI.Utils.getAllETLTableNames(), name);
    },

    resetPoolCurrentUsedTables: function () {
        Pool.current_edit_etl_used = [];
    },

    setSaveInfo: function (name, desc) {
        var o = this.options;
        o.id = BI.UUID();
        o.name = name;
        o.describe = desc;
    },

    getValue: function () {
        var o = this.options;
        return {
            id: o.id,
            name: o.name,
            describe: o.describe
        };
    }

});