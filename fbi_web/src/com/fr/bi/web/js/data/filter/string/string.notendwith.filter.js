;
!(function(){
    BI.StringNotEndWithFilterValue = function(end){
        this.end = end;
    };
    BI.StringNotEndWithFilterValue.prototype = {
        constructor: BI.StringNotEndWithFilterValue,

        isStringNotEndWith: function(value){
            if (value == null && this.end == null) {
                return true;
            }
            if (value == null || this.end == null) {
                return false;
            }
            return !value.endWith(this.end);
        },

        getFilterResult: function(array) {
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isStringNotEndWith(val);
            });
        }
    }
})();