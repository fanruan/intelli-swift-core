;
!(function(){
    BI.EmptyFilter = function(){

    };
    BI.EmptyFilter.prototype = {
        constructor: BI.EmptyFilter,

        isQualified: function(value, array){
            return true;
        }
    }
})();