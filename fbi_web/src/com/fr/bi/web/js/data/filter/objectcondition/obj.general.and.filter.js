;
!(function(){
    BI.ObjectGeneralAndFilter = function(filter){
        if(BI.has(filter, "filter_value")){
            var filter_value = filter.filter_value || [];
            this.childs = BI.map(filter_value, function(idx, fv){
                return BI.FilterFactory.parseFilter(fv);
            })
        }
        this.key = BI.UUID();
    };
    BI.ObjectGeneralAndFilter.prototype = {
        constructor: BI.ObjectGeneralAndFilter,

        getKey: function(){
            return this.key;
        },

        getFilterResult: function(array) {
            if(BI.isEmptyArray(array)){
                return [];
            }
            var self = this;
            var filterValueMap = {};
            BI.each(this.childs, function(i, child){
                filterValueMap[child.getKey()] = child.getFilterResult(array);
            });
            return BI.filter(array, function(idx, item){
                var find = BI.find(self.childs, function(idx, child){
                    if(BI.isNotNull(child) && !BI.contains(filterValueMap[child.getKey()], item[child.getKey()])){
                        return true;
                    }
                });
                BI.isNull(find);
            });
        }
    }
})();