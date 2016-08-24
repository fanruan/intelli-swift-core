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
            return BI.filter(array, function(idx, val){
                return this.isStringNull(val);
            });
        }
    }
})();