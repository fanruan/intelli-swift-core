;
!(function(){
    BI.NumberEqualFilterValue = function(value){
        if(!BI.isNumber(value)){
            this.value = BI.parseFloat(value);
        }else{
            this.value = value || 0;
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

        getFilterResult: function(array) {
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isNumberEqual(val);
            });
        }
    }
})();