MultiRelationDetailTableView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(MultiRelationDetailTableView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-multi-relation-detail-table bi-mvc-layout"
        })
    },

    _init: function () {
        MultiRelationDetailTableView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {


        var relations = [{
            "foreignKey": {
                "id": "table_circle",
                "field_name": "ID1",
                "table_name": "tableA"
            },
            "primaryKey": {
                "id": "table_circle",
                "field_name": "ID2",
                "table_name": "tableB"
            }
        }, {
            "foreignKey": {
                "id": "table_circle",
                "field_name": "3",
                "table_name": "table_circle"
            },
            "primaryKey": {
                "id": "table_circle",
                "field_name": "ID",
                "table_name": "table_circle"
            }
        }];


        var tableSet = BI.createWidget({
            type: "bi.detail_table_common_table_set",
            items: [{
                text:"foreigntableA",
                value: "fA",
                children: [{
                    text:"primarytableA",
                    value: "pA"
                }, {
                    text:"primarytableB",
                    value: "pB"
                }]
            },
                {
                    text:"foreigntableB",
                    value: "fB",
                    children: [{
                        text:"primarytableA",
                        value: "pA"
                    }, {
                        text:"primarytableB",
                        value: "pB"
                    }, {
                        text: "primarytableC",
                        value: "pC"
                    }],
                    selected: true
                }]
        });

        var switcher = BI.createWidget({
            type: "bi.detail_table_path_setting_switch",
            items: [{value: "first"}, {value: "second", type: "bi.text_button"}]
        });

        var tab = BI.createWidget({
            type: "bi.detail_table_path_setting_tab",
            //relations: relations,
            foreignFieldName: "foreignFieldName",
            //primaryFieldName: "primaryFieldName",
            choosePath: []

        });


        var items = [{
            value: "foreigntableA",
            children: [{
                value: "primarytableA"
            }, {
                value: "primarytablB"
            }]
        },
            {
                value: "foreigntableB",
                children: [{
                    value: "primarytableA"
                }, {
                    value: "primatytableB"
                }, {
                    value: "primarytableC"
                }],
                selected: true
            }
        ];

        var combo = BI.createWidget({
            type: "bi.detail_table_path_setting_combo",
        });
        combo.populate(items);
        combo.setValue(items[0].value);

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [
                //path,
                tableSet,
                switcher,
                tab,
                combo
            ],
            vgap: 20
        })

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.button",
                    text: "getValue",
                    handler: function () {
                        BI.Msg.toast(JSON.stringify(tableSet.getValue()));
                    }
                },
                left: 100,
                bottom: 10
            }]
        })

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.button",
                    text: "getComboValue",
                    handler: function () {
                        BI.Msg.toast(JSON.stringify(combo.getValue()));
                    }
                },
                left: 200,
                bottom: 10
            }]
        })
    }
});

MultiRelationDetailTableModel = BI.inherit(BI.Model, {});