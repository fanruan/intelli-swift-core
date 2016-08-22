;
!(function(){
    BI.NumberNotEqualFilterValue = function(value){
        if(!BI.isNumber(value)){
            this.value = BI.parseFloat(value);
        }else{
            this.value = value || 0;
        }
    };
    BI.NumberNotEqualFilterValue.prototype = {
        constructor: BI.NumberNotEqualFilterValue,

        isNumberNotEqual: function(value){
            if (value == null) {
                return true;
            }
            var v = value;
            if(!BI.isNumber(value)){
                v = BI.parseFloat(value);
            }
            if (BI.isNaN(v)) {
                return false;
            }
            return (v + "") !== (this.value + "");
        },

        isQualified: function(value){
            return this.isNumberNotEqual(value);
        }
    }
})();