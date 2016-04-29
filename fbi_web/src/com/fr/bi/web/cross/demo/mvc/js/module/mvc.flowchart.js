ETLFlowChartView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(ETLAddFieldView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "etl-flow-chart-view"
        })
    },

    _init: function(){
        ETLFlowChartView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.float_center_adapt",
            element: vessel,
            items: [{
                type: "bi.etl_tables_pane",
                tables: [{
                    connection_name: "__FR_BI_ETL__",
                    etl_type: "union",
                    etl_value: {},
                    fields: [],
                    id: "94f8a1459c29f6fb",
                    md5: "19ac9a2a",
                    relations: {},
                    tables: [{
                        connection_name: "__FR_BI_ETL__",
                        etl_type: "union",
                        etl_value: {},
                        fields: [],
                        id: "a2b11ed826121df7",
                        md5: "19ac9a2a",
                        relations: {},
                        reopen: false,
                        tables: [{
                            connection_name: "local",
                            fields: [],
                            id: "2c524fbeab468685",
                            md5: "88afa39c",
                            relations: {},
                            table_name: "A",
                            translations: {}
                        }, {
                            connection_name: "local",
                            fields: [],
                            id: "3c524fbeab468685",
                            md5: "88afa39c",
                            relations: {},
                            table_name: "B",
                            translations: {}
                        }],
                        translations: {},
                        union_tables: [],
                        usedFields: []
                    }, {
                        connection_name: "local",
                        fields: [],
                        id: "4c524fbeab468685",
                        md5: "88afa39c",
                        relations: {},
                        table_name: "activity_fee",
                        translations: {}
                    }]
                }]
            }]
        })
    }
});

ETLFlowChartModel = BI.inherit(BI.Model, {

});