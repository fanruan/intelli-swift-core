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
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isNumberNull(val);
            });
        }
    }
})();