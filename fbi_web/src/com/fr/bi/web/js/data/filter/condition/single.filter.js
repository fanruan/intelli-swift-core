;
!(function(){
    BI.SingleFilter = function(filter){
        this.filterValue = BI.FilterValueFactory.parseFilterValue(filter);
    };
    BI.SingleFilter.prototype = {
        constructor: BI.SingleFilter,

        isQualified: function(value, array){
            return this.filterValue.isQualified(value, array);
        }
    }
})();