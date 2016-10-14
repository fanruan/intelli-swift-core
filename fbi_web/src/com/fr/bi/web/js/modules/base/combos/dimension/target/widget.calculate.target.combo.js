/**
 * Created by roy on 16/4/6.
 */
BI.CalculateTargetCombo = BI.inherit(BI.AbstractDimensionTargetCombo, {

    constants: {
        CHART_TYPE_POSITION: 2,
        CordonPos: 1
    },
    defaultItems: function () {
        return [
            [{
                text: BI.i18nText("BI-Modify_Cal_Target"),
                value: BICst.CALCULATE_TARGET_COMBO.UPDATE_TARGET
            }],
            [{
                text: BI.i18nText("BI-Style_Setting"),
                value: BICst.CALCULATE_TARGET_COMBO.FORM_SETTING,
                warningTitle: BI.i18nText("BI-Unmodified_in_Current_Mode"),
                cls: "style-set-h-font"
            }],
            [{
                el: {
                    text: BI.i18nText("BI-Chart_Type"),
                    value: BICst.TARGET_COMBO.CHART_TYPE,
                    iconCls1: "",
                    disabled: true
                },
                children: [{
                    text: BI.i18nText("BI-Column_Chart"),
                    value: BICst.WIDGET.AXIS,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Stacked_Chart"),
                    value: BICst.WIDGET.ACCUMULATE_AXIS,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Line_Chart"),
                    value: BICst.WIDGET.LINE,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Area_Chart"),
                    value: BICst.WIDGET.AREA,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Accumulate_Area"),
                    value: BICst.WIDGET.ACCUMULATE_AREA,
                    cls: "dot-e-font"
                }]
            }],
            //[{
            //    text: BI.i18nText("BI-Display"),
            //    value: BICst.CALCULATE_TARGET_COMBO.DISPLAY,
            //    cls: "dot-ha-font"
            //}, {
            //    text: BI.i18nText("BI-Hidden"),
            //    value: BICst.CALCULATE_TARGET_COMBO.HIDDEN,
            //    cls: "dot-ha-font"
            //}],
            [{
                text: BI.i18nText("BI-Show_Field"),
                value: BICst.TARGET_COMBO.SHOW_FIELD,
                cls: BI.Utils.isDimensionUsable(this.options.dId) ? "widget-combo-show-title-font" : ""
            }],
            [{
                text: BI.i18nText("BI-Rename"),
                value: BICst.TARGET_COMBO.RENAME
            }],
            [{
                text: BI.i18nText("BI-Copy"),
                value: BICst.CALCULATE_TARGET_COMBO.COPY,
                cls: "copy-h-font"
            }],
            [{
                text: BI.i18nText("BI-Remove"),
                value: BICst.CALCULATE_TARGET_COMBO.DELETE,
                cls: "delete-h-font"
            }],
            [{
                text: BI.i18nText("BI-This_Target_From") + ": ",
                tipType: "success",
                value: BICst.TARGET_COMBO.INFO,
                cls: "dimension-from-font",
                disabled: true
            }]
        ];
    },

    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-calculate-target-combo"
        })
    },

    _init: function () {
        BI.CalculateTargetCombo.superclass._init.apply(this, arguments);
    },

    _rebuildItems: function () {
        var o = this.options;
        var item = this.defaultItems();
        var wId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        var regionType = BI.Utils.getRegionTypeByDimensionID(o.dId);
        var wType = BI.Utils.getWidgetTypeByID(wId);
        var dataLable = BI.Utils.getWSShowDataLabelByID(wId);
        var minimalist = BI.Utils.getWSMinimalistByID(wId);
        var e = BI.Utils.getExpressionByDimensionID(o.dId);
        var bigDataMode = BI.Utils.getWSBigDataModelByID(wId);
        var ids = e.ids;
        BI.each(ids , function (idx , id){
            //这边的dimensionId不一定能拿到fieldId,计算指标没有fieldId
            var fromText = "";
            if(BI.Utils.isCalculateTargetByDimensionID(id)){
                fromText = BI.Utils.getDimensionNameByID(id);
            }else{
                var fieldID = BI.Utils.getFieldIDByDimensionID(id);
                var fieldName = BI.Utils.getFieldNameByID(fieldID);
                var tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fieldID));
                fromText = tableName + "." + fieldName;
            }
            item[item.length - 1].push({
                text: fromText,
                tipType: "success",
                value: BICst.TARGET_COMBO.INFO,
                disabled: true
            })
        });

        switch (wType) {
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
                item[this.constants.CordonPos][0].cls = "";
                item[this.constants.CordonPos][0] = {
                    el: item[this.constants.CordonPos][0],
                    children: [{
                        text: BI.i18nText("BI-Cordon") + "(" + BI.i18nText("BI-Horizontal") + ")",
                        value: BICst.TARGET_COMBO.CORDON
                    },{
                        text: BI.i18nText("BI-Data_Label"),
                        value: BICst.TARGET_COMBO.DATA_LABEL,
                        warningTitle: BI.i18nText("BI-Data_Label_Donnot_Show")
                    },{
                        text: BI.i18nText("BI-Data_Image"),
                        value: BICst.TARGET_COMBO.DATA_IMAGE,
                        warningTitle: BI.i18nText("BI-Data_Image_Donnot_Show")
                    }]
                };
                if(minimalist) {
                    item[this.constants.CordonPos][0].disabled = true
                }
                if(!dataLable){
                    item[this.constants.CordonPos][0].children[1].disabled = true
                }
                BI.removeAt(item, this.constants.CHART_TYPE_POSITION);
                break;
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                item[this.constants.CordonPos][0].cls = "";
                item[this.constants.CordonPos][0] = {
                    el: item[this.constants.CordonPos][0],
                    children: [{
                        text: BI.i18nText("BI-Cordon") + "(" + BI.i18nText("BI-Horizontal") + ")",
                        value: BICst.TARGET_COMBO.CORDON
                    },{
                        text: BI.i18nText("BI-Data_Label"),
                        value: BICst.TARGET_COMBO.DATA_LABEL,
                        warningTitle: BI.i18nText("BI-Data_Label_Donnot_Show")
                    }]
                };
                if(minimalist) {
                    item[this.constants.CordonPos][0].disabled = true
                }
                if(!dataLable){
                    item[this.constants.CordonPos][0].children[1].disabled = true
                }
                BI.removeAt(item, this.constants.CHART_TYPE_POSITION);
                break;
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                item[this.constants.CordonPos][0].cls = "";
                item[this.constants.CordonPos][0] = {
                    el: item[this.constants.CordonPos][0],
                    children: [{
                        text: BI.i18nText("BI-Cordon") + "(" + BI.i18nText("BI-Horizontal") + ")",
                        value: BICst.TARGET_COMBO.CORDON
                    },{
                        text: BI.i18nText("BI-Data_Label"),
                        value: BICst.TARGET_COMBO.DATA_LABEL,
                        warningTitle: BI.i18nText("BI-Data_Label_Donnot_Show")
                    },{
                        text: BI.i18nText("BI-Data_Image"),
                        value: BICst.TARGET_COMBO.DATA_IMAGE,
                        warningTitle: BI.i18nText("BI-Data_Image_Donnot_Show")
                    }]
                };
                if(minimalist) {
                    item[this.constants.CordonPos][0].disabled = true
                }
                if(!dataLable){
                    item[this.constants.CordonPos][0].children[1].disabled = true
                }
                item[this.constants.CHART_TYPE_POSITION][0].el.disabled = false;
                break;
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.BUBBLE:
                var text = BI.i18nText("BI-Horizontal");
                switch (regionType) {
                    case BICst.REGION.TARGET1:
                        text = BI.i18nText("BI-Horizontal");
                        break;
                    case BICst.REGION.TARGET2:
                        text = BI.i18nText("BI-Vertical");
                        break;
                    case BICst.REGION.TARGET3:
                        item[this.constants.CordonPos][0].cls = "";
                        item[this.constants.CordonPos][0] = {
                            el: item[this.constants.CordonPos][0],
                            children: [{
                                text: BI.i18nText("BI-Data_Label"),
                                value: BICst.TARGET_COMBO.DATA_LABEL_OTHER,
                                warningTitle: BI.i18nText("BI-Data_Label_Donnot_Show")
                            }]
                        };
                        if(!dataLable){
                            item[this.constants.CordonPos][0].children.disabled = true
                        }
                        BI.removeAt(item, this.constants.CHART_TYPE_POSITION);
                        return item;
                }
                item[this.constants.CordonPos][0].cls = "";
                item[this.constants.CordonPos][0] = {
                    el: item[this.constants.CordonPos][0],
                    children: [{
                        text: BI.i18nText("BI-Cordon") + "(" + text + ")",
                        value: BICst.TARGET_COMBO.CORDON
                    },{
                        text: BI.i18nText("BI-Data_Label"),
                        value: BICst.TARGET_COMBO.DATA_LABEL_OTHER,
                        warningTitle: BI.i18nText("BI-Data_Label_Donnot_Show")
                    }]
                };
                if(bigDataMode){
                    item[this.constants.CordonPos][0].disabled = true;
                }
                if(!dataLable){
                    item[this.constants.CordonPos][0].children[1].disabled = true
                }
                BI.removeAt(item, this.constants.CHART_TYPE_POSITION);
                break;
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                BI.removeAt(item, this.constants.CHART_TYPE_POSITION);
                BI.removeAt(item, 1);
                break;
            default:
                BI.removeAt(item, this.constants.CHART_TYPE_POSITION);
                break;
        }
        return item;
    },

    _assertChartType:function(val){
        val || (val = {});
        val.type || (val.type = BICst.WIDGET.AXIS);
        return val;
    },

    _createValue: function () {
        var o = this.options;
        var chartType = BI.Utils.getDimensionStyleOfChartByID(o.dId);
        chartType = this._assertChartType(chartType);
        var result = {};
        result.chartType = {
            value: BICst.TARGET_COMBO.CHART_TYPE,
            childValue: chartType.type
        };
        return [result.chartType];
    }
});
$.shortcut("bi.calculate_target_combo", BI.CalculateTargetCombo);
