;
!(function(){
    BI.NumberNotNullFilterValue = function(){
    };
    BI.NumberNotNullFilterValue.prototype = {
        constructor: BI.NumberNotNullFilterValue,

        isNumberNotNull: function(value){
            return BI.isNotNull(value);
        },

        isQualified: function(value){
            return this.isNumberNotNull(value);
        }
    }
})();