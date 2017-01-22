;
!(function(){
    BI.NumberNotInRangeFilterValue = function(range){
        this.range = {};
        range = range || {};
        this.range.min = range.min || BI.MIN;
        this.range.max = range.max || BI.MAX;
        this.range.closemin = BI.isNotNull(range.closemin) ? range.closemin : true;
        this.range.closemax = BI.isNotNull(range.closemax) ? range.closemax : true;
    };
    BI.NumberNotInRangeFilterValue.prototype = {
        constructor: BI.NumberNotInRangeFilterValue,

        isNumberNotInRange: function(value){
            if(value == null){
                return true;
            }
            return (this.range.closemin ? value < this.range.min : value <= this.range.min) ||
                (this.range.closemax ? value > this.range.max : value >= this.range.max);
        },

        getFilterResult: function(array) {
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isNumberNotInRange(val);
            });
        }
    }
})();