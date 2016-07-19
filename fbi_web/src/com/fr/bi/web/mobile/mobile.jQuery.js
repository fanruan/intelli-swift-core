var arr = [];

var getProto = Object.getPrototypeOf;

var slice = arr.slice;

var class2type = {};

var hasOwn = class2type.hasOwnProperty;

var fnToString = hasOwn.toString;

var ObjectFunctionString = fnToString.call(Object);

jQuery = function (selector, context) {

    // The jQuery object is actually just the init constructor 'enhanced'
    // Need init if jQuery is called (just allow error to be thrown if not included)
    return new jQuery.fn.init(selector, context);
};

jQuery.fn = jQuery.prototype = {

    // The current version of jQuery being used
    jquery: version,

    constructor: jQuery,

    // The default length of a jQuery object is 0
    length: 0,

    toArray: function () {
        return slice.call(this);
    },

    // Get the Nth element in the matched element set OR
    // Get the whole matched element set as a clean array
    get: function (num) {
        return num != null ?

            // Return just the one element from the set
            ( num < 0 ? this[num + this.length] : this[num] ) :

            // Return all the elements in a clean array
            slice.call(this);
    },

    // Take an array of elements and push it onto the stack
    // (returning the new matched element set)
    pushStack: function (elems) {

        // Build a new jQuery matched element set
        var ret = jQuery.merge(this.constructor(), elems);

        // Add the old object onto the stack (as a reference)
        ret.prevObject = this;

        // Return the newly-formed element set
        return ret;
    },

    // Execute a callback for every element in the matched set.
    each: function (callback) {
        return jQuery.each(this, callback);
    },

    map: function (callback) {
        return this.pushStack(jQuery.map(this, function (elem, i) {
            return callback.call(elem, i, elem);
        }));
    },

    slice: function () {
        return this.pushStack(slice.apply(this, arguments));
    },

    first: function () {
        return this.eq(0);
    },

    last: function () {
        return this.eq(-1);
    },

    eq: function (i) {
        var len = this.length,
            j = +i + ( i < 0 ? len : 0 );
        return this.pushStack(j >= 0 && j < len ? [this[j]] : []);
    },

    end: function () {
        return this.prevObject || this.constructor();
    },

    // For internal use only.
    // Behaves like an Array's method, not like a jQuery method.
    push: push,
    sort: arr.sort,
    splice: arr.splice
};

jQuery.extend({

    isPlainObject: function (obj) {
        var proto, Ctor;

        // Detect obvious negatives
        // Use toString instead of jQuery.type to catch host objects
        if (!obj || toString.call(obj) !== "[object Object]") {
            return false;
        }

        proto = getProto(obj);

        // Objects with no prototype (e.g., `Object.create( null )`) are plain
        if (!proto) {
            return true;
        }

        // Objects with prototype are plain iff they were constructed by a global Object function
        Ctor = hasOwn.call(proto, "constructor") && proto.constructor;
        return typeof Ctor === "function" && fnToString.call(Ctor) === ObjectFunctionString;
    },

    isWindow: function (obj) {
        return obj != null && obj === obj.window;
    },

    isNumeric: function (obj) {

        // As of jQuery 3.0, isNumeric is limited to
        // strings and numbers (primitives or objects)
        // that can be coerced to finite numbers (gh-2662)
        var type = jQuery.type(obj);
        return ( type === "number" || type === "string" ) &&

                // parseFloat NaNs numeric-cast false positives ("")
                // ...but misinterprets leading-number strings, particularly hex literals ("0x...")
                // subtraction forces infinities to NaN
            !isNaN(obj - parseFloat(obj));
    },

    type: function (obj) {
        if (obj == null) {
            return obj + "";
        }

        // Support: Android <=2.3 only (functionish RegExp)
        return typeof obj === "object" || typeof obj === "function" ?
        class2type[toString.call(obj)] || "object" :
            typeof obj;
    },

    trim: function (text) {
        return text == null ?
            "" :
            ( text + "" ).replace(rtrim, "");
    },

    merge: function (first, second) {
        var len = +second.length,
            j = 0,
            i = first.length;

        for (; j < len; j++) {
            first[i++] = second[j];
        }

        first.length = i;

        return first;
    }
});

// Populate the class2type map
jQuery.each("Boolean Number String Function Array Date RegExp Object Error Symbol".split(" "),
    function (i, name) {
        class2type["[object " + name + "]"] = name.toLowerCase();
    });


if (!jQuery.browser) {
    matched = jQuery.uaMatch(navigator.userAgent);
    browser = {};

    if (matched.browser) {
        browser[matched.browser] = true;
        browser.version = matched.version;
    }

    // Chrome is Webkit, but Webkit is also Safari.
    if (browser.chrome) {
        browser.webkit = true;
    } else if (browser.webkit) {
        browser.safari = true;
    }

    jQuery.browser = browser;
}

jQuery.uaMatch = function (ua) {
    ua = ua.toLowerCase();

    var match = /(chrome)[ \/]([\w.]+)/.exec(ua) ||
        /(webkit)[ \/]([\w.]+)/.exec(ua) ||
        /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(ua) ||
        /(msie) ([\w.]+)/.exec(ua) ||
        ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua) ||
        [];

    return {
        browser: match[1] || "",
        version: match[2] || "0"
    };
};


