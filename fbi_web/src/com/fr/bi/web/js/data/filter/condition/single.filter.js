;
!(function(){
    BI.SingleFilter = function(filter){
        this.filterValue = BI.FilterValueFactory.parseFilterValue(filter);
    };
    BI.SingleFilter.prototype = {
        constructor: BI.SingleFilter,

        isQualified: function(value, array){
            return this.filterValue.isQualified(value, array);
        },

        getFilterResult: function(array) {
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isQualified(val, array);
            });
        }
    }
})();