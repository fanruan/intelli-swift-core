;
!(function(){
    BI.StringNotStartWithFilterValue = function(start){
        this.start = start;
    };
    BI.StringNotStartWithFilterValue.prototype = {
        constructor: BI.StringNotStartWithFilterValue,

        isStringNotStartWith: function(value){
            if (value == null && this.start == null) {
                return true;
            }
            if (value == null || this.start == null) {
                return false;
            }
            return !value.startWith(this.start);
        },

        getFilterResult: function(array) {
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isStringNotStartWith(val);
            });
        }
    }
})();