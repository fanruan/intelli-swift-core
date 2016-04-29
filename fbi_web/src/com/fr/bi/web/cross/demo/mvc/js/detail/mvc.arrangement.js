ArrangementView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ArrangementView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-arrangement bi-mvc-layout"
        })
    },

    _init: function () {
        ArrangementView.superclass._init.apply(this, arguments);
    },

    _createItem: function () {
        var self = this;
        var item = BI.createWidget({
            type: "bi.text_button",
            cls: "layout-bg" + BI.random(1, 8),
            handler: function () {
                self.arrangement.deleteRegion(this.getName());
            }
        });
        item.setValue(item.getName());
        return item;
    },

    _render: function (vessel) {
        var self = this;
        this.arrangement = BI.createWidget({
            type: "bi.arrangement",
            cls: "mvc-border",
            width: 800,
            height: 400,
            items: []
        });
        var drag = BI.createWidget({
            type: "bi.label",
            cls: "mvc-border",
            width: 50,
            height: 25,
            text: "drag me"
        });

        drag.element.draggable({
            revert: true,
            cursorAt: {left: 0, top: 0},
            drag: function (e, ui) {
                self.arrangement.setPosition({
                    left: ui.position.left,
                    top: ui.position.top
                })
            },
            stop: function (e, ui) {
                self.arrangement.addRegion({
                    el: self._createItem()
                });
            },
            helper: function (e) {
                var helper = self.arrangement.getHelper();
                return helper.element;
            }
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: drag,
                left: 30,
                top: 450
            }, {
                el: this.arrangement,
                left: 30,
                top: 30
            }, {
                el: {
                    type: "bi.button",
                    text: "回撤",
                    height: 25,
                    handler: function () {
                        //self.arrangement.revoke();
                    }
                },
                left: 130,
                top: 450
            }, {
                el: {
                    type: "bi.button",
                    text: "getAllRegions",
                    height: 25,
                    handler: function () {
                        var items = [];
                        BI.each(self.arrangement.getAllRegions(), function (i, region) {
                            items.push({
                                id: region.id,
                                left: region.left,
                                top: region.top,
                                width: region.width,
                                height: region.height
                            });
                        });
                        BI.Msg.toast(JSON.stringify(items));
                    }
                },
                left: 230,
                top: 450
            }]
        });
    }
});

ArrangementModel = BI.inherit(BI.Model, {});