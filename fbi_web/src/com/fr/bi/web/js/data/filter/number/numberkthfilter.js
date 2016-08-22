;
!(function(){
    BI.NumberKthFilterValue = function(kNumber){
        this.kNumber = kNumber;
    };
    BI.NumberKthFilterValue.prototype = {
        constructor: BI.NumberKthFilterValue,

        getNumberKth: function(array, k){
            if(k > array.length){
                k = array.length;
            }
            var low = 0;
            var high = array.length - 1;
            while (true){
                var pos = partition(array, low, high);
                if(pos === k - 1){
                    return array[pos];
                }
                if(pos < k - 1){
                    low = pos + 1;
                }
                if(pos > k - 1){
                    high = pos - 1;
                }
            }

            function partition(array, i, j){
                var tmp = array[i];
                while (i < j){
                    while (i < j && array[j] <= tmp){
                        j--;
                    }
                    if(i < j){
                        array[i++] = array[j];
                    }
                    while (i < j && array[i] >= tmp){
                        i++;
                    }
                    if(i < j){
                        array[j--] = array[i];
                    }
                    array[i] = tmp;
                }
                return i;
            }
        },

        isQualified: function(value, array){
            if(BI.isNull(this.kthValue)){
                this.kthValue = this.getNumberKth(array, this.kNumber);
            }
            return this.kthValue + "" === BI.parseFloat(value) + "";
        }
    }
})();