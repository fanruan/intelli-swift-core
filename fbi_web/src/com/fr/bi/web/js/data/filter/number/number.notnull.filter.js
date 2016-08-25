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
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isNumberNotNull(val);
            });
        }
    }
})();