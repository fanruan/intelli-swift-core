/**
 * Created by fay on 2016/7/25.
 */
BI.DataLabelFilterItemFactory = {
    createFilterItemByFieldType: function (fieldType) {
        var filterType, type;
        switch (fieldType) {
            case BICst.COLUMN.STRING:
            case BICst.COLUMN.DATE:
                filterType = BICst.DIMENSION_FILTER_STRING.BELONG_VALUE;
                type = "bi.data_label_string_field_filter_item";
                break;
            case BICst.COLUMN.NUMBER:
            case BICst.COLUMN.COUNTER:
                filterType = BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE;
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
            case BICst.DIMENSION_FILTER_STRING.BELONG_VALUE:
            case BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE:
            case BICst.DIMENSION_FILTER_STRING.CONTAIN:
            case BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN:
            case BICst.DIMENSION_FILTER_STRING.IS_NULL:
            case BICst.DIMENSION_FILTER_STRING.NOT_NULL:
            case BICst.DIMENSION_FILTER_STRING.BEGIN_WITH:
            case BICst.DIMENSION_FILTER_STRING.END_WITH:
            case BICst.DIMENSION_FILTER_STRING.NOT_BEGIN_WITH:
            case BICst.DIMENSION_FILTER_STRING.NOT_END_WITH:
                type = "bi.data_label_string_field_filter_item";
                break;
            case BICst.TARGET_FILTER_NUMBER.EQUAL_TO:
            case BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO:
            case BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_VALUE:
            case BICst.DIMENSION_FILTER_NUMBER.BELONG_USER:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_USER:
            case BICst.DIMENSION_FILTER_NUMBER.IS_NULL:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_NULL:
            case BICst.DIMENSION_FILTER_NUMBER.LARGE_THAN_CAL_LINE:
            case BICst.DIMENSION_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE:
            case BICst.DIMENSION_FILTER_NUMBER.SMALL_THAN_CAL_LINE:
            case BICst.DIMENSION_FILTER_NUMBER.SMALL_OR_EQUAL_CAL_LINE:
            case BICst.DIMENSION_FILTER_NUMBER.TOP_N:
            case BICst.DIMENSION_FILTER_NUMBER.BOTTOM_N:
                type = "bi.data_label_number_field_filter_item";
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