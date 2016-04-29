SelectPartFieldView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectPartFieldView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-select-part-field bi-mvc-layout"
        })
    },

    _init: function () {
        SelectPartFieldView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var list = BI.createWidget({
            type: "bi.select_part_field_list",
            element: vessel
        });

        list.populate([{field_name: "字段1"}, {field_name: "字段2"}]);
    }
});

SelectPartFieldModel = BI.inherit(BI.Model, {});