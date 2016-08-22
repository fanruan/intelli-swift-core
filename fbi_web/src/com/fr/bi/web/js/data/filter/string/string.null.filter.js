;
!(function(){
    BI.StringNullFilterValue = function(){
    };
    BI.StringNullFilterValue.prototype = {
        constructor: BI.StringNullFilterValue,

        isStringNull: function(value){
            return BI.isNull(value) || BI.isEmptyString(value);
        },

        isQualified: function(value){
            return this.isStringNull(value);
        }
    }
})();