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

        isQualified: function(value, array){
            var res = BI.find(this.childs, function(idx, child){
                if(BI.isNotNull(child) && !child.isQualified(value, array)){
                    return true;
                }
            });
            return BI.isNull(res);
        },

        getFilterResult: function(array) {
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isQualified(val, array);
            });
        }
    }
})();