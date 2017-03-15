/**
 * Created by roy on 16/1/25.
 */
ETLAddGroupFieldView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ETLAddGroupFieldView.superclass._defaultConfig(this, arguments), {
            baseCls: "etl-add-group-field-view"
        })
    },
    _init: function () {
        ETLAddGroupFieldView.superclass._init.apply(this, arguments)
    },

    _render: function (vessel) {
        var self = this;
        this.fieldList = BI.createWidget({
            type: "bi.formula_list",
            width: 230
        });

        this.fieldList.on(BI.FormulaList.EVENT_CLICK_SET, function (id) {
            FloatBoxes.open(BI.UUID(), "newGroupsData." + id, {}, self, {
                data: {
                    fields: self.model.cat("fields"),
                    reopen: self.model.get("reopen"),
                    open2change: true,
                    table_name: self.model.get("table_name"),
                    connection_name: self.model.get("connection_name"),
                    tables: self.model.get("tables")
                }
            });
        });

        var addgroupfieldwidget = BI.createWidget({
            type: "bi.add_group_field",
            height:400
        })


        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [
                this.fieldList,
                addgroupfieldwidget
            ],
            hgap: 20,
            vgap: 20
        })
    },

    refresh: function () {
        var groups = [];
        var newGroupsData = this.model.get("newGroupsData");
        BI.each(newGroupsData, function (id, groupObj) {
            var groupValue = "";
            var groupItem = {};
            BI.each(groupObj.group.details, function (i, groupDetail) {
                var value = groupDetail.value + ":";
                BI.each(groupDetail.content, function (i, fieldObj) {
                    if (i === 0) {
                        value = value + fieldObj.value;
                    } else {
                        value = value + "," + fieldObj.value;
                    }
                });
                if (i === 0) {
                    groupValue = groupValue + value;
                } else {
                    groupValue = groupValue + ";" + value;
                }

            });
            groupItem.fieldName = groupObj.table_infor.field_name;
            groupItem.formulaString = groupValue;
            groupItem.id = id;
            groups.push(groupItem);
        });
        this.fieldList.populate(groups);
    }
});

ETLAddGroupFieldModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(ETLAddGroupFieldModel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "etl-add-group-field-model"
        })
    },

    _init: function () {
        ETLAddGroupFieldModel.superclass._init.apply(this, arguments)
    },

    refresh: function () {
        var id = BI.UUID();
        this.set("newGroupsData", {
            id: {
                group: {
                    details: [{
                        content: [{
                            value: "asdasda"
                        }],
                        value: "43"
                    }],
                    ungroup2Other: 0,
                    ungroup2OtherName: ""
                },
                table_infor: {
                    field_name: "123",
                    target_field_name: "Name2"
                }
            }
        })
    }

});

ETLAddGroupFieldFloatboxView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function () {
        return BI.extend(ETLAddGroupFieldFloatboxView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "etl-add-group-field-floatbox-view"
        })
    },
    _init: function () {
        ETLAddGroupFieldFloatboxView.superclass._init.apply(this, arguments)
    }


});

ETLAddGroupFieldFloatboxModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(ETLAddGroupFieldFloatboxModel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "etl-add-group-field-floatbox-model"
        })
    },
    _init: function () {
        ETLAddGroupFieldFloatboxModel.superclass._init.apply(this, arguments)
    }
});