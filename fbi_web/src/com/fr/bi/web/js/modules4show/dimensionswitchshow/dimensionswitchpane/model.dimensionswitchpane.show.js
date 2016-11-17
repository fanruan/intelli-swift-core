/**
 * Created by zcf on 2016/11/4.
 */
BI.DimensionSwitchPaneShowModel = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionSwitchPaneShowModel.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        });
    },

    _init: function () {
        BI.DimensionSwitchPaneShowModel.superclass._init.apply(this, arguments);
        this.populate();
    },

    _getRegionName: function (type) {
        switch (type) {
            case BICst.WIDGET.TABLE:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Row_Header");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Target");
                break;
            // case BICst.WIDGET.TREE:
            // case BICst.WIDGET.TREE_LABEL:
            //     this.regions[BICst.REGION.DIMENSION1] = this._createTreeDimensionRegion();
            //     break;
            case BICst.WIDGET.DETAIL:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Data");
                break;
            case BICst.WIDGET.CROSS_TABLE:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Row_Header");
                this.regionName[BICst.REGION.DIMENSION2] = BI.i18nText("BI-Column_Header");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Target");
                break;
            // case BICst.WIDGET.DATE:
            // case BICst.WIDGET.YMD:
            // case BICst.WIDGET.NUMBER:
            // case BICst.WIDGET.SINGLE_SLIDER:
            // case BICst.WIDGET.INTERVAL_SLIDER:
            // case BICst.WIDGET.YEAR:
            // case BICst.WIDGET.MONTH:
            // case BICst.WIDGET.QUARTER:
            // case BICst.WIDGET.STRING:
            // case BICst.WIDGET.LIST_LABEL:
            //     this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Data"), BICst.REGION.DIMENSION1);
            //     break;
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.DIMENSION2] = BI.i18nText("BI-Series");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Left_Value_Axis");
                this.regionName[BICst.REGION.TARGET2] = BI.i18nText("BI-Right_Value_Axis");
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.ACCUMULATE_BAR:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.DIMENSION2] = BI.i18nText("BI-Series");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Value_Axis");
                break;
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Positive_Value_Axis");
                this.regionName[BICst.REGION.TARGET2] = BI.i18nText("BI-Negative_Value_Axis");
                break;
            case BICst.WIDGET.FALL_AXIS:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Value_Axis");
                break;
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.RANGE_AREA:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Value_Axis_One");
                this.regionName[BICst.REGION.TARGET2] = BI.i18nText("BI-Value_Axis_Two");
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Left_Value_Axis");
                this.regionName[BICst.REGION.TARGET2] = BI.i18nText("BI-Right_Value_Axis_One");
                this.regionName[BICst.REGION.TARGET3] = BI.i18nText("BI-Right_Value_Axis_Two");
                break;
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.DIMENSION2] = BI.i18nText("BI-Series");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Target");
                break;
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.FUNNEL:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Target");
                break;
            case BICst.WIDGET.MAP:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Region_Name");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Target");
                this.regionName[BICst.REGION.TARGET2] = BI.i18nText("BI-Region_Suspension_Target");
                break;
            case BICst.WIDGET.GIS_MAP:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Address");
                this.regionName[BICst.REGION.DIMENSION2] = BI.i18nText("BI-Name_Title");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Region_Target");
                break;
            case BICst.WIDGET.BUBBLE:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Y_Value");
                this.regionName[BICst.REGION.TARGET2] = BI.i18nText("BI-X_Value");
                this.regionName[BICst.REGION.TARGET3] = BI.i18nText("BI-Bubble_Size");
                break;
            case BICst.WIDGET.SCATTER:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Y_Value");
                this.regionName[BICst.REGION.TARGET2] = BI.i18nText("BI-X_Value");
                break;
        }
    },

    getRegionNameByType: function (dimensionType) {
        return this.regionName[dimensionType];
    },

    getView: function () {
        return this.view;
    },

    populate: function () {
        var o = this.options;
        var type = BI.Utils.getWidgetTypeByID(o.wId);
        this.view = {};
        this.regionName = {};
        if (type != BICst.WIDGET.COMPLEX_TABLE) {
            this.view = BI.Utils.getWidgetViewByID(o.wId);
            this._getRegionName(type);
        }

    }
});