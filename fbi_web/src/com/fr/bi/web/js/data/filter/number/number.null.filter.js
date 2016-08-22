;
!(function(){
    BI.NumberNullFilterValue = function(){
    };
    BI.NumberNullFilterValue.prototype = {
        constructor: BI.NumberNullFilterValue,

        isNumberNull: function(value){
            return BI.isNull(value);
        },

        isQualified: function(value){
            return this.isNumberNull(value);
        }
    }
})();