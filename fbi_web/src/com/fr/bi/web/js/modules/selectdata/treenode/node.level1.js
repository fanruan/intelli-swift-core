/**
 * Created by GUY on 2015/9/6.
 * @class BI.DetailSelectDataLevel1Node
 * @extends BI.Widget
 */
BI.DetailSelectDataLevel1Node = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectDataLevel1Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-select-data-level1-node",
            id: "",
            pId: "",
            open: false,
            height: 25
        })
    },
    _init: function () {
        BI.DetailSelectDataLevel1Node.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.node = BI.createWidget({
            type: "bi.select_data_level1_node",
            element: this.element,
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: o.text,
            value: o.value
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
        var vals = [];
        BI.each(items, function (i, item) {
            if (BI.isString(item)) {
                vals.push(BI.Utils.getFieldNameByID(item));
            } else {
                if (BI.isNotNull(item.field_id)) {
                    var name = BI.Utils.getFieldNameByID(item.field_id);

                    if (BI.isNotNull(item.group)) {
                        switch (item.group.type) {
                            case BICst.GROUP.Y:
                                name += "(" + BI.i18nText("BI-Year") + ")";
                                break;
                            case BICst.GROUP.M:
                                name += "(" + BI.i18nText("BI-Month") + ")";
                                break;
                            case BICst.GROUP.YMD:
                                name += "(" + BI.i18nText("BI-Date") + ")";
                                break;
                            case BICst.GROUP.S:
                                name += "(" + BI.i18nText("BI-Quarter") + ")";
                                break;
                        }
                    }
                    vals.push(name);
                }
            }
        });
        this.node.setValue(vals);
    },

    setEnable: function (b) {
        BI.DetailSelectDataLevel1Node.superclass.setEnable.apply(this, arguments);
        !b && this.node.isOpened() && this.node.triggerCollapse();
    }
});

$.shortcut("bi.detail_select_data_level1_node", BI.DetailSelectDataLevel1Node);