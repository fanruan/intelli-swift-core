/**
 * Array原型拓展
 * Created by wang on 15/6/23.
 */
!function () {
    _.each(['contains', 'indexOf', 'lastIndexOf'], function (name) {
        Array.prototype[name] = function () {
            var arr = _.toArray(arguments);
            arr.unshift(this);
            return BI[name].apply(this, arr);
        };
    });
    Array.prototype.pushArray = function (array) {
        for (var i = 0; i < array.length; i++) {
            this.push(array[i]);
        }
    };
    Array.prototype.pushDistinct = function (obj) {
        if (!this.contains(obj)) {
            this.push(obj);
        }
    };
    Array.prototype.pushDistinctArray = function (array) {
        for (var i = 0, len = array.length; i < len; i++) {
            this.pushDistinct(array[i]);
        }
    };
}();

/**
 * 规定bi的数组分为两种，其中，value和type值为key值
 * 1、[{"text":1,"value":2,"children":[]}]
 * 2、[{"name":1,"type":2,"children":[]}]
 * guy
 * 对数组的操作
 * @type {{}}
 */
ArrayUtils = {};

$.extend(ArrayUtils, {
    /**
     * 遍历方法
     * @param array
     * @param back
     */
    traversal: function (array, back) {
        if (FR.isNull(array)) {
            return;
        }
        var self = this;
        BI.each(array, function (i, item) {
            if (back(i, item) === BI.Status.END) {
                return false;
            }
            self.traversal(item.children, back);
        })
    },

    getAllChildNames: function (array) {
        var names = [];
        this.traversal(array, function (i, item) {
            if (BI.isNotEmptyArray(item.children)) {
                return BI.Status.RUNNING;
            }
            names.push(item.text || item.name);
        });
        return names;
    },

    /**
     * 获取第一个子节点
     * @param array
     */
    getFirstChild: function (array) {
        var first = {};
        this.traversal(array, function (i, item) {
            if (BI.isNotEmptyArray(item.children)) {
                return;
            }
            first = item;
            return BI.Status.END;
        })
        return first;
    },

    /**
     * 获取最后一个子节点
     * @param array
     */
    getLastChild: function (array) {
        var first = {};
        this.traversal(array, function (i, item) {
            if (item.children && item.children.length > 0) {
                return;
            }
            first = item;
        })
        return first;
    },

    getTextByValue: function (array, value) {
        if (!array) {
            return value;
        }
        var text = "";
        this.traversal(array, function (i, item) {
            if (BI.isEqual(item.value, value)) {
                text = item.text;
                return BI.Status.END;
            }
        });
        return text;
    },

    getNameByType: function (array, type) {
        if (!array) {
            return type;
        }
        var name = "";
        this.traversal(array, function (i, item) {
            if (BI.isEqual(item.type, type)) {
                name = item.name;
                return BI.Status.END;
            }
        });
        return name;
    },

    getItemByText: function (array, text) {
        var res = void 0;
        this.traversal(array, function (i, item) {
            if (BI.isCapitalEqual(item.text, text)) {
                res = item;
                return BI.Status.END;
            }
        });
        return res;
    },

    getIndexByText: function (array, text) {
        var res = -1;
        this.traversal(array, function (i, item) {
            if (BI.isCapitalEqual(item.text, text)) {
                res = i;
                return BI.Status.END;
            }
        });
        return res;
    },

    getItemByValue: function (array, value) {
        var res = void 0;
        this.traversal(array, function (i, item) {
            if (BI.isEqual(item.value, value)) {
                res = item;
                return BI.Status.END;
            }
        });
        return res;
    },

    getIndexByValue: function (array, value) {
        var res = -1;
        this.traversal(array, function (i, item) {
            if (BI.isEqual(item.value, value)) {
                res = i;
                return BI.Status.END;
            }
        });
        return res;
    },

    getItemByName: function (array, name) {
        var res = void 0;
        this.traversal(array, function (i, item) {
            if (BI.isCapitalEqual(item.name, name)) {
                res = item;
                return BI.Status.END;
            }
        });
        return res;
    },

    getIndexByName: function (array, name) {
        var res = -1;
        this.traversal(array, function (i, item) {
            if (BI.isCapitalEqual(item.name, name)) {
                res = i;
                return BI.Status.END;
            }
        });
        return res;
    },

    getItemByType: function (array, type) {
        var res = void 0;
        this.traversal(array, function (i, item) {
            if (BI.isEqual(item.type, type)) {
                res = item;
                return BI.Status.END;
            }
        });
        return res;
    },

    getIndexByType: function (array, type) {
        var res = -1;
        this.traversal(array, function (i, item) {
            if (BI.isEqual(item.type, type)) {
                res = i;
                return BI.Status.END;
            }
        });
        return res;
    },

    deleteItemByType: function (array, type) {
        var item = this.getItemByType(array, type);
        array.remove(item);
    },

    deleteItemByName: function (array, name) {
        var item = this.getItemByName(array, name);
        array.remove(item);
    },

    deleteItemByValue: function (array, value) {
        var item = this.getItemByValue(array, value);
        array.remove(item);
    }
});