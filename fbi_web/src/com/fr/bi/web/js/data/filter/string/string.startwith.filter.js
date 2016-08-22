;
!(function(){
    BI.StringStartWithFilterValue = function(start){
        this.start = start;
    };
    BI.StringStartWithFilterValue.prototype = {
        constructor: BI.StringStartWithFilterValue,

        isStringStartWith: function(value){
            if (value == null && this.start == null) {
                return true;
            }
            if (value == null || this.start == null) {
                return false;
            }
            return value.startWith(this.start);
        },

        isQualified: function(value){
            return this.isStringStartWith(value);
        }
    }
})();