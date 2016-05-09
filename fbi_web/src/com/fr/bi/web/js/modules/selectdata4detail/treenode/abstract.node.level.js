/**
 * Created by GUY on 2016/4/18.
 * @class BI.AbstractDetailDetailSelectDataNode
 * @extends BI.Widget
 */
BI.AbstractDetailDetailSelectDataNode = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AbstractDetailDetailSelectDataNode.superclass._defaultConfig.apply(this, arguments), {
            id: "",
            wId: "",
            pId: "",
            open: false,
            height: 25
        })
    },

    _init: function () {
        BI.AbstractDetailDetailSelectDataNode.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.node = this._createNode();
        this.node.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this._initBroadcast();
    },

    _createNode: function () {

    },

    _initBroadcast: function () {
        var self = this, o = this.options;
        if (!BI.Utils.isTableUsableByWidgetID(o.value, o.wId)) {
            this.setEnable(false);
        }
        BI.Broadcasts.on(BICst.BROADCAST.DIMENSIONS_PREFIX + o.wId, function (tableId) {
            var enable = BI.Utils.isTableUsableByWidgetID(o.value, o.wId);
            if (enable === false) {
                self.setEnable(false);
                return;
            }
            if (BI.isNotEmptyString(tableId)) {
                var dIds = BI.Utils.getAllDimensionIDs(o.wId);
                var tIds = [];
                BI.each(dIds, function (id, dId) {
                    tIds.push(BI.Utils.getTableIDByDimensionID(dId));
                });
                tIds.push(tableId);
                enable = BI.Utils.isTableInRelativeTables(tIds, tableId);
            }
            self.setEnable(enable);
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
        BI.AbstractDetailDetailSelectDataNode.superclass.setEnable.apply(this, arguments);
        !b && this.node.isOpened() && this.node.triggerCollapse();
        this.node.setEnable(b);
    }
});