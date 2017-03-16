describe("测试BI.Utils方法", function () {
    it("getWidgetCalculationByID最简单的情况", function () {
        environment();
        var calculation = BI.Utils.getWidgetCalculationByID("1");
        expect(calculation.filter).toEqual({
            filter_type: 80,
            filter_value: []
        });
    });
});