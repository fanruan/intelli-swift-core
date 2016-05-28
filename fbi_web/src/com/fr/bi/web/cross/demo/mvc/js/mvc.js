BI.each(_Models, function (k, models) {
    BI.each(models, function (i, model) {
        if (model.route) {
            _Routes["/" + model.value] = model.route;
        }
        if (model.js) {
            _JS.push(model.js);
        }
    });
});

BI.each(_JS, function (i, js) {
    FR.$defaultImport('com/fr/bi/web/cross/demo/mvc/js/' + js, 'js');
});
//入口View
TestView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TestView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc"
        })
    },

    events: {
        "resizable resizable": "_resize",
        "resizable baseBorder": "_baseResize",
        "resizable layoutBorder": "_layoutResize",
        "resizable componentBorder": "_componentResize"
    },

    _init: function () {
        TestView.superclass._init.apply(this, arguments);
        this.populate();
    },

    _resize: function (item) {
        var self = this;
        return {
            handles: "e",
            minWidth: 220,
            maxWidth: 500,
            resize: function (e, ui) {
                self.borderLayout.options.items.west.width = item.element.outerWidth();
                self.borderLayout.resize();
            },
            stop: function (e) {

            }
        }
    },

    _baseResize: function (item) {
        var self = this;
        return {
            handles: "s",
            minHeight: 30,
            resize: function (e, ui) {
                self.rightLeftNav.options.items[0].height = item.element.outerHeight();
                self.rightLeftNav.resize();
            },
            stop: function (e) {

            }
        }
    },

    _layoutResize: function (item) {
        var self = this;
        return {
            handles: "s",
            minHeight: 30,
            resize: function (e, ui) {
                self.rightLeftNav.options.items[1].height = item.element.outerHeight();
                self.rightLeftNav.resize();
            },
            stop: function (e) {

            }
        }
    },

    _componentResize: function (item) {
        var self = this;
        return {
            handles: "s",
            minHeight: 30,
            resize: function (e, ui) {
                self.rightRightNav.options.items[0].height = item.element.outerHeight();
                self.rightRightNav.resize();
            },
            stop: function (e) {

            }
        }
    },

    _createBaseNav: function () {
        var self = this;
        var nav = this.baseGroupNav = BI.createWidget({
            type: "bi.button_group",
            cls: "left-nav",
            items: BI.createItems(this.model.get("items").base, {
                type: "bi.text_button",
                cls: "left-nav-button",
                height: 30,
                textAlign: "left",
                hgap: 20
            }),
            layouts: [{
                type: "bi.vertical",
                vgap: 3,
                hgap: 3
            }]
        });
        nav.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self._onButtonClick(nav.getValue()[0], "base");
        });
        return BI.createWidget({
            type: "bi.border",
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        text: "基础 >",
                        cls: "left-classify",
                        textAlign: "left",
                        hgap: 30,
                        height: 30
                    },
                    height: 30
                },
                center: {
                    el: nav
                }
            }
        })
    },

    _createExtendNav: function () {
        var self = this;
        var nav = this.extendGroupNav = BI.createWidget({
            type: "bi.button_group",
            cls: "left-nav",
            items: BI.createItems(this.model.get("items").extend, {
                type: "bi.text_button",
                cls: "left-nav-button",
                height: 30,
                textAlign: "left",
                hgap: 20
            }),
            layouts: [{
                type: "bi.vertical",
                vgap: 3,
                hgap: 3
            }]
        })
        nav.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self._onButtonClick(nav.getValue()[0], "extend");
        })
        return BI.createWidget({
            type: "bi.border",
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        text: "拓展 >",
                        cls: "left-classify",
                        textAlign: "left",
                        hgap: 30,
                        height: 30
                    },
                    height: 30
                },
                center: {
                    el: nav
                }
            }
        })
    },

    _createSceneNav: function () {
        var self = this;
        var nav = this.sceneGroupNav = BI.createWidget({
            type: "bi.button_group",
            cls: "left-nav",
            items: BI.createItems(this.model.get("items").scene, {
                type: "bi.text_button",
                cls: "left-nav-button",
                height: 30,
                textAlign: "left",
                hgap: 20
            }),
            layouts: [{
                type: "bi.vertical",
                vgap: 3,
                hgap: 3
            }]
        })
        nav.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self._onButtonClick(nav.getValue()[0], "scene");
        })
        return BI.createWidget({
            type: "bi.border",
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        text: "场景 >",
                        cls: "left-classify",
                        textAlign: "left",
                        hgap: 30,
                        height: 30
                    },
                    height: 30
                },
                center: {
                    el: nav
                }
            }
        })
    },

    _createBaseControlNav: function () {
        var self = this;
        var nav = this.widgetGroupNav = BI.createWidget({
            type: "bi.level_tree",
            chooseType: 0,
            items: this.model.get("items").widget
        })
        nav.on(BI.LevelTree.EVENT_CHANGE, function (v) {
            self._onButtonClick(nav.getValue()[0], "widget")
        })
        return this.baseBorder = BI.createWidget({
            type: "bi.border",
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        text: "基础 >",
                        cls: "right-classify",
                        textAlign: "left",
                        hgap: 30,
                        height: 30
                    },
                    height: 30
                },
                center: {
                    el: nav
                }
            }
        })
    },

    _createLayoutControlNav: function () {
        var self = this;
        var nav = this.layoutGroupNav = BI.createWidget({
            type: "bi.button_group",
            cls: "right-nav",
            items: BI.createItems(this.model.get("items").layout, {
                type: "bi.text_button",
                cls: "right-nav-button",
                height: 30,
                textAlign: "left",
                hgap: 20
            }),
            layouts: [{
                type: "bi.vertical",
                vgap: 3,
                hgap: 3
            }]
        })
        nav.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self._onButtonClick(nav.getValue()[0], "layout");
        })
        return this.layoutBorder = BI.createWidget({
            type: "bi.border",
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        text: "布局 >",
                        cls: "right-classify",
                        textAlign: "left",
                        hgap: 30,
                        height: 30
                    },
                    height: 30
                },
                center: {
                    el: nav
                }
            }
        })
    },

    _createDetailControlNav: function () {
        var self = this;
        var nav = this.detailGroupNav = BI.createWidget({
            type: "bi.level_tree",
            cls: "right-nav",
            items: this.model.get("items").detail
        })
        nav.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self._onButtonClick(nav.getValue()[0], "detail");
        })
        return BI.createWidget({
            type: "bi.border",
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        text: "详细控件 >",
                        cls: "right-classify",
                        textAlign: "left",
                        hgap: 30,
                        height: 30
                    },
                    height: 30
                },
                center: {
                    el: nav
                }
            }
        })
    },

    _createComponentControlNav: function () {
        var self = this;
        var nav = this.componentGroupNav = BI.createWidget({
            type: "bi.level_tree",
            cls: "right-nav",
            items: this.model.get("items").component
        })
        nav.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self._onButtonClick(nav.getValue()[0], "component");
        })
        return this.componentBorder = BI.createWidget({
            type: "bi.border",
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        text: "部件+服务 >",
                        cls: "right-classify",
                        textAlign: "left",
                        hgap: 30,
                        height: 30
                    },
                    height: 30
                },
                center: {
                    el: nav
                }
            }
        })
    },

    _createModuleControlNav: function () {
        var self = this;
        var nav = this.moduleGroupNav = BI.createWidget({
            type: "bi.level_tree",
            cls: "right-nav",
            items: this.model.get("items").module
        })
        nav.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self._onButtonClick(nav.getValue()[0], "module");
        })
        return BI.createWidget({
            type: "bi.border",
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        text: "功能模块 >",
                        cls: "right-classify",
                        textAlign: "left",
                        hgap: 30,
                        height: 30
                    },
                    height: 30
                },
                center: {
                    el: nav
                }
            }
        })
    },

    _createNav: function () {
        return BI.createWidget({
            type: "bi.division",
            columns: 1,
            rows: 3,
            items: [{
                column: 0,
                row: 0,
                width: 100,
                height: 70,
                el: this._createBaseNav()
            }, {
                column: 0,
                row: 1,
                width: 100,
                height: 30,
                el: this._createExtendNav()
            }, {
                column: 0,
                row: 2,
                width: 100,
                height: 30,
                el: this._createSceneNav()
            }]
        })
    },

    _createControlNav: function () {
        this.rightLeftNav = BI.createWidget({
            type: "bi.vtape",
            items: [{
                height: 200,
                el: this._createBaseControlNav()
            }, {
                height: 100,
                el: this._createLayoutControlNav()
            }, {
                height: "fill",
                el: this._createDetailControlNav()
            }]
        });
        this.rightRightNav = BI.createWidget({
            type: "bi.vtape",
            items: [{
                height: 300,
                el: this._createComponentControlNav()
            }, {
                height: "fill",
                el: this._createModuleControlNav()
            }]
        });
        return BI.createWidget({
            type: "bi.grid",
            items: [[this.rightLeftNav, this.rightRightNav]]
        })
    },

    _onButtonClick: function (v, which) {
        var self = this;
        var flatten = ["base", "extend", "scene", "widget", "layout", "detail", "module", "component"];
        BI.each(flatten, function (i, flat) {
            if (which != flat) {
                self[flat + "GroupNav"].setValue();
            }
        });
        this.skipTo(v, "pane");
    },

    _createPane: function () {
        var pane = BI.createWidget({
            type: "bi.layout",
            cls: "center-pane"
        });
        this.addSubVessel("pane", pane, {
            defaultShowName: "default"
        });
        return pane;
    },

    _render: function (vessel) {
        vessel.css("z-index", 0);
        var west = this.resizable = BI.createWidget({
            type: "bi.border",
            cls: "left",
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        cls: "left-title",
                        text: "BI 框架",
                        height: 30
                    },
                    height: 30
                },
                center: {
                    el: this._createNav()
                }
            }
        })
        var east = BI.createWidget({
            type: "bi.border",
            cls: "right",
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        cls: "right-title",
                        text: "BI 控件",
                        height: 30
                    },
                    height: 30
                },
                center: {
                    el: this._createControlNav()
                }
            }
        })
        this.borderLayout = BI.createWidget({
            type: "bi.border",
            element: vessel,
            items: {
                north: {
                    el: {
                        type: "bi.label",
                        cls: "top",
                        text: "包含bi框架、控件使用和less规范的用法",
                        height: 30
                    },
                    height: 30,
                    bottom: 1
                },
                west: {
                    el: west,
                    width: 220,
                    right: 2
                },
                east: {
                    el: east,
                    width: 440,
                    left: 2
                },
                south: {
                    el: {
                        type: "bi.label",
                        cls: "bottom",
                        text: "包含bi框架、控件使用和less规范的用法",
                        height: 20
                    },
                    height: 20,
                    top: 1
                },
                center: {
                    el: this._createPane()
                }
            }
        })
    },

    refresh: function () {
        this.skipTo("default", "pane");
    }
});

