/**
 * Created by fay on 2016/7/25.
 */
BI.DataLabelFilterItemFactory = {
    createFilterItemByFieldType: function (fieldType) {
        var filterType, type;
        switch (fieldType) {
            case BICst.COLUMN.STRING:
            case BICst.COLUMN.DATE:
                filterType = BICst.DATA_LABEL_FILTER_STRING.BELONG_VALUE;
                type = "bi.data_label_string_field_filter_item";
                break;
            case BICst.COLUMN.NUMBER:
            case BICst.COLUMN.COUNTER:
                filterType = BICst.DATA_LABEL_FILTER_NUMBER.BELONG_VALUE;
                type = "bi.data_label_number_field_filter_item";
                break;
            default :
                type = "bi.data_label_no_type_field_filter_item";
                break;
        }
        return {
            type: type,
            filter_type: filterType
        };
    },

    createFilterItemByFilterType: function (filterType) {
        var type = "";
        switch (filterType) {
            case BICst.DATA_LABEL_FILTER_STRING.BELONG_VALUE:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_BELONG_VALUE:
            case BICst.DATA_LABEL_FILTER_STRING.CONTAIN:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_CONTAIN:
            case BICst.DATA_LABEL_FILTER_STRING.IS_NULL:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_NULL:
            case BICst.DATA_LABEL_FILTER_STRING.BEGIN_WITH:
            case BICst.DATA_LABEL_FILTER_STRING.END_WITH:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_BEGIN_WITH:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_END_WITH:
                type = "bi.data_label_string_field_filter_item";
                break;
            case BICst.DATA_LABEL_FILTER_NUMBER.EQUAL_TO:
            case BICst.DATA_LABEL_FILTER_NUMBER.NOT_EQUAL_TO:
            case BICst.DATA_LABEL_FILTER_NUMBER.BELONG_VALUE:
            case BICst.DATA_LABEL_FILTER_NUMBER.NOT_BELONG_VALUE:
            case BICst.DATA_LABEL_FILTER_NUMBER.BELONG_USER:
            case BICst.DATA_LABEL_FILTER_NUMBER.NOT_BELONG_USER:
            case BICst.DATA_LABEL_FILTER_NUMBER.IS_NULL:
            case BICst.DATA_LABEL_FILTER_NUMBER.NOT_NULL:
            case BICst.DATA_LABEL_FILTER_NUMBER.LARGE_THAN_CAL_LINE:
            case BICst.DATA_LABEL_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE:
            case BICst.DATA_LABEL_FILTER_NUMBER.SMALL_THAN_CAL_LINE:
            case BICst.DATA_LABEL_FILTER_NUMBER.SMALL_OR_EQUAL_CAL_LINE:
            case BICst.DATA_LABEL_FILTER_NUMBER.TOP_N:
            case BICst.DATA_LABEL_FILTER_NUMBER.BOTTOM_N:
                type = "bi.data_label_number_field_filter_item";
                break;
            case BICst.FILTER_DATE.BELONG_DATE_RANGE:
            case BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE:
            case BICst.FILTER_DATE.BELONG_WIDGET_VALUE:
            case BICst.FILTER_DATE.NOT_BELONG_WIDGET_VALUE:
            case BICst.FILTER_DATE.LATER_THAN:
            case BICst.FILTER_DATE.EARLY_THAN:
            case BICst.FILTER_DATE.EQUAL_TO:
            case BICst.FILTER_DATE.NOT_EQUAL_TO:
            case BICst.FILTER_DATE.IS_NULL:
            case BICst.FILTER_DATE.NOT_NULL:
                type = "bi.data_label_string_field_filter_item";
                break;
            case BICst.FILTER_TYPE.EMPTY_CONDITION:
                type = "bi.data_label_no_type_field_filter_item";
                break;
            default :
                type = "bi.data_label_no_type_field_filter_item";
                break;
        }
        return {
            type: type
        };
    }
};