BI.FilterFactory = {

    parseFilter: function(filterValue){
        var filter = null;
        if(BI.has(filterValue, "filter_type")){
            switch (filterValue.filter_type){
                case BICst.FILTER_TYPE.AND:
                    filter = new BI.GeneralAndFilter(filterValue.filter_value);
                    break;
                case BICst.FILTER_TYPE.OR:
                    filter = new BI.GeneralOrFilter(filterValue.filter_value);
                    break;
                case BICst.FILTER_TYPE.EMPTY_CONDITION:
                    filter = new BI.EmptyFilter();
                    break;
                default:
                    filter = new BI.SingleFilter(filterValue.filter_value);
                    break;
            }
        }
        return filter;
    }

};
