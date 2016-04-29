FiltersView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FiltersView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-etl-circle-view"
        })
    },

    _init: function () {
        FiltersView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        this.filterOpeartor = BI.createWidget({
            type: "bi.field_filter",
            cls: "filter-operator-pane",
            height: 550,
            width: 490
        });

        BI.createWidget({
            type: "bi.center_adapt",
            element: vessel,
            items: [this.filterOpeartor]
        });

        var table = BI.firstObject(Pool.tables);
        this.filterOpeartor.populate({
            conditions: [],
            table: table
        });
    }
});

FiltersModel = BI.inherit(BI.Model, {

});