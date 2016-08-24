;
!(function(){
    BI.StringNotNullFilterValue = function(){
    };
    BI.StringNotNullFilterValue.prototype = {
        constructor: BI.StringNotNullFilterValue,

        isStringNotNull: function(value){
            return BI.isNotNull(value) && BI.isNotEmptyString(value);
        },

        getFilterResult: function(array) {
            return BI.filter(array, function(idx, val){
                return this.isStringNotNull(val);
            });
        }
    }
})();