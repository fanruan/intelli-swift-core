/**
 * @class BI.PackageTableRelationsPane
 * @extend BI.Widget
 * 单个业务包界面所有表关联
 */
BI.PackageTableRelationsPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.PackageTableRelationsPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-package-table-relations-pane"
        })
    },

    _init: function () {
        BI.PackageTableRelationsPane.superclass._init.apply(this, arguments);
        this.model = new BI.PackageTableRelationsPaneModel({});
        var self = this;
        this.relationView = BI.createWidget({
            type: "bi.relation_view"
        });
        this.relationView.on(BI.RelationView.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.PackageTableRelationsPane.EVENT_CLICK_TABLE, v);
        });
        this.relationView.on(BI.RelationView.EVENT_PREVIEW, function (tableId, show) {
            var relationTables = self.model.getRelationTablesByTableId(tableId);
            relationTables.push(tableId);
            this.previewRelationTables(relationTables, show);
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.relationView]
        });
    },

    refresh: function (items) {
        this.relationView.populate(items);
    },

    _populate: function () {
        var self = this;
        var handler = function () {
            self.fireEvent(BI.PackageTableRelationsPane.EVENT_CLICK_TABLE, self.options.value);
        };
        this.model.createItems(handler);
        this.relationView.populate(this.model.getCacheItems());
    },

    populate: function (items) {
        var self = this;
        this.model.initData(function () {
            self._populate();
        });
        this.model.populate(items);
    },

    getCacheItems: function () {
        return BI.deepClone(this.model.getCacheItems());
    },

    getValue: function () {

    },

    setValue: function () {

    }
});
BI.PackageTableRelationsPane.EVENT_CLICK_TABLE = "EVENT_CLICK_TABLE";
$.shortcut("bi.package_table_relations_pane", BI.PackageTableRelationsPane);