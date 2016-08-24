;
!(function(){
    BI.StringNotContainValue = function(value){
        this.value = value;
    };
    BI.StringNotContainValue.prototype = {
        constructor: BI.StringNotContainValue,

        isStringContain: function(target){
            if (this.value == null && target == null) {
                return true;
            }
            if (this.value == null || target == null) {
                return false;
            }
            return target.indexOf(this.value) === -1;
        },

        getFilterResult: function(array) {
            return BI.filter(array, function(idx, val){
                return this.isStringContain(val);
            });
        }
    }
})();