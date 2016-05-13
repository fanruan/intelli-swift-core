/**
 * @class BI.DragIconGroup
 * @extend BI.Widget
 * dashboard左侧拖拽
 */
BI.DragIconGroup = BI.inherit(BI.Widget, {
    _const: {
        valueMore: -100,
        valueReuse: -1,
        iconWidth: 36,
        iconHeight: 30,
        showMoreCount: 14
    },

    _defaultConfig: function () {
        return BI.extend(BI.DragIconGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-drag-icon-group",
            drag: BI.emptyFn
        })
    },

    _formatConfig: function (config) {
        var self = this, c = this._const;
        var result = [];
        BI.each(config, function (i, cfg) {
            if (BI.isArray(cfg)) {
                result.push(self._formatConfig(cfg));
                return;
            }
            if (i >= c.showMoreCount) {
                return;
            }
            cfg.cls = (cfg.cls || "") + " widget-generator";
            if (i > c.showMoreCount - 1) {
                var items = [];
                for (var k = c.showMoreCount; k < config.length; k++) {
                    items.push(config[k]);
                }
                cfg = {
                    text: BI.i18nText("BI-More"),
                    invalid: true,
                    value: c.valueMore,
                    cls: cfg.cls + " chart-more-font",
                    children: items
                }
            }
            result.push(cfg);
            if (BI.isArray(cfg.children)) {
                self._formatConfig(cfg.children);
                return;
            }
        })
        return result;
    },

    _createOneGroupIcons: function (items) {
        var self = this, o = this.options, c = this._const;
        var icons = [];
        BI.each(items, function (i, item) {
            if (BI.isNull(item.children)) {
                if (item.value !== c.valueReuse) {
                    var dragIcon = BI.createWidget({
                        type: "bi.drag_icon_button",
                        height: c.iconHeight,
                        width: c.iconWidth,
                        drag: o.drag,
                        stop: o.stop,
                        helper: o.helper
                    }, item);
                } else {
                    var dragIcon = BI.createWidget({
                        type: "bi.combo",
                        direction: "top,right",
                        adjustXOffset: -1,
                        adjustYOffset: -10,
                        isNeedAdjustWidth: false,
                        isNeedAdjustHeight: false,
                        height: c.iconHeight,
                        width: c.iconWidth,
                        el: BI.extend({
                            type: "bi.icon_button"
                        }, item),
                        popup: {
                            stopPropagation: false,
                            lgap: 5,
                            width: 210,
                            height: 587,
                            logic: {
                                dynamic: false
                            },
                            el: {
                                type: "bi.reuse_pane",
                                drag: function(info, position){
                                    dragIcon.hideView();
                                    o.drag.apply(self, [info, position]);
                                },
                                stop: function(info, position){
                                    dragIcon.showView();
                                    o.stop.apply(self, [info, position]);
                                },
                                helper: o.helper
                            }
                        }
                    });
                }
                icons.push(dragIcon);
                return;
            }

            var childIcons = BI.createWidget({
                type: "bi.button_group",
                items: BI.createItems(item.children, {
                    type: "bi.drag_icon_button",
                    height: 30,
                    width: 36,
                    drag: o.drag,
                    stop: o.stop,
                    helper: o.helper
                }),
                layouts: [{
                    type: "bi.vertical"
                }, {
                    type: "bi.center_adapt",
                    height: 40
                }]
            });

            var el = BI.extend(item, {
                type: "bi.icon_button",
                height: 30,
                width: 36
            });
            if (item.value !== c.valueMore) {
                el.type = "bi.drag_icon_button";
                el.drag = o.drag;
                el.stop = o.stop;
                el.helper = o.helper;
            }
            var dragCombo = BI.createWidget({
                type: "bi.combo",
                direction: "right",
                trigger: "hover",
                adjustLength: -1,
                height: 30,
                width: 36,
                el: el,
                popup: {
                    el: childIcons,
                    minWidth: 50
                }
            });
            icons.push(dragCombo);
        });
        return BI.createWidget({
            type: "bi.button_group",
            items: icons,
            layouts: [{
                type: "bi.left"
            }, {
                type: "bi.center_adapt",
                height: 40,
                width: 40
            }]
        })
    },

    _init: function () {
        BI.DragIconGroup.superclass._init.apply(this, arguments);
        var self = this, o = this.options, c = this._const, icons = [];

        this.dragIcons = [];

        var conf = this._formatConfig(BI.deepClone(BICst.DASHBOARD_WIDGET_ICON));

        var gps = [];
        BI.each(conf, function (i, items) {
            if (i > 0) {
                gps.push(BI.createWidget({
                    type: "bi.center_adapt",
                    height: 12,
                    items: [{
                        type: "bi.label",
                        height: 1,
                        width: 60,
                        cls: "widget-generator-gap"
                    }]
                }));
            }
            gps.push(self._createOneGroupIcons(items));
        });

        BI.createWidget({
            type: "bi.horizontal_auto",
            scrollable: false,
            scrolly: true,
            element: this.element,
            items: [{
                type: "bi.vertical",
                width: 80,
                items: gps
            }]
        });
    }
});
$.shortcut("bi.drag_icon_group", BI.DragIconGroup);