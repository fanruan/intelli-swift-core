/**
 * Created by fay on 2016/7/25.
 */
BI.ScatterFilterItemFactory = {
    createFilterItemByFieldType: function (fieldType) {
        var filterType, type;
        switch (fieldType) {
            case BICst.COLUMN.STRING:
            case BICst.COLUMN.DATE:
                filterType = BICst.DATA_LABEL_FILTER_STRING.BELONG_VALUE;
                type = "bi.scatter_string_field_filter_item";
                break;
            case BICst.COLUMN.NUMBER:
            case BICst.COLUMN.COUNTER:
                filterType = BICst.DATA_LABEL_FILTER_NUMBER.BELONG_VALUE;
                type = "bi.scatter_number_field_filter_item";
                break;
            case BICst.COLUMN.XANDY:
                filterType = BICst.DATA_LABEL_FILTER_NUMBER.BELONG_VALUE;
                type = "bi.scatter_double_field_filter_item";
                break;
            default :
                type = "bi.scatter_no_type_field_filter_item";
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
                type = "bi.scatter_string_field_filter_item";
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
                type = "bi.scatter_number_field_filter_item";
                break;
            case BICst.FILTER_TYPE.EMPTY_CONDITION:
                type = "bi.scatter_no_type_field_filter_item";
                break;
            default :
                type = "bi.scatter_no_type_field_filter_item";
                break;
        }
        return {
            type: type
        };
    }
};