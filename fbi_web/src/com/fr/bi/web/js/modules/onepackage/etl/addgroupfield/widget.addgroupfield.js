/**
 * Created by roy on 16/3/15.
 */
BI.AddGroupField = BI.inherit(BI.Widget, {
    constants: {
        ADD_GROUP_FIELD_NORTH_HEIGHT: 50,
        ADD_GROUP_FIELD_SOUTH_HEIGHT: 60,
        ADD_GROUP_FIELD_BUTTON_GAP: 20,
        ADD_GROUP_FIELD_BUTTON_HEIGHT: 30,
        ADD_GROUP_FIELD_BUTTON_WIDTH: 110,
        ADD_GROUP_FIELD_PERCENT_OF_LEFT_WIDTH: 0.4,
        ADD_GROUP_FIELD_PERCENT_OF_RIGHT_WIDTH: 0.6,
        ADD_GROUP_FIELD_EDITOR_HEIGHT: 30,
        ADD_GROUP_FIELD_REGION_HEIGHT: 180,
        ADD_GROUP_FIELD_WEST_WIDTH: 530,
        ADD_GROUP_FIELD_GAP_TEN: 10,
        ADD_GROUP_FIELD_GAP_FIVE: 5,
        ADD_GROUP_FIELD_GAP_TWENTY: 20,
        ADD_GROUP_FIELD_TYPE_NUMBER: 1,
        ADD_GROUP_FIELD_TYPE_STRING: 0,
        ADD_GROUP_FIELD_TYPE_DATE: 2,
        PREVIEW_TABLE_HEIGHT: 160
    },
    _defaultConfig: function () {
        return BI.extend(BI.AddGroupField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-add-group-field"
        })
    },

    _init: function () {
        BI.AddGroupField.superclass._init.apply(this, arguments);
        var o = this.options;
        this.model = new BI.AddGroupFieldModel({
            info: o.info
        });

        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                center: this._buildCenter(),
                south: {
                    el: this._buildSouth(),
                    height: this.constants.ADD_GROUP_FIELD_SOUTH_HEIGHT
                }
            }
        })
    },

    _buildCenter: function () {
        var self = this;
        var northLabel = BI.createWidget({
            type: "bi.label",
            cls: "bi-add-group-field-north-label",
            value: BI.i18nText("BI-Grouping_Column_Super"),
            textAlign: "left",
            textHeight: this.constants.ADD_GROUP_FIELD_NORTH_HEIGHT
        });

        var tableLabel = BI.createWidget({
            type: "bi.label",
            cls: "bi-etl-add-group-field-label",
            textAlign: "left",
            textHeight: this.constants.ADD_GROUP_FIELD_EDITOR_HEIGHT,
            text: BI.i18nText("BI-Table_Name") + ":"
        });

        this.tableNameLabel = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            hgap: 5,
            height: this.constants.ADD_GROUP_FIELD_EDITOR_HEIGHT
        });

        var addButton = BI.createWidget({
            type: "bi.button",
            value: BI.i18nText("BI-Add_Grouping_Column"),
            height: this.constants.ADD_GROUP_FIELD_EDITOR_HEIGHT
        });

        addButton.on(BI.Button.EVENT_CHANGE, function () {
            var id = BI.UUID();
            var popup = BI.createWidget({
                type: "bi.add_group_field_popover",
                info: {
                    id: id,
                    fields: self.model.getFields(),
                    newGroupsData: self.model.getNewGroupsData(),
                    reopen: self.model.isReopen(),
                    tableInfo: self.model.getTableInfo()
                }
            });
            BI.Popovers.remove("addGroupField");
            BI.Popovers.create("addGroupField", popup).open("addGroupField");
            popup.populate();
            popup.on(BI.AddGroupFieldPopover.EVENT_SAVE, function (data) {
                var newGroupsData = self.model.getNewGroupsData();
                var id = data.id;
                newGroupsData[id] = data;
                self.model.setNewGroupsData(newGroupsData);
                self.model.setGenerated(false);
                self._refreshPane();
            });

        });

        this.fieldList = BI.createWidget({
            type: "bi.formula_list"
        });

        this.fieldList.on(BI.FormulaList.EVENT_CLICK_SET, function (id) {
            var popup = BI.createWidget({
                type: "bi.add_group_field_popover",
                info: {
                    id: id,
                    fields: self.model.getFields(),
                    open2change: true,
                    newGroupsData: self.model.getNewGroupsData(),
                    reopen: self.model.isReopen(),
                    tableInfo: self.model.getTableInfo()
                }
            });
            BI.Popovers.remove("addGroupField");
            BI.Popovers.create("addGroupField", popup).open("addGroupField");
            popup.populate();
            popup.on(BI.AddGroupFieldPopover.EVENT_SAVE, function (data) {
                var newGroupsData = self.model.getNewGroupsData();
                var id = data.id;
                newGroupsData[id] = data;
                self.model.setNewGroupsData(newGroupsData);
                self.model.setGenerated(false);
                self._refreshPane();
            });

        });

        this.fieldList.on(BI.FormulaList.EVENT_CLICK_DELETE, function (id) {
            var newGroupsData = self.model.getNewGroupsData();
            delete newGroupsData[id];
            self.model.newGroupsData = newGroupsData;
            self.model.setGenerated(false);
            self._refreshPane();
        });

        this.previewPane = BI.createWidget({
            type: "bi.horizontal_adapt",
            scrollable: true
        });

        this.fieldPane = BI.createWidget({
            type: "BI.vertical",
            cls: "bi-field-pane",
            items: [
                {
                    el: {
                        type: "bi.left_right_vertical_adapt",
                        cls: "bi-elt-add-group-field-label-and-add-button",
                        items: {
                            left: [
                                {
                                    type: "bi.label",
                                    cls: "bi-etl-add-group-field-label",
                                    value: BI.i18nText("BI-Grouping_Column_Super") + ":",
                                    textAlign: "left",
                                    textHeight: this.constants.ADD_GROUP_FIELD_EDITOR_HEIGHT
                                }
                            ],
                            right: [
                                addButton
                            ]
                        },
                        height: 40
                    },

                },
                {
                    el: this.fieldList
                }
            ],
            vgap: 5
        });


        return BI.createWidget({
            type: "bi.border",
            cls: "bi-etl-add-group-field-border",
            items: {
                north: {
                    el: {
                        type: "bi.absolute",
                        items: [{
                            el: northLabel,
                            top: 0,
                            bottom: 0,
                            left: this.constants.ADD_GROUP_FIELD_BUTTON_GAP,
                            right: 0
                        }]
                    },
                    height: this.constants.ADD_GROUP_FIELD_NORTH_HEIGHT
                },
                west: {
                    el: {
                        type: "bi.vtape",
                        items: [
                            {
                                type: "bi.absolute",
                                cls: "bi-etl-add-group-field-merge",
                                items: [
                                    {
                                        el: {
                                            type: "bi.htape",
                                            items: [
                                                {
                                                    el: tableLabel,
                                                    width: this.constants.ADD_GROUP_FIELD_NORTH_HEIGHT
                                                }, this.tableNameLabel
                                            ],
                                        },
                                        left: this.constants.ADD_GROUP_FIELD_GAP_TEN,
                                        right: this.constants.ADD_GROUP_FIELD_GAP_TEN,
                                        top: this.constants.ADD_GROUP_FIELD_GAP_TEN,
                                        bottom: this.constants.ADD_GROUP_FIELD_GAP_TEN
                                    }
                                ],
                                height: this.constants.ADD_GROUP_FIELD_NORTH_HEIGHT
                            }, {
                                el: {
                                    type: "bi.layout"
                                },
                                height: 10
                            }, {
                                type: "bi.absolute",
                                cls: "bi-etl-add-group-field-merge",
                                items: [
                                    {
                                        el: this.fieldPane,
                                        left: this.constants.ADD_GROUP_FIELD_GAP_TEN,
                                        right: this.constants.ADD_GROUP_FIELD_GAP_TEN,
                                        top: 0,
                                        bottom: this.constants.ADD_GROUP_FIELD_GAP_TEN
                                    }
                                ]
                            }
                        ],
                        hgap: this.constants.ADD_GROUP_FIELD_GAP_TWENTY
                    },
                    width: this.constants.ADD_GROUP_FIELD_WEST_WIDTH
                },
                center: {
                    el: {
                        type: "bi.absolute",
                        items: [{
                            el: {
                                type: "bi.vtape",
                                cls: "bi-etl-add-group-field-merge",
                                items: [
                                    {
                                        el: {
                                            type: "bi.absolute",
                                            cls: "bi-etl-label-border-bottom",
                                            items: [{
                                                el: {
                                                    type: "bi.horizontal",
                                                    cls: "bi-etl-add-group-field-label",
                                                    items: [{
                                                        type: "bi.label",
                                                        value: BI.i18nText("BI-Table_Data"),
                                                        height: this.constants.ADD_GROUP_FIELD_NORTH_HEIGHT
                                                    }],
                                                    hgap: this.constants.ADD_GROUP_FIELD_GAP_TEN
                                                }
                                            }],
                                            left: this.constants.ADD_GROUP_FIELD_GAP_TWENTY,
                                            right: this.constants.ADD_GROUP_FIELD_GAP_TWENTY,
                                            top: 0,
                                            bottom: 0
                                        },
                                        height: this.constants.ADD_GROUP_FIELD_NORTH_HEIGHT
                                    }, {
                                        el: this.previewPane
                                    }
                                ],
                                bgap: this.constants.ADD_GROUP_FIELD_GAP_TEN
                            },
                            left: this.constants.ADD_GROUP_FIELD_GAP_TWENTY,
                            right: this.constants.ADD_GROUP_FIELD_GAP_TWENTY,
                            top: 0,
                            bottom: 0
                        }]
                    }
                }

            }
        });
    },

    _buildSouth: function () {
        var self = this;
        var cancel = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: this.constants.ADD_GROUP_FIELD_BUTTON_HEIGHT
        });

        this.save = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Save"),
            height: this.constants.ADD_GROUP_FIELD_BUTTON_HEIGHT,
            disabled: true,
            tipType: "warning",
            title: BI.i18nText("BI-Need_Add_Group_Column")
        });


        this.save.on(BI.Button.EVENT_CHANGE, function () {
            var groupsData = self.model.getNewGroupsData();
            var new_groups = [];
            BI.each(groupsData, function (id, groupData) {
                var groupObj = {};
                groupObj.table_infor = groupData.table_infor;
                groupObj.group = groupData.group;
                new_groups.push(groupObj);
            });

            var data = {
                connection_name: BICst.CONNECTION.ETL_CONNECTION,
                etl_type: "new_group",
                etl_value: {
                    new_groups: new_groups
                },
                tables: self.model.getAllTables(),
            };
            self.fireEvent(BI.AddGroupField.EVENT_SAVE, data);
        });

        cancel.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.AddGroupField.EVENT_CANCEL);
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "bi-conf-etl-south",
            items: {
                left: [cancel],
                right: [this.save]
            },
            lhgap: this.constants.ADD_GROUP_FIELD_BUTTON_GAP,
            rhgap: this.constants.ADD_GROUP_FIELD_BUTTON_GAP
        });
    },

    _createTableItems: function (data) {
        var self = this;
        var fields = data.fields, values = data.value;
        var header = [], items = [];
        BI.each(fields, function (i, field) {
            header.push({
                text: field
            });
        });


        var fieldTypes = [];
        BI.each(this.model.getAllFields(), function (i, fs) {
            BI.each(fs, function (j, field) {
                fieldTypes.push(field.field_type);
            });
        });


        BI.each(values, function (i, value) {
            var isDate = fieldTypes[i] === BICst.COLUMN.DATE;
            BI.each(value, function (j, v) {
                if (BI.isNotNull(items[j])) {
                    items[j].push({text: isDate === true ? self._formatDate(v) : v});
                } else {
                    items.push([{text: isDate === true ? self._formatDate(v) : v}]);
                }
            });
        });
        return {
            header: [header],
            items: items
        }
    },

    _populatePreview: function () {
        var self = this;
        this.previewPane.empty();
        var isGenerated = self.model.isCubeGenerated();
        if (BI.isNotNull(isGenerated) && isGenerated === true) {
            var previewButton = BI.createWidget({
                type: "bi.button",
                text: BI.i18nText("BI-Preview"),
                height: this.constants.ADD_GROUP_FIELD_BUTTON_HEIGHT,
                width: this.constants.ADD_GROUP_FIELD_BUTTON_WIDTH
            });
            previewButton.on(BI.Button.EVENT_CHANGE, function () {
                self.previewPane.empty();
                var mask = BI.createWidget({
                    type: "bi.loading_mask",
                    masker: self.element,
                    text: BI.i18nText("BI-Loading")
                });
                var table = self.model.getTableInfo();
                BI.Utils.getPreviewDataByTableAndFields(table, [], function (data) {
                    mask.destroy();
                    var item = self._createTableItems(data);
                    var tableView = BI.createWidget({
                        type: "bi.preview_table",
                        items: item.items,
                        header: item.header
                    });
                    var wrapper = BI.createWidget({
                        type: "bi.vertical",
                        items: [{
                            type: "bi.label",
                            text: table.table_name,
                            cls: "original-table-name",
                            height: self.constants.ADD_GROUP_FIELD_BUTTON_HEIGHT
                        }, {
                            el: {
                                type: "bi.vertical",
                                items: [tableView]
                            }
                        }],
                        hgap: self.constants.ADD_GROUP_FIELD_GAP_TEN
                    });
                    self.previewPane.addItem(wrapper);
                })
            });
            self.previewPane.addItem(previewButton);
        } else {
            this.previewPane.addItem({
                type: "bi.label",
                cls: "bi-etl-add-group-field-label",
                text: BI.i18nText("BI-Generate_Cube_Then_Review"),
                height: this.constants.PREVIEW_TABLE_HEIGHT
            });
        }
    },


    _checkSaveEnable: function () {
        if (this.model.checkSaveEnable()) {
            this.save.setEnable(true);
            this.save.setWarningTitle("");
        } else {
            this.save.setEnable(false);
            this.save.setWarningTitle(BI.i18nText("BI-Need_Add_Group_Column"));
        }
    },


    _formatDate: function (d) {
        if (BI.isNull(d) || !BI.isNumeric(d)) {
            return d || "";
        }
        var date = new Date(BI.parseInt(d));
        return date.print("%Y/%X/%d %H:%M:%S")
    },


    _refreshPane: function () {
        var tableName = this.model.getDefaultTableName();
        this.tableNameLabel.setValue(tableName);
        this._populatePreview();
        var groups = [];
        var newGroupsData = this.model.getNewGroupsData();
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
                    groupValue = groupValue + value + ";";
                } else {
                    groupValue = groupValue + ";" + value + ";";
                }

            });
            if (groupObj.group.ungroup2Other === 1) {
                groupValue = groupValue + groupObj.group.ungroup2OtherName + ":...";
            } else {
                groupValue = groupValue + BI.i18nText("BI-Ungrouped_China") + ":...";
            }
            groupItem.fieldName = groupObj.table_infor.field_name;
            groupItem.formulaString = groupValue;
            groupItem.id = id;
            groups.push(groupItem);
        });
        this.fieldList.populate(groups);
        this._checkSaveEnable();
    },

    populate: function () {
        var self = this;
        this.model.initData(function () {
            self._refreshPane();
        });


    }

});
BI.AddGroupField.EVENT_SAVE = "EVENT_SAVE";
BI.AddGroupField.EVENT_CANCEL = "EVENT_CANCEL";
$.shortcut("bi.add_group_field", BI.AddGroupField);