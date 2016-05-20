/**
 * @class BI.ChartType
 * @extend BI.Widget
 * 选择图表类型组
 */
BI.ChartType = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ChartType.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-type"
        })
    },

    _init: function () {
        BI.ChartType.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.buttonTree = BI.createWidget({
            type: "bi.button_tree",
            element: this.element,
            items: this._formatItems(BI.deepClone(BICst.DASHBOARD_WIDGETS[0])),
            layouts: [{
                type: "bi.horizontal",
                scrollx: false,
                scrollable: false,
                vgap: 3,
                hgap: 3
            }]
        });
        this.buttonTree.on(BI.ButtonTree.EVENT_CHANGE, function () {
            self.fireEvent(BI.ChartType.EVENT_CHANGE, arguments);
        })
    },

    _formatItems: function (items) {
        var self = this;
        var result = [];
        BI.each(items, function (i, item) {
            if (BI.isNotEmptyArray(item.children)) {
                BI.each(item.children, function (i, child) {
                    child.iconClass = child.cls;
                });
                result.push(BI.extend({
                    type: "bi.icon_combo",
                    width: 40,
                    iconClass: item.cls,
                    items: item.children
                }, item, {
                    cls: "chart-type-combo"
                }));
            } else {
                result.push(BI.extend({
                    type: "bi.icon_button",
                    width: 40
                }, item, {
                    cls: item.cls + " chart-type-icon"
                }));
            }
        });
        return result;
    },

    getValue: function () {
        return this.buttonTree.getValue()[0];
    },

    setValue: function (v) {
        this.buttonTree.setValue(v);
    }
});
BI.ChartType.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_type", BI.ChartType);
