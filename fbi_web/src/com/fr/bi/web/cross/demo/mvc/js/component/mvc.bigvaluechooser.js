BigValueChooserView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BigValueChooserView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-pane-modules bi-mvc-layout"
        })
    },

    _init: function () {
        BigValueChooserView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var self = this;
        var table = BI.firstObject(Pool.tables);
        this.fieldName = BI.Utils.getFieldNameByID(BI.Utils.getFieldIDsOfTableID(table.id)[0]);

        var widget = BI.createWidget({
            type: "bi.big_value_chooser_combo",
            itemsCreator: function (options, callback) {
                BI.Utils.getConfDataByField(table, self.fieldName, {
                    keyword: options.keyword,
                    selected_values: options.selected_values,
                    type: options.type,
                    times: options.times
                }, function (data, hasNext) {
                    var result = [];
                    BI.each(data, function (idx, value) {
                        result.push({
                            text: value,
                            value: value,
                            title: value
                        })
                    });

                    switch (options.type) {
                        case BI.MultiSelectCombo.REQ_GET_ALL_DATA:
                            callback({
                                items: result
                            });
                            break;
                        case BI.MultiSelectCombo.REQ_GET_DATA_LENGTH:
                            callback({count: result.length});
                            break;
                        default:
                            callback({
                                items: result,
                                hasNext: hasNext
                            });
                    }
                });
            }
        });
        BI.createWidget({
            type: "bi.vertical",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [widget]
        })
    }
});

BigValueChooserModel = BI.inherit(BI.Model, {});