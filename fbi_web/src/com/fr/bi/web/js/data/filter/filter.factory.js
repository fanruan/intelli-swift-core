BI.FilterFactory = {

    parseFilter: function(filterValue){
        var filter = null;
        if(BI.has(filterValue, "filter_type")){
            switch (filterValue.filter_type){
                case BICst.FILTER_TYPE.AND:
                    filter = new BI.GeneralAndFilter(filterValue);
                    break;
                case BICst.FILTER_TYPE.OR:
                    filter = new BI.GeneralOrFilter(filterValue);
                    break;
                case BICst.FILTER_TYPE.EMPTY_CONDITION:
                    filter = new BI.EmptyFilter();
                    break;
                default:
                    filter = new BI.SingleFilter(filterValue);
                    break;
            }
        }
        return filter;
    }

};
