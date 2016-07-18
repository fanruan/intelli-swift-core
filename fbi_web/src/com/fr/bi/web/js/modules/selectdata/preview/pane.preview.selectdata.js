/**
 * 字段预览界面面板
 *
 * Created by GUY on 2015/10/13.
 * @class BI.DetailSelectDataPreviewPane
 * @extends BI.Pane
 */
BI.DetailSelectDataPreviewPane = BI.inherit(BI.Pane, {

    constant: {
        FIELD_WIDTH: 120,
        FIELD_HEIGHT: 25,
        CHECK_WIDTH: 10,
        CHECK_HEIGHT: 25
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectDataPreviewPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-select-data-preview-pane",
            text: "",
            value: ""
        });
    },

    _init: function () {
        BI.DetailSelectDataPreviewPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tableId = BI.Utils.getTableIdByFieldID(BI.isNotNull(o.value.field_id) ? o.value.field_id : o.value);
        BI.Utils.getPreviewTableDataByTableId(tableId, function (data) {
            self.populate(data);
            self.loaded();
        });
        self.loading();
    },

    populate: function (data) {
        var self = this, o = this.options;
        var value = data.value, fieldNames = data.fields;
        var currentId = BI.isNotNull(o.value.group) ? (o.value.field_id + o.value.group.type) : o.value;
        var tableId = BI.Utils.getTableIdByFieldID(BI.isNotNull(o.value.field_id) ? o.value.field_id : o.value);
        var sortedFieldIds = BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId);
        this.sortedFieldIdsArray = [];
        this.checkBoxes = [];
        this.mapValues = {};
        var fieldItems = [];
        //所有被选中的，对于日期的，可以使用fId+group拼接成id
        var dateGroup = [BICst.GROUP.Y, BICst.GROUP.S, BICst.GROUP.M, BICst.GROUP.W, BICst.GROUP.YMD, BICst.GROUP.YMDHMS];
        BI.each(sortedFieldIds, function (i, fId) {
            if(BI.Utils.getFieldTypeByID(fId) === BICst.COLUMN.COUNTER) {
                return;
            }
            var fieldName = BI.Utils.getOriginalFieldNameByID(fId);
            var index = fieldNames.indexOf(fieldName);
            //日期类型特殊处理
            if (BI.Utils.getFieldTypeByID(fId) === BICst.COLUMN.DATE) {
                var check = {};
                BI.each(dateGroup, function (i, group) {
                    check[group] = BI.createWidget({
                        type: "bi.checkbox",
                        selected: currentId === fId + group
                    });
                    check[group].on(BI.Checkbox.EVENT_CHANGE, function () {
                        self._refreshDataTable();
                    });
                    self.checkBoxes.push(check[group]);
                });

                fieldItems.push({
                    type: "bi.vertical",
                    cls: "field-date-container",
                    items: [{
                        items: [{
                            type: "bi.label",
                            text: fieldName,
                            title: fieldName,
                            width: self.constant.FIELD_WIDTH,
                            height: self.constant.FIELD_HEIGHT,
                            whiteSpace: "nowrap",
                            textAlign: "left"
                        }],
                        type: "bi.left"
                    }, {
                        type: "bi.htape",
                        items: [{
                            el: {
                                type: "bi.vertical_adapt",
                                items: [check[BICst.GROUP.Y]]
                            },
                            width: 20
                        }, {
                            el: {
                                type: "bi.label",
                                text: BI.i18nText("BI-Year_Fen"),
                                title: BI.i18nText("BI-Year_Fen"),
                                height: 30,
                                lgap: 5,
                                whiteSpace: "nowrap",
                                textAlign: "left"
                            }
                        }],
                        height: 30
                    }, {
                        type: "bi.htape",
                        items: [{
                            el: {
                                type: "bi.vertical_adapt",
                                items: [check[BICst.GROUP.S]]
                            },
                            width: 20
                        }, {
                            el: {
                                type: "bi.label",
                                text: BI.i18nText("BI-Quarter"),
                                title: BI.i18nText("BI-Quarter"),
                                height: 30,
                                lgap: 5,
                                whiteSpace: "nowrap",
                                textAlign: "left"
                            }
                        }],
                        height: 30
                    }, {
                        type: "bi.htape",
                        items: [{
                            el: {
                                type: "bi.vertical_adapt",
                                items: [check[BICst.GROUP.M]]
                            },
                            width: 20
                        }, {
                            el: {
                                type: "bi.label",
                                text: BI.i18nText("BI-Multi_Date_Month"),
                                title: BI.i18nText("BI-Multi_Date_Month"),
                                height: 30,
                                lgap: 5,
                                whiteSpace: "nowrap",
                                textAlign: "left"
                            }
                        }],
                        height: 30
                    }, {
                        type: "bi.htape",
                        items: [{
                            el: {
                                type: "bi.vertical_adapt",
                                items: [check[BICst.GROUP.W]]
                            },
                            width: 20
                        }, {
                            el: {
                                type: "bi.label",
                                text: BI.i18nText("BI-Week_XingQi"),
                                title: BI.i18nText("BI-Week_XingQi"),
                                height: 30,
                                lgap: 5,
                                whiteSpace: "nowrap",
                                textAlign: "left"
                            }
                        }],
                        height: 30
                    }, {
                        type: "bi.htape",
                        items: [{
                            el: {
                                type: "bi.vertical_adapt",
                                items: [check[BICst.GROUP.YMD]]
                            },
                            width: 20
                        }, {
                            el: {
                                type: "bi.label",
                                text: BI.i18nText("BI-Date"),
                                title: BI.i18nText("BI-Date"),
                                height: 30,
                                lgap: 5,
                                whiteSpace: "nowrap",
                                textAlign: "left"
                            }
                        }],
                        height: 30
                    }, {
                        type: "bi.htape",
                        items: [{
                            el: {
                                type: "bi.vertical_adapt",
                                items: [check[BICst.GROUP.YMDHMS]]
                            },
                            width: 20
                        }, {
                            el: {
                                type: "bi.label",
                                text: BI.i18nText("BI-Time_ShiKe"),
                                title: BI.i18nText("BI-Time_ShiKe"),
                                height: 30,
                                lgap: 5,
                                whiteSpace: "nowrap",
                                textAlign: "left"
                            }
                        }],
                        height: 30
                    }]
                });
                var fieldValues = value[index];
                //年份
                var year = [], season = [], month = [], week = [], date_ = [], date_hms = [];

                function getSeason(month) {
                    return Math.floor(month / 3);
                }

                BI.each(fieldValues, function (i, v) {
                    var d = new Date(v);
                    year.push(d.getFullYear());
                    season.push(Date._QN[getSeason(d.getMonth()) + 1]);
                    month.push(Date._MN[d.getMonth()]);
                    week.push(Date._DN[d.getWeekNumber()]);
                    date_.push(d.print("%Y-%X-%d"));
                    date_hms.push(d.print("%Y-%X-%d %H:%M:%S"))

                });
                self.mapValues[fId + BICst.GROUP.Y] = year;
                self.mapValues[fId + BICst.GROUP.S] = season;
                self.mapValues[fId + BICst.GROUP.M] = month;
                self.mapValues[fId + BICst.GROUP.W] = week;
                self.mapValues[fId + BICst.GROUP.YMD] = date_;
                self.mapValues[fId + BICst.GROUP.YMDHMS] = date_hms;
                self.sortedFieldIdsArray.pushArray([{
                    field_id: fId, group: BICst.GROUP.Y
                }, {
                    field_id: fId, group: BICst.GROUP.S
                }, {
                    field_id: fId, group: BICst.GROUP.M
                }, {
                    field_id: fId, group: BICst.GROUP.W
                }, {
                    field_id: fId, group: BICst.GROUP.YMD
                }, {
                    field_id: fId, group: BICst.GROUP.YMDHMS
                }]);
            } else {
                var check = BI.createWidget({
                    type: "bi.checkbox",
                    selected: currentId === fId
                });
                check.on(BI.Checkbox.EVENT_CHANGE, function () {
                    self._refreshDataTable();
                });
                self.checkBoxes.push(check);
                fieldItems.push({
                    type: "bi.htape",
                    items: [{
                        el: {
                            type: "bi.vertical_adapt",
                            items: [check]
                        },
                        width: 20
                    }, {
                        el: {
                            type: "bi.label",
                            text: fieldName,
                            title: fieldName,
                            height: 30,
                            lgap: 5,
                            whiteSpace: "nowrap",
                            textAlign: "left"
                        }
                    }],
                    height: 30
                });
                self.mapValues[fId] = value[index];
                self.sortedFieldIdsArray.push(fId);
            }
        });
        this.dataTable = BI.createWidget({
            type: "bi.preview_table",
            cls: "table-data-container"
        });
        this._refreshDataTable();
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.vertical",
                    items: fieldItems,
                    cls: "field-container",
                    hgap: 10
                },
                width: 160,
                left: 0,
                top: 0,
                bottom: 0
            }, {
                el: this.dataTable,
                top: 0,
                bottom: 0,
                left: 175,
                right: 0
            }]
        })
    },

    //根据选中的checkbox展示表格
    _refreshDataTable: function () {
        var self = this;
        var header = [], items = [], dataArray = [], selectedFields = [];
        BI.each(this.checkBoxes, function (i, check) {
            if (check.isSelected()) {
                selectedFields.push(self.sortedFieldIdsArray[i]);
            }
        });
        BI.each(selectedFields, function (i, value) {
            if (BI.isNotNull(value.group)) {
                var fieldName = BI.Utils.getFieldNameByID(value.field_id);
                dataArray.push(self.mapValues[value.field_id + value.group]);
                switch (value.group) {
                    case BICst.GROUP.Y:
                        fieldName = BI.i18nText("BI-Year_Fen") + "(" + fieldName + ")";
                        break;
                    case BICst.GROUP.S:
                        fieldName = BI.i18nText("BI-Quarter") + "(" + fieldName + ")";
                        break;
                    case BICst.GROUP.M:
                        fieldName = BI.i18nText("BI-Multi_Date_Month") + "(" + fieldName + ")";
                        break;
                    case BICst.GROUP.W:
                        fieldName = BI.i18nText("BI-Week_XingQi") + "(" + fieldName + ")";
                        break;
                    case BICst.GROUP.YMD:
                        fieldName = BI.i18nText("BI-Date") + "(" + fieldName + ")";
                        break;
                    case BICst.GROUP.YMDHMS:
                        fieldName = BI.i18nText("BI-Time_ShiKe") + "(" + fieldName + ")";
                        break;
                }
                header.push({text: fieldName});
            } else {
                dataArray.push(self.mapValues[value]);
                header.push({text: BI.Utils.getFieldNameByID(value)});
            }
        });
        BI.each(dataArray, function (i, value) {
            BI.each(value, function (j, v) {
                if (BI.isNotNull(items[j])) {
                    items[j].push({
                        text: v,
                        height: 25
                    });
                } else {
                    items.push([{
                        text: v,
                        height: 25
                    }]);
                }
            });
        });
        this.dataTable.populate(items, [header]);
    }
});
BI.DetailSelectDataPreviewPane.EVENT_CHANGE = "DetailSelectDataPreviewPane.EVENT_CHANGE";
$.shortcut('bi.detail_select_data_preview_pane', BI.DetailSelectDataPreviewPane);