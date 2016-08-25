;
!(function(){
    BI.ObjectSingleFilter = function(filter){
        this.filterValue = BI.FilterValueFactory.parseFilterValue(filter);
        this.key = filter.key;
    };
    BI.ObjectSingleFilter.prototype = {
        constructor: BI.ObjectSingleFilter,

        getKey: function(){
            return this.key;
        },

        getFilterResult: function(array) {
            var self = this;
            var filter = this.filterValue.getFilterResult(BI.pluck(array, this.key));
            return BI.filter(array, function(idx, item){
                return BI.contains(filter, item[self.getKey()]);
            });
        }
    }
})();