class LinkHashMap {
    constructor() {
        this.array = [];
        this.map = {};
    }

    has(key) {
        if (key in this.map) {
            return true;
        }
        return false;
    }

    add(key, value) {
        if (typeof key == 'undefined') {
            return;
        }
        if (key in this.map) {
            this.map[key] = value;
        } else {
            this.array.push(key);
            this.map[key] = value;
        }
    }

    remove(key) {
        if (key in this.map) {
            delete this.map[key];
            for (var i = 0; i < this.array.length; i++) {
                if (this.array[i] == key) {
                    this.array.splice(i, 1);
                    break;
                }
            }
        }
    }

    size() {
        return this.array.length;
    }

    each(fn, scope) {
        var scope = scope || window;
        var fn = fn || null;
        if (fn == null || typeof (fn) != "function") {
            return;
        }
        for (var i = 0; i < this.array.length; i++) {
            var key = this.array[i];
            var value = this.map[key];
            var re = fn.call(scope, key, value, i, this.array, this.map);
            if (re == false) {
                break;
            }
        }
    }

    get(key) {
        return this.map[key];
    }

    toArray() {
        var array = [];
        this.each(function (key, value) {
            array.push(value);
        })
        return array;
    }
}

export default LinkHashMap