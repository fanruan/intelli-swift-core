BI.FilterObjectFactory = {

    parseFilter: function(filterValue){
        var filter = null;
        if(BI.has(filterValue, "filter_type")){
            switch (filterValue.filter_type){
                case BICst.FILTER_TYPE.AND:
                    filter = new BI.ObjectGeneralAndFilter(filterValue);
                    break;
                case BICst.FILTER_TYPE.OR:
                    filter = new BI.ObjectGeneralOrFilter(filterValue);
                    break;
                case BICst.FILTER_TYPE.EMPTY_CONDITION:
                    filter = new BI.EmptyFilter();
                    break;
                default:
                    filter = new BI.ObjectSingleFilter(filterValue);
                    break;
            }
        }
        return filter;
    }

};