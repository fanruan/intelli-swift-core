;
!(function(){
    BI.NumberNotNullFilterValue = function(){
    };
    BI.NumberNotNullFilterValue.prototype = {
        constructor: BI.NumberNotNullFilterValue,

        isNumberNotNull: function(value){
            return BI.isNotNull(value);
        },

        getFilterResult: function(array) {
            return BI.filter(array, function(idx, val){
                return this.isNumberNotNull(val);
            });
        }
    }
})();