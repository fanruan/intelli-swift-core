;
!(function(){
    BI.NumberNotInRangeFilterValue = function(range){
        this.range = {};
        range = range || {};
        this.range.min = range.min || 0;
        this.range.max = range.max || 0;
        this.range.closemin = range.closemin || true;
        this.range.closemax = range.closemax || true;
    };
    BI.NumberNotInRangeFilterValue.prototype = {
        constructor: BI.NumberNotInRangeFilterValue,

        isNumberNotInRange: function(value){
            if(value = null){
                return true;
            }
            return (this.range.closemin ? value < this.range.min : value <= this.range.min) &&
                (this.range.closemax ? value > this.range.max : value >= this.range.max);
        },

        isQualified: function(value){
            return this.isNumberNotInRange(value);
        }
    }
})();