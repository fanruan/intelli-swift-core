;
!(function(){
    BI.ObjectGeneralOrFilter = function(filter){
        if(BI.has(filter, "filter_value")){
            var filter_value = filter.filter_value || [];
            this.childs = BI.map(filter_value, function(idx, fv){
                return BI.FilterObjectFactory.parseFilter(fv);
            })
        }
        this.key = BI.UUID()
    };
    BI.ObjectGeneralOrFilter.prototype = {
        constructor: BI.ObjectGeneralOrFilter,

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
                    if(BI.isNotNull(child) && BI.deepContains(filterValueMap[child.getKey()], item)){
                        return true;
                    }
                });
                return BI.isNotNull(find);
            });
        }
    }
})();