MultiRelationView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(MultiRelationView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-multi-relation bi-mvc-layout"
        })
    },

    _init: function () {
        MultiRelationView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var list = BI.createWidget({
            type: "bi.multi_relation",
            relations: [[
                [{
                    "foreignKey": {
                        "id": "table_circle",
                        "field_name": "1",
                        "table_name": "table_circle"
                    },
                    "primaryKey": {
                        "id": "table_circle",
                        "field_name": "ID",
                        "table_name": "table_circle"
                    }
                }], [{
                    "foreignKey": {
                        "id": "table_circle",
                        "field_name": "5",
                        "table_name": "table_circle"
                    },
                    "primaryKey": {
                        "id": "table_circle",
                        "field_name": "ID",
                        "table_name": "table_circle"
                    }
                }], [{
                    "foreignKey": {
                        "id": "table_circle",
                        "field_name": "4",
                        "table_name": "table_circle"
                    },
                    "primaryKey": {
                        "id": "table_circle",
                        "field_name": "ID",
                        "table_name": "table_circle"
                    }
                }], [{
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
                }], [{
                    "foreignKey": {
                        "id": "table_circle",
                        "field_name": "2",
                        "table_name": "table_circle"
                    },
                    "primaryKey": {
                        "id": "table_circle",
                        "field_name": "ID",
                        "table_name": "table_circle"
                    }
                }]
            ], [[{
                "foreignKey": {
                    "id": "table_circle",
                    "field_name": "1",
                    "table_name": "table_circle"
                },
                "primaryKey": {
                    "id": "table_circle",
                    "field_name": "ID",
                    "table_name": "table_circle"
                }
            }], [{
                "foreignKey": {
                    "id": "table_circle",
                    "field_name": "5",
                    "table_name": "table_circle"
                },
                "primaryKey": {
                    "id": "table_circle",
                    "field_name": "ID",
                    "table_name": "table_circle"
                }
            }], [{
                "foreignKey": {
                    "id": "table_circle",
                    "field_name": "4",
                    "table_name": "table_circle"
                },
                "primaryKey": {
                    "id": "table_circle",
                    "field_name": "ID",
                    "table_name": "table_circle"
                }
            }], [{
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
            }], [{
                "foreignKey": {
                    "id": "table_circle",
                    "field_name": "2",
                    "table_name": "table_circle"
                },
                "primaryKey": {
                    "id": "table_circle",
                    "field_name": "ID",
                    "table_name": "table_circle"
                }
            }]]]
        });

        var tableFieldItem = BI.createWidget({
            type: "bi.multi_relation_table_field_item",
            tableName: "table1",
            fieldName: "field1"
        });

        var getNotSelectedButton = BI.createWidget({
            type: "bi.button",
            text: "getNotSelectedValue",
            handler: function () {
                var value = list.getNotSelectedValue();
                BI.Msg.alert(value.toString);
            }

        });

        var setValueButton = BI.createWidget({
            type: "bi.button",
            text: "setValue",
            handler: function () {
                var value = [{
                    "foreignKey": {
                        "id": "table_circle",
                        "field_name": "1",
                        "table_name": "table_circle"
                    },
                    "primaryKey": {
                        "id": "table_circle",
                        "field_name": "ID",
                        "table_name": "table_circle"
                    }
                }];
                list.setValue([value]);
            }
        });


        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [
                tableFieldItem,
                list,
                getNotSelectedButton,
                setValueButton
            ],
            vgap: 20
        })
    }
});

MultiRelationModel = BI.inherit(BI.Model, {});