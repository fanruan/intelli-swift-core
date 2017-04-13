/**
 * Created by Young's on 2017/4/10.
 */
BI.AnalysisMainPaneModel = BI.inherit(BI.OB, {
    _init: function() {
        BI.AnalysisMainPaneModel.superclass._init.apply(this, arguments);
        this.tableModel = new BI.AnalysisDynamicTabModel(this.options.table);
    },

    get: function(key) {
        return this.options[key];
    },

    getId: function() {
        return this.options.id;
    },

    getSheetLength : function () {
        return this.tableModel.options.items.length;
    },

    getTableDefaultName : function () {
        var id = this.tableModel.get(ETLCst.ITEMS)[0];
        var name = this.options.name || this.tableModel.get(id).get("tableName");
        return BI.Utils.createDistinctName(BI.Utils.getAllETLTableNames(), name);
    },

    resetPoolCurrentUsedTables: function() {
        Pool.current_edit_etl_used = [];
    },

    setSaveInfo: function(name, desc){
        var o = this.options;
        o.id = BI.UUID();
        o.name = name;
        o.describe = desc;
    },

    getValue: function() {
        var o = this.options;
        var value = {
            id : o.id,
            name : o.name,
            describe : o.describe
        };
        value.table = this.tableModel.getValue();
        return value;
    }

});