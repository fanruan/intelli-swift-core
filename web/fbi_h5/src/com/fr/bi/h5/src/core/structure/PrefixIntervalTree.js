'use strict';

var invariant = require('fbjs/lib/invariant');

var parent = node => Math.floor(node / 2);

var Int32Array = global.Int32Array ||
    function (size) {
        var xs = [];
        for (var i = size - 1; i >= 0; --i) {
            xs[i] = 0;
        }
        return xs;
    };

function ceilLog2(x:number) {
    var y = 1;
    while (y < x) {
        y *= 2;
    }
    return y;
}

/**
 *
 *   - O(ln n) 更新
 *   - O(1) 查找
 *   - O(ln n) 求和
 *   - O(n) 空间
 *
 */
class PrefixIntervalTree {
    _size;

    _half;

    _heap;

    constructor(xs) {
        this._size = xs.length;
        this._half = ceilLog2(this._size);
        this._heap = new Int32Array(2 * this._half);

        var i;
        for (i = 0; i < this._size; ++i) {
            this._heap[this._half + i] = xs[i];
        }

        for (i = this._half - 1; i > 0; --i) {
            this._heap[i] = this._heap[2 * i] + this._heap[2 * i + 1];
        }
    }

    static uniform(size, initialValue) {
        var xs = [];
        for (var i = size - 1; i >= 0; --i) {
            xs[i] = initialValue;
        }

        return new PrefixIntervalTree(xs);
    }

    static empty(size):PrefixIntervalTree {
        return PrefixIntervalTree.uniform(size, 0);
    }

    set(index, value) {
        invariant(
            0 <= index && index < this._size,
            'Index out of range %s',
            index
        );

        var node = this._half + index;
        this._heap[node] = value;

        node = parent(node);
        for (; node !== 0; node = parent(node)) {
            this._heap[node] =
                this._heap[2 * node] + this._heap[2 * node + 1];
        }
    }

    get(index) {
        invariant(
            0 <= index && index < this._size,
            'Index out of range %s',
            index
        );

        var node = this._half + index;
        return this._heap[node];
    }

    getSize() {
        return this._size;
    }

    /**
     * get(0) + get(1) + ... + get(end - 1).
     */
    sumUntil(end) {
        invariant(
            0 <= end && end < this._size + 1,
            'Index out of range %s',
            end
        );

        if (end === 0) {
            return 0;
        }

        var node = this._half + end - 1;
        var sum = this._heap[node];
        for (; node !== 1; node = parent(node)) {
            if (node % 2 === 1) {
                sum += this._heap[node - 1];
            }
        }

        return sum;
    }

    /**
     * get(0) + get(1) + ... + get(inclusiveEnd).
     */
    sumTo(inclusiveEnd) {
        invariant(
            0 <= inclusiveEnd && inclusiveEnd < this._size,
            'Index out of range %s',
            inclusiveEnd
        );
        return this.sumUntil(inclusiveEnd + 1);
    }

    /**
     * sum get(begin) + get(begin + 1) + ... + get(end - 1).
     */
    sum(begin, end) {
        invariant(begin <= end, 'Begin must precede end');
        return this.sumUntil(end) - this.sumUntil(begin);
    }

    /**
     * Returns the smallest i such that 0 <= i <= size and sumUntil(i) <= t, or
     * -1 if no such i exists.
     */
    greatestLowerBound(t) {
        if (t < 0) {
            return -1;
        }

        var node = 1;
        if (this._heap[node] <= t) {
            return this._size;
        }

        while (node < this._half) {
            var leftSum = this._heap[2 * node];
            if (t < leftSum) {
                node = 2 * node;
            } else {
                node = 2 * node + 1;
                t -= leftSum;
            }
        }

        return node - this._half;
    }

    /**
     * Returns the smallest i such that 0 <= i <= size and sumUntil(i) < t, or
     * -1 if no such i exists.
     */
    greatestStrictLowerBound(t) {
        if (t <= 0) {
            return -1;
        }

        var node = 1;
        if (this._heap[node] < t) {
            return this._size;
        }

        while (node < this._half) {
            var leftSum = this._heap[2 * node];
            if (t <= leftSum) {
                node = 2 * node;
            } else {
                node = 2 * node + 1;
                t -= leftSum;
            }
        }

        return node - this._half;
    }

    /**
     * Returns the smallest i such that 0 <= i <= size and t <= sumUntil(i), or
     * size + 1 if no such i exists.
     */
    leastUpperBound(t) {
        return this.greatestStrictLowerBound(t) + 1;
    }

    /**
     * Returns the smallest i such that 0 <= i <= size and t < sumUntil(i), or
     * size + 1 if no such i exists.
     */
    leastStrictUpperBound(t) {
        return this.greatestLowerBound(t) + 1;
    }
}

module.exports = PrefixIntervalTree;
