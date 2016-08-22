;
!(function(){
    BI.NumberLessThanFilterValue = function(){

    };
    BI.NumberLessThanFilterValue.prototype = {
        constructor: BI.NumberLessThanFilterValue,

        getNumberAvg: function(array){
            if(array.length === 0){
                return;
            }
            var sum = 0;
            BI.each(array, function(idx, num){
                sum = BI.parseFloat(sum.add(num));
            });
            return BI.parseFloat(sum.div(array.length));
        },

        isQualified: function(value, array){
            if(BI.isNull(this.avgValue)){
                this.avgValue = this.getNumberAvg(array);
            }
            return value < this.avgValue;
        }
    }
})();