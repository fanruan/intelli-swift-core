/**
 * Created by Young's on 2016/5/21.
 */
BI.DimensionFilterItem = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.DimensionFilterItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-filter-item"
        })
    },

    _init: function(){
        BI.DimensionFilterItem.superclass._init.apply(this, arguments);
        var o = this.options;
        var tId = o.tId, filter = o.filter;
        var relation = "", value = "";
        var model = new BI.WidgetFilterModel();
        switch (filter.filter_type) {
            //number
            case BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE:
                relation = BI.i18nText("BI-In");
                value = model.getNumberRangeText(filter.filter_value);
                break;
            case BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_VALUE:
                relation = BI.i18nText("BI-Not_In");
                value = model.getNumberRangeText(filter.filter_value);
                break;
            case BICst.DIMENSION_FILTER_NUMBER.MORE_THAN_AVG:
                relation = BI.i18nText("BI-Above_Average");
                break;
            case BICst.DIMENSION_FILTER_NUMBER.LESS_THAN_AVG:
                relation = BI.i18nText("BI-Below_Average");
                break;
            case BICst.DIMENSION_FILTER_NUMBER.IS_NULL:
                relation = BI.i18nText("BI-Is_Null");
                break;
            case BICst.DIMENSION_FILTER_NUMBER.NOT_NULL:
                relation = BI.i18nText("BI-Not_Null");
                break;
            case BICst.DIMENSION_FILTER_NUMBER.TOP_N:
                relation = BI.i18nText("BI-Top_N");
                value = filter.filter_value;
                break;
            case BICst.DIMENSION_FILTER_NUMBER.BOTTOM_N:
                relation = BI.i18nText("BI-Bottom_N");
                value = filter.filter_value;
                break;
            
            //string
            case BICst.DIMENSION_FILTER_STRING.BELONG_VALUE:
                var v = filter.filter_value;
                var sType = v.type;
                relation = sType === BI.Selection.All ? BI.i18nText("BI-Not_In") : BI.i18nText("BI-In");
                value = v.value;
                break;
            case BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE:
                var v = filter.filter_value;
                var sType = v.type;
                relation = sType === BI.Selection.All ? BI.i18nText("BI-In") : BI.i18nText("BI-Not_In");
                value = v.value;
                break;
            case BICst.DIMENSION_FILTER_STRING.CONTAIN:
                relation = BI.i18nText("BI-Contain");
                value = filter.filter_value;
                break;
            case BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN:
                relation = BI.i18nText("BI-Not_Contain");
                value = filter.filter_value;
                break;
            case BICst.DIMENSION_FILTER_STRING.IS_NULL:
                relation = BI.i18nText("BI-Is_Null");
                break;
            case BICst.DIMENSION_FILTER_STRING.NOT_NULL:
                relation = BI.i18nText("BI-Not_Null");
                break;
            case BICst.DIMENSION_FILTER_STRING.BEGIN_WITH:
                relation = BI.i18nText("BI-Begin_With");
                value = filter.filter_value;
                break;
            case BICst.DIMENSION_FILTER_STRING.END_WITH:
                relation = BI.i18nText("BI-End_With");
                value = filter.filter_value;
                break;
            case BICst.DIMENSION_FILTER_STRING.TOP_N:
                relation = BI.i18nText("BI-Top_N");
                value = filter.filter_value;
                break;
            case BICst.DIMENSION_FILTER_STRING.BOTTOM_N:
                relation = BI.i18nText("BI-Bottom_N");
                value = filter.filter_value;
                break;
            case BICst.DIMENSION_FILTER_STRING.NOT_BEGIN_WITH:
                relation = BI.i18nText("BI-Not_Begin_With");
                value = filter.filter_value;
                break;
            case BICst.DIMENSION_FILTER_STRING.NOT_END_WITH:
                relation = BI.i18nText("BI-Not_End_With");
                value = filter.filter_value;
                break;
            //date
            case BICst.FILTER_DATE.BELONG_DATE_RANGE:
                relation = BI.i18nText("BI-In");
                value = model.getDateRangeText(filter.filter_value);
                break;
            case BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE:
                relation = BI.i18nText("BI-Not_In");
                value = model.getDateRangeText(filter.filter_value);
                break;
            case BICst.FILTER_DATE.BELONG_WIDGET_VALUE:
                break;
            case BICst.FILTER_DATE.NOT_BELONG_WIDGET_VALUE:
                break;
            case BICst.FILTER_DATE.EQUAL_TO:
                relation = BI.i18nText("BI-Equal");
                value = filter.filter_value;
                break;
            case BICst.FILTER_DATE.NOT_EQUAL_TO:
                relation = BI.i18nText("BI-Not_Equal_To");
                value = filter.filter_value;
                break;
            case BICst.FILTER_DATE.IS_NULL:
                relation = BI.i18nText("BI-Is_Null");
                break;
            case BICst.FILTER_DATE.NOT_NULL:
                relation = BI.i18nText("BI-Not_Null");
                break;
            case BICst.FILTER_DATE.EARLY_THAN:
                relation = BI.i18nText("BI-Sooner_Than");
                break;
            case BICst.FILTER_DATE.LATER_THAN:
                relation = BI.i18nText("BI-Later_Than");
                break;
            case BICst.FILTER_DATE.CONTAINS:
                relation = BI.i18nText("BI-Contain");
                break;

        }
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [{
                type: "bi.label",
                text: BI.Utils.getDimensionNameByID(tId) || BI.Utils.getFieldNameByID(tId),
                cls: "dimension-name",
                height: 30
            }, {
                type: "bi.label",
                text: relation,
                height: 30,
                cls: ""
            }, {
                type: "bi.label",
                text: BI.isNotNull(value) ? value.toString() : "",
                height: 30,
                cls: ""
            }],
            hgap: 10,
            vgap: 5
        })
    }
});
$.shortcut("bi.dimension_filter_item", BI.DimensionFilterItem);