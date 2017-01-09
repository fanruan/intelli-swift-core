/**
 * Created by zcf on 2016/12/27.
 */
BI.ShowRegionManagerModel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowRegionManagerModel.superclass._defaultConfig.apply(this, arguments), {
            wId: ''
        })
    },
    _init: function () {
        BI.ShowRegionManagerModel.superclass._init.apply(this, arguments);
        this.populate();
    },

    _createRegionName: function (type) {
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
            case BICst.WIDGET.COMPLEX_TABLE:
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
            case BICst.WIDGET.PARETO:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Category");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Target");
                break;
            case BICst.WIDGET.MAP:
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Region_Name");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Target");
                this.regionName[BICst.REGION.TARGET2] = BI.i18nText("BI-Region_Suspension_Target");
                break;
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.HEAT_MAP:
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
            default :
                this.regionName[BICst.REGION.DIMENSION1] = BI.i18nText("BI-Row_Header");
                this.regionName[BICst.REGION.TARGET1] = BI.i18nText("BI-Target");
                break;
        }
    },

    _createRegionType: function (type) {
        switch (type) {
            case BICst.WIDGET.TABLE:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            // case BICst.WIDGET.TREE:
            // case BICst.WIDGET.TREE_LABEL:
            //     this.regions[BICst.REGION.DIMENSION1] = this._createTreeDimensionRegion();
            //     break;
            case BICst.WIDGET.DETAIL:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                break;
            case BICst.WIDGET.CROSS_TABLE:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.DIMENSION2] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.COMPLEX_TABLE:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_WRAPPER_DIMENSION;
                this.regionType[BICst.REGION.DIMENSION2] = BICst.REGION_TYPE.REGION_WRAPPER_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_WRAPPER_TARGET;
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
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.DIMENSION2] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                this.regionType[BICst.REGION.TARGET2] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.COMBINE_CHART:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.DIMENSION2] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_WRAPPER_TARGET_SETTING;
                this.regionType[BICst.REGION.TARGET2] = BICst.REGION_TYPE.REGION_WRAPPER_TARGET_SETTING;
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.ACCUMULATE_BAR:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.DIMENSION2] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                this.regionType[BICst.REGION.TARGET2] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.FALL_AXIS:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.RANGE_AREA:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                this.regionType[BICst.REGION.TARGET2] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                this.regionType[BICst.REGION.TARGET2] = BICst.REGION_TYPE.REGION_TARGET;
                this.regionType[BICst.REGION.TARGET3] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.DIMENSION2] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.PARETO:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.MAP:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                this.regionType[BICst.REGION.TARGET2] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.HEAT_MAP:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.DIMENSION2] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.BUBBLE:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                this.regionType[BICst.REGION.TARGET2] = BICst.REGION_TYPE.REGION_TARGET;
                this.regionType[BICst.REGION.TARGET3] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            case BICst.WIDGET.SCATTER:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                this.regionType[BICst.REGION.TARGET2] = BICst.REGION_TYPE.REGION_TARGET;
                break;
            default:
                this.regionType[BICst.REGION.DIMENSION1] = BICst.REGION_TYPE.REGION_DIMENSION;
                this.regionType[BICst.REGION.TARGET1] = BICst.REGION_TYPE.REGION_TARGET;
                break;
        }
    },

    getRegionNameByType: function (viewType) {
        return this.regionName[viewType];
    },

    getRegionsType: function () {
        return this.regionType;
    },

    populate: function () {
        var o = this.options;
        var type = BI.Utils.getWidgetTypeByID(o.wId);
        this.regionName = {};
        this.regionType = {};
        this._createRegionName(type);
        this._createRegionType(type);
    }
});