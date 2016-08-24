;
!(function(){
    BI.StringEndWithFilterValue = function(end){
        this.end = end;
    };
    BI.StringEndWithFilterValue.prototype = {
        constructor: BI.StringEndWithFilterValue,

        isStringEndWith: function(value){
            if (value == null && this.end == null) {
                return true;
            }
            if (value == null || this.end == null) {
                return false;
            }
            return value.endWith(this.end);
        },

        getFilterResult: function(array) {
            return BI.filter(array, function(idx, val){
                return this.isStringEndWith(val);
            });
        }
    }
})();