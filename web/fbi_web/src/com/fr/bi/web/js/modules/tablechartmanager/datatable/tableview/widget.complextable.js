/**
 * 复杂表
 * Created by Young's on 2017/1/20.
 */
BI.ComplexTable = BI.inherit(BI.CrossTable, {
    _defaultConfig: function() {
        return BI.extend(BI.ComplexTable.superclass._defaultConfig.apply(this, arguments, {
            baseCls: "bi-complex-table"
        }));
    },

    _init: function() {
        BI.ComplexTable.superclass._init.apply(this, arguments);
    },

    _initModel: function() {
        var o = this.options;
        this.model = new BI.ComplexTableModel({
            wId: o.wId,
            status: o.status
        });
    }
});
BI.ComplexTable.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.complex_table", BI.ComplexTable);
