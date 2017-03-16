;
!(function(){
    BI.GeneralOrFilter = function(filter){
        if(BI.has(filter, "filter_value")){
            var filter_value = filter.filter_value || [];
            this.childs = BI.map(filter_value, function(idx, fv){
                return BI.FilterFactory.parseFilter(fv);
            })
        }
    };
    BI.GeneralOrFilter.prototype = {
        constructor: BI.GeneralOrFilter,

        getFilterResult: function(array) {
            var result = [];
            BI.each(this.childs, function(idx, filter){
                result = BI.union(result, filter.getFilterResult(array));
            });
            return result;
        }
    }
})();