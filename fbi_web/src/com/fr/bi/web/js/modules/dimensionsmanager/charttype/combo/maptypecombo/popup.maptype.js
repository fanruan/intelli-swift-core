/**
 * @class BI.MapTypePopup
 * @extend BI.Pane
 */
BI.MapTypePopup = BI.inherit(BI.Pane, {

    constants: {
        SVG_MAP: 1,
        BIT_MAP: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.MapTypePopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-map-type-popup"
        });
    },

    _init: function () {
        BI.MapTypePopup.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.popup = BI.createWidget({
            type: "bi.custom_tree",
            expander: {
                isDefaultInit: true
            },
            items: self._createItems(),
            el: {
                type: "bi.button_tree",
                layouts: [{
                    type: "bi.vertical"
                }]
            }
        });

        this.popup.on(BI.Controller.EVENT_CHANGE, function (type, val, obj) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            if (type === BI.Events.CLICK) {
                self.fireEvent(BI.MapTypePopup.EVENT_CHANGE, val, obj);
            }
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.popup]
        });
    },

    _createItems: function () {
        var items = [{
            id: 1,
            type: "bi.triangle_group_node",
            text: BI.i18nText("BI-SVG_Map"),
            value: 1,
            isParent: true,
            open: true
        }, {
            id: 2,
            type: "bi.triangle_group_node",
            text: BI.i18nText("BI-BIT_Map"),
            value: 2,
            isParent: true,
            open: false
        }];
        BI.each(BICst.SVG_MAP_TYPE, function(idx, it){
            items.push(BI.extend({
                type: "bi.multilayer_icon_tree_leaf_item",
                height: 30,
                iconHeight: 24,
                iconWidth: 24,
                layer: 1,
                id: BI.UUID(),
                pId: 1,
                iconCls: it.cls
            }, it));
        });
        return items;
    },

    getValue: function () {
        return this.popup.getValue();
    },

    setValue: function (v) {
        this.popup.setValue(v);
    }

});
BI.MapTypePopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.map_type_popup", BI.MapTypePopup);