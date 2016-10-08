import Immutable from 'immutable'

function compare(instance, nextProps, nextState) {
    return !shallowEqual(instance.props, nextProps) ||
        !shallowEqual(instance.state, nextState);
}

var ReactComponentWithImmutableRenderMixin = {
    shouldComponentUpdate: function (nextProps, nextState) {
        return compare(this, nextProps, nextState);
    }
};

var hasOwnProperty = Object.prototype.hasOwnProperty;

function shallowEqual(objA, objB) {
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
        if (!hasOwnProperty.call(objB, keysA[i]) || !Immutable.is(objA[keysA[i]], objB[keysA[i]])) {
            return false;
        }
    }

    return true;
}

module.exports = ReactComponentWithImmutableRenderMixin;