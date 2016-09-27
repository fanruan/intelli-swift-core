;
!(function(){
    BI.EmptyFilter = function(){

    };
    BI.EmptyFilter.prototype = {
        constructor: BI.EmptyFilter,

        getFilterResult: function() {
            return [];
        }
    }
})();