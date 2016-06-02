describe("测试DimensionsManager", function () {
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
        expect(val.view).toEqual({10000: ["1", "2"]});

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
        expect(val.view).toEqual({10000: ["1", "2"]});
        env.deleteDimensionById("1");
        manager.populate();
        var val = manager.getValue();
        expect(val.view).toEqual({10000: ["2"]});

    });
});