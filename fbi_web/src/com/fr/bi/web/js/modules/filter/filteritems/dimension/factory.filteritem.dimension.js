BI.DimensionFilterItemFactory = {
    createFilterItemByFieldType: function (fieldType) {
        var filterType, type;
        switch (fieldType) {
            case BICst.COLUMN.STRING:
                filterType = BICst.DIMENSION_FILTER_STRING.BELONG_VALUE;
                type = "bi.dimension_string_field_filter_item";
                break;
            case BICst.COLUMN.COUNTER:
            case BICst.COLUMN.NUMBER:
                filterType = BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE;
                type = "bi.dimension_number_field_filter_item";
                break;
            case BICst.COLUMN.DATE:
                filterType = BICst.DIMENSION_FILTER_STRING.BELONG_VALUE;
                type = "bi.dimension_string_field_filter_item";
                break;
            default :
                type = "bi.dimension_no_type_field_filter_item";
                break;
        }
        return {
            type: type,
            filter_type: filterType
        };
    },

    createFilterItemByFilterType: function (filterType) {
        var type = "";
        switch (filterType){
            case BICst.DIMENSION_FILTER_STRING.BELONG_VALUE:
            case BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE:
            case BICst.DIMENSION_FILTER_STRING.CONTAIN:
            case BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN:
            case BICst.DIMENSION_FILTER_STRING.IS_NULL:
            case BICst.DIMENSION_FILTER_STRING.NOT_NULL:
            case BICst.DIMENSION_FILTER_STRING.BEGIN_WITH:
            case BICst.DIMENSION_FILTER_STRING.END_WITH:
            case BICst.DIMENSION_FILTER_STRING.TOP_N:
            case BICst.DIMENSION_FILTER_STRING.BOTTOM_N:
                type = "bi.dimension_string_field_filter_item";
                break;
            case BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_VALUE:
            case BICst.DIMENSION_FILTER_NUMBER.BELONG_USER:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_USER:
            case BICst.DIMENSION_FILTER_NUMBER.IS_NULL:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_NULL:
            case BICst.DIMENSION_FILTER_NUMBER.MORE_THAN_AVG:
            case BICst.DIMENSION_FILTER_NUMBER.LESS_THAN_AVG:
            case BICst.DIMENSION_FILTER_NUMBER.TOP_N:
            case BICst.DIMENSION_FILTER_NUMBER.BOTTOM_N:
                type = "bi.dimension_number_field_filter_item";
                break;
            case BICst.FILTER_DATE.BELONG_DATE_RANGE:
            case BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE:
            case BICst.FILTER_DATE.LATER_THAN:
            case BICst.FILTER_DATE.EARLY_THAN:
            case BICst.FILTER_DATE.EQUAL_TO:
            case BICst.FILTER_DATE.NOT_EQUAL_TO:
            case BICst.FILTER_DATE.IS_NULL:
            case BICst.FILTER_DATE.NOT_NULL:
                type = "bi.dimension_string_field_filter_item";
                break;
            case BICst.FILTER_TYPE.AND:
            case BICst.FILTER_TYPE.OR:
                type = "bi.filter_expander";
                break;
            case BICst.FILTER_TYPE.FORMULA:
                type = "bi.dimension_formula_filter_item";
                break;
            case BICst.FILTER_TYPE.EMPTY_FORMULA:
                type = "bi.dimension_formula_empty_filter_item";
                break;
            case BICst.FILTER_TYPE.EMPTY_CONDITION:
                type = "bi.dimension_no_type_field_filter_item";
                break;
            default :
                type = "bi.dimension_no_type_field_filter_item";
                break;
        }
        return {
            type: type
        };
    }
};