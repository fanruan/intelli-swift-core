/**
 * 联动
 *
 * Created by GUY on 2016/3/14.
 * @class BI.Linkage
 * @extends BI.Widget
 */
BI.Linkage = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.Linkage.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-linkage",
            wId: ""
        });
    },

    _init: function () {
        BI.Linkage.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.LinkageModel({
            wId: o.wId
        });

        this.mask = BI.createWidget({
            type: "bi.layout",
            cls: "bi-message-mask"
        });
        this.arrangement = BI.createWidget({
            type: "bi.adaptive_arrangement",
            isNeedResizeContainer: false,
            resizable: false,
            layoutType: Data.SharingPool.get("layoutType")
        });
        this.store = {};
        this.linkages = {};

        var offset = $(".fit-dashboard").offset();
        var left = offset.left, top = offset.top;
        var right = $("body").width() - $(".fit-dashboard").width() - left,
            bottom = $("body").height() - $(".fit-dashboard").height() - top;
        right = right < 0 ? 0 : right;
        bottom = bottom < 0 ? 0 : bottom;

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.mask,
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            }, {
                el: this.arrangement,
                left: left,
                right: right,
                top: top,
                bottom: bottom
            }]
        });

        this._createToolBarContainer();
    },

    _createHelper: function (tId) {
        var name = BI.Utils.getDimensionNameByID(tId);
        var help = BI.createWidget({
            type: "bi.helper",
            data: {
                tId: tId
            },
            text: name
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{el: help}]
        });
        return help;
    },

    _createCalHelper: function (tId, cIds) {
        var name = BI.Utils.getDimensionNameByID(tId);
        var help = BI.createWidget({
            type: "bi.helper",
            data: {
                tId: tId,
                cIds: cIds
            },
            text: name
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{el: help}]
        });
        return help;
    },

    _createCalculatorTargetLinkage: function (cId, pId) {
        var cIds = pId ? BI.concat(pId, cId) : [cId];
        var self = this;
        var name = BI.Utils.getDimensionNameByID(cId);
        var icon = BI.createWidget({
            type: "bi.center_adapt",
            items: [{
                type: "bi.icon",
                width: 20
            }],
            width: 24,
            height: 30,
            cls: "calculate-target-font"
        });
        var nameLabel = BI.createWidget({
            type: "bi.label",
            cls: "linkage-toolbar-target-name",
            text: name,
            hgap: 5,
            height: 30
        });
        var linkToLabel = BI.createWidget({
            type: "bi.label",
            hgap: 5,
            cls: "linkage-toolbar-target-linkto",
            text: BI.i18nText("BI-Link_To"),
            height: 30
        });
        var target = BI.createWidget({
            type: "bi.left",
            cls: "linkage-toolbar-target",
            items: [icon, {
                el: nameLabel,
                rgap: 30
            }, linkToLabel]
        });

        var items = [];
        BI.each(BI.Utils.getExpressionValuesByDimensionID(cId), function (idx, item) {
            if (BI.Utils.isCalculateTargetByDimensionID(item)) {
                items.push(BI.createWidget({
                    type: "bi.vertical",
                    items: [BI.createWidget({
                        type: "bi.vertical",
                        items: [self._createCalculatorTargetLinkage(item, cIds)],
                        cls: "linkage-toolbar-target-cal"
                    })],
                    vgap: 5,
                    hgap: 5
                }));
            } else {
                items.push(BI.createWidget({
                    type: "bi.vertical",
                    items: [createChildrenLinkage(item)],
                    vgap: 5,
                    hgap: 5
                }));
            }
        });

        var container = BI.createWidget({
            type: "bi.vertical",
            cls: "",
            items: items
        });

        return BI.createWidget({
            type: "bi.vertical",
            items: [target, container]
        });

        function createChildrenLinkage(tId) {
            var name = BI.Utils.getDimensionNameByID(tId);
            var nameLabel = BI.createWidget({
                type: "bi.label",
                cls: "linkage-toolbar-target-name",
                text: name,
                hgap: 5,
                height: 30
            });
            var target = BI.createWidget({
                type: "bi.left",
                cls: "linkage-toolbar-target",
                rgap: 30,
                items: [nameLabel]
            });

            target.element.draggable({
                cursor: BICst.cursorUrl,
                cursorAt: {left: 0, top: 0},
                start: function (e, ui) {
                    self.toolbarContainer.setVisible(false);
                },
                drag: function (e, ui) {
                },
                stop: function (e, ui) {
                    self.toolbarContainer.setVisible(true);
                },
                helper: function () {
                    return self._createCalHelper(tId, cIds).element;
                }
            });

            var targetContainer = BI.createWidget({
                type: "bi.vertical",
                items: [target],
                cls: "linkage-toolbar-target-cal"
            });

            var linkedWIds = self.model.getLinkedWidgetsByTargetIdAndCalculateIds(tId, cIds);
            BI.each(linkedWIds, function (i, wId) {
                var widgetType = BI.Utils.getWidgetTypeByID(wId);
                targetContainer.addItem({
                    type: "bi.htape",
                    items: [{
                        el: {
                            type: "bi.center_adapt",
                            cls: BICst.WIDGET_ICON_CLS_MAP[widgetType] + " widget-type-icon",
                            items: [{
                                type: "bi.icon",
                                width: 20,
                                height: 20
                            }],
                            width: 26,
                            height: 26
                        },
                        width: 26
                    }, {
                        el: {
                            type: "bi.label",
                            text: BI.Utils.getWidgetNameByID(wId),
                            height: 26,
                            textAlign: "left"
                        }
                    }, {
                        el: {
                            type: "bi.icon_button",
                            cls: "close-h-font",
                            width: 20,
                            height: 26,
                            handler: function () {
                                self.model.deleteLinkage(tId, wId, cIds);
                                self._populate();
                            }
                        },
                        width: 20
                    }],
                    height: 30
                });
            });

            return targetContainer;
        }
    },

    _createTargetLinkage: function (tId) {
        var self = this;
        var name = BI.Utils.getDimensionNameByID(tId);
        var nameLabel = BI.createWidget({
            type: "bi.label",
            cls: "linkage-toolbar-target-name",
            text: name,
            hgap: 5,
            height: 30
        });
        var linkToLabel = BI.createWidget({
            type: "bi.label",
            hgap: 5,
            cls: "linkage-toolbar-target-linkto",
            text: BI.i18nText("BI-Link_To"),
            height: 30
        });
        var target = BI.createWidget({
            type: "bi.left",
            cls: "linkage-toolbar-target",
            rgap: 30,
            items: [nameLabel, linkToLabel]
        });

        target.element.draggable({
            cursor: BICst.cursorUrl,
            cursorAt: {left: 0, top: 0},
            start: function (e, ui) {
                self.toolbarContainer.setVisible(false);
            },
            drag: function (e, ui) {
            },
            stop: function (e, ui) {
                self.toolbarContainer.setVisible(true);
            },
            helper: function () {
                return self._createHelper(tId).element;
            }
        });
        return target;
    },

    _refreshDragContainer: function () {
        var self = this;
        this.dragContainer.empty();
        var tIds = BI.Utils.getAllTargetDimensionIDs(this.options.wId);
        BI.each(tIds, function (i, tId) {
            if (BI.Utils.isTargetByDimensionID(tId) || BI.Utils.isCounterTargetByDimensionID(tId)) {
                var targetContainer = BI.createWidget({
                    type: "bi.vertical",
                    cls: "single-target-container",
                    items: [self._createTargetLinkage(tId)]
                });
                var linkedWIds = self.model.getLinkedWidgetsByTargetId(tId);
                BI.each(linkedWIds, function (i, wId) {
                    var widgetType = BI.Utils.getWidgetTypeByID(wId);
                    targetContainer.addItem({
                        type: "bi.htape",
                        items: [{
                            el: {
                                type: "bi.center_adapt",
                                cls: BICst.WIDGET_ICON_CLS_MAP[widgetType] + " widget-type-icon",
                                items: [{
                                    type: "bi.icon",
                                    width: 20,
                                    height: 20
                                }],
                                width: 26,
                                height: 26
                            },
                            width: 26
                        }, {
                            el: {
                                type: "bi.label",
                                text: BI.Utils.getWidgetNameByID(wId),
                                height: 26,
                                textAlign: "left"
                            }
                        }, {
                            el: {
                                type: "bi.icon_button",
                                cls: "close-h-font",
                                width: 20,
                                height: 26,
                                handler: function () {
                                    self.model.deleteLinkage(tId, wId, []);
                                    self._populate();
                                }
                            },
                            width: 20
                        }],
                        height: 30
                    });
                });
                self.dragContainer.addItem(targetContainer);
            }
            if (BI.Utils.isCalculateTargetByDimensionID(tId)) {
                var targetContainer = BI.createWidget({
                    type: "bi.vertical",
                    cls: "single-target-container",
                    items: [self._createCalculatorTargetLinkage(tId)]
                });
                self.dragContainer.addItem(targetContainer);
            }
        });
    },

    _createToolBar: function () {
        var self = this;
        // var title = BI.createWidget({
        //     type: "bi.label",
        //     cls: "linkage-toolbar-title",
        //     text: BI.i18nText("BI-Drag_Target_To_Set_Linkage"),
        //     textAlign: BI.HorizontalAlign.Left,
        //     lgap: 10,
        //     height: 40
        // });
        this.dragContainer = BI.createWidget({
            type: "bi.vertical",
            hgap: 10,
            tgap: 10,
            vgap: 5
        });
        this._refreshDragContainer();
        var cancel = BI.createWidget({
            type: "bi.button",
            height: 30,
            level: "ignore",
            text: BI.i18nText("BI-Cancel")
        });
        cancel.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.Linkage.EVENT_CANCEL);
        });
        var save = BI.createWidget({
            type: "bi.button",
            height: 30,
            text: BI.i18nText("BI-Sure")
        });
        save.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.Linkage.EVENT_CONFIRM);
        });
        var mask = BI.createWidget({
            type: "bi.absolute",
            cls: "linkage-tool-bar-mask",
            width: "100%",
            height: "100%"
        });
        return BI.createWidget({
            type: "bi.absolute",
            cls: "linkage-tool-bar",
            items: [{
                el: mask,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }, {
                el: this.dragContainer,
                left: 0,
                right: 0,
                top: 0,
                bottom: 40
            }, {
                el: cancel,
                left: 10,
                bottom: 10,
                right: 135
            }, {
                el: save,
                left: 135,
                bottom: 10,
                right: 10
            }]
        })
    },

    _createToolBarContainer: function () {
        this.toolbar = this._createToolBar();
        this.toolbarContainer = BI.createWidget({
            type: "bi.absolute",
            width: 260,
            items: [{
                el: this.toolbar,
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            }]
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.toolbarContainer,
                left: 0,
                top: 30,
                bottom: 30
            }]
        })
    },

    _createDropContainer: function (wId) {
        var self = this;
        var linkages = BI.createWidget({
            type: "bi.linkage_targets",
            model: this.model,
            from: this.options.wId,
            to: wId
        });
        linkages.on(BI.LinkageTargets.EVENT_DELETE, function (tId, cIds) {
            self.model.deleteLinkage(tId, wId, cIds);
            BI.nextTick(function () {
                self._populate();
            });
        });
        linkages.on(BI.LinkageTargets.EVENT_ADD, function (tId, cIds) {
            self.model.addLinkage(tId, wId, cIds);
            BI.nextTick(function () {
                self._populate();
            });
        });
        linkages.element.droppable({
            accept: ".linkage-toolbar-target",
            drop: function (event, ui) {
                var helper = ui.helper;
                var targetId = helper.data("tId");
                var cIds = helper.data("cIds") || [];
                linkages.addOneLinkage(targetId, cIds);
            },
            over: function (event, ui) {
                var helperWidget = ui.helper.data().helperWidget;
                var helper = BI.createWidget({
                    type: "bi.label",
                    cls: "linkage-drop-helper",
                    hgap: 10,
                    text: BI.i18nText("BI-Drop_To_Complete_Setting")
                });
                helperWidget.modifyContent(helper);
            },
            out: function (event, ui) {
                var helperWidget = ui.helper.data().helperWidget;
                helperWidget.populate();
            }
        });
        return linkages;
    },

    _createWidget: function (wId) {
        if (this.model.isWidgetCanLinkageTo(wId)) {
            var mask = BI.createWidget({
                type: "bi.absolute",
                cls: "linkage-widget-mask",
                width: "100%",
                height: "100%"
            });
            var drop = this._createDropContainer(wId);
            this.linkages[wId] = drop;
            var widget = BI.createWidget({
                type: "bi.absolute",
                cls: "linkage-widget",
                items: [{
                    el: mask,
                    left: 0,
                    top: 0,
                    right: 0
                }, {
                    el: drop,
                    left: 0,
                    right: 0,
                    top: 0,
                    bottom: 0
                }]
            });
            this.store[wId] = BI.createWidget({
                type: "bi.absolute",
                id: wId,
                items: [{
                    el: widget,
                    top: 5,
                    bottom: 5,
                    left: 5,
                    right: 5
                }]
            });
        } else {
            if (wId === this.options.wId) {
                var widget = BI.createWidget({
                    type: "bi.absolute",
                    cls: "linkage-widget-self"
                });
            } else if (BI.Utils.isControlWidgetByWidgetId(wId) || BI.Utils.isInstantControlWidgetByWidgetId(wId)) {
                var mask = BI.createWidget({
                    type: "bi.absolute",
                    cls: "linkage-widget-control",
                    width: "100%",
                    height: "100%"
                });
                var widget = BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: mask,
                        left: 0,
                        top: 0,
                        right: 0,
                        bottom: 0
                    }]
                });
            } else {
                var mask = BI.createWidget({
                    type: "bi.absolute",
                    cls: "linkage-widget-disabled-mask",
                    width: "100%",
                    height: "100%"
                });
                var content = this._createExistLinkagePanel(wId);
                var widget = BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: mask,
                        left: 0,
                        top: 0,
                        right: 0,
                        bottom: 0
                    }, {
                        el: content,
                        left: 0,
                        right: 0,
                        top: 0,
                        bottom: 0
                    }]
                });
            }
            this.store[wId] = BI.createWidget({
                type: "bi.absolute",
                id: wId,
                items: [{
                    el: widget,
                    top: 5,
                    bottom: 5,
                    left: 5,
                    right: 5
                }]
            });
        }
        return this.store[wId];
    },

    _createExistLinkagePanel: function (wId) {
        var linkageList = [], result = [];
        this.model.getExistLinkageByWidgetId(wId, this.options.wId, [], linkageList);

        BI.each(linkageList, function (idx, linkages) {
            var temp = [];
            BI.each(linkages, function (i, link) {
                switch(i) {
                    case 0:
                        temp.push({
                            el: {
                                type: "bi.label",
                                cls: "linkage-widget-disabled-item active",
                                text: BI.Utils.getWidgetNameByID(link),
                                title: BI.Utils.getWidgetNameByID(link),
                                width: 200,
                                height: 30
                            },
                            tgap: 20
                        });
                        temp.push({
                            el: {
                                type: "bi.icon_button",
                                cls: "down-arrow",
                                invalid: true,
                                iconWidth: 16,
                                iconHeight: 16,
                                width: 16,
                                height: 16
                            },
                            tgap: 2,
                            bgap: 2
                        });
                        break;
                    case linkages.length - 1:
                        temp.push({
                            el: {
                                type: "bi.label",
                                cls: "linkage-widget-disabled-item active",
                                text: BI.Utils.getWidgetNameByID(link),
                                title: BI.Utils.getWidgetNameByID(link),
                                width: 200,
                                height: 30
                            }
                        });
                        break;
                    default:
                        temp.push({
                            el: {
                                type: "bi.label",
                                cls: "linkage-widget-disabled-item",
                                text: BI.Utils.getWidgetNameByID(link),
                                title: BI.Utils.getWidgetNameByID(link),
                                width: 200,
                                height: 30
                            }
                        });
                        temp.push({
                            el: {
                                type: "bi.icon_button",
                                cls: "down-arrow",
                                invalid: false,
                                iconWidth: 16,
                                iconHeight: 16,
                                width: 16,
                                height: 16
                            },
                            tgap: 2,
                            bgap: 2
                        });
                }
            });
            result.push(BI.createWidget({
                type: "bi.horizontal_auto",
                items: temp
            }));
        });

        var tip = BI.createWidget({
            type: "bi.label",
            cls: "linkage-widget-disabled-title",
            text: BI.i18nText("BI-Already_Has_Linkage")
        });

        var content = BI.createWidget({
            type: "bi.horizontal_auto",
            items: BI.concat([tip], result)
        });

        return BI.createWidget({
            type: "bi.center_adapt",
            scrolly: true,
            cls: "linkage-widget-disabled",
            items: [content]
        });
    },

    getValue: function () {
        var result = [];
        BI.each(this.linkages, function (wId, linkage) {
            result = result.concat(linkage.getValue());
        });
        return result;
    },

    _populate: function () {
        var self = this, o = this.options;
        var ids = BI.Utils.getAllWidgetIDs();
        var items = [];
        BI.each(this.store, function (i, w) {
            w.destroy();
        });
        this.store = {};
        BI.each(ids, function (i, wId) {
            var bounds = BI.Utils.getWidgetBoundsByID(wId);
            var widget = self._createWidget(wId);
            items.push(BI.extend({}, bounds, {
                el: widget
            }))
        });
        this.arrangement.populate(items);
        this._refreshDragContainer();
    },

    populate: function () {
        var self = this, o = this.options;
        this.model.reset();
        this._populate();
    },

    destroy: function () {
        BI.Resizers.remove(this.getName());
        BI.Linkage.superclass.destroy.apply(this, arguments);
    }
});
BI.Linkage.EVENT_CONFIRM = "Linkage.EVENT_CONFIRM";
BI.Linkage.EVENT_CANCEL = "Linkage.EVENT_CANCEL";
$.shortcut('bi.linkage', BI.Linkage);