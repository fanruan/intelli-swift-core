/**
 * 统计组件上的下拉框
 *
 * Created by GUY on 2015/10/14.
 * @class BI.WidgetCombo
 * @extends BI.Widget
 */
BI.WidgetCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        var conf = BI.WidgetCombo.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-widget-combo"
        })
    },

    _init: function () {
        BI.WidgetCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            el: {
                type: "bi.icon_button",
                cls: "widget-combo-pull-down-font pull-down-trigger",
                width: 20,
                height: 20
            },
            element: this.element,
            height: 20,
            width: 20
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.WidgetCombo.EVENT_CHANGE, v);
        });
        this.combo.on(BI.DownListCombo.EVENT_SON_VALUE_CHANGE, function (v) {
            self.fireEvent(BI.WidgetCombo.EVENT_CHANGE, v);
        });
        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW, function () {
            this.populate(self._rebuildItems());
            self.fireEvent(BI.WidgetCombo.EVENT_BEFORE_POPUPVIEW);
        });
    },

    _rebuildItems: function () {
        var wId = this.options.wId;
        var isEdit = Data.SharingPool.get("edit");
        if(!isEdit) {
            return this._createShowComboItems();
        }
        switch (BI.Utils.getWidgetTypeByID(wId)) {
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.FUNNEL:
                return this._createWidgetComboItems();
            case BICst.WIDGET.DETAIL:
                return this._createDetailWidgetComboItems();

            case BICst.WIDGET.DATE:
            case BICst.WIDGET.YEAR :
            case BICst.WIDGET.QUARTER :
            case BICst.WIDGET.MONTH:
            case BICst.WIDGET.YMD :
                return BICst.TIME_CONTROL_SETCOMBO_ITEMS;

            case BICst.WIDGET.STRING:
                return this._createStringTreeComboItems();
            case BICst.WIDGET.TREE :
                return this._createStringTreeComboItems();
            case BICst.WIDGET.NUMBER :
                return BICst.NUMBER_CONTROL_SETCOMBO_ITEMS;

            case BICst.WIDGET.GENERAL_QUERY:
                return BICst.GENERNAL_QUERY_CONTROL_SETCOMBO_ITEMS;
        }
    },

    _createWidgetComboItems: function () {
        var wId = this.options.wId;
        var isShowName = BI.Utils.getWSShowNameByID(wId);
        var namePos = BI.Utils.getWSNamePosByID(wId);
        return [
            [{
                text: BI.i18nText("BI-Detailed_Setting"),
                value: BICst.DASHBOARD_WIDGET_EXPAND,
                cls: "widget-combo-expand-font"
            }],
            [{
                text: BI.i18nText("BI-Link_To_Dots"),
                value: BICst.DASHBOARD_WIDGET_LINKAGE,
                cls: "widget-combo-linkage-font"
            }],
            [{
                text: BI.i18nText("BI-Show_Title"),
                value: BICst.DASHBOARD_WIDGET_SHOW_NAME,
                cls: isShowName ? "widget-combo-show-title-font" : ""
            }, {
                text: BI.i18nText("BI-Rename"),
                value: BICst.DASHBOARD_WIDGET_RENAME,
                cls: "widget-combo-rename-edit-font",
                disabled: !isShowName,
                warningTitle: BI.i18nText("BI-Show_Widget_Name_First")
            }, {
                el: {
                    text: BI.i18nText("BI-Title_Position"),
                    value: BICst.DASHBOARD_WIDGET_NAME_POS,
                    iconCls1: namePos === BICst.DASHBOARD_WIDGET_NAME_POS_LEFT ?
                        "widget-combo-title-left-font" : "widget-combo-title-center-font",
                    warningTitle: BI.i18nText("BI-Show_Widget_Name_First"),
                    disabled: !isShowName
                },
                disabled: !isShowName,
                children: [{
                    text: BI.i18nText("BI-Position_Left"),
                    value: BICst.DASHBOARD_WIDGET_NAME_POS_LEFT,
                    selected: namePos === BICst.DASHBOARD_WIDGET_NAME_POS_LEFT,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Position_Center"),
                    value: BICst.DASHBOARD_WIDGET_NAME_POS_CENTER,
                    selected: namePos === BICst.DASHBOARD_WIDGET_NAME_POS_CENTER,
                    cls: "dot-e-font"
                }]
            }],
            [{
                text: BI.i18nText("BI-Show_Filters"),
                cls: "widget-combo-show-filter-font",
                value: BICst.DASHBOARD_WIDGET_FILTER
            }],
            [{
                text: BI.i18nText("BI-Export_As_Excel"),
                cls: "widget-combo-export-excel-font",
                value: BICst.DASHBOARD_WIDGET_EXCEL
            }],
            [{
                text: BI.i18nText("BI-Copy"),
                cls: "widget-combo-copy",
                value: BICst.DASHBOARD_WIDGET_COPY
            }],
            [{
                text: BI.i18nText("BI-Delete"),
                cls: "widget-combo-delete",
                value: BICst.DASHBOARD_WIDGET_DELETE
            }]
        ]
    },

    _createDetailWidgetComboItems: function () {
        var wId = this.options.wId;
        var isShowName = BI.Utils.getWSShowNameByID(wId);
        var namePos = BI.Utils.getWSNamePosByID(wId);
        return [
            [{
                text: BI.i18nText("BI-Detailed_Setting"),
                value: BICst.DASHBOARD_WIDGET_EXPAND,
                cls: "widget-combo-expand-font"
            }],
            [{
                text: BI.i18nText("BI-Show_Title"),
                value: BICst.DASHBOARD_WIDGET_SHOW_NAME,
                cls: isShowName ? "widget-combo-show-title-font" : ""
            }, {
                text: BI.i18nText("BI-Rename"),
                value: BICst.DASHBOARD_WIDGET_RENAME,
                cls: "widget-combo-rename-edit-font",
                disabled: !isShowName,
                warningTitle: BI.i18nText("BI-Show_Widget_Name_First")
            }, {
                el: {
                    text: BI.i18nText("BI-Title_Position"),
                    value: BICst.DASHBOARD_WIDGET_NAME_POS,
                    iconCls1: namePos === BICst.DASHBOARD_WIDGET_NAME_POS_LEFT ?
                        "widget-combo-title-left-font" : "widget-combo-title-center-font",
                    warningTitle: BI.i18nText("BI-Show_Widget_Name_First"),
                    disabled: !isShowName
                },
                disabled: !isShowName,
                warningTitle: BI.i18nText("BI-Show_Widget_Name_First"),
                children: [{
                    text: BI.i18nText("BI-Position_Left"),
                    value: BICst.DASHBOARD_WIDGET_NAME_POS_LEFT,
                    selected: namePos === BICst.DASHBOARD_WIDGET_NAME_POS_LEFT,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Position_Center"),
                    value: BICst.DASHBOARD_WIDGET_NAME_POS_CENTER,
                    selected: namePos === BICst.DASHBOARD_WIDGET_NAME_POS_CENTER,
                    cls: "dot-e-font"
                }]
            }],
            [{
                text: BI.i18nText("BI-Show_Filters"),
                cls: "widget-combo-show-filter-font",
                value: BICst.DASHBOARD_WIDGET_FILTER
            }],
            [{
                text: BI.i18nText("BI-Export_As_Excel"),
                cls: "widget-combo-export-excel-font",
                value: BICst.DASHBOARD_WIDGET_EXCEL
            }],
            [{
                text: BI.i18nText("BI-Copy"),
                cls: "widget-combo-copy",
                value: BICst.DASHBOARD_WIDGET_COPY
            }],
            [{
                text: BI.i18nText("BI-Delete"),
                cls: "widget-combo-delete",
                value: BICst.DASHBOARD_WIDGET_DELETE
            }]
        ]
    },

    _createStringTreeComboItems: function () {
        var wId = this.options.wId;
        var sort = {}, dims = BI.Utils.getAllDimDimensionIDs(wId);
        if (dims.length > 0) {
            sort = BI.Utils.getDimensionSortByID(dims[0]);
        }
        return [
            [{
                value: BICst.DASHBOARD_WIDGET_EXPAND,
                text: BI.i18nText("BI-Detailed_Setting"),
                cls: "widget-combo-expand-font"
            }],
            [{
                value: BICst.DASHBOARD_CONTROL_RANG_ASC,
                text: BI.i18nText("BI-Ascend"),
                selected: sort.type === BICst.SORT.ASC,
                cls: "dot-e-font"
            }, {
                value: BICst.DASHBOARD_CONTROL_RANG_DESC,
                text: BI.i18nText("BI-Descend"),
                selected: sort.type === BICst.SORT.DESC,
                cls: "dot-e-font"
            }],
            [{
                value: BICst.DASHBOARD_CONTROL_CLEAR,
                text: BI.i18nText("BI-Clear_Selected_Value"),
                cls: "widget-combo-clear-font"
            }],
            [{
                value: BICst.DASHBOARD_WIDGET_RENAME,
                text: BI.i18nText("BI-Rename"),
                cls: "widget-combo-rename-edit-font"
            }],
            [{
                value: BICst.DASHBOARD_WIDGET_COPY,
                text: BI.i18nText("BI-Copy"),
                cls: "widget-combo-copy"
            }],
            [{
                value: BICst.DASHBOARD_WIDGET_DELETE,
                text: BI.i18nText("BI-Delete_Control"),
                cls: "widget-combo-delete"
            }]
        ]
    },

    _createShowComboItems: function () {
        var wId = this.options.wId;
        var children = [{
            value: BICst.WIDGET.TABLE,
            text: BI.i18nText("BI-Group_Table"),
            cls: "dot-e-font"
        }, {
            value: BICst.WIDGET.TABLE,
            text: BI.i18nText("BI-Cross_Table"),
            cls: "dot-e-font"
        }, {
            value: BICst.WIDGET.TABLE,
            text: BI.i18nText("BI-Complex_Table"),
            cls: "dot-e-font"
        }];
        var wType = BI.Utils.getWidgetTypeByID(wId);
        if (wType !== BICst.WIDGET.TABLE &&
            wType !== BICst.WIDGET.CROSS_TABLE &&
            wType !== BICst.WIDGET.COMPLEX_TABLE) {
            children.push({
                value: wType,
                text: BI.i18nText(),
                cls: "dot-e-font"
            })
        }
        return [
            [{
                text: BI.i18nText("BI-Show_Filters"),
                cls: "widget-combo-show-filter-font",
                value: BICst.DASHBOARD_WIDGET_FILTER
            }],
            [{
                el: {
                    text: BI.i18nText("BI-Chart_Type"),
                    iconCls1: "widget-combo-show-filter-font",
                    value: BICst.DASHBOARD_WIDGET_SWITCH_CHART
                },
                children: children
            }]
        ];
    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }
});
BI.WidgetCombo.EVENT_BEFORE_POPUPVIEW = "EVENT_BEFORE_POPUPVIEW";
BI.WidgetCombo.EVENT_CHANGE = "WidgetCombo.EVENT_CHANGE";
$.shortcut('bi.widget_combo', BI.WidgetCombo);
