/**
 * Created by GUY on 2015/9/6.
 * @class BI.DetailSelectDataLevel0Node
 * @extends BI.NodeButton
 */
BI.DetailSelectDataLevel0Node = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectDataLevel0Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-select-data-level0-node",
            id: "",
            pId: "",
            layer: 0,
            open: false,
            height: 25
        })
    },
    _init: function () {
        BI.DetailSelectDataLevel0Node.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.node = BI.createWidget({
            type: "bi.select_data_level_node",
            element: this.element,
            layer: o.layer,
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: o.text,
            value: o.value,
            keyword: o.keyword,
            title: o.title
        });
        this,ids = [];
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

    _createSetValue: function(items){
        var self = this, vals = [];
        BI.each(items, function (i, item) {
            if (BI.isString(item)) {
                vals.push(BI.Utils.getFieldNameByID(item));
                self.ids.push(item);
            } else {
                if (BI.isNotNull(item.field_id)) {
                    var name = BI.Utils.getFieldNameByID(item.field_id);
                    self.ids.push(item.field_id);
                    if (BI.isNotNull(item.group)) {
                        switch (item.group.type) {
                            case BICst.GROUP.Y:
                                name += "(" + BI.i18nText("BI-Year") + ")";
                                break;
                            case BICst.GROUP.M:
                                name += "(" + BI.i18nText("BI-Basic_Month") + ")";
                                break;
                            case BICst.GROUP.YMD:
                                name += "(" + BI.i18nText("BI-Basic_Date") + ")";
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
        return vals;
    },

    setValue: function (items) {
        var self = this;
        this.ids = [];
        BI.each(this.removeBroadcasts, function(idx, broadcast){
            broadcast();
        });
        this.removeBroadcasts = [];
        BI.each(items, function(idx, item){
            var removeFunc;
            if (BI.isString(item)) {
                removeFunc = BI.Broadcasts.on(BICst.BROADCAST.SRC_PREFIX + item, function () {
                    BI.defer(function(){
                        BI.remove(self.ids, item);
                        self.node.setValue(self._createSetValue(self.ids));
                    });
                });
                self.removeBroadcasts.push(removeFunc)
            }else{
                removeFunc = BI.Broadcasts.on(BICst.BROADCAST.SRC_PREFIX + item.field_id, function () {
                    BI.defer(function(){
                        BI.remove(self.ids, item.field_id);
                        self.node.setValue(self._createSetValue(self.ids));
                    });
                });
                self.removeBroadcasts.push(removeFunc)
            }
        });

        this.node.setValue(this._createSetValue(items));
    },

    setEnable: function (b) {
        BI.DetailSelectDataLevel0Node.superclass.setEnable.apply(this, arguments);
        !b && this.node.isOpened() && this.node.triggerCollapse();
    }
});

$.shortcut("bi.detail_select_data_level_node", BI.DetailSelectDataLevel0Node);