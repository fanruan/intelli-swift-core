;
!(function(){
    BI.NumberNullFilterValue = function(){
    };
    BI.NumberNullFilterValue.prototype = {
        constructor: BI.NumberNullFilterValue,

        isNumberNull: function(value){
            return BI.isNull(value);
        },

        getFilterResult: function(array) {
            return BI.filter(array, function(idx, val){
                return this.isNumberNull(val);
            });
        }
    }
})();