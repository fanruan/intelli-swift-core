TableFieldInfoView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TableFieldInfoView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "table-field-info-view"
        })
    },

    _init: function () {
        TableFieldInfoView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            element: vessel,
            type: "bi.table_field_info_with_search_pane",
            tableInfo: {
                id: BI.UUID(),
                relations: {
                    connectionSet: [],
                    primKeyMap: [],
                    foreignKeyMap: []
                },
                translations: {},
                fields: [[{
                    class_type: 5,
                    field_name: "ID",
                    field_size: 32,
                    field_type: 1,
                    id: "a926eac8ID",
                    is_enable: true,
                    is_usable: true,
                    table_id: "a926eac8"
                }, {
                    class_type: 5,
                    field_name: "NAME",
                    field_size: 32,
                    field_type: 1,
                    id: "a926eac8NAME",
                    is_enable: true,
                    is_usable: true,
                    table_id: "a926eac8"
                }, {
                    class_type: 5,
                    field_name: "region",
                    field_size: 32,
                    field_type: 1,
                    id: "a926eac8region",
                    is_enable: true,
                    is_usable: true,
                    table_id: "a926eac8"
                }], [{
                    class_type: 1,
                    field_name: "VALUE",
                    field_size: 8,
                    field_type: 2,
                    id: "a926eac8VALUE",
                    is_enable: true,
                    is_usable: true,
                    table_id: "a926eac8"
                }], []],
                usedFields: ["ID", "NAME", "region", "VALUE"]
            }
        })
    }
});

TableFieldInfoModel = BI.inherit(BI.Model, {});