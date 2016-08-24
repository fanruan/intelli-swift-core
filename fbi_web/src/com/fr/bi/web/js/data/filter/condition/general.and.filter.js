;
!(function(){
    BI.GeneralAndFilter = function(filter){
        if(BI.has(filter, "filter_value")){
            var filter_value = filter.filter_value || [];
            this.childs = BI.map(filter_value, function(idx, fv){
                return BI.FilterFactory.parseFilter(fv);
            })
        }
    };
    BI.GeneralAndFilter.prototype = {
        constructor: BI.GeneralAndFilter,

        getFilterResult: function(array) {
            var result = [];
            BI.each(this.childs, function(idx, filter){
                result = BI.intersection(result, filter.getFilterResult(array));
            });
            return result;
        }
    }
})();