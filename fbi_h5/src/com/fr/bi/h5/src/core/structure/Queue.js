class Queue {
    constructor(capacity) {
        this.capacity = capacity;
        this.array = [];
    }

    contains(v) {
        return this.array.indexOf(v) > -1;
    }

    indexOf(v) {
        return this.array.indexOf(v);
    }

    getElementByIndex(index) {
        return this.array[index];
    }

    push(v) {
        this.array.push(v);
        if (this.capacity && this.array.length > this.capacity) {
            this.array.shift();
        }
    }

    pop() {
        this.array.pop();
    }

    shift() {
        this.array.shift();
    }

    unshift(v) {
        this.array.unshift(v);
        if (this.capacity && this.array.length > this.capacity) {
            this.array.pop();
        }
    }

    remove(v) {
        this.array.remove(v);
    }

    splice() {
        this.array.splice.apply(this.array, arguments);
    }

    slice() {
        this.array.slice.apply(this.array, arguments);
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
            var re = fn.call(scope, i, this.array[i], this.array);
            if (re == false) {
                break;
            }
        }
    }

    toArray() {
        return this.array;
    }

    fromArray(array) {
        this.array.forEach((v)=> {
            this.push(v);
        });
    }

    clear() {
        this.array.clear();
    }
}

export default Queue