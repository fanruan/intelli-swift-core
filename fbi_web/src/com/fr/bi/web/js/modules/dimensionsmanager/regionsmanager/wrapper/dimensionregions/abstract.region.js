/**
 * Created by GUY on 2016/3/16.
 * @class BI.AbstractRegion
 * @extends BI.Widget
 */
BI.AbstractRegion = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AbstractRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-region",
            dimensionCreator: BI.emptyFn,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _init: function () {
        BI.AbstractRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.store = {};
        this.emptyTip = null;
        this.center = BI.createWidget({
            type: "bi.button_group",
            cls: this._getDimensionContainerClass(),
            layouts: [{
                type: "bi.vertical",
                scrolly: true,
                width: "100%",
                height: "100%",
                hgap: 5,
                vgap: 5
            }]
        });

        this.center.element.sortable({
            containment: o.containment.element,
            connectWith: "." + this._getDimensionContainerClass(),
            tolerance: "pointer",
            helper: o.helperContainer ? function (event, ui) {
                var drag = BI.createWidget({
                    type: "bi.layout",
                    cls: self._getSortableHelperClass()
                });
                drag.element.append(ui.html()).css({"margin": "5px"});
                BI.createWidget({
                    type: "bi.default",
                    element: o.helperContainer.element,
                    items: [drag]
                });
                return drag.element;
            } : "original",
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height() - 2
                    });
                    holder.element.css({"margin": "5px"});
                    return holder.element;
                },
                update: function () {

                }
            },
            items: "." + this._getDimensionClass(),
            update: function (event, ui) {
                self.fireEvent(BI.AbstractRegion.EVENT_CHANGE);
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });

        this.element.droppable({
            accept: ".select-data-level0-item-button, .select-data-level1-item-button",
            tolerance: "pointer",
            drop: function (event, ui) {
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();
                var helper = ui.helper;
                var data = helper.data("data");
                data = self._dropDataFilter(data);
                BI.each(data, function (i, dimension) {
                    if (!BI.has(dimension, "used")) {
                        dimension.used = true;
                    }
                    var dId = BI.UUID();
                    o.dimensionCreator(dId, dimension);
                });
                BI.Broadcasts.send(BICst.BROADCAST.FIELD_DROP_PREFIX);
                //滚到最下面
                BI.nextTick(function () {
                    self.center.element.scrollTop(self.center.element[0].scrollHeight);
                });
                self._dropHook();
            },
            over: function (event, ui) {
                if (BI.isNull(self.forbiddenMask) || !self.forbiddenMask.isVisible()) {
                    self.dropArea = BI.createWidget({
                        type: "bi.layout",
                        height: 25,
                        cls: "region-drop-area"
                    });
                    self.center.addItems([self.dropArea]);
                    self.emptyTip && self.emptyTip.setVisible(false);
                }
                var helperWidget = ui.helper.data().helperWidget;
                var helper = self._getFieldDropOverHelper();
                if (BI.isNotNull(helper)) {
                    helperWidget.modifyContent(helper);
                }
                self._overHook();
            },
            out: function (event, ui) {
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();
                var helperWidget = ui.helper.data().helperWidget;
                helperWidget.populate();
                self.emptyTip && self.emptyTip.setVisible(true);
                self._outHook();
            }
        });

        BI.createWidget({
            type: "bi.default",
            element: this.element,
            items: [this.center]
        });

        BI.Broadcasts.on(BICst.BROADCAST.FIELD_DRAG_START, function (fields) {
            self.dimensions = fields;
            self._fieldDragStart();
        });
        BI.Broadcasts.on(BICst.BROADCAST.FIELD_DRAG_STOP, function () {
            self.dimensions = null;
            self._fieldDragStop();
        });
    },

    _createEmptyTip: function () {
        return BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Drag_Left_Field"),
            cls: "region-empty-tip",
            height: 25
        });
    },

    _getDimensionClass: function () {
    },

    _getDimensionContainerClass: function () {
    },

    _getSortableHelperClass: function () {
    },

    _dropDataFilter: function (data) {
        return data;
    },

    _fieldDragStart: function () {
        var onlyCounter = !BI.some(this.dimensions, function (i, dim) {
            return BI.Utils.isDimensionType(dim.type);
        });
        if (onlyCounter) {
            this._showForbiddenMask();
        }
    },

    _fieldDragStop: function () {
        this._hideForbiddenMask();
    },

    _dropHook: function () {
    },
    _overHook: function () {
    },

    _outHook: function () {
    },

    _getFieldDropOverHelper: function () {
        //可以放置的字段 + 不可放置的字段
        var total = this.dimensions.length;
        var counters = 0;
        var _set = [BICst.TARGET_TYPE.FORMULA,
            BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE,
            BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE,
            BICst.TARGET_TYPE.RANK,
            BICst.TARGET_TYPE.RANK_IN_GROUP,
            BICst.TARGET_TYPE.SUM_OF_ABOVE,
            BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP,
            BICst.TARGET_TYPE.SUM_OF_ALL,
            BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP,
            BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE,
            BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE,
            BICst.TARGET_TYPE.COUNTER
        ];
        BI.each(this.dimensions, function (i, dim) {
            if (BI.contains(_set, dim.type)) {
                counters++;
            }
        });
        if (counters > 0 && counters !== total) {
            return BI.createWidget({
                type: "bi.left",
                cls: "helper-warning",
                items: [{
                    type: "bi.left",
                    cls: "drag-helper-active-font",
                    items: [{
                        type: "bi.icon",
                        width: 20,
                        height: 20
                    }, {
                        type: "bi.label",
                        text: total - counters
                    }],
                    lgap: 5
                }, {
                    type: "bi.left",
                    cls: "drag-helper-forbidden-font",
                    items: [{
                        type: "bi.icon",
                        width: 20,
                        height: 20
                    }, {
                        type: "bi.label",
                        text: counters
                    }],
                    lgap: 5
                }],
                rgap: 5
            });
        } else if (counters === total) {
            return BI.createWidget({
                type: "bi.left",
                cls: "helper-warning drag-helper-forbidden-font",
                items: [{
                    type: "bi.icon",
                    width: 20,
                    height: 20
                }],
                hgap: 5
            });
        }
    },

    _showForbiddenMask: function () {
        if (BI.isNotNull(this.forbiddenMask)) {
            this.forbiddenMask.setVisible(true);
        } else {
            this.forbiddenMask = BI.createWidget({
                type: "bi.layout",
                cls: "forbidden-mask"
            });
            BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: this.forbiddenMask,
                    top: 0,
                    left: 0,
                    bottom: 0,
                    right: 0
                }]
            });
        }
    },

    _hideForbiddenMask: function () {
        BI.isNotNull(this.forbiddenMask) && this.forbiddenMask.setVisible(false);
    },

    _createDimension: function (dId) {
        var o = this.options;
        return BI.createWidget({
            type: "bi.absolute",
            cls: this._getDimensionClass() + " dimension-tag",
            data: {
                dId: dId
            },
            height: 25,
            items: [{
                el: o.dimensionCreator(dId),
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            }]
        });
    },

    getValue: function () {
        var o = this.options;
        var dimensions = $(".dimension-tag", this.element);
        var view = {}, dIds = [];
        BI.each(dimensions, function (i, dimension) {
            dIds.push($(dimension).data("dId"));
        });
        if (dIds.length > 0) {
            view[o.viewType] = dIds;
        }
        return view;
    },

    populate: function () {
        var self = this, o = this.options;
        BI.DOM.hang(this.store);
        this.store = {};
        var dIds = [];
        if (BI.isNotNull(o.regionType)) {
            var view = BI.Utils.getWidgetViewByID(o.wId);
            dIds = view[o.regionType];
        }
        BI.each(dIds, function (i, dId) {
            self.store[dId] = self._createDimension(dId);
        });
        this.center.populate(BI.toArray(this.store));
        if (BI.size(this.store) > 0) {
            this.emptyTip && this.emptyTip.destroy();
            this.emptyTip = null;
        } else {
            this.emptyTip = this._createEmptyTip();
            this.center.addItems([this.emptyTip]);
        }
    }
});
BI.AbstractRegion.EVENT_CHANGE = "EVENT_CHANGE";