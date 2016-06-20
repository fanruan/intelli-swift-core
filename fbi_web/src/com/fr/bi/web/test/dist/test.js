function environment() {
    function init(widgets, layoutType) {
        var dims = {};
        BI.each(widgets, function (id, widget) {
            BI.extend(dims, widget.dimensions);
        });
        Data.SharingPool.put("dimensions", dims);
        Data.SharingPool.put("widgets", widgets);
        if (layoutType != null) {
            Data.SharingPool.put("layoutType", layoutType);
        }
    }

    var dimensions = {1: {}, 2: {}, 3: {}, 4: {}, 5: {}};
    var widgets = {
        1: {
            type: BICst.WIDGET.AXIS,
            dimensions: dimensions,
            view: {10000: ["1", "2"], 20000: ["3"], 30000: ["4", "5"]},
            bounds: {
                left: 0,
                top: 0,
                width: 200,
                height: 200
            }
        }
    };
    init(widgets, BI.Arrangement.LAYOUT_TYPE.ADAPTIVE);

    return {
        deleteDimensionById: function (id) {
            delete dimensions[id];
            BI.each(widgets, function (i, wi) {
                BI.each(wi.view, function (i, v) {
                    if (v.contains(id)) {
                        BI.remove(v, id);
                    }
                })
            });
            init(widgets);
        },

        setWidgetTypeById: function (id, type) {
            widgets[id].type = type;
            init(widgets);
        }
    }
};describe("测试DimensionsManager", function () {
    it("维度的构建", function () {
        environment();
        var dimensionsVessel = {};
        var manager = BI.createWidget({
            type: "bi.dimensions_manager",
            element: $("#wrapper"),
            wId: "1",
            dimensionCreator: function (id, info) {
                if (!dimensionsVessel[id]) {
                    dimensionsVessel[id] = BI.createWidget();
                }
                return dimensionsVessel[id];
            }
        });
        manager.populate();
        var val = manager.getValue();
        $("#wrapper").empty();
        expect(val.view).toEqual({10000: ["1", "2"], 20000: ["3"], 30000: ["4", "5"]});

    });
    it("维度的删除", function () {
        var env = environment();
        var dimensionsVessel = {};
        var manager = BI.createWidget({
            type: "bi.dimensions_manager",
            element: $("#wrapper"),
            wId: "1",
            dimensionCreator: function (id, info) {
                if (!dimensionsVessel[id]) {
                    dimensionsVessel[id] = BI.createWidget();
                }
                return dimensionsVessel[id];
            }
        });
        manager.populate();
        var val = manager.getValue();
        expect(val.view).toEqual({10000: ["1", "2"], 20000: ["3"], 30000: ["4", "5"]});
        env.deleteDimensionById("1");
        manager.populate();
        var val = manager.getValue();
        expect(val.view).toEqual({10000: ["2"], 20000: ["3"], 30000: ["4", "5"]});

    });
    it("组件类型的切换", function () {
        var env = environment();
        var dimensionsVessel = {};
        var manager = BI.createWidget({
            type: "bi.dimensions_manager",
            element: $("#wrapper"),
            wId: "1",
            dimensionCreator: function (id, info) {
                if (!dimensionsVessel[id]) {
                    dimensionsVessel[id] = BI.createWidget();
                }
                return dimensionsVessel[id];
            }
        });
        manager.populate();
        var val = manager.getValue();
        expect(val.view).toEqual({10000: ["1", "2"], 20000: ["3"], 30000: ["4", "5"]});
        manager.model.setType(BICst.WIDGET.TABLE);
        var val = manager.getValue();
        expect(val.view).toEqual({10000: ["1", "2", "3"], 30000: ["4", "5"]});

    });
});;describe("测试Fit", function () {

    it("自适应布局的构建", function (done) {
        environment();
        var widgetVessel = {};
        var fit = BI.createWidget({
            type: "bi.fit",
            element: $("#wrapper"),
            layoutType: BI.Arrangement.LAYOUT_TYPE.ADAPTIVE,
            widgetCreator: function (id, info) {
                if (!widgetVessel[id]) {
                    widgetVessel[id] = BI.createWidget();
                }
                return widgetVessel[id];
            }
        });
        fit.populate();
        BI.nextTick(function () {
            var bodyWidth = $("#wrapper").width(), bodyHeight = $("#wrapper").height();
            var regions = fit.getAllRegions();
            var region = regions[0];
            var bounds = {
                left: region.left,
                top: region.top,
                width: region.width,
                height: region.height
            };
            expect(bounds).toEqual({
                left: 0,
                top: 0,
                width: bodyWidth - 141,
                height: bodyHeight
            });
            $("#wrapper").empty();
            done();
        });
    });
    it("自适应布局的拷贝", function (done) {
        environment();
        var widgetVessel = {};
        var fit = BI.createWidget({
            type: "bi.fit",
            element: $("#wrapper"),
            layoutType: BI.Arrangement.LAYOUT_TYPE.ADAPTIVE,
            widgetCreator: function (id, info) {
                if (!widgetVessel[id]) {
                    widgetVessel[id] = BI.createWidget();
                }
                return widgetVessel[id];
            }
        });
        fit.populate();
        BI.nextTick(function () {
            fit.copyRegion("1");

            var regions = fit.getAllRegions();
            expect(regions.length).toEqual(2);
            $("#wrapper").empty();
            done();
        });
    });
    it("自适应布局的删除", function (done) {
        environment();
        var widgetVessel = {};
        var fit = BI.createWidget({
            type: "bi.fit",
            element: $("#wrapper"),
            layoutType: BI.Arrangement.LAYOUT_TYPE.ADAPTIVE,
            widgetCreator: function (id, info) {
                if (!widgetVessel[id]) {
                    widgetVessel[id] = BI.createWidget();
                }
                return widgetVessel[id];
            }
        });
        fit.populate();
        BI.nextTick(function () {
            fit.deleteRegion("1");
            var regions = fit.getAllRegions();
            expect(regions.length).toEqual(0);
            $("#wrapper").empty();
            done();
        });
    });
});;describe("测试BI.Utils方法", function () {
    it("getWidgetCalculationByID最简单的情况", function () {
        environment();
        var calculation = BI.Utils.getWidgetCalculationByID("1");
        expect(calculation.filter).toEqual({
            filter_type: 80,
            filter_value: []
        });
    });
});