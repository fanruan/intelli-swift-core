BI.FilterValueFactory = {

    parseFilterValue: function(filter){
        var filterValue = null;
        if (BI.has(filter, "filter_type")) {
            var type = filter.filter_type;
            switch (type) {
                case BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE:
                    filterValue = new BI.NumberInRangeFilterValue(filter.filter_value);
                    break;
                case BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_VALUE:
                    filterValue = new BI.NumberNotInRangeFilterValue(filter.filter_value);
                    break;
                case BICst.TARGET_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE:
                    filterValue = new BI.NumberLargeThanOrEqualFilterValue();
                    break;
                case BICst.TARGET_FILTER_NUMBER.SMALL_THAN_CAL_LINE:
                    filterValue = new BI.NumberLessThanFilterValue();
                    break;
                case BICst.DIMENSION_FILTER_NUMBER.IS_NULL:
                    filterValue = new BI.NumberNullFilterValue();
                    break;
                case BICst.DIMENSION_FILTER_NUMBER.NOT_NULL:
                    filterValue = new BI.NumberNotNullFilterValue();
                    break;
                case BICst.DIMENSION_FILTER_NUMBER.TOP_N:
                    filterValue = new BI.NumberKthFilterValue(filter.filter_value);
                    break;
                case BICst.TARGET_FILTER_NUMBER.EQUAL_TO:
                    filterValue = new BI.NumberEqualFilterValue(filter.filter_value);
                    break;
                case BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO:
                    filterValue = new BI.NumberNotEqualFilterValue(filter.filter_value);
                    break;
                case BICst.DIMENSION_FILTER_STRING.BELONG_VALUE:
                    filterValue = new BI.StringInFilterValue(filter.filter_value);
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE:
                    filterValue = new BI.StringNotInFilterValue(filter.filter_value);
                    break;
                case BICst.DIMENSION_FILTER_STRING.CONTAIN:
                    filterValue = new BI.StringContainValue(filter.filter_value);
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN:
                    filterValue = new BI.StringNotContainValue(filter.filter_value);
                    break;
                case BICst.DIMENSION_FILTER_STRING.IS_NULL:
                    filterValue = new BI.StringNullFilterValue();
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_NULL:
                    filterValue = new BI.StringNotNullFilterValue();
                    break;
                case BICst.DIMENSION_FILTER_STRING.BEGIN_WITH:
                    filterValue = new BI.StringStartWithFilterValue(filter.filter_value);
                    break;
                case BICst.DIMENSION_FILTER_STRING.END_WITH:
                    filterValue = new BI.StringEndWithFilterValue(filter.filter_value);
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_BEGIN_WITH:
                    filterValue = new BI.StringNotStartWithFilterValue(filter.filter_value);
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_END_WITH:
                    filterValue = new BI.StringNotEndWithFilterValue(filter.filter_value);
                    break;
                default:
                    break;
            }
        }
        return filterValue;
    }

};