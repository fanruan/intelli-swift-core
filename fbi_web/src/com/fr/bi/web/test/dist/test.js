describe("测试BI.Utils方法", function () {
    it("getWidgetCalculationByID最简单的情况", function () {
        var dimensions = {};
        var widgets = {
            1: {
                dimensions: dimensions,
                bounds: {}
            }
        };
        Data.SharingPool.put("widgets", widgets);
        Data.SharingPool.put("dimensions", dimensions);
        var calculation = BI.Utils.getWidgetCalculationByID("1");
        expect(calculation.filter).toEqual({
            filter_type: 80,
            filter_value: []
        });
    });
});