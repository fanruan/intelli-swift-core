/**
 * @class BI.PackageTablePane
 * @extend BI.Widget
 * 业务包选择表
 */
BI.PackageTablePane = BI.inherit(BI.Widget, {

    _constant: {
        TABLE_GAP: 10
    },

    _defaultConfig: function () {
        return BI.extend(BI.PackageTablePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-package-table-pane"
        })
    },

    _init: function () {
        BI.PackageTablePane.superclass._init.apply(this, arguments);
        var self = this;
        this.packageTables = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            cls: "package-tables",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_MULTI,
            layouts: [{
                type: "bi.left",
                scrollable: true,
                hgap: this._constant.TABLE_GAP,
                vgap: this._constant.TABLE_GAP
            }]
        });
        this.packageTables.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.PackageTablePane.EVENT_CHANGE);
        });
    },
    
    populate: function(tables) {
        this.packageTables.populate(tables);
    },

    setValue: function(v) {
        this.packageTables.setValue(v);
    },

    getValue: function () {
        return this.packageTables.getValue();
    }
});
BI.PackageTablePane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.package_table_pane", BI.PackageTablePane);