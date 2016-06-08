/**
 * @class BI.MapTypePopup
 * @extend BI.Pane
 */
BI.MapTypePopupShow = BI.inherit(BI.Pane, {

    constants: {
        SVG_MAP: 1,
        BIT_MAP: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.MapTypePopupShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-map-type-popup"
        });
    },

    _init: function () {
        BI.MapTypePopupShow.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.popup = BI.createWidget({
            type: "bi.select_data_tree",
            el: {
                el: {
                    chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE
                }
            },
            itemsCreator: function(op, populate){
                if (!op.node) {
                    populate([
                        {
                            id: 1,
                            type: "bi.triangle_group_node",
                            text: BI.i18nText("BI-SVG_Map"),
                            value: 1,
                            isParent: true,
                            open: false
                        }, {
                            id: 2,
                            type: "bi.triangle_group_node",
                            text: BI.i18nText("BI-BIT_Map"),
                            value: 2,
                            isParent: true,
                            open: false
                        }
                    ]);
                    return;
                }
                if (BI.isNotNull(op.node.isParent)) {
                    populate(self._createItemsByParentNodeId(op.node.id));
                }
            }
        });

        this.popup.on(BI.Controller.EVENT_CHANGE, function (type, val, obj) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            if (type === BI.Events.CLICK) {
                self.fireEvent(BI.MapTypePopupShow.EVENT_CHANGE, val, obj);
            }
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.popup]
        });

        this.popup.populate();
    },

    _createItemsByParentNodeId: function(pId){
        var items = pId === this.constants.SVG_MAP ? BICst.SVG_MAP_TYPE : [];
        return BI.map(items, function(idx, item){
            return BI.extend({
                type: "bi.multilayer_icon_tree_leaf_item",
                height: 30,
                iconHeight: 24,
                iconWidth: 24,
                layer: 1,
                id: BI.UUID(),
                pId: pId,
                iconCls: item.cls
            }, item);
        });
    },

    getValue: function () {
        return this.popup.getValue();
    },

    setValue: function (v) {
        this.popup.setValue(v);
    }

});
BI.MapTypePopupShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.map_type_popup_show", BI.MapTypePopupShow);