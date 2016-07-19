/**
 * Created by GUY on 2015/9/15.
 * @class BI.DetailSelectDataLevel1DateNode
 * @extends BI.Widget
 */
BI.DetailSelectDataLevel1DateNode = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectDataLevel1DateNode.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-select-data-level1-date-node",
            id: "",
            pId: "",
            layer: 1,
            open: false,
            height: 25
        })
    },
    _init: function () {
        BI.DetailSelectDataLevel0Node.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.node = BI.createWidget({
            type: "bi.select_data_level1_date_node",
            element: this.element,
            layer: o.layer,
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: o.text,
            value: o.value,
            title: o.title
        });
        this.node.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
    },

    doRedMark: function () {
        this.node.doRedMark.apply(this.node, arguments);
    },

    unRedMark: function () {
        this.node.unRedMark.apply(this.node, arguments);
    },

    setSelected: function (b) {
        return this.node.setSelected(b);
    },

    isSelected: function () {
        return this.node.isSelected();
    },

    isOpened: function () {
        return this.node.isOpened();
    },

    setOpened: function (v) {
        this.node.setOpened(v);
    },

    setValue: function (items) {
        this.node.setValue(items);
    },

    setEnable: function (b) {
        BI.DetailSelectDataLevel1DateNode.superclass.setEnable.apply(this, arguments);
        !b && this.node.isOpened() && this.node.triggerCollapse();
    }
});

$.shortcut("bi.detail_select_data_level1_date_node", BI.DetailSelectDataLevel1DateNode);