describe("测试Fit", function () {

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
});