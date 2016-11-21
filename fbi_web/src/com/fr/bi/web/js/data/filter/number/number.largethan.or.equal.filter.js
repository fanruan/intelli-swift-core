;
!(function(){
    BI.NumberLargeThanOrEqualFilterValue = function(){

    };
    BI.NumberLargeThanOrEqualFilterValue.prototype = {
        constructor: BI.NumberLargeThanOrEqualFilterValue,

        getNumberAvg: function(array){
            if(array.length === 0){
                return;
            }
            var sum = 0;
            BI.each(array, function(idx, num){
                sum = BI.parseFloat(sum.add(num || 0));
            });
            return BI.parseFloat(sum.div(array.length));
        },

        getFilterResult: function(array) {
            var avgValue = this.getNumberAvg(array);
            return BI.filter(array, function(idx, val){
                return val >= avgValue;
            });
        }
    }
})();