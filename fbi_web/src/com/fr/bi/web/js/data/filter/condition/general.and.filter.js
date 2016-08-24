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

        isQualified: function(value, array){
            var res = BI.find(this.childs, function(idx, child){
                if(BI.isNotNull(child) && child.isQualified(value, array)){
                    return true;
                }
            });
            return BI.isNotNull(res);
        },

        getFilterResult: function(array) {
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isQualified(val, array);
            });
        }
    }
})();