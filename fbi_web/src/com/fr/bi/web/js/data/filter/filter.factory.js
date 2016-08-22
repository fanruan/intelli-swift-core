BI.FilterFactory = {

    parseFilter: function(filter){
        var filter = null;
        if(BI.has(filter, "filter_type")){
            switch (filter.filter_type){
                case BICst.FILTER_TYPE.AND:
                    filter = new GeneralANDDimensionFilter();
                    break;
                case BICst.FILTER_TYPE.OR:
                    filter = new GeneralORDimensionFilter();
                    break;
                case BICst.FILTER_TYPE.EMPTY_CONDITION:
                    filter = new EmptyDimensionTargetValueFilter();
                    break;
                default:
                    filter = new DimensionTargetValueFilter();
                    break;
            }
        }
        if (filter != null){
            filter.parseJSON(jo, userId);
        }
        return filter;
    }

};
