;
!(function(){
    BI.StringNotNullFilterValue = function(){
    };
    BI.StringNotNullFilterValue.prototype = {
        constructor: BI.StringNotNullFilterValue,

        isStringNotNull: function(value){
            return BI.isNotNull(value) && BI.isNotEmptyString(value);
        },

        isQualified: function(value){
            return this.isStringNotNull(value);
        }
    }
})();