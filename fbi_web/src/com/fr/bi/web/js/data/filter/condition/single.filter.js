;
!(function(){
    BI.SingleFilter = function(filter){
        this.filterValue = BI.FilterValueFactory.parseFilterValue(filter);
    };
    BI.SingleFilter.prototype = {
        constructor: BI.SingleFilter,

        getFilterResult: function(array) {
            return this.filterValue.getFilterResult(array);
        }
    }
})();