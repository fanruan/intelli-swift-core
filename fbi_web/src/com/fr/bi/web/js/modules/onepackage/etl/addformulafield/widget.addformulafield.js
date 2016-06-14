/**
 * Created by roy on 16/3/17.
 */
BI.AddFormulaField = BI.inherit(BI.Widget, {
    constants: {
        ADD_FIELD_NORTH_HEIGHT: 50,
        ADD_FIELD_SOUTH_HEIGHT: 60,
        ADD_FIELD_BUTTON_GAP: 20,
        ADD_FIELD_BUTTON_HEIGHT: 30,
        ADD_FIELD_PERCENT_OF_LEFT_WIDTH: 0.4,
        ADD_FIELD_PERCENT_OF_RIGHT_WIDTH: 0.6,
        ADD_FIELD_EDITOR_HEIGHT: 26,
        ADD_FIELD_REGION_HEIGHT: 180,
        ADD_FIELD_BUTTON_WIDTH: 110,
        ADD_FIELD_WEST_WIDTH: 530,
        ADD_FIELD_GAP_TEN: 10,
        ADD_FIELD_GAP_FIVE: 5,
        ADD_FIELD_GAP_TWENTY: 20,
        ADD_FIELD_TYPE_NUMBER: 1,
        ADD_FIELD_TYPE_STRING: 0,
        ADD_FIELD_TYPE_DATE: 2,
        PREVIEW_TABLE_HEIGHT: 160
    },
    _defaultConfig: function () {
        return BI.extend(BI.AddFormulaField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-add-formula-field"
        })
    },

    _init: function () {
        var o = this.options;
        BI.AddFormulaField.superclass._init.apply(this, arguments);
        this.model = new BI.AddFormulaFieldModel({
            info: o.info
        });

        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                center: this._buildCenter(),
                south: {
                    el: this._buildSouth(),
                    height: this.constants.ADD_FIELD_SOUTH_HEIGHT
                }
            }
        })
    },

    _buildSouth: function () {
        var self = this;
        var cancel = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: this.constants.ADD_FIELD_BUTTON_HEIGHT
        });

        this.save = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Save"),
            height: this.constants.ADD_FIELD_BUTTON_HEIGHT,
            disabled: true,
            warningTitle: BI.i18nText("BI-Need_Add_Formula_Column")

        });

        this.save.on(BI.Button.EVENT_CHANGE, function () {
            var formulaData = self.model.getFormulaData();
            var formulas = [];
            BI.each(formulaData, function (id, item) {
                formulas.push(item.formula)
            });
            var tables = self.model.getAllTables();
            var data = {
                connection_name: BICst.CONNECTION.ETL_CONNECTION,
                etl_type: "formula",
                etl_value: {
                    formulas: formulas
                },
                tables: tables
            };
            self.fireEvent(BI.AddFormulaField.EVENT_SAVE, data);
        });

        cancel.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.AddFormulaField.EVENT_CANCEL);
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "bi-conf-etl-south",
            items: {
                left: [cancel],
                right: [self.save]
            },
            lhgap: this.constants.ADD_FIELD_BUTTON_GAP,
            rhgap: this.constants.ADD_FIELD_BUTTON_GAP
        });
    },

    _buildCenter: function () {
        var self = this;
        this.tableNameLabel = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            hgap: this.constants.ADD_FIELD_GAP_FIVE,
            height: this.constants.ADD_FIELD_EDITOR_HEIGHT
        });


        var tableLabel = BI.createWidget({
            type: "bi.label",
            cls: "bi-etl-add-field-label",
            textAlign: "left",
            textHeight: this.constants.ADD_FIELD_EDITOR_HEIGHT,
            text: BI.i18nText("BI-Table_Name") + ":",
            height: this.constants.ADD_FIELD_EDITOR_HEIGHT
        });

        var formulaLabel = BI.createWidget({
            type: "bi.label",
            cls: "north-left-label",
            text: BI.i18nText("BI-Formula_Column_Super"),
            textAlign: "left",
            textHeight: this.constants.ADD_FIELD_NORTH_HEIGHT
        });

        this.formulaList = BI.createWidget({
            type: "bi.formula_list"
        });

        this.formulaList.on(BI.FormulaList.EVENT_CLICK_SET, function (id) {
            var popup = BI.createWidget({
                type: "bi.add_formula_field_popover",
                info: {
                    id: id,
                    fields: self.model.getFields(),
                    formulaData: self.model.getFormulaData(),
                    reopen: true
                }
            });
            BI.Popovers.remove("addFormulaField");
            BI.Popovers.create("addFormulaField", popup).open("addFormulaField");
            popup.populate();
            popup.on(BI.AddFormulaFieldPopover.EVENT_SAVE, function (data) {
                var id = data.id;
                var formulaData = self.model.getFormulaData();
                formulaData[id] = data;
                self.model.setFormulaData(formulaData);
                self.model.setGenerated(false);
                self._refreshPane();
            });
        });

        this.formulaList.on(BI.FormulaList.EVENT_CLICK_DELETE, function (id) {
            var formulaData = self.model.getFormulaData();
            delete formulaData[id];
            self.model.setFormulaData(formulaData);
            self.model.setGenerated(false);
            self._refreshPane();
        });

        var addButton = BI.createWidget({
            type: "bi.button",
            value: "+" + BI.i18nText("BI-Add_Formula_Column"),
            height: this.constants.ADD_FIELD_EDITOR_HEIGHT
        });

        addButton.on(BI.Button.EVENT_CHANGE, function () {
            var id = BI.UUID();
            var popup = BI.createWidget({
                type: "bi.add_formula_field_popover",
                info: {
                    id: id,
                    fields: self.model.getFields(),
                    formulaData: self.model.getFormulaData()
                }
            });
            BI.Popovers.remove("addFormulaField");
            BI.Popovers.create("addFormulaField", popup).open("addFormulaField");
            popup.populate();
            popup.on(BI.AddFormulaFieldPopover.EVENT_SAVE, function (data) {
                var id = data.id;
                var formulaData = self.model.getFormulaData();
                formulaData[id] = data;
                self.model.setFormulaData(formulaData);
                self.model.setGenerated(false);
                self._refreshPane();
            });

        });

        this.formulaPane = BI.createWidget({
            type: "BI.vertical",
            cls: "bi-formula-pane",
            items: [
                {
                    el: {
                        type: "bi.left_right_vertical_adapt",
                        cls: "bi-elt-add-field-label-and-add-button",
                        items: {
                            left: [
                                {
                                    type: "bi.label",
                                    cls: "bi-etl-add-field-label",
                                    value: BI.i18nText("BI-Formula_Column_Super") + ":",
                                    textAlign: "left",
                                    textHeight: this.constants.ADD_FIELD_EDITOR_HEIGHT
                                }
                            ],
                            right: [
                                addButton
                            ]
                        },
                        height: 40
                    }
                },
                {
                    el: this.formulaList
                }
            ],
            vgap: 5
        });


        this.previewPane = BI.createWidget({
            type: "bi.horizontal_adapt",
            scrollable: true
        });

        return BI.createWidget({
            type: "bi.border",
            cls: "bi-etl-add-field-border",
            items: {
                north: {
                    el: {
                        type: "bi.absolute",
                        items: [{
                            el: formulaLabel,
                            top: 0,
                            bottom: 0,
                            left: this.constants.ADD_FIELD_BUTTON_GAP,
                            right: 0
                        }]
                    },
                    height: this.constants.ADD_FIELD_NORTH_HEIGHT
                },
                west: {
                    el: {
                        type: "bi.vtape",
                        items: [
                            {
                                type: "bi.absolute",
                                cls: "bi-etl-add-field-merge",
                                items: [
                                    {
                                        el: {
                                            type: "bi.htape",
                                            items: [
                                                {
                                                    el: tableLabel,
                                                    width: this.constants.ADD_FIELD_NORTH_HEIGHT
                                                }, this.tableNameLabel
                                            ]
                                        },
                                        left: this.constants.ADD_FIELD_GAP_TEN,
                                        right: this.constants.ADD_FIELD_GAP_TEN,
                                        top: this.constants.ADD_FIELD_GAP_TEN,
                                        bottom: this.constants.ADD_FIELD_GAP_TEN
                                    }
                                ],
                                height: this.constants.ADD_FIELD_NORTH_HEIGHT
                            }, {
                                el: {
                                    type: "bi.layout"
                                },
                                height: 10
                            }, {
                                type: "bi.absolute",
                                cls: "bi-etl-add-field-merge",
                                items: [
                                    {
                                        el: this.formulaPane,
                                        left: this.constants.ADD_FIELD_GAP_TWENTY,
                                        right: this.constants.ADD_FIELD_GAP_TWENTY,
                                        top: 0,
                                        bottom: this.constants.ADD_FIELD_GAP_TWENTY
                                    }
                                ]
                            }
                        ],
                        hgap: this.constants.ADD_FIELD_GAP_TWENTY
                    },
                    width: this.constants.ADD_FIELD_WEST_WIDTH
                },
                center: {
                    el: {
                        type: "bi.absolute",
                        items: [{
                            el: {
                                type: "bi.vtape",
                                cls: "bi-etl-add-field-merge",
                                items: [
                                    {
                                        el: {
                                            type: "bi.absolute",
                                            cls: "bi-etl-add-formula-field-label",
                                            items: [{
                                                el: {
                                                    type: "bi.horizontal",
                                                    items: [{
                                                        type: "bi.label",
                                                        cls: "bi-etl-add-field-label",
                                                        value: BI.i18nText("BI-Table_Data"),
                                                        height: this.constants.ADD_FIELD_NORTH_HEIGHT
                                                    }],
                                                    hgap: this.constants.ADD_FIELD_GAP_TEN
                                                }
                                            }],
                                            left: this.constants.ADD_FIELD_GAP_TEN,
                                            right: this.constants.ADD_FIELD_GAP_TEN,
                                            top: 0,
                                            bottom: 0
                                        },
                                        height: this.constants.ADD_FIELD_NORTH_HEIGHT
                                    }, {
                                        el: this.previewPane
                                    }
                                ],
                                bgap: this.constants.ADD_FIELD_GAP_TEN
                            },
                            left: this.constants.ADD_FIELD_GAP_TWENTY,
                            right: this.constants.ADD_FIELD_GAP_TWENTY,
                            top: 0,
                            bottom: 0
                        }]
                    }
                }
            }
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
                height: this.constants.ADD_FIELD_BUTTON_HEIGHT,
                width: this.constants.ADD_FIELD_BUTTON_WIDTH
            });
            previewButton.on(BI.Button.EVENT_CHANGE, function () {
                self.previewPane.empty();
                var table = self.model.getTableInfo();
                var mask = BI.createWidget({
                    type: "bi.loading_mask",
                    masker: self.element,
                    text: BI.i18nText("BI-Loading")
                });
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
                            height: self.constants.ADD_FIELD_BUTTON_HEIGHT
                        }, {
                            el: {
                                type: "bi.vertical",
                                items: [tableView]
                            }
                        }],
                        hgap: self.constants.ADD_FIELD_GAP_TEN
                    });
                    self.previewPane.addItem(wrapper);
                })
            });
            self.previewPane.addItem(previewButton);
        } else {
            this.previewPane.addItem({
                type: "bi.label",
                cls: "north-left-label",
                text: BI.i18nText("BI-Generate_Cube_Then_Review"),
                height: this.constants.PREVIEW_TABLE_HEIGHT
            });
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
        var self = this;
        var formulaItems = [];
        var table_name = self.model.getDefaultTableName();
        var formulaData = self.model.getFormulaData();
        BI.each(formulaData, function (id, formulaItem) {
            var item = {};
            item.formulaString = BI.Func.getFormulaStringFromFormulaValue(formulaItem.formula.expression);
            item.formulaValue = formulaItem.formula.expression;
            item.fieldName = formulaItem.formula.field_name;
            item.id = id;
            formulaItems.push(item);
        });
        self.tableNameLabel.setValue(table_name);
        self.formulaList.populate(formulaItems);
        self._populatePreview();
        if (this.model.checkSaveEnable()) {
            this.save.setEnable(true);
            this.save.setWarningTitle("");
        } else {
            this.save.setEnable(false);
            this.save.setWarningTitle(BI.i18nText("BI-Need_Add_Formula_Column"));
        }
    },

    populate: function () {
        var self = this;
        this.model.initData(function () {
            self._refreshPane();
        });
    }
});
BI.AddFormulaField.EVENT_SAVE = "EVENT_SAVE";
BI.AddFormulaField.EVENT_CANCEL = "EVENT_CANCEL";
$.shortcut("bi.add_formula_field", BI.AddFormulaField);