//入口Model，名称必须是TestModel， 即将View替换成Model
TestModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(TestModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _static: function () {
        return {
            items: _Models
        }
    },

    _init: function () {
        TestModel.superclass._init.apply(this, arguments);
    }
})

//定义Model路由
TModels = new (BI.inherit(BI.WRouter, {
    routes: {
        "": "index"
    },

    index: function () {
        return {};
    }
}));

//定义View路由
TViews = new (BI.inherit(BI.WRouter, {
    routes: _Routes,

    getSkipToView: function (v) {
        switch (v) {
            case "red":
                return "SkipToRedView";
            case "blue":
                return "SkipToBlueView";
            case "green":
                return "SkipToGreenView";
            case "yellow":
                return "SkipToYellowView";
            default :
                return "SkipToRedView";
        }

    }
}));

//定义floatbox路由
FloatBoxes = new (BI.inherit(BI.FloatBoxRouter, {
    routes: {
        "/floatbox/:id": "FloatBoxChildView",
        "/formula_insert/:id": "FormulaFloatBoxChildView",
        "/自定义分组/:id": "CustomGroupFloatBoxChildView",
        "/数值区间自定义分组/:id": "NumberIntervalCustomGroupChildView",
        "/etl新增公式列/:id": "ETLAddFieldFloatboxView",
        "/etl新增分组列/:id": "ETLAddGroupFieldFloatboxView"


    }
}));

//配置View
BI.View.prototype.createView = function (url, modelData, viewData) {
    return BI.Factory.createView(url, TViews.get(url), _.extend({}, TModels.get(url), modelData), viewData || {}, this);
};

$(function () {
    //入口
    var AppRouter = BI.Router.extend({
        routes: {
            "": "index"
        },
        index: function () {
            BI.Factory.createView("", TViews.get(""), BI.extend({}, TModels.get("")), {
                element: "body"
            }, null);
        }
    });
    this.router = new AppRouter;
    BI.history.start();
});