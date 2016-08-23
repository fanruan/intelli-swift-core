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
            return BI.filter(array, function(idx, val){
                return this.isQualified(val, array);
            });
        }
    }
})();