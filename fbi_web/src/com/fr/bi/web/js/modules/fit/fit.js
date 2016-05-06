/**
 * 自适应布局
 *
 * Created by GUY on 2016/3/8.
 * @class BI.Fit
 * @extends BI.Widget
 */
BI.Fit = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.Fit.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-fit",
            widgetCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.Fit.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.arrangement = BI.createWidget({
            type: "bi.adaptive_arrangement",
            layoutType: o.layoutType,
            cls: "fit-dashboard",
            items: o.items
        });
        this.arrangement.on(BI.AdaptiveArrangement.EVENT_RESIZE, function () {
            self.fireEvent(BI.Fit.EVENT_CHANGE, arguments);
        });

        this.store = {};

        var nav = this._createNav();

        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                "west": {el: nav, width: 90, right: 1},
                "center": {el: this.arrangement}
            }
        });

        this.layoutCombo = BI.createWidget({
            type: "bi.text_value_combo",
            items: BICst.DASHBOARD_LAYOUT_ARRAY,
            cls: "layout-combo"
        });
        this.layoutCombo.setValue(o.layoutType);
        this.layoutCombo.on(BI.TextValueCombo.EVENT_CHANGE, function (v) {
            self.arrangement.setLayoutType(v);
            self.fireEvent(BI.Fit.EVENT_CHANGE);
        });

        return BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.right_vertical_adapt",
                    height: 30,
                    items: [this.layoutCombo],
                    hgap: 10
                },
                right: 0,
                top: -30
            }]
        });
    },

    _createItem: function (id, info) {
        var self = this, o = this.options;
        id || (id = BI.UUID());
        if (BI.isNotNull(this.store[id])) {
            var widget = this.store[id];
        } else {
            var widget = this.store[id] = BI.createWidget({
                type: "bi.fit_widget",
                widgetCreator: function () {
                    return o.widgetCreator(id, info);
                },
                id: id
            });
            widget.getDraggable().element.draggable({
                cursor: BICst.cursorUrl,
                cursorAt: {left: 0, top: 0},
                start: function (e, ui) {
                    self._startDrag(widget, ui.position, e);
                },
                drag: function (e, ui) {
                    self._drag(id, info, ui.position);
                },
                stop: function (e, ui) {
                    self._stopDrag(widget);
                },
                helper: function (e) {
                    var helper = self.arrangement.getHelper();
                    return helper.element;
                }
            });
        }
        return widget;
    },

    _startDrag: function (widget, position, e) {
        switch (this.getLayoutType()) {
            case BI.Arrangement.LAYOUT_TYPE.ADAPTIVE:
                this.flag = this.arrangement.deleteRegion(widget.attr("id"));
                break;
            case BI.Arrangement.LAYOUT_TYPE.FREE:
                this.diff = {
                    width: e.offsetX,
                    height: e.offsetY
                };
                break;
        }
    },

    _drag: function (id, info, position) {
        switch (this.getLayoutType()) {
            case BI.Arrangement.LAYOUT_TYPE.ADAPTIVE:
                if (BI.isNotNull(this.flag)) {
                    this.arrangement.setPosition({
                        left: position.left,
                        top: position.top
                    }, {
                        width: info.bounds.width,
                        height: info.bounds.height
                    });
                }
                break;
            case BI.Arrangement.LAYOUT_TYPE.FREE:
                this.arrangement.setRegionPosition(id, {
                    left: position.left - this.diff.width,
                    top: position.top - this.diff.height
                });
                break;
        }
    },

    _stopDrag: function (widget) {
        var flag = false;
        switch (this.getLayoutType()) {
            case BI.Arrangement.LAYOUT_TYPE.ADAPTIVE:
                if (BI.isNotNull(this.flag) && !(flag = this.arrangement.addRegion({
                        el: widget
                    }))) {
                    this.arrangement.revoke();
                }
                break;
            case BI.Arrangement.LAYOUT_TYPE.FREE:
                flag = true;
                break;
        }
        if (flag === true) {
            this.fireEvent(BI.Fit.EVENT_CHANGE);
        }
    },

    _startDragIcon: function () {
        switch (this.getLayoutType()) {
            case BI.Arrangement.LAYOUT_TYPE.ADAPTIVE:
                break;
            case BI.Arrangement.LAYOUT_TYPE.FREE:
                break;
        }
    },

    _dragIcon: function (info, position) {
        switch (this.getLayoutType()) {
            case BI.Arrangement.LAYOUT_TYPE.ADAPTIVE:
                this.arrangement.setPosition({
                    left: position.left,
                    top: position.top
                }, {
                    width: info.bounds.width,
                    height: info.bounds.height
                });
                break;
            case BI.Arrangement.LAYOUT_TYPE.FREE:
                this.arrangement.setPosition({
                    left: position.left,
                    top: position.top
                }, {
                    width: info.bounds.width,
                    height: info.bounds.height
                });
                break;
        }
    },

    _stopDragIcon: function (info, position) {
        var flag = false;
        info.bounds.left = position.left;
        info.bounds.top = position.top;
        switch (this.getLayoutType()) {
            case BI.Arrangement.LAYOUT_TYPE.ADAPTIVE:
                flag = this.arrangement.addRegion({
                    el: this._createItem(BI.UUID(), info)
                });
                break;
            case BI.Arrangement.LAYOUT_TYPE.FREE:
                flag = this.arrangement.addRegion({
                    el: this._createItem(BI.UUID(), info)
                });
                break;
        }
        if (flag === true) {
            this.fireEvent(BI.Fit.EVENT_CHANGE);
        }
    },

    _createNav: function () {
        var self = this;
        var dragGroup = BI.createWidget({
            type: "bi.drag_icon_group",
            drag: function (info, position) {
                self._dragIcon(info, position);
            },
            stop: function (info, position) {
                self._stopDragIcon(info, position);
            },
            helper: function () {
                var helper = self.arrangement.getHelper();
                return helper.element;
            }
        });
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: dragGroup,
                top: 10,
                left: 20,
                right: 20,
                bottom: 10
            }]
        });
    },

    setLayoutType: function (type) {
        this.layoutCombo.setValue(type);
        var old = this.getLayoutType();
        this.arrangement.setLayoutType(type);
        if (old !== type) {
            this.fireEvent(BI.Fit.EVENT_CHANGE);
        }
    },

    getLayoutType: function () {
        return this.arrangement.getLayoutType();
    },

    getAllRegions: function () {
        var regions = this.arrangement.getAllRegions();
        var result = [];
        BI.each(regions, function (i, region) {
            result.push({
                id: region.id,
                left: region.left,
                top: region.top,
                width: region.width,
                height: region.height
            })
        });
        return result;
    },

    getValue: function () {
        return {
            layoutStyle: this.getLayoutType(),
            regions: this.getAllRegions()
        }
    },

    copyRegion: function (id, newId) {
        var flag = false;
        var region = this.arrangement.getRegionByName(id);
        if (!(flag = this.arrangement.addRegion({
                el: this._createItem(newId),
                width:region.width,
                height: region.height
            }, {
                left: region.left + region.width / 2,
                top: region.top + region.height / 2
            }))) {
            if (!(flag = this.arrangement.addRegion(el, {
                    left: region.left + region.width / 2,
                    top: region.top + region.height / 4
                }))) {
                if (!(flag = this.arrangement.addRegion(el, {
                        left: region.left + region.width / 2,
                        top: region.top + region.height * 3 / 4
                    }))) {
                }
            }
        }
        if(flag === true){
            this.fireEvent(BI.Fit.EVENT_CHANGE);
        }
        return flag;
    },

    deleteRegion: function (id) {
        var flag = this.arrangement.deleteRegion(id);
        if (flag === true) {
            this.fireEvent(BI.Fit.EVENT_CHANGE);
        }
        return flag;
    },

    populate: function () {
        var self = this;
        var layoutStyle = Data.SharingPool.get("layoutStyle") || BI.Arrangement.LAYOUT_TYPE.ADAPTIVE;
        var result = [];
        var widgets = Data.SharingPool.cat("widgets");
        BI.each(widgets, function (id, widget) {
            var item = self._createItem(id, widget);
            var bounds = widget.bounds;
            result.push({
                el: item,
                left: bounds.left,
                top: bounds.top,
                width: bounds.width,
                height: bounds.height
            });
        });
        this.setLayoutType(layoutStyle);
        this.arrangement.populate(result);
    },

    destroy: function () {
        BI.Resizers.remove(this.getName());
        BI.Fit.superclass.destroy.apply(this, arguments);
    }
});
BI.Fit.EVENT_CHANGE = "Fit.EVENT_CHANGE";
$.shortcut('bi.fit', BI.Fit);
