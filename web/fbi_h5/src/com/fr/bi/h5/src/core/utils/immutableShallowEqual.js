import Immutable from 'immutable'
import isFunction from 'lodash/isFunction'
var hasOwnProperty = Object.prototype.hasOwnProperty;
export default function shallowEqual(objA, objB) {
    if (Immutable.is(objA, objB)) {
        return true;
    }

    if (typeof objA !== 'object' || objA === null || typeof objB !== 'object' || objB === null) {
        return false;
    }

    var keysA = Object.keys(objA);
    var keysB = Object.keys(objB);

    if (keysA.length !== keysB.length) {
        return false;
    }

    // Test for A's keys different from B.
    for (var i = 0; i < keysA.length; i++) {
        if (isFunction(objA[keysA[i]] && isFunction(objB[keysA[i]]))) {
            continue;
        }
        if (!hasOwnProperty.call(objB, keysA[i]) || !Immutable.is(objA[keysA[i]], objB[keysA[i]])) {
            return false;
        }
    }

    return true;
}