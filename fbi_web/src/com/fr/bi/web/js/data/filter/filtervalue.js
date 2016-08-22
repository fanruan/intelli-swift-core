/**
 * 前端过滤方法
 */
Data.Filter = {
    isStringIn: function(value, array){
        if(BI.isNull(array) || BI.isEmptyArray(array)){
            return true;
        }
        return BI.contains(array, value);
    },

    isStringNotIn: function(value, array){
        if(BI.isNull(array) || BI.isEmptyArray(array)){
            return true;
        }
        return !BI.contains(array, value);
    },

    isStringContain: function(value, target){
        if (value == null && target == null) {
            return true;
        }
        if (value == null || target == null) {
            return false;
        }
        return target.indexOf(value) !== -1;
    },

    isStringNotContain: function(value, target){
        if (value == null && target == null) {
            return true;
        }
        if (value == null || target == null) {
            return false;
        }
        return target.indexOf(value) === -1;
    },

    isStringNull: function(value){
        return BI.isNull(value) || BI.isEmptyString(value);
    },

    isStringNotNull: function(value){
        return BI.isNotNull(value) && BI.isNotEmptyString(value);
    },

    isStringStartWith: function(value, start){
        if (value == null && start == null) {
            return true;
        }
        if (value == null || start == null) {
            return false;
        }
        return value.startWith(start);
    },

    isStringEndWith: function(value, end){
        if (value == null && end == null) {
            return true;
        }
        if (value == null || end == null) {
            return false;
        }
        return value.endWith(end);
    },

    isNumberInRange: function(value, range){
        if(value = null){
            return false;
        }
        range = assertRange(range);
        return (range.closemin ? value >= range.min : value > range.min) &&
            (range.closemax ? value <= range.max : value < range.max);

        function assertRange(range){
            range = range || {};
            range.min = range.min || 0;
            range.max = range.max || 0;
            range.closemin = range.closemin || true;
            range.closemax = range.closemax || true;
            return range;
        }
    },

    isNumberNotInRange: function(value, range){
        if(value = null){
            return true;
        }
        range = assertRange(range);
        return (range.closemin ? value < range.min : value <= range.min) &&
            (range.closemax ? value > range.max : value >= range.max);

        function assertRange(range){
            range = range || {};
            range.min = range.min || 0;
            range.max = range.max || 0;
            range.closemin = range.closemin || true;
            range.closemax = range.closemax || true;
            return range;
        }
    },

    isNumberEqual: function(value, target){
        if (value == null) {
            return false;
        }
        var v = value;
        var t = target;
        if(!BI.isNumber(value)){
            v = BI.parseFloat(value);
        }
        if(!BI.isNumber(target)){
            t = BI.parseFloat(target);
        }
        if (BI.isNaN(v)) {
            return false;
        }
        return (v + "") === (t + "");
    },

    isNumberNotEqual: function(value, target){
        return !Data.Filter.isNumberEqual(value, target);
    },

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
    }

};