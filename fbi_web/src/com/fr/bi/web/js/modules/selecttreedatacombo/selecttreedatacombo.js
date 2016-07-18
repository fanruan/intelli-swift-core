/**
 * 树控件
 *
 * Created by GUY on 2016/6/24.
 * @class BI.SelectTreeDataCombo
 * @extend BI.Widget
 */
BI.SelectTreeDataCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectTreeDataCombo.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        })
    },

    _init: function () {
        BI.SelectTreeDataCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.multi_tree_combo",
            element: this.element,
            itemsCreator: function (op, callback) {
                var data = BI.extend({
                    floors: BI.size(BI.Utils.getAllDimensionIDs(o.wId))
                }, op);
                BI.Utils.getWidgetDataByID(o.wId, function (jsonData) {
                    var hasNext = !!jsonData.hasNext, nodes = jsonData.items || [];
                    //if (op.times === 1) {
                    //    if (nodes.length === 0) {
                    //        jsonData.items = [{
                    //            value: BI.i18nText("BI-(Empty)"),
                    //            text: BI.i18nText("BI-(Empty)"),
                    //            id: BI.UUID(),
                    //            nocheck: true
                    //        }]
                    //    }
                    //}
                    callback(jsonData);
                }, {tree_options: data})
            }
        });
        this.combo.on(BI.MultiTreeCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.SelectTreeDataCombo.EVENT_CONFIRM, arguments);
        });
    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    },

    populate: function () {
        this.combo.populate.apply(this.combo, arguments);
    }
});
BI.SelectTreeDataCombo.EVENT_CONFIRM = "SelectTreeDataCombo.EVENT_CONFIRM";
$.shortcut("bi.select_tree_data_combo", BI.SelectTreeDataCombo);