;
!(function(){
    BI.StringNullFilterValue = function(){
    };
    BI.StringNullFilterValue.prototype = {
        constructor: BI.StringNullFilterValue,

        isStringNull: function(value){
            return BI.isNull(value) || BI.isEmptyString(value);
        },

        getFilterResult: function(array) {
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isStringNull(val);
            });
        }
    }
})();