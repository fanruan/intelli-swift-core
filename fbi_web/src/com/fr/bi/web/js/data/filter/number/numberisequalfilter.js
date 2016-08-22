;
!(function(){
    BI.NumberEqualFilterValue = function(value){
        if(!BI.isNumber(value)){
            this.value = BI.parseFloat(value);
        }else{
            this.value = value;
        }
    };
    BI.NumberEqualFilterValue.prototype = {
        constructor: BI.NumberEqualFilterValue,

        isNumberEqual: function(value){
            if (value == null) {
                return false;
            }
            var v = value;
            if(!BI.isNumber(value)){
                v = BI.parseFloat(value);
            }
            if (BI.isNaN(v)) {
                return false;
            }
            return (v + "") === (this.value + "");
        },

        isQualified: function(value){
            return this.isNumberEqual(value);
        }
    }
})();