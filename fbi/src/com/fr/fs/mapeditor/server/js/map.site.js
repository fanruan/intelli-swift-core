require=(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
    /*!
     * The buffer module from node.js, for the browser.
     *
     * @author   Feross Aboukhadijeh <feross@feross.org> <http://feross.org>
     * @license  MIT
     */
    /* eslint-disable no-proto */

    'use strict'

    var base64 = require('base64-js')
    var ieee754 = require('ieee754')

    exports.Buffer = Buffer
    exports.SlowBuffer = SlowBuffer
    exports.INSPECT_MAX_BYTES = 50

    var K_MAX_LENGTH = 0x7fffffff
    exports.kMaxLength = K_MAX_LENGTH

    /**
     * If `Buffer.TYPED_ARRAY_SUPPORT`:
     *   === true    Use Uint8Array implementation (fastest)
     *   === false   Print warning and recommend using `buffer` v4.x which has an Object
     *               implementation (most compatible, even IE6)
     *
     * Browsers that support typed arrays are IE 10+, Firefox 4+, Chrome 7+, Safari 5.1+,
     * Opera 11.6+, iOS 4.2+.
     *
     * We report that the browser does not support typed arrays if the are not subclassable
     * using __proto__. Firefox 4-29 lacks support for adding new properties to `Uint8Array`
     * (See: https://bugzilla.mozilla.org/show_bug.cgi?id=695438). IE 10 lacks support
     * for __proto__ and has a buggy typed array implementation.
     */
    Buffer.TYPED_ARRAY_SUPPORT = typedArraySupport()

    if (!Buffer.TYPED_ARRAY_SUPPORT && typeof console !== 'undefined' &&
        typeof console.error === 'function') {
        console.error(
            'This browser lacks typed array (Uint8Array) support which is required by ' +
            '`buffer` v5.x. Use `buffer` v4.x if you require old browser support.'
        )
    }

    function typedArraySupport () {
        // Can typed array instances can be augmented?
        try {
            var arr = new Uint8Array(1)
            arr.__proto__ = {__proto__: Uint8Array.prototype, foo: function () { return 42 }}
            return arr.foo() === 42
        } catch (e) {
            return false
        }
    }

    function createBuffer (length) {
        if (length > K_MAX_LENGTH) {
            throw new RangeError('Invalid typed array length')
        }
        // Return an augmented `Uint8Array` instance
        var buf = new Uint8Array(length)
        buf.__proto__ = Buffer.prototype
        return buf
    }

    /**
     * The Buffer constructor returns instances of `Uint8Array` that have their
     * prototype changed to `Buffer.prototype`. Furthermore, `Buffer` is a subclass of
     * `Uint8Array`, so the returned instances will have all the node `Buffer` methods
     * and the `Uint8Array` methods. Square bracket notation works as expected -- it
     * returns a single octet.
     *
     * The `Uint8Array` prototype remains unmodified.
     */

    function Buffer (arg, encodingOrOffset, length) {
        // Common case.
        if (typeof arg === 'number') {
            if (typeof encodingOrOffset === 'string') {
                throw new Error(
                    'If encoding is specified then the first argument must be a string'
                )
            }
            return allocUnsafe(arg)
        }
        return from(arg, encodingOrOffset, length)
    }

// Fix subarray() in ES2016. See: https://github.com/feross/buffer/pull/97
    if (typeof Symbol !== 'undefined' && Symbol.species &&
        Buffer[Symbol.species] === Buffer) {
        Object.defineProperty(Buffer, Symbol.species, {
            value: null,
            configurable: true,
            enumerable: false,
            writable: false
        })
    }

    Buffer.poolSize = 8192 // not used by this implementation

    function from (value, encodingOrOffset, length) {
        if (typeof value === 'number') {
            throw new TypeError('"value" argument must not be a number')
        }

        if (value instanceof ArrayBuffer) {
            return fromArrayBuffer(value, encodingOrOffset, length)
        }

        if (typeof value === 'string') {
            return fromString(value, encodingOrOffset)
        }

        return fromObject(value)
    }

    /**
     * Functionally equivalent to Buffer(arg, encoding) but throws a TypeError
     * if value is a number.
     * Buffer.from(str[, encoding])
     * Buffer.from(array)
     * Buffer.from(buffer)
     * Buffer.from(arrayBuffer[, byteOffset[, length]])
     **/
    Buffer.from = function (value, encodingOrOffset, length) {
        return from(value, encodingOrOffset, length)
    }

// Note: Change prototype *after* Buffer.from is defined to workaround Chrome bug:
// https://github.com/feross/buffer/pull/148
    Buffer.prototype.__proto__ = Uint8Array.prototype
    Buffer.__proto__ = Uint8Array

    function assertSize (size) {
        if (typeof size !== 'number') {
            throw new TypeError('"size" argument must be a number')
        } else if (size < 0) {
            throw new RangeError('"size" argument must not be negative')
        }
    }

    function alloc (size, fill, encoding) {
        assertSize(size)
        if (size <= 0) {
            return createBuffer(size)
        }
        if (fill !== undefined) {
            // Only pay attention to encoding if it's a string. This
            // prevents accidentally sending in a number that would
            // be interpretted as a start offset.
            return typeof encoding === 'string'
                ? createBuffer(size).fill(fill, encoding)
                : createBuffer(size).fill(fill)
        }
        return createBuffer(size)
    }

    /**
     * Creates a new filled Buffer instance.
     * alloc(size[, fill[, encoding]])
     **/
    Buffer.alloc = function (size, fill, encoding) {
        return alloc(size, fill, encoding)
    }

    function allocUnsafe (size) {
        assertSize(size)
        return createBuffer(size < 0 ? 0 : checked(size) | 0)
    }

    /**
     * Equivalent to Buffer(num), by default creates a non-zero-filled Buffer instance.
     * */
    Buffer.allocUnsafe = function (size) {
        return allocUnsafe(size)
    }
    /**
     * Equivalent to SlowBuffer(num), by default creates a non-zero-filled Buffer instance.
     */
    Buffer.allocUnsafeSlow = function (size) {
        return allocUnsafe(size)
    }

    function fromString (string, encoding) {
        if (typeof encoding !== 'string' || encoding === '') {
            encoding = 'utf8'
        }

        if (!Buffer.isEncoding(encoding)) {
            throw new TypeError('"encoding" must be a valid string encoding')
        }

        var length = byteLength(string, encoding) | 0
        var buf = createBuffer(length)

        var actual = buf.write(string, encoding)

        if (actual !== length) {
            // Writing a hex string, for example, that contains invalid characters will
            // cause everything after the first invalid character to be ignored. (e.g.
            // 'abxxcd' will be treated as 'ab')
            buf = buf.slice(0, actual)
        }

        return buf
    }

    function fromArrayLike (array) {
        var length = array.length < 0 ? 0 : checked(array.length) | 0
        var buf = createBuffer(length)
        for (var i = 0; i < length; i += 1) {
            buf[i] = array[i] & 255
        }
        return buf
    }

    function fromArrayBuffer (array, byteOffset, length) {
        if (byteOffset < 0 || array.byteLength < byteOffset) {
            throw new RangeError('\'offset\' is out of bounds')
        }

        if (array.byteLength < byteOffset + (length || 0)) {
            throw new RangeError('\'length\' is out of bounds')
        }

        var buf
        if (byteOffset === undefined && length === undefined) {
            buf = new Uint8Array(array)
        } else if (length === undefined) {
            buf = new Uint8Array(array, byteOffset)
        } else {
            buf = new Uint8Array(array, byteOffset, length)
        }

        // Return an augmented `Uint8Array` instance
        buf.__proto__ = Buffer.prototype
        return buf
    }

    function fromObject (obj) {
        if (Buffer.isBuffer(obj)) {
            var len = checked(obj.length) | 0
            var buf = createBuffer(len)

            if (buf.length === 0) {
                return buf
            }

            obj.copy(buf, 0, 0, len)
            return buf
        }

        if (obj) {
            if (isArrayBufferView(obj) || 'length' in obj) {
                if (typeof obj.length !== 'number' || numberIsNaN(obj.length)) {
                    return createBuffer(0)
                }
                return fromArrayLike(obj)
            }

            if (obj.type === 'Buffer' && Array.isArray(obj.data)) {
                return fromArrayLike(obj.data)
            }
        }

        throw new TypeError('First argument must be a string, Buffer, ArrayBuffer, Array, or array-like object.')
    }

    function checked (length) {
        // Note: cannot use `length < K_MAX_LENGTH` here because that fails when
        // length is NaN (which is otherwise coerced to zero.)
        if (length >= K_MAX_LENGTH) {
            throw new RangeError('Attempt to allocate Buffer larger than maximum ' +
                'size: 0x' + K_MAX_LENGTH.toString(16) + ' bytes')
        }
        return length | 0
    }

    function SlowBuffer (length) {
        if (+length != length) { // eslint-disable-line eqeqeq
            length = 0
        }
        return Buffer.alloc(+length)
    }

    Buffer.isBuffer = function isBuffer (b) {
        return b != null && b._isBuffer === true
    }

    Buffer.compare = function compare (a, b) {
        if (!Buffer.isBuffer(a) || !Buffer.isBuffer(b)) {
            throw new TypeError('Arguments must be Buffers')
        }

        if (a === b) return 0

        var x = a.length
        var y = b.length

        for (var i = 0, len = Math.min(x, y); i < len; ++i) {
            if (a[i] !== b[i]) {
                x = a[i]
                y = b[i]
                break
            }
        }

        if (x < y) return -1
        if (y < x) return 1
        return 0
    }

    Buffer.isEncoding = function isEncoding (encoding) {
        switch (String(encoding).toLowerCase()) {
            case 'hex':
            case 'utf8':
            case 'utf-8':
            case 'ascii':
            case 'latin1':
            case 'binary':
            case 'base64':
            case 'ucs2':
            case 'ucs-2':
            case 'utf16le':
            case 'utf-16le':
                return true
            default:
                return false
        }
    }

    Buffer.concat = function concat (list, length) {
        if (!Array.isArray(list)) {
            throw new TypeError('"list" argument must be an Array of Buffers')
        }

        if (list.length === 0) {
            return Buffer.alloc(0)
        }

        var i
        if (length === undefined) {
            length = 0
            for (i = 0; i < list.length; ++i) {
                length += list[i].length
            }
        }

        var buffer = Buffer.allocUnsafe(length)
        var pos = 0
        for (i = 0; i < list.length; ++i) {
            var buf = list[i]
            if (!Buffer.isBuffer(buf)) {
                throw new TypeError('"list" argument must be an Array of Buffers')
            }
            buf.copy(buffer, pos)
            pos += buf.length
        }
        return buffer
    }

    function byteLength (string, encoding) {
        if (Buffer.isBuffer(string)) {
            return string.length
        }
        if (isArrayBufferView(string) || string instanceof ArrayBuffer) {
            return string.byteLength
        }
        if (typeof string !== 'string') {
            string = '' + string
        }

        var len = string.length
        if (len === 0) return 0

        // Use a for loop to avoid recursion
        var loweredCase = false
        for (;;) {
            switch (encoding) {
                case 'ascii':
                case 'latin1':
                case 'binary':
                    return len
                case 'utf8':
                case 'utf-8':
                case undefined:
                    return utf8ToBytes(string).length
                case 'ucs2':
                case 'ucs-2':
                case 'utf16le':
                case 'utf-16le':
                    return len * 2
                case 'hex':
                    return len >>> 1
                case 'base64':
                    return base64ToBytes(string).length
                default:
                    if (loweredCase) return utf8ToBytes(string).length // assume utf8
                    encoding = ('' + encoding).toLowerCase()
                    loweredCase = true
            }
        }
    }
    Buffer.byteLength = byteLength

    function slowToString (encoding, start, end) {
        var loweredCase = false

        // No need to verify that "this.length <= MAX_UINT32" since it's a read-only
        // property of a typed array.

        // This behaves neither like String nor Uint8Array in that we set start/end
        // to their upper/lower bounds if the value passed is out of range.
        // undefined is handled specially as per ECMA-262 6th Edition,
        // Section 13.3.3.7 Runtime Semantics: KeyedBindingInitialization.
        if (start === undefined || start < 0) {
            start = 0
        }
        // Return early if start > this.length. Done here to prevent potential uint32
        // coercion fail below.
        if (start > this.length) {
            return ''
        }

        if (end === undefined || end > this.length) {
            end = this.length
        }

        if (end <= 0) {
            return ''
        }

        // Force coersion to uint32. This will also coerce falsey/NaN values to 0.
        end >>>= 0
        start >>>= 0

        if (end <= start) {
            return ''
        }

        if (!encoding) encoding = 'utf8'

        while (true) {
            switch (encoding) {
                case 'hex':
                    return hexSlice(this, start, end)

                case 'utf8':
                case 'utf-8':
                    return utf8Slice(this, start, end)

                case 'ascii':
                    return asciiSlice(this, start, end)

                case 'latin1':
                case 'binary':
                    return latin1Slice(this, start, end)

                case 'base64':
                    return base64Slice(this, start, end)

                case 'ucs2':
                case 'ucs-2':
                case 'utf16le':
                case 'utf-16le':
                    return utf16leSlice(this, start, end)

                default:
                    if (loweredCase) throw new TypeError('Unknown encoding: ' + encoding)
                    encoding = (encoding + '').toLowerCase()
                    loweredCase = true
            }
        }
    }

// This property is used by `Buffer.isBuffer` (and the `is-buffer` npm package)
// to detect a Buffer instance. It's not possible to use `instanceof Buffer`
// reliably in a browserify context because there could be multiple different
// copies of the 'buffer' package in use. This method works even for Buffer
// instances that were created from another copy of the `buffer` package.
// See: https://github.com/feross/buffer/issues/154
    Buffer.prototype._isBuffer = true

    function swap (b, n, m) {
        var i = b[n]
        b[n] = b[m]
        b[m] = i
    }

    Buffer.prototype.swap16 = function swap16 () {
        var len = this.length
        if (len % 2 !== 0) {
            throw new RangeError('Buffer size must be a multiple of 16-bits')
        }
        for (var i = 0; i < len; i += 2) {
            swap(this, i, i + 1)
        }
        return this
    }

    Buffer.prototype.swap32 = function swap32 () {
        var len = this.length
        if (len % 4 !== 0) {
            throw new RangeError('Buffer size must be a multiple of 32-bits')
        }
        for (var i = 0; i < len; i += 4) {
            swap(this, i, i + 3)
            swap(this, i + 1, i + 2)
        }
        return this
    }

    Buffer.prototype.swap64 = function swap64 () {
        var len = this.length
        if (len % 8 !== 0) {
            throw new RangeError('Buffer size must be a multiple of 64-bits')
        }
        for (var i = 0; i < len; i += 8) {
            swap(this, i, i + 7)
            swap(this, i + 1, i + 6)
            swap(this, i + 2, i + 5)
            swap(this, i + 3, i + 4)
        }
        return this
    }

    Buffer.prototype.toString = function toString () {
        var length = this.length
        if (length === 0) return ''
        if (arguments.length === 0) return utf8Slice(this, 0, length)
        return slowToString.apply(this, arguments)
    }

    Buffer.prototype.equals = function equals (b) {
        if (!Buffer.isBuffer(b)) throw new TypeError('Argument must be a Buffer')
        if (this === b) return true
        return Buffer.compare(this, b) === 0
    }

    Buffer.prototype.inspect = function inspect () {
        var str = ''
        var max = exports.INSPECT_MAX_BYTES
        if (this.length > 0) {
            str = this.toString('hex', 0, max).match(/.{2}/g).join(' ')
            if (this.length > max) str += ' ... '
        }
        return '<Buffer ' + str + '>'
    }

    Buffer.prototype.compare = function compare (target, start, end, thisStart, thisEnd) {
        if (!Buffer.isBuffer(target)) {
            throw new TypeError('Argument must be a Buffer')
        }

        if (start === undefined) {
            start = 0
        }
        if (end === undefined) {
            end = target ? target.length : 0
        }
        if (thisStart === undefined) {
            thisStart = 0
        }
        if (thisEnd === undefined) {
            thisEnd = this.length
        }

        if (start < 0 || end > target.length || thisStart < 0 || thisEnd > this.length) {
            throw new RangeError('out of range index')
        }

        if (thisStart >= thisEnd && start >= end) {
            return 0
        }
        if (thisStart >= thisEnd) {
            return -1
        }
        if (start >= end) {
            return 1
        }

        start >>>= 0
        end >>>= 0
        thisStart >>>= 0
        thisEnd >>>= 0

        if (this === target) return 0

        var x = thisEnd - thisStart
        var y = end - start
        var len = Math.min(x, y)

        var thisCopy = this.slice(thisStart, thisEnd)
        var targetCopy = target.slice(start, end)

        for (var i = 0; i < len; ++i) {
            if (thisCopy[i] !== targetCopy[i]) {
                x = thisCopy[i]
                y = targetCopy[i]
                break
            }
        }

        if (x < y) return -1
        if (y < x) return 1
        return 0
    }

// Finds either the first index of `val` in `buffer` at offset >= `byteOffset`,
// OR the last index of `val` in `buffer` at offset <= `byteOffset`.
//
// Arguments:
// - buffer - a Buffer to search
// - val - a string, Buffer, or number
// - byteOffset - an index into `buffer`; will be clamped to an int32
// - encoding - an optional encoding, relevant is val is a string
// - dir - true for indexOf, false for lastIndexOf
    function bidirectionalIndexOf (buffer, val, byteOffset, encoding, dir) {
        // Empty buffer means no match
        if (buffer.length === 0) return -1

        // Normalize byteOffset
        if (typeof byteOffset === 'string') {
            encoding = byteOffset
            byteOffset = 0
        } else if (byteOffset > 0x7fffffff) {
            byteOffset = 0x7fffffff
        } else if (byteOffset < -0x80000000) {
            byteOffset = -0x80000000
        }
        byteOffset = +byteOffset  // Coerce to Number.
        if (numberIsNaN(byteOffset)) {
            // byteOffset: it it's undefined, null, NaN, "foo", etc, search whole buffer
            byteOffset = dir ? 0 : (buffer.length - 1)
        }

        // Normalize byteOffset: negative offsets start from the end of the buffer
        if (byteOffset < 0) byteOffset = buffer.length + byteOffset
        if (byteOffset >= buffer.length) {
            if (dir) return -1
            else byteOffset = buffer.length - 1
        } else if (byteOffset < 0) {
            if (dir) byteOffset = 0
            else return -1
        }

        // Normalize val
        if (typeof val === 'string') {
            val = Buffer.from(val, encoding)
        }

        // Finally, search either indexOf (if dir is true) or lastIndexOf
        if (Buffer.isBuffer(val)) {
            // Special case: looking for empty string/buffer always fails
            if (val.length === 0) {
                return -1
            }
            return arrayIndexOf(buffer, val, byteOffset, encoding, dir)
        } else if (typeof val === 'number') {
            val = val & 0xFF // Search for a byte value [0-255]
            if (typeof Uint8Array.prototype.indexOf === 'function') {
                if (dir) {
                    return Uint8Array.prototype.indexOf.call(buffer, val, byteOffset)
                } else {
                    return Uint8Array.prototype.lastIndexOf.call(buffer, val, byteOffset)
                }
            }
            return arrayIndexOf(buffer, [ val ], byteOffset, encoding, dir)
        }

        throw new TypeError('val must be string, number or Buffer')
    }

    function arrayIndexOf (arr, val, byteOffset, encoding, dir) {
        var indexSize = 1
        var arrLength = arr.length
        var valLength = val.length

        if (encoding !== undefined) {
            encoding = String(encoding).toLowerCase()
            if (encoding === 'ucs2' || encoding === 'ucs-2' ||
                encoding === 'utf16le' || encoding === 'utf-16le') {
                if (arr.length < 2 || val.length < 2) {
                    return -1
                }
                indexSize = 2
                arrLength /= 2
                valLength /= 2
                byteOffset /= 2
            }
        }

        function read (buf, i) {
            if (indexSize === 1) {
                return buf[i]
            } else {
                return buf.readUInt16BE(i * indexSize)
            }
        }

        var i
        if (dir) {
            var foundIndex = -1
            for (i = byteOffset; i < arrLength; i++) {
                if (read(arr, i) === read(val, foundIndex === -1 ? 0 : i - foundIndex)) {
                    if (foundIndex === -1) foundIndex = i
                    if (i - foundIndex + 1 === valLength) return foundIndex * indexSize
                } else {
                    if (foundIndex !== -1) i -= i - foundIndex
                    foundIndex = -1
                }
            }
        } else {
            if (byteOffset + valLength > arrLength) byteOffset = arrLength - valLength
            for (i = byteOffset; i >= 0; i--) {
                var found = true
                for (var j = 0; j < valLength; j++) {
                    if (read(arr, i + j) !== read(val, j)) {
                        found = false
                        break
                    }
                }
                if (found) return i
            }
        }

        return -1
    }

    Buffer.prototype.includes = function includes (val, byteOffset, encoding) {
        return this.indexOf(val, byteOffset, encoding) !== -1
    }

    Buffer.prototype.indexOf = function indexOf (val, byteOffset, encoding) {
        return bidirectionalIndexOf(this, val, byteOffset, encoding, true)
    }

    Buffer.prototype.lastIndexOf = function lastIndexOf (val, byteOffset, encoding) {
        return bidirectionalIndexOf(this, val, byteOffset, encoding, false)
    }

    function hexWrite (buf, string, offset, length) {
        offset = Number(offset) || 0
        var remaining = buf.length - offset
        if (!length) {
            length = remaining
        } else {
            length = Number(length)
            if (length > remaining) {
                length = remaining
            }
        }

        // must be an even number of digits
        var strLen = string.length
        if (strLen % 2 !== 0) throw new TypeError('Invalid hex string')

        if (length > strLen / 2) {
            length = strLen / 2
        }
        for (var i = 0; i < length; ++i) {
            var parsed = parseInt(string.substr(i * 2, 2), 16)
            if (numberIsNaN(parsed)) return i
            buf[offset + i] = parsed
        }
        return i
    }

    function utf8Write (buf, string, offset, length) {
        return blitBuffer(utf8ToBytes(string, buf.length - offset), buf, offset, length)
    }

    function asciiWrite (buf, string, offset, length) {
        return blitBuffer(asciiToBytes(string), buf, offset, length)
    }

    function latin1Write (buf, string, offset, length) {
        return asciiWrite(buf, string, offset, length)
    }

    function base64Write (buf, string, offset, length) {
        return blitBuffer(base64ToBytes(string), buf, offset, length)
    }

    function ucs2Write (buf, string, offset, length) {
        return blitBuffer(utf16leToBytes(string, buf.length - offset), buf, offset, length)
    }

    Buffer.prototype.write = function write (string, offset, length, encoding) {
        // Buffer#write(string)
        if (offset === undefined) {
            encoding = 'utf8'
            length = this.length
            offset = 0
            // Buffer#write(string, encoding)
        } else if (length === undefined && typeof offset === 'string') {
            encoding = offset
            length = this.length
            offset = 0
            // Buffer#write(string, offset[, length][, encoding])
        } else if (isFinite(offset)) {
            offset = offset >>> 0
            if (isFinite(length)) {
                length = length >>> 0
                if (encoding === undefined) encoding = 'utf8'
            } else {
                encoding = length
                length = undefined
            }
        } else {
            throw new Error(
                'Buffer.write(string, encoding, offset[, length]) is no longer supported'
            )
        }

        var remaining = this.length - offset
        if (length === undefined || length > remaining) length = remaining

        if ((string.length > 0 && (length < 0 || offset < 0)) || offset > this.length) {
            throw new RangeError('Attempt to write outside buffer bounds')
        }

        if (!encoding) encoding = 'utf8'

        var loweredCase = false
        for (;;) {
            switch (encoding) {
                case 'hex':
                    return hexWrite(this, string, offset, length)

                case 'utf8':
                case 'utf-8':
                    return utf8Write(this, string, offset, length)

                case 'ascii':
                    return asciiWrite(this, string, offset, length)

                case 'latin1':
                case 'binary':
                    return latin1Write(this, string, offset, length)

                case 'base64':
                    // Warning: maxLength not taken into account in base64Write
                    return base64Write(this, string, offset, length)

                case 'ucs2':
                case 'ucs-2':
                case 'utf16le':
                case 'utf-16le':
                    return ucs2Write(this, string, offset, length)

                default:
                    if (loweredCase) throw new TypeError('Unknown encoding: ' + encoding)
                    encoding = ('' + encoding).toLowerCase()
                    loweredCase = true
            }
        }
    }

    Buffer.prototype.toJSON = function toJSON () {
        return {
            type: 'Buffer',
            data: Array.prototype.slice.call(this._arr || this, 0)
        }
    }

    function base64Slice (buf, start, end) {
        if (start === 0 && end === buf.length) {
            return base64.fromByteArray(buf)
        } else {
            return base64.fromByteArray(buf.slice(start, end))
        }
    }

    function utf8Slice (buf, start, end) {
        end = Math.min(buf.length, end)
        var res = []

        var i = start
        while (i < end) {
            var firstByte = buf[i]
            var codePoint = null
            var bytesPerSequence = (firstByte > 0xEF) ? 4
                : (firstByte > 0xDF) ? 3
                : (firstByte > 0xBF) ? 2
                : 1

            if (i + bytesPerSequence <= end) {
                var secondByte, thirdByte, fourthByte, tempCodePoint

                switch (bytesPerSequence) {
                    case 1:
                        if (firstByte < 0x80) {
                            codePoint = firstByte
                        }
                        break
                    case 2:
                        secondByte = buf[i + 1]
                        if ((secondByte & 0xC0) === 0x80) {
                            tempCodePoint = (firstByte & 0x1F) << 0x6 | (secondByte & 0x3F)
                            if (tempCodePoint > 0x7F) {
                                codePoint = tempCodePoint
                            }
                        }
                        break
                    case 3:
                        secondByte = buf[i + 1]
                        thirdByte = buf[i + 2]
                        if ((secondByte & 0xC0) === 0x80 && (thirdByte & 0xC0) === 0x80) {
                            tempCodePoint = (firstByte & 0xF) << 0xC | (secondByte & 0x3F) << 0x6 | (thirdByte & 0x3F)
                            if (tempCodePoint > 0x7FF && (tempCodePoint < 0xD800 || tempCodePoint > 0xDFFF)) {
                                codePoint = tempCodePoint
                            }
                        }
                        break
                    case 4:
                        secondByte = buf[i + 1]
                        thirdByte = buf[i + 2]
                        fourthByte = buf[i + 3]
                        if ((secondByte & 0xC0) === 0x80 && (thirdByte & 0xC0) === 0x80 && (fourthByte & 0xC0) === 0x80) {
                            tempCodePoint = (firstByte & 0xF) << 0x12 | (secondByte & 0x3F) << 0xC | (thirdByte & 0x3F) << 0x6 | (fourthByte & 0x3F)
                            if (tempCodePoint > 0xFFFF && tempCodePoint < 0x110000) {
                                codePoint = tempCodePoint
                            }
                        }
                }
            }

            if (codePoint === null) {
                // we did not generate a valid codePoint so insert a
                // replacement char (U+FFFD) and advance only 1 byte
                codePoint = 0xFFFD
                bytesPerSequence = 1
            } else if (codePoint > 0xFFFF) {
                // encode to utf16 (surrogate pair dance)
                codePoint -= 0x10000
                res.push(codePoint >>> 10 & 0x3FF | 0xD800)
                codePoint = 0xDC00 | codePoint & 0x3FF
            }

            res.push(codePoint)
            i += bytesPerSequence
        }

        return decodeCodePointsArray(res)
    }

// Based on http://stackoverflow.com/a/22747272/680742, the browser with
// the lowest limit is Chrome, with 0x10000 args.
// We go 1 magnitude less, for safety
    var MAX_ARGUMENTS_LENGTH = 0x1000

    function decodeCodePointsArray (codePoints) {
        var len = codePoints.length
        if (len <= MAX_ARGUMENTS_LENGTH) {
            return String.fromCharCode.apply(String, codePoints) // avoid extra slice()
        }

        // Decode in chunks to avoid "call stack size exceeded".
        var res = ''
        var i = 0
        while (i < len) {
            res += String.fromCharCode.apply(
                String,
                codePoints.slice(i, i += MAX_ARGUMENTS_LENGTH)
            )
        }
        return res
    }

    function asciiSlice (buf, start, end) {
        var ret = ''
        end = Math.min(buf.length, end)

        for (var i = start; i < end; ++i) {
            ret += String.fromCharCode(buf[i] & 0x7F)
        }
        return ret
    }

    function latin1Slice (buf, start, end) {
        var ret = ''
        end = Math.min(buf.length, end)

        for (var i = start; i < end; ++i) {
            ret += String.fromCharCode(buf[i])
        }
        return ret
    }

    function hexSlice (buf, start, end) {
        var len = buf.length

        if (!start || start < 0) start = 0
        if (!end || end < 0 || end > len) end = len

        var out = ''
        for (var i = start; i < end; ++i) {
            out += toHex(buf[i])
        }
        return out
    }

    function utf16leSlice (buf, start, end) {
        var bytes = buf.slice(start, end)
        var res = ''
        for (var i = 0; i < bytes.length; i += 2) {
            res += String.fromCharCode(bytes[i] + (bytes[i + 1] * 256))
        }
        return res
    }

    Buffer.prototype.slice = function slice (start, end) {
        var len = this.length
        start = ~~start
        end = end === undefined ? len : ~~end

        if (start < 0) {
            start += len
            if (start < 0) start = 0
        } else if (start > len) {
            start = len
        }

        if (end < 0) {
            end += len
            if (end < 0) end = 0
        } else if (end > len) {
            end = len
        }

        if (end < start) end = start

        var newBuf = this.subarray(start, end)
        // Return an augmented `Uint8Array` instance
        newBuf.__proto__ = Buffer.prototype
        return newBuf
    }

    /*
     * Need to make sure that buffer isn't trying to write out of bounds.
     */
    function checkOffset (offset, ext, length) {
        if ((offset % 1) !== 0 || offset < 0) throw new RangeError('offset is not uint')
        if (offset + ext > length) throw new RangeError('Trying to access beyond buffer length')
    }

    Buffer.prototype.readUIntLE = function readUIntLE (offset, byteLength, noAssert) {
        offset = offset >>> 0
        byteLength = byteLength >>> 0
        if (!noAssert) checkOffset(offset, byteLength, this.length)

        var val = this[offset]
        var mul = 1
        var i = 0
        while (++i < byteLength && (mul *= 0x100)) {
            val += this[offset + i] * mul
        }

        return val
    }

    Buffer.prototype.readUIntBE = function readUIntBE (offset, byteLength, noAssert) {
        offset = offset >>> 0
        byteLength = byteLength >>> 0
        if (!noAssert) {
            checkOffset(offset, byteLength, this.length)
        }

        var val = this[offset + --byteLength]
        var mul = 1
        while (byteLength > 0 && (mul *= 0x100)) {
            val += this[offset + --byteLength] * mul
        }

        return val
    }

    Buffer.prototype.readUInt8 = function readUInt8 (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 1, this.length)
        return this[offset]
    }

    Buffer.prototype.readUInt16LE = function readUInt16LE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 2, this.length)
        return this[offset] | (this[offset + 1] << 8)
    }

    Buffer.prototype.readUInt16BE = function readUInt16BE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 2, this.length)
        return (this[offset] << 8) | this[offset + 1]
    }

    Buffer.prototype.readUInt32LE = function readUInt32LE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 4, this.length)

        return ((this[offset]) |
            (this[offset + 1] << 8) |
            (this[offset + 2] << 16)) +
            (this[offset + 3] * 0x1000000)
    }

    Buffer.prototype.readUInt32BE = function readUInt32BE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 4, this.length)

        return (this[offset] * 0x1000000) +
            ((this[offset + 1] << 16) |
            (this[offset + 2] << 8) |
            this[offset + 3])
    }

    Buffer.prototype.readIntLE = function readIntLE (offset, byteLength, noAssert) {
        offset = offset >>> 0
        byteLength = byteLength >>> 0
        if (!noAssert) checkOffset(offset, byteLength, this.length)

        var val = this[offset]
        var mul = 1
        var i = 0
        while (++i < byteLength && (mul *= 0x100)) {
            val += this[offset + i] * mul
        }
        mul *= 0x80

        if (val >= mul) val -= Math.pow(2, 8 * byteLength)

        return val
    }

    Buffer.prototype.readIntBE = function readIntBE (offset, byteLength, noAssert) {
        offset = offset >>> 0
        byteLength = byteLength >>> 0
        if (!noAssert) checkOffset(offset, byteLength, this.length)

        var i = byteLength
        var mul = 1
        var val = this[offset + --i]
        while (i > 0 && (mul *= 0x100)) {
            val += this[offset + --i] * mul
        }
        mul *= 0x80

        if (val >= mul) val -= Math.pow(2, 8 * byteLength)

        return val
    }

    Buffer.prototype.readInt8 = function readInt8 (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 1, this.length)
        if (!(this[offset] & 0x80)) return (this[offset])
        return ((0xff - this[offset] + 1) * -1)
    }

    Buffer.prototype.readInt16LE = function readInt16LE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 2, this.length)
        var val = this[offset] | (this[offset + 1] << 8)
        return (val & 0x8000) ? val | 0xFFFF0000 : val
    }

    Buffer.prototype.readInt16BE = function readInt16BE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 2, this.length)
        var val = this[offset + 1] | (this[offset] << 8)
        return (val & 0x8000) ? val | 0xFFFF0000 : val
    }

    Buffer.prototype.readInt32LE = function readInt32LE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 4, this.length)

        return (this[offset]) |
            (this[offset + 1] << 8) |
            (this[offset + 2] << 16) |
            (this[offset + 3] << 24)
    }

    Buffer.prototype.readInt32BE = function readInt32BE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 4, this.length)

        return (this[offset] << 24) |
            (this[offset + 1] << 16) |
            (this[offset + 2] << 8) |
            (this[offset + 3])
    }

    Buffer.prototype.readFloatLE = function readFloatLE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 4, this.length)
        return ieee754.read(this, offset, true, 23, 4)
    }

    Buffer.prototype.readFloatBE = function readFloatBE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 4, this.length)
        return ieee754.read(this, offset, false, 23, 4)
    }

    Buffer.prototype.readDoubleLE = function readDoubleLE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 8, this.length)
        return ieee754.read(this, offset, true, 52, 8)
    }

    Buffer.prototype.readDoubleBE = function readDoubleBE (offset, noAssert) {
        offset = offset >>> 0
        if (!noAssert) checkOffset(offset, 8, this.length)
        return ieee754.read(this, offset, false, 52, 8)
    }

    function checkInt (buf, value, offset, ext, max, min) {
        if (!Buffer.isBuffer(buf)) throw new TypeError('"buffer" argument must be a Buffer instance')
        if (value > max || value < min) throw new RangeError('"value" argument is out of bounds')
        if (offset + ext > buf.length) throw new RangeError('Index out of range')
    }

    Buffer.prototype.writeUIntLE = function writeUIntLE (value, offset, byteLength, noAssert) {
        value = +value
        offset = offset >>> 0
        byteLength = byteLength >>> 0
        if (!noAssert) {
            var maxBytes = Math.pow(2, 8 * byteLength) - 1
            checkInt(this, value, offset, byteLength, maxBytes, 0)
        }

        var mul = 1
        var i = 0
        this[offset] = value & 0xFF
        while (++i < byteLength && (mul *= 0x100)) {
            this[offset + i] = (value / mul) & 0xFF
        }

        return offset + byteLength
    }

    Buffer.prototype.writeUIntBE = function writeUIntBE (value, offset, byteLength, noAssert) {
        value = +value
        offset = offset >>> 0
        byteLength = byteLength >>> 0
        if (!noAssert) {
            var maxBytes = Math.pow(2, 8 * byteLength) - 1
            checkInt(this, value, offset, byteLength, maxBytes, 0)
        }

        var i = byteLength - 1
        var mul = 1
        this[offset + i] = value & 0xFF
        while (--i >= 0 && (mul *= 0x100)) {
            this[offset + i] = (value / mul) & 0xFF
        }

        return offset + byteLength
    }

    Buffer.prototype.writeUInt8 = function writeUInt8 (value, offset, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) checkInt(this, value, offset, 1, 0xff, 0)
        this[offset] = (value & 0xff)
        return offset + 1
    }

    Buffer.prototype.writeUInt16LE = function writeUInt16LE (value, offset, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) checkInt(this, value, offset, 2, 0xffff, 0)
        this[offset] = (value & 0xff)
        this[offset + 1] = (value >>> 8)
        return offset + 2
    }

    Buffer.prototype.writeUInt16BE = function writeUInt16BE (value, offset, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) checkInt(this, value, offset, 2, 0xffff, 0)
        this[offset] = (value >>> 8)
        this[offset + 1] = (value & 0xff)
        return offset + 2
    }

    Buffer.prototype.writeUInt32LE = function writeUInt32LE (value, offset, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) checkInt(this, value, offset, 4, 0xffffffff, 0)
        this[offset + 3] = (value >>> 24)
        this[offset + 2] = (value >>> 16)
        this[offset + 1] = (value >>> 8)
        this[offset] = (value & 0xff)
        return offset + 4
    }

    Buffer.prototype.writeUInt32BE = function writeUInt32BE (value, offset, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) checkInt(this, value, offset, 4, 0xffffffff, 0)
        this[offset] = (value >>> 24)
        this[offset + 1] = (value >>> 16)
        this[offset + 2] = (value >>> 8)
        this[offset + 3] = (value & 0xff)
        return offset + 4
    }

    Buffer.prototype.writeIntLE = function writeIntLE (value, offset, byteLength, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) {
            var limit = Math.pow(2, (8 * byteLength) - 1)

            checkInt(this, value, offset, byteLength, limit - 1, -limit)
        }

        var i = 0
        var mul = 1
        var sub = 0
        this[offset] = value & 0xFF
        while (++i < byteLength && (mul *= 0x100)) {
            if (value < 0 && sub === 0 && this[offset + i - 1] !== 0) {
                sub = 1
            }
            this[offset + i] = ((value / mul) >> 0) - sub & 0xFF
        }

        return offset + byteLength
    }

    Buffer.prototype.writeIntBE = function writeIntBE (value, offset, byteLength, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) {
            var limit = Math.pow(2, (8 * byteLength) - 1)

            checkInt(this, value, offset, byteLength, limit - 1, -limit)
        }

        var i = byteLength - 1
        var mul = 1
        var sub = 0
        this[offset + i] = value & 0xFF
        while (--i >= 0 && (mul *= 0x100)) {
            if (value < 0 && sub === 0 && this[offset + i + 1] !== 0) {
                sub = 1
            }
            this[offset + i] = ((value / mul) >> 0) - sub & 0xFF
        }

        return offset + byteLength
    }

    Buffer.prototype.writeInt8 = function writeInt8 (value, offset, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) checkInt(this, value, offset, 1, 0x7f, -0x80)
        if (value < 0) value = 0xff + value + 1
        this[offset] = (value & 0xff)
        return offset + 1
    }

    Buffer.prototype.writeInt16LE = function writeInt16LE (value, offset, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) checkInt(this, value, offset, 2, 0x7fff, -0x8000)
        this[offset] = (value & 0xff)
        this[offset + 1] = (value >>> 8)
        return offset + 2
    }

    Buffer.prototype.writeInt16BE = function writeInt16BE (value, offset, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) checkInt(this, value, offset, 2, 0x7fff, -0x8000)
        this[offset] = (value >>> 8)
        this[offset + 1] = (value & 0xff)
        return offset + 2
    }

    Buffer.prototype.writeInt32LE = function writeInt32LE (value, offset, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) checkInt(this, value, offset, 4, 0x7fffffff, -0x80000000)
        this[offset] = (value & 0xff)
        this[offset + 1] = (value >>> 8)
        this[offset + 2] = (value >>> 16)
        this[offset + 3] = (value >>> 24)
        return offset + 4
    }

    Buffer.prototype.writeInt32BE = function writeInt32BE (value, offset, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) checkInt(this, value, offset, 4, 0x7fffffff, -0x80000000)
        if (value < 0) value = 0xffffffff + value + 1
        this[offset] = (value >>> 24)
        this[offset + 1] = (value >>> 16)
        this[offset + 2] = (value >>> 8)
        this[offset + 3] = (value & 0xff)
        return offset + 4
    }

    function checkIEEE754 (buf, value, offset, ext, max, min) {
        if (offset + ext > buf.length) throw new RangeError('Index out of range')
        if (offset < 0) throw new RangeError('Index out of range')
    }

    function writeFloat (buf, value, offset, littleEndian, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) {
            checkIEEE754(buf, value, offset, 4, 3.4028234663852886e+38, -3.4028234663852886e+38)
        }
        ieee754.write(buf, value, offset, littleEndian, 23, 4)
        return offset + 4
    }

    Buffer.prototype.writeFloatLE = function writeFloatLE (value, offset, noAssert) {
        return writeFloat(this, value, offset, true, noAssert)
    }

    Buffer.prototype.writeFloatBE = function writeFloatBE (value, offset, noAssert) {
        return writeFloat(this, value, offset, false, noAssert)
    }

    function writeDouble (buf, value, offset, littleEndian, noAssert) {
        value = +value
        offset = offset >>> 0
        if (!noAssert) {
            checkIEEE754(buf, value, offset, 8, 1.7976931348623157E+308, -1.7976931348623157E+308)
        }
        ieee754.write(buf, value, offset, littleEndian, 52, 8)
        return offset + 8
    }

    Buffer.prototype.writeDoubleLE = function writeDoubleLE (value, offset, noAssert) {
        return writeDouble(this, value, offset, true, noAssert)
    }

    Buffer.prototype.writeDoubleBE = function writeDoubleBE (value, offset, noAssert) {
        return writeDouble(this, value, offset, false, noAssert)
    }

// copy(targetBuffer, targetStart=0, sourceStart=0, sourceEnd=buffer.length)
    Buffer.prototype.copy = function copy (target, targetStart, start, end) {
        if (!start) start = 0
        if (!end && end !== 0) end = this.length
        if (targetStart >= target.length) targetStart = target.length
        if (!targetStart) targetStart = 0
        if (end > 0 && end < start) end = start

        // Copy 0 bytes; we're done
        if (end === start) return 0
        if (target.length === 0 || this.length === 0) return 0

        // Fatal error conditions
        if (targetStart < 0) {
            throw new RangeError('targetStart out of bounds')
        }
        if (start < 0 || start >= this.length) throw new RangeError('sourceStart out of bounds')
        if (end < 0) throw new RangeError('sourceEnd out of bounds')

        // Are we oob?
        if (end > this.length) end = this.length
        if (target.length - targetStart < end - start) {
            end = target.length - targetStart + start
        }

        var len = end - start
        var i

        if (this === target && start < targetStart && targetStart < end) {
            // descending copy from end
            for (i = len - 1; i >= 0; --i) {
                target[i + targetStart] = this[i + start]
            }
        } else if (len < 1000) {
            // ascending copy from start
            for (i = 0; i < len; ++i) {
                target[i + targetStart] = this[i + start]
            }
        } else {
            Uint8Array.prototype.set.call(
                target,
                this.subarray(start, start + len),
                targetStart
            )
        }

        return len
    }

// Usage:
//    buffer.fill(number[, offset[, end]])
//    buffer.fill(buffer[, offset[, end]])
//    buffer.fill(string[, offset[, end]][, encoding])
    Buffer.prototype.fill = function fill (val, start, end, encoding) {
        // Handle string cases:
        if (typeof val === 'string') {
            if (typeof start === 'string') {
                encoding = start
                start = 0
                end = this.length
            } else if (typeof end === 'string') {
                encoding = end
                end = this.length
            }
            if (val.length === 1) {
                var code = val.charCodeAt(0)
                if (code < 256) {
                    val = code
                }
            }
            if (encoding !== undefined && typeof encoding !== 'string') {
                throw new TypeError('encoding must be a string')
            }
            if (typeof encoding === 'string' && !Buffer.isEncoding(encoding)) {
                throw new TypeError('Unknown encoding: ' + encoding)
            }
        } else if (typeof val === 'number') {
            val = val & 255
        }

        // Invalid ranges are not set to a default, so can range check early.
        if (start < 0 || this.length < start || this.length < end) {
            throw new RangeError('Out of range index')
        }

        if (end <= start) {
            return this
        }

        start = start >>> 0
        end = end === undefined ? this.length : end >>> 0

        if (!val) val = 0

        var i
        if (typeof val === 'number') {
            for (i = start; i < end; ++i) {
                this[i] = val
            }
        } else {
            var bytes = Buffer.isBuffer(val)
                ? val
                : new Buffer(val, encoding)
            var len = bytes.length
            for (i = 0; i < end - start; ++i) {
                this[i + start] = bytes[i % len]
            }
        }

        return this
    }

// HELPER FUNCTIONS
// ================

    var INVALID_BASE64_RE = /[^+/0-9A-Za-z-_]/g

    function base64clean (str) {
        // Node strips out invalid characters like \n and \t from the string, base64-js does not
        str = str.trim().replace(INVALID_BASE64_RE, '')
        // Node converts strings with length < 2 to ''
        if (str.length < 2) return ''
        // Node allows for non-padded base64 strings (missing trailing ===), base64-js does not
        while (str.length % 4 !== 0) {
            str = str + '='
        }
        return str
    }

    function toHex (n) {
        if (n < 16) return '0' + n.toString(16)
        return n.toString(16)
    }

    function utf8ToBytes (string, units) {
        units = units || Infinity
        var codePoint
        var length = string.length
        var leadSurrogate = null
        var bytes = []

        for (var i = 0; i < length; ++i) {
            codePoint = string.charCodeAt(i)

            // is surrogate component
            if (codePoint > 0xD7FF && codePoint < 0xE000) {
                // last char was a lead
                if (!leadSurrogate) {
                    // no lead yet
                    if (codePoint > 0xDBFF) {
                        // unexpected trail
                        if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD)
                        continue
                    } else if (i + 1 === length) {
                        // unpaired lead
                        if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD)
                        continue
                    }

                    // valid lead
                    leadSurrogate = codePoint

                    continue
                }

                // 2 leads in a row
                if (codePoint < 0xDC00) {
                    if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD)
                    leadSurrogate = codePoint
                    continue
                }

                // valid surrogate pair
                codePoint = (leadSurrogate - 0xD800 << 10 | codePoint - 0xDC00) + 0x10000
            } else if (leadSurrogate) {
                // valid bmp char, but last char was a lead
                if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD)
            }

            leadSurrogate = null

            // encode utf8
            if (codePoint < 0x80) {
                if ((units -= 1) < 0) break
                bytes.push(codePoint)
            } else if (codePoint < 0x800) {
                if ((units -= 2) < 0) break
                bytes.push(
                    codePoint >> 0x6 | 0xC0,
                    codePoint & 0x3F | 0x80
                )
            } else if (codePoint < 0x10000) {
                if ((units -= 3) < 0) break
                bytes.push(
                    codePoint >> 0xC | 0xE0,
                    codePoint >> 0x6 & 0x3F | 0x80,
                    codePoint & 0x3F | 0x80
                )
            } else if (codePoint < 0x110000) {
                if ((units -= 4) < 0) break
                bytes.push(
                    codePoint >> 0x12 | 0xF0,
                    codePoint >> 0xC & 0x3F | 0x80,
                    codePoint >> 0x6 & 0x3F | 0x80,
                    codePoint & 0x3F | 0x80
                )
            } else {
                throw new Error('Invalid code point')
            }
        }

        return bytes
    }

    function asciiToBytes (str) {
        var byteArray = []
        for (var i = 0; i < str.length; ++i) {
            // Node's code seems to be doing this and not & 0x7F..
            byteArray.push(str.charCodeAt(i) & 0xFF)
        }
        return byteArray
    }

    function utf16leToBytes (str, units) {
        var c, hi, lo
        var byteArray = []
        for (var i = 0; i < str.length; ++i) {
            if ((units -= 2) < 0) break

            c = str.charCodeAt(i)
            hi = c >> 8
            lo = c % 256
            byteArray.push(lo)
            byteArray.push(hi)
        }

        return byteArray
    }

    function base64ToBytes (str) {
        return base64.toByteArray(base64clean(str))
    }

    function blitBuffer (src, dst, offset, length) {
        for (var i = 0; i < length; ++i) {
            if ((i + offset >= dst.length) || (i >= src.length)) break
            dst[i + offset] = src[i]
        }
        return i
    }

// Node 0.10 supports `ArrayBuffer` but lacks `ArrayBuffer.isView`
    function isArrayBufferView (obj) {
        return (typeof ArrayBuffer.isView === 'function') && ArrayBuffer.isView(obj)
    }

    function numberIsNaN (obj) {
        return obj !== obj // eslint-disable-line no-self-compare
    }

},{"base64-js":2,"ieee754":3}],2:[function(require,module,exports){
    'use strict'

    exports.byteLength = byteLength
    exports.toByteArray = toByteArray
    exports.fromByteArray = fromByteArray

    var lookup = []
    var revLookup = []
    var Arr = typeof Uint8Array !== 'undefined' ? Uint8Array : Array

    var code = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'
    for (var i = 0, len = code.length; i < len; ++i) {
        lookup[i] = code[i]
        revLookup[code.charCodeAt(i)] = i
    }

    revLookup['-'.charCodeAt(0)] = 62
    revLookup['_'.charCodeAt(0)] = 63

    function placeHoldersCount (b64) {
        var len = b64.length
        if (len % 4 > 0) {
            throw new Error('Invalid string. Length must be a multiple of 4')
        }

        // the number of equal signs (place holders)
        // if there are two placeholders, than the two characters before it
        // represent one byte
        // if there is only one, then the three characters before it represent 2 bytes
        // this is just a cheap hack to not do indexOf twice
        return b64[len - 2] === '=' ? 2 : b64[len - 1] === '=' ? 1 : 0
    }

    function byteLength (b64) {
        // base64 is 4/3 + up to two characters of the original data
        return b64.length * 3 / 4 - placeHoldersCount(b64)
    }

    function toByteArray (b64) {
        var i, j, l, tmp, placeHolders, arr
        var len = b64.length
        placeHolders = placeHoldersCount(b64)

        arr = new Arr(len * 3 / 4 - placeHolders)

        // if there are placeholders, only get up to the last complete 4 chars
        l = placeHolders > 0 ? len - 4 : len

        var L = 0

        for (i = 0, j = 0; i < l; i += 4, j += 3) {
            tmp = (revLookup[b64.charCodeAt(i)] << 18) | (revLookup[b64.charCodeAt(i + 1)] << 12) | (revLookup[b64.charCodeAt(i + 2)] << 6) | revLookup[b64.charCodeAt(i + 3)]
            arr[L++] = (tmp >> 16) & 0xFF
            arr[L++] = (tmp >> 8) & 0xFF
            arr[L++] = tmp & 0xFF
        }

        if (placeHolders === 2) {
            tmp = (revLookup[b64.charCodeAt(i)] << 2) | (revLookup[b64.charCodeAt(i + 1)] >> 4)
            arr[L++] = tmp & 0xFF
        } else if (placeHolders === 1) {
            tmp = (revLookup[b64.charCodeAt(i)] << 10) | (revLookup[b64.charCodeAt(i + 1)] << 4) | (revLookup[b64.charCodeAt(i + 2)] >> 2)
            arr[L++] = (tmp >> 8) & 0xFF
            arr[L++] = tmp & 0xFF
        }

        return arr
    }

    function tripletToBase64 (num) {
        return lookup[num >> 18 & 0x3F] + lookup[num >> 12 & 0x3F] + lookup[num >> 6 & 0x3F] + lookup[num & 0x3F]
    }

    function encodeChunk (uint8, start, end) {
        var tmp
        var output = []
        for (var i = start; i < end; i += 3) {
            tmp = (uint8[i] << 16) + (uint8[i + 1] << 8) + (uint8[i + 2])
            output.push(tripletToBase64(tmp))
        }
        return output.join('')
    }

    function fromByteArray (uint8) {
        var tmp
        var len = uint8.length
        var extraBytes = len % 3 // if we have 1 byte left, pad 2 bytes
        var output = ''
        var parts = []
        var maxChunkLength = 16383 // must be multiple of 3

        // go through the array every three bytes, we'll deal with trailing stuff later
        for (var i = 0, len2 = len - extraBytes; i < len2; i += maxChunkLength) {
            parts.push(encodeChunk(uint8, i, (i + maxChunkLength) > len2 ? len2 : (i + maxChunkLength)))
        }

        // pad the end with zeros, but make sure to not forget the extra bytes
        if (extraBytes === 1) {
            tmp = uint8[len - 1]
            output += lookup[tmp >> 2]
            output += lookup[(tmp << 4) & 0x3F]
            output += '=='
        } else if (extraBytes === 2) {
            tmp = (uint8[len - 2] << 8) + (uint8[len - 1])
            output += lookup[tmp >> 10]
            output += lookup[(tmp >> 4) & 0x3F]
            output += lookup[(tmp << 2) & 0x3F]
            output += '='
        }

        parts.push(output)

        return parts.join('')
    }

},{}],3:[function(require,module,exports){
    exports.read = function (buffer, offset, isLE, mLen, nBytes) {
        var e, m
        var eLen = nBytes * 8 - mLen - 1
        var eMax = (1 << eLen) - 1
        var eBias = eMax >> 1
        var nBits = -7
        var i = isLE ? (nBytes - 1) : 0
        var d = isLE ? -1 : 1
        var s = buffer[offset + i]

        i += d

        e = s & ((1 << (-nBits)) - 1)
        s >>= (-nBits)
        nBits += eLen
        for (; nBits > 0; e = e * 256 + buffer[offset + i], i += d, nBits -= 8) {}

        m = e & ((1 << (-nBits)) - 1)
        e >>= (-nBits)
        nBits += mLen
        for (; nBits > 0; m = m * 256 + buffer[offset + i], i += d, nBits -= 8) {}

        if (e === 0) {
            e = 1 - eBias
        } else if (e === eMax) {
            return m ? NaN : ((s ? -1 : 1) * Infinity)
        } else {
            m = m + Math.pow(2, mLen)
            e = e - eBias
        }
        return (s ? -1 : 1) * m * Math.pow(2, e - mLen)
    }

    exports.write = function (buffer, value, offset, isLE, mLen, nBytes) {
        var e, m, c
        var eLen = nBytes * 8 - mLen - 1
        var eMax = (1 << eLen) - 1
        var eBias = eMax >> 1
        var rt = (mLen === 23 ? Math.pow(2, -24) - Math.pow(2, -77) : 0)
        var i = isLE ? 0 : (nBytes - 1)
        var d = isLE ? 1 : -1
        var s = value < 0 || (value === 0 && 1 / value < 0) ? 1 : 0

        value = Math.abs(value)

        if (isNaN(value) || value === Infinity) {
            m = isNaN(value) ? 1 : 0
            e = eMax
        } else {
            e = Math.floor(Math.log(value) / Math.LN2)
            if (value * (c = Math.pow(2, -e)) < 1) {
                e--
                c *= 2
            }
            if (e + eBias >= 1) {
                value += rt / c
            } else {
                value += rt * Math.pow(2, 1 - eBias)
            }
            if (value * c >= 2) {
                e++
                c /= 2
            }

            if (e + eBias >= eMax) {
                m = 0
                e = eMax
            } else if (e + eBias >= 1) {
                m = (value * c - 1) * Math.pow(2, mLen)
                e = e + eBias
            } else {
                m = value * Math.pow(2, eBias - 1) * Math.pow(2, mLen)
                e = 0
            }
        }

        for (; mLen >= 8; buffer[offset + i] = m & 0xff, i += d, m /= 256, mLen -= 8) {}

        e = (e << mLen) | m
        eLen += mLen
        for (; eLen > 0; buffer[offset + i] = e & 0xff, i += d, e /= 256, eLen -= 8) {}

        buffer[offset + i - d] |= s * 128
    }

},{}],4:[function(require,module,exports){
    (function (Buffer){
        'use strict';

        function objectToString(o) {
            return Object.prototype.toString.call(o);
        }

// shim for Node's 'util' package
// DO NOT REMOVE THIS! It is required for compatibility with EnderJS (http://enderjs.com/).
        var util = {
            isArray: function (ar) {
                return Array.isArray(ar) || (typeof ar === 'object' && objectToString(ar) === '[object Array]');
            },
            isDate: function (d) {
                return typeof d === 'object' && objectToString(d) === '[object Date]';
            },
            isRegExp: function (re) {
                return typeof re === 'object' && objectToString(re) === '[object RegExp]';
            },
            getRegExpFlags: function (re) {
                var flags = '';
                re.global && (flags += 'g');
                re.ignoreCase && (flags += 'i');
                re.multiline && (flags += 'm');
                return flags;
            }
        };


        if (typeof module === 'object')
            module.exports = clone;

        /**
         * Clones (copies) an Object using deep copying.
         *
         * This function supports circular references by default, but if you are certain
         * there are no circular references in your object, you can save some CPU time
         * by calling clone(obj, false).
         *
         * Caution: if `circular` is false and `parent` contains circular references,
         * your program may enter an infinite loop and crash.
         *
         * @param `parent` - the object to be cloned
         * @param `circular` - set to true if the object to be cloned may contain
         *    circular references. (optional - true by default)
         * @param `depth` - set to a number if the object is only to be cloned to
         *    a particular depth. (optional - defaults to Infinity)
         * @param `prototype` - sets the prototype to be used when cloning an object.
         *    (optional - defaults to parent prototype).
         */

        function clone(parent, circular, depth, prototype) {
            // maintain two arrays for circular references, where corresponding parents
            // and children have the same index
            var allParents = [];
            var allChildren = [];

            var useBuffer = typeof Buffer != 'undefined';

            if (typeof circular == 'undefined')
                circular = true;

            if (typeof depth == 'undefined')
                depth = Infinity;

            // recurse this function so we don't reset allParents and allChildren
            function _clone(parent, depth) {
                // cloning null always returns null
                if (parent === null)
                    return null;

                if (depth == 0)
                    return parent;

                var child;
                if (typeof parent != 'object') {
                    return parent;
                }

                if (util.isArray(parent)) {
                    child = [];
                } else if (util.isRegExp(parent)) {
                    child = new RegExp(parent.source, util.getRegExpFlags(parent));
                    if (parent.lastIndex) child.lastIndex = parent.lastIndex;
                } else if (util.isDate(parent)) {
                    child = new Date(parent.getTime());
                } else if (useBuffer && Buffer.isBuffer(parent)) {
                    child = new Buffer(parent.length);
                    parent.copy(child);
                } else {
                    if (typeof prototype == 'undefined') child = Object.create(Object.getPrototypeOf(parent));
                    else child = Object.create(prototype);
                }

                if (circular) {
                    var index = allParents.indexOf(parent);

                    if (index != -1) {
                        return allChildren[index];
                    }
                    allParents.push(parent);
                    allChildren.push(child);
                }

                for (var i in parent) {
                    child[i] = _clone(parent[i], depth - 1);
                }

                return child;
            }

            return _clone(parent, depth);
        }

        /**
         * Simple flat clone using prototype, accepts only objects, usefull for property
         * override on FLAT configuration object (no nested props).
         *
         * USE WITH CAUTION! This may not behave as you wish if you do not know how this
         * works.
         */
        clone.clonePrototype = function(parent) {
            if (parent === null)
                return null;

            var c = function () {};
            c.prototype = parent;
            return new c();
        };

    }).call(this,require("buffer").Buffer)
},{"buffer":1}],5:[function(require,module,exports){
    /*!
     * escape-html
     * Copyright(c) 2012-2013 TJ Holowaychuk
     * Copyright(c) 2015 Andreas Lubbe
     * Copyright(c) 2015 Tiancheng "Timothy" Gu
     * MIT Licensed
     */

    'use strict';

    /**
     * Module variables.
     * @private
     */

    var matchHtmlRegExp = /["'&<>]/;

    /**
     * Module exports.
     * @public
     */

    module.exports = escapeHtml;

    /**
     * Escape special characters in the given string of html.
     *
     * @param  {string} string The string to escape for inserting into HTML
     * @return {string}
     * @public
     */

    function escapeHtml(string) {
        var str = '' + string;
        var match = matchHtmlRegExp.exec(str);

        if (!match) {
            return str;
        }

        var escape;
        var html = '';
        var index = 0;
        var lastIndex = 0;

        for (index = match.index; index < str.length; index++) {
            switch (str.charCodeAt(index)) {
                case 34: // "
                    escape = '&quot;';
                    break;
                case 38: // &
                    escape = '&amp;';
                    break;
                case 39: // '
                    escape = '&#39;';
                    break;
                case 60: // <
                    escape = '&lt;';
                    break;
                case 62: // >
                    escape = '&gt;';
                    break;
                default:
                    continue;
            }

            if (lastIndex !== index) {
                html += str.substring(lastIndex, index);
            }

            lastIndex = index + 1;
            html += escape;
        }

        return lastIndex !== index
            ? html + str.substring(lastIndex, index)
            : html;
    }

},{}],6:[function(require,module,exports){
    var geojsonArea = require('geojson-area');

    module.exports = rewind;

    function rewind(gj, outer) {
        switch ((gj && gj.type) || null) {
            case 'FeatureCollection':
                gj.features = gj.features.map(curryOuter(rewind, outer));
                return gj;
            case 'Feature':
                gj.geometry = rewind(gj.geometry, outer);
                return gj;
            case 'Polygon':
            case 'MultiPolygon':
                return correct(gj, outer);
            default:
                return gj;
        }
    }

    function curryOuter(a, b) {
        return function(_) { return a(_, b); };
    }

    function correct(_, outer) {
        if (_.type === 'Polygon') {
            _.coordinates = correctRings(_.coordinates, outer);
        } else if (_.type === 'MultiPolygon') {
            _.coordinates = _.coordinates.map(curryOuter(correctRings, outer));
        }
        return _;
    }

    function correctRings(_, outer) {
        outer = !!outer;
        _[0] = wind(_[0], outer);
        for (var i = 1; i < _.length; i++) {
            _[i] = wind(_[i], !outer);
        }
        return _;
    }

    function wind(_, dir) {
        return cw(_) === dir ? _ : _.reverse();
    }

    function cw(_) {
        return geojsonArea.ring(_) >= 0;
    }

},{"geojson-area":7}],7:[function(require,module,exports){
    var wgs84 = require('wgs84');

    module.exports.geometry = geometry;
    module.exports.ring = ringArea;

    function geometry(_) {
        if (_.type === 'Polygon') return polygonArea(_.coordinates);
        else if (_.type === 'MultiPolygon') {
            var area = 0;
            for (var i = 0; i < _.coordinates.length; i++) {
                area += polygonArea(_.coordinates[i]);
            }
            return area;
        } else {
            return null;
        }
    }

    function polygonArea(coords) {
        var area = 0;
        if (coords && coords.length > 0) {
            area += Math.abs(ringArea(coords[0]));
            for (var i = 1; i < coords.length; i++) {
                area -= Math.abs(ringArea(coords[i]));
            }
        }
        return area;
    }

    /**
     * Calculate the approximate area of the polygon were it projected onto
     *     the earth.  Note that this area will be positive if ring is oriented
     *     clockwise, otherwise it will be negative.
     *
     * Reference:
     * Robert. G. Chamberlain and William H. Duquette, "Some Algorithms for
     *     Polygons on a Sphere", JPL Publication 07-03, Jet Propulsion
     *     Laboratory, Pasadena, CA, June 2007 http://trs-new.jpl.nasa.gov/dspace/handle/2014/40409
     *
     * Returns:
     * {float} The approximate signed geodesic area of the polygon in square
     *     meters.
     */

    function ringArea(coords) {
        var area = 0;

        if (coords.length > 2) {
            var p1, p2;
            for (var i = 0; i < coords.length - 1; i++) {
                p1 = coords[i];
                p2 = coords[i + 1];
                area += rad(p2[0] - p1[0]) * (2 + Math.sin(rad(p1[1])) + Math.sin(rad(p2[1])));
            }

            area = area * wgs84.RADIUS * wgs84.RADIUS / 2;
        }

        return area;
    }

    function rad(_) {
        return _ * Math.PI / 180;
    }

},{"wgs84":8}],8:[function(require,module,exports){
    module.exports.RADIUS = 6378137;
    module.exports.FLATTENING = 1/298.257223563;
    module.exports.POLAR_RADIUS = 6356752.3142;

},{}],9:[function(require,module,exports){
    (function(window) {
        var HAS_HASHCHANGE = (function() {
            var doc_mode = window.documentMode;
            return ('onhashchange' in window) &&
                (doc_mode === undefined || doc_mode > 7);
        })();

        L.Hash = function(map) {
            this.onHashChange = L.Util.bind(this.onHashChange, this);

            if (map) {
                this.init(map);
            }
        };

        L.Hash.parseHash = function(hash) {
            if(hash.indexOf('#') === 0) {
                hash = hash.substr(1);
            }
            var args = hash.split("/");
            if (args.length == 3) {
                var zoom = parseInt(args[0], 10),
                    lat = parseFloat(args[1]),
                    lon = parseFloat(args[2]);
                if (isNaN(zoom) || isNaN(lat) || isNaN(lon)) {
                    return false;
                } else {
                    return {
                        center: new L.LatLng(lat, lon),
                        zoom: zoom
                    };
                }
            } else {
                return false;
            }
        };

        L.Hash.formatHash = function(map) {
            var center = map.getCenter(),
                zoom = map.getZoom(),
                precision = Math.max(0, Math.ceil(Math.log(zoom) / Math.LN2));

            return "#" + [zoom,
                    center.lat.toFixed(precision),
                    center.lng.toFixed(precision)
                ].join("/");
        },

            L.Hash.prototype = {
                map: null,
                lastHash: null,

                parseHash: L.Hash.parseHash,
                formatHash: L.Hash.formatHash,

                init: function(map) {
                    this.map = map;

                    // reset the hash
                    this.lastHash = null;
                    this.onHashChange();

                    if (!this.isListening) {
                        this.startListening();
                    }
                },

                removeFrom: function(map) {
                    if (this.changeTimeout) {
                        clearTimeout(this.changeTimeout);
                    }

                    if (this.isListening) {
                        this.stopListening();
                    }

                    this.map = null;
                },

                onMapMove: function() {
                    // bail if we're moving the map (updating from a hash),
                    // or if the map is not yet loaded

                    if (this.movingMap || !this.map._loaded) {
                        return false;
                    }

                    var hash = this.formatHash(this.map);
                    if (this.lastHash != hash) {
                        location.replace(hash);
                        this.lastHash = hash;
                    }
                },

                movingMap: false,
                update: function() {
                    var hash = location.hash;
                    if (hash === this.lastHash) {
                        return;
                    }
                    var parsed = this.parseHash(hash);
                    if (parsed) {
                        this.movingMap = true;

                        this.map.setView(parsed.center, parsed.zoom);

                        this.movingMap = false;
                    } else {
                        this.onMapMove(this.map);
                    }
                },

                // defer hash change updates every 100ms
                changeDefer: 100,
                changeTimeout: null,
                onHashChange: function() {
                    // throttle calls to update() so that they only happen every
                    // `changeDefer` ms
                    if (!this.changeTimeout) {
                        var that = this;
                        this.changeTimeout = setTimeout(function() {
                            that.update();
                            that.changeTimeout = null;
                        }, this.changeDefer);
                    }
                },

                isListening: false,
                hashChangeInterval: null,
                startListening: function() {
                    this.map.on("moveend", this.onMapMove, this);

                    if (HAS_HASHCHANGE) {
                        L.DomEvent.addListener(window, "hashchange", this.onHashChange);
                    } else {
                        clearInterval(this.hashChangeInterval);
                        this.hashChangeInterval = setInterval(this.onHashChange, 50);
                    }
                    this.isListening = true;
                },

                stopListening: function() {
                    this.map.off("moveend", this.onMapMove, this);

                    if (HAS_HASHCHANGE) {
                        L.DomEvent.removeListener(window, "hashchange", this.onHashChange);
                    } else {
                        clearInterval(this.hashChangeInterval);
                    }
                    this.isListening = false;
                }
            };
        L.hash = function(map) {
            return new L.Hash(map);
        };
        L.Map.prototype.addHash = function() {
            this._hash = L.hash(this);
        };
        L.Map.prototype.removeHash = function() {
            this._hash.removeFrom();
        };
    })(window);

},{}],10:[function(require,module,exports){
    /*
     * Given a querystring, return an object of that querystring's components.
     *
     * @param {String} str
     * @returns {Object} parsed querystring
     */
    module.exports.stringQs = function(str) {
        return str.split('&').reduce(function(obj, pair){
            var parts = pair.split('=');
            if (parts.length === 2) {
                obj[parts[0]] = (null === parts[1]) ? '' : decodeURIComponent(parts[1]);
            }
            return obj;
        }, {});
    };
    /*
     * Given a parsed querystring as an object,
     * return a string representing it.
     *
     * @param {Object} obj parsed querystring
     * @param {Boolean} noencode skip URL-encoding querystring parameters
     * @returns {String} generated string
     */
    module.exports.qsString = function(obj, noencode) {
        noencode = true;
        function softEncode(s) { return ('' + s).replace('&', '%26'); }
        return Object.keys(obj).sort().map(function(key) {
            return encodeURIComponent(key) + '=' + (
                    noencode ? softEncode(obj[key]) : encodeURIComponent(obj[key]));
        }).join('&');
    };

},{}],11:[function(require,module,exports){
    var type = require("./type"),
        topojson = require("../../");

    module.exports = function(topology, propertiesById) {
        var bind = type({
            geometry: function(geometry) {
                var properties0 = geometry.properties,
                    properties1 = propertiesById[geometry.id];
                if (properties1) {
                    if (properties0) for (var k in properties1) properties0[k] = properties1[k];
                    else for (var k in properties1) { geometry.properties = properties1; break; }
                }
                this.defaults.geometry.call(this, geometry);
            },
            LineString: noop,
            MultiLineString: noop,
            Point: noop,
            MultiPoint: noop,
            Polygon: noop,
            MultiPolygon: noop
        });

        for (var key in topology.objects) {
            bind.object(topology.objects[key]);
        }
    };

    function noop() {}

},{"../../":"topojson","./type":39}],12:[function(require,module,exports){

// Computes the bounding box of the specified hash of GeoJSON objects.
    module.exports = function(objects) {
        var x0 = Infinity,
            y0 = Infinity,
            x1 = -Infinity,
            y1 = -Infinity;

        function boundGeometry(geometry) {
            if (geometry && boundGeometryType.hasOwnProperty(geometry.type)) boundGeometryType[geometry.type](geometry);
        }

        var boundGeometryType = {
            GeometryCollection: function(o) { o.geometries.forEach(boundGeometry); },
            Point: function(o) { boundPoint(o.coordinates); },
            MultiPoint: function(o) { o.coordinates.forEach(boundPoint); },
            LineString: function(o) { boundLine(o.coordinates); },
            MultiLineString: function(o) { o.coordinates.forEach(boundLine); },
            Polygon: function(o) { o.coordinates.forEach(boundLine); },
            MultiPolygon: function(o) { o.coordinates.forEach(boundMultiLine); }
        };

        function boundPoint(coordinates) {
            var x = coordinates[0],
                y = coordinates[1];
            if (x < x0) x0 = x;
            if (x > x1) x1 = x;
            if (y < y0) y0 = y;
            if (y > y1) y1 = y;
        }

        function boundLine(coordinates) {
            coordinates.forEach(boundPoint);
        }

        function boundMultiLine(coordinates) {
            coordinates.forEach(boundLine);
        }

        for (var key in objects) {
            boundGeometry(objects[key]);
        }

        return [x0, y0, x1, y1];
    };

},{}],13:[function(require,module,exports){
    exports.name = "cartesian";
    exports.formatDistance = formatDistance;
    exports.ringArea = ringArea;
    exports.absoluteArea = Math.abs;
    exports.triangleArea = triangleArea;
    exports.distance = distance;

    function formatDistance(d) {
        return d.toString();
    }

    function ringArea(ring) {
        var i = -1,
            n = ring.length,
            a,
            b = ring[n - 1],
            area = 0;

        while (++i < n) {
            a = b;
            b = ring[i];
            area += a[0] * b[1] - a[1] * b[0];
        }

        return area * .5;
    }

    function triangleArea(triangle) {
        return Math.abs(
            (triangle[0][0] - triangle[2][0]) * (triangle[1][1] - triangle[0][1])
            - (triangle[0][0] - triangle[1][0]) * (triangle[2][1] - triangle[0][1])
        );
    }

    function distance(x0, y0, x1, y1) {
        var dx = x0 - x1, dy = y0 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

},{}],14:[function(require,module,exports){
    var type = require("./type"),
        systems = require("./coordinate-systems"),
        topojson = require("../../");

    module.exports = function(object, options) {
        if (object.type === "Topology") clockwiseTopology(object, options);
        else clockwiseGeometry(object, options);
    };

    function clockwiseGeometry(object, options) {
        var system = null;

        if (options)
            "coordinate-system" in options && (system = systems[options["coordinate-system"]]);

        var clockwisePolygon = clockwisePolygonSystem(system.ringArea, reverse);

        type({
            LineString: noop,
            MultiLineString: noop,
            Point: noop,
            MultiPoint: noop,
            Polygon: function(polygon) { clockwisePolygon(polygon.coordinates); },
            MultiPolygon: function(multiPolygon) { multiPolygon.coordinates.forEach(clockwisePolygon); }
        }).object(object);

        function reverse(array) { array.reverse(); }
    }

    function clockwiseTopology(topology, options) {
        var system = null;

        if (options)
            "coordinate-system" in options && (system = systems[options["coordinate-system"]]);

        var clockwisePolygon = clockwisePolygonSystem(ringArea, reverse);

        var clockwise = type({
            LineString: noop,
            MultiLineString: noop,
            Point: noop,
            MultiPoint: noop,
            Polygon: function(polygon) { clockwisePolygon(polygon.arcs); },
            MultiPolygon: function(multiPolygon) { multiPolygon.arcs.forEach(clockwisePolygon); }
        });

        for (var key in topology.objects) {
            clockwise.object(topology.objects[key]);
        }

        function ringArea(ring) {
            return system.ringArea(topojson.feature(topology, {type: "Polygon", arcs: [ring]}).geometry.coordinates[0]);
        }

        // TODO It might be slightly more compact to reverse the arc.
        function reverse(ring) {
            var i = -1, n = ring.length;
            ring.reverse();
            while (++i < n) ring[i] = ~ring[i];
        }
    };

    function clockwisePolygonSystem(ringArea, reverse) {
        return function(rings) {
            if (!(n = rings.length)) return;
            var n,
                areas = new Array(n),
                max = -Infinity,
                best,
                area,
                t;
            // Find the largest absolute ring area; this should be the exterior ring.
            for (var i = 0; i < n; ++i) {
                var area = Math.abs(areas[i] = ringArea(rings[i]));
                if (area > max) max = area, best = i;
            }
            // Ensure the largest ring appears first.
            if (best) {
                t = rings[best], rings[best] = rings[0], rings[0] = t;
                t = areas[best], areas[best] = areas[0], areas[0] = t;
            }
            if (areas[0] < 0) reverse(rings[0]);
            for (var i = 1; i < n; ++i) {
                if (areas[i] > 0) reverse(rings[i]);
            }
        };
    }

    function noop() {}

},{"../../":"topojson","./coordinate-systems":16,"./type":39}],15:[function(require,module,exports){
// Given a hash of GeoJSON objects and an id function, invokes the id function
// to compute a new id for each object that is a feature. The function is passed
// the feature and is expected to return the new feature id, or null if the
// feature should not have an id.
    module.exports = function(objects, id) {
        if (arguments.length < 2) id = function(d) { return d.id; };

        function idObject(object) {
            if (object && idObjectType.hasOwnProperty(object.type)) idObjectType[object.type](object);
        }

        function idFeature(feature) {
            var i = id(feature);
            if (i == null) delete feature.id;
            else feature.id = i;
        }

        var idObjectType = {
            Feature: idFeature,
            FeatureCollection: function(collection) { collection.features.forEach(idFeature); }
        };

        for (var key in objects) {
            idObject(objects[key]);
        }

        return objects;
    };

},{}],16:[function(require,module,exports){
    module.exports = {
        cartesian: require("./cartesian"),
        spherical: require("./spherical")
    };

},{"./cartesian":13,"./spherical":26}],17:[function(require,module,exports){
// Given a TopoJSON topology in absolute (quantized) coordinates,
// converts to fixed-point delta encoding.
// This is a destructive operation that modifies the given topology!
    module.exports = function(topology) {
        var arcs = topology.arcs,
            i = -1,
            n = arcs.length;

        while (++i < n) {
            var arc = arcs[i],
                j = 0,
                m = arc.length,
                point = arc[0],
                x0 = point[0],
                y0 = point[1],
                x1,
                y1;
            while (++j < m) {
                point = arc[j];
                x1 = point[0];
                y1 = point[1];
                arc[j] = [x1 - x0, y1 - y0];
                x0 = x1;
                y0 = y1;
            }
        }

        return topology;
    };

},{}],18:[function(require,module,exports){
    var type = require("./type"),
        prune = require("./prune"),
        clockwise = require("./clockwise"),
        systems = require("./coordinate-systems"),
        topojson = require("../../");

    module.exports = function(topology, options) {
        var system = null,
            forceClockwise = true, // force exterior rings to be clockwise?
            preserveAttached = true, // e.g., remove islands but not small counties
            preserveRing = preserveNone,
            minimumArea;

        if (options)
            "coordinate-system" in options && (system = systems[options["coordinate-system"]]),
            "minimum-area" in options && (minimumArea = +options["minimum-area"]),
            "preserve-attached" in options && (preserveAttached = !!options["preserve-attached"]),
            "force-clockwise" in options && (forceClockwise = !!options["force-clockwise"]);

        if (forceClockwise) clockwise(topology, options); // deprecated; for backwards-compatibility

        if (!(minimumArea > 0)) minimumArea = Number.MIN_VALUE;

        if (preserveAttached) {
            var uniqueRingByArc = {}, // arc index -> index of unique associated ring, or -1 if used by multiple rings
                ringIndex = 0;

            var checkAttachment = type({
                LineString: noop,
                MultiLineString: noop,
                Point: noop,
                MultiPoint: noop,
                MultiPolygon: function(multiPolygon) {
                    var arcs = multiPolygon.arcs, i = -1, n = arcs.length;
                    while (++i < n) this.polygon(arcs[i]);
                },
                Polygon: function(polygon) {
                    this.polygon(polygon.arcs);
                },
                polygon: function(arcs) {
                    for (var i = 0, n = arcs.length; i < n; ++i, ++ringIndex) {
                        for (var ring = arcs[i], j = 0, m = ring.length; j < m; ++j) {
                            var arc = ring[j];
                            if (arc < 0) arc = ~arc;
                            var uniqueRing = uniqueRingByArc[arc];
                            if (uniqueRing >= 0 && uniqueRing !== ringIndex) uniqueRingByArc[arc] = -1;
                            else uniqueRingByArc[arc] = ringIndex;
                        }
                    }
                }
            });

            preserveRing = function(ring) {
                for (var j = 0, m = ring.length; j < m; ++j) {
                    var arc = ring[j];
                    if (uniqueRingByArc[arc < 0 ? ~arc : arc] < 0) {
                        return true;
                    }
                }
            };

            for (var key in topology.objects) {
                checkAttachment.object(topology.objects[key]);
            }
        }

        var filter = type({
            LineString: noop, // TODO remove empty lines
            MultiLineString: noop,
            Point: noop,
            MultiPoint: noop,
            Polygon: function(polygon) {
                polygon.arcs = filterPolygon(polygon.arcs);
                if (!polygon.arcs || !polygon.arcs.length) {
                    polygon.type = null;
                    delete polygon.arcs;
                }
            },
            MultiPolygon: function(multiPolygon) {
                multiPolygon.arcs = multiPolygon.arcs
                    .map(filterPolygon)
                    .filter(function(polygon) { return polygon && polygon.length; });
                if (!multiPolygon.arcs.length) {
                    multiPolygon.type = null;
                    delete multiPolygon.arcs;
                }
            },
            GeometryCollection: function(collection) {
                this.defaults.GeometryCollection.call(this, collection);
                collection.geometries = collection.geometries.filter(function(geometry) { return geometry.type != null; });
                if (!collection.geometries.length) {
                    collection.type = null;
                    delete collection.geometries;
                }
            }
        });

        for (var key in topology.objects) {
            filter.object(topology.objects[key]);
        }

        prune(topology, options);

        function filterPolygon(arcs) {
            return arcs.length && filterExteriorRing(arcs[0]) // if the exterior is small, ignore any holes
                ? [arcs.shift()].concat(arcs.filter(filterInteriorRing))
                : null;
        }

        function filterExteriorRing(ring) {
            return preserveRing(ring) || system.absoluteArea(ringArea(ring)) >= minimumArea;
        }

        function filterInteriorRing(ring) {
            return preserveRing(ring) || system.absoluteArea(-ringArea(ring)) >= minimumArea;
        }

        function ringArea(ring) {
            return system.ringArea(topojson.feature(topology, {type: "Polygon", arcs: [ring]}).geometry.coordinates[0]);
        }
    };

    function noop() {}

    function preserveNone() {
        return false;
    }

},{"../../":"topojson","./clockwise":14,"./coordinate-systems":16,"./prune":22,"./type":39}],19:[function(require,module,exports){
// Given a hash of GeoJSON objects, replaces Features with geometry objects.
// This is a destructive operation that modifies the input objects!
    module.exports = function(objects) {

        function geomifyObject(object) {
            return (object && geomifyObjectType.hasOwnProperty(object.type)
                ? geomifyObjectType[object.type]
                : geomifyGeometry)(object);
        }

        function geomifyFeature(feature) {
            var geometry = feature.geometry;
            if (geometry == null) {
                feature.type = null;
            } else {
                geomifyGeometry(geometry);
                feature.type = geometry.type;
                if (geometry.geometries) feature.geometries = geometry.geometries;
                else if (geometry.coordinates) feature.coordinates = geometry.coordinates;
            }
            delete feature.geometry;
            return feature;
        }

        function geomifyGeometry(geometry) {
            if (!geometry) return {type: null};
            if (geomifyGeometryType.hasOwnProperty(geometry.type)) geomifyGeometryType[geometry.type](geometry);
            return geometry;
        }

        var geomifyObjectType = {
            Feature: geomifyFeature,
            FeatureCollection: function(collection) {
                collection.type = "GeometryCollection";
                collection.geometries = collection.features;
                collection.features.forEach(geomifyFeature);
                delete collection.features;
                return collection;
            }
        };

        var geomifyGeometryType = {
            GeometryCollection: function(o) {
                var geometries = o.geometries, i = -1, n = geometries.length;
                while (++i < n) geometries[i] = geomifyGeometry(geometries[i]);
            },
            MultiPoint: function(o) {
                if (!o.coordinates.length) {
                    o.type = null;
                    delete o.coordinates;
                } else if (o.coordinates.length < 2) {
                    o.type = "Point";
                    o.coordinates = o.coordinates[0];
                }
            },
            LineString: function(o) {
                if (!o.coordinates.length) {
                    o.type = null;
                    delete o.coordinates;
                }
            },
            MultiLineString: function(o) {
                for (var lines = o.coordinates, i = 0, N = 0, n = lines.length; i < n; ++i) {
                    var line = lines[i];
                    if (line.length) lines[N++] = line;
                }
                if (!N) {
                    o.type = null;
                    delete o.coordinates;
                } else if (N < 2) {
                    o.type = "LineString";
                    o.coordinates = lines[0];
                } else {
                    o.coordinates.length = N;
                }
            },
            Polygon: function(o) {
                for (var rings = o.coordinates, i = 0, N = 0, n = rings.length; i < n; ++i) {
                    var ring = rings[i];
                    if (ring.length) rings[N++] = ring;
                }
                if (!N) {
                    o.type = null;
                    delete o.coordinates;
                } else {
                    o.coordinates.length = N;
                }
            },
            MultiPolygon: function(o) {
                for (var polygons = o.coordinates, j = 0, M = 0, m = polygons.length; j < m; ++j) {
                    for (var rings = polygons[j], i = 0, N = 0, n = rings.length; i < n; ++i) {
                        var ring = rings[i];
                        if (ring.length) rings[N++] = ring;
                    }
                    if (N) {
                        rings.length = N;
                        polygons[M++] = rings;
                    }
                }
                if (!M) {
                    o.type = null;
                    delete o.coordinates;
                } else if (M < 2) {
                    o.type = "Polygon";
                    o.coordinates = polygons[0];
                } else {
                    polygons.length = M;
                }
            }
        };

        for (var key in objects) {
            objects[key] = geomifyObject(objects[key]);
        }

        return objects;
    };

},{}],20:[function(require,module,exports){
    var quantize = require("./quantize");

    module.exports = function(topology, Q0, Q1) {
        if (Q0) {
            if (Q1 === Q0 || !topology.bbox.every(isFinite)) return topology;
            var k = Q1 / Q0,
                q = quantize(0, 0, k, k);

            topology.transform.scale[0] /= k;
            topology.transform.scale[1] /= k;
        } else {
            var bbox = topology.bbox,
                x0 = isFinite(bbox[0]) ? bbox[0] : 0,
                y0 = isFinite(bbox[1]) ? bbox[1] : 0,
                x1 = isFinite(bbox[2]) ? bbox[2] : 0,
                y1 = isFinite(bbox[3]) ? bbox[3] : 0,
                kx = x1 - x0 ? (Q1 - 1) / (x1 - x0) : 1,
                ky = y1 - y0 ? (Q1 - 1) / (y1 - y0) : 1,
                q = quantize(-x0, -y0, kx, ky);

            topology.transform = q.transform;
        }

        function quantizeGeometry(geometry) {
            if (geometry && quantizeGeometryType.hasOwnProperty(geometry.type)) quantizeGeometryType[geometry.type](geometry);
        }

        var quantizeGeometryType = {
            GeometryCollection: function(o) { o.geometries.forEach(quantizeGeometry); },
            Point: function(o) { q.point(o.coordinates); },
            MultiPoint: function(o) { o.coordinates.forEach(q.point); }
        };

        for (var key in topology.objects) {
            quantizeGeometry(topology.objects[key]);
        }

        // XXX shared points are bad mmkay
        topology.arcs = topology.arcs.map(function(arc) {
            q.line(arc = arc.map(function(point) { return point.slice(); }));
            if (arc.length < 2) arc.push(arc[0]); // arcs must have at least two points
            return arc;
        });

        return topology;
    };

},{"./quantize":23}],21:[function(require,module,exports){
    var quantize = require("./quantize");

    module.exports = function(objects, bbox, Q0, Q1) {
        if (arguments.length < 4) Q1 = Q0;

        var x0 = isFinite(bbox[0]) ? bbox[0] : 0,
            y0 = isFinite(bbox[1]) ? bbox[1] : 0,
            x1 = isFinite(bbox[2]) ? bbox[2] : 0,
            y1 = isFinite(bbox[3]) ? bbox[3] : 0,
            kx = x1 - x0 ? (Q1 - 1) / (x1 - x0) * Q0 / Q1 : 1,
            ky = y1 - y0 ? (Q1 - 1) / (y1 - y0) * Q0 / Q1 : 1,
            q = quantize(-x0, -y0, kx, ky);

        function quantizeGeometry(geometry) {
            if (geometry && quantizeGeometryType.hasOwnProperty(geometry.type)) quantizeGeometryType[geometry.type](geometry);
        }

        var quantizeGeometryType = {
            GeometryCollection: function(o) { o.geometries.forEach(quantizeGeometry); },
            Point: function(o) { q.point(o.coordinates); },
            MultiPoint: function(o) { o.coordinates.forEach(q.point); },
            LineString: function(o) {
                var line = o.coordinates;
                q.line(line);
                if (line.length < 2) line[1] = line[0]; // must have 2+
            },
            MultiLineString: function(o) {
                for (var lines = o.coordinates, i = 0, n = lines.length; i < n; ++i) {
                    var line = lines[i];
                    q.line(line);
                    if (line.length < 2) line[1] = line[0]; // must have 2+
                }
            },
            Polygon: function(o) {
                for (var rings = o.coordinates, i = 0, n = rings.length; i < n; ++i) {
                    var ring = rings[i];
                    q.line(ring);
                    while (ring.length < 4) ring.push(ring[0]); // must have 4+
                }
            },
            MultiPolygon: function(o) {
                for (var polygons = o.coordinates, i = 0, n = polygons.length; i < n; ++i) {
                    for (var rings = polygons[i], j = 0, m = rings.length; j < m; ++j) {
                        var ring = rings[j];
                        q.line(ring);
                        while (ring.length < 4) ring.push(ring[0]); // must have 4+
                    }
                }
            }
        };

        for (var key in objects) {
            quantizeGeometry(objects[key]);
        }

        return q.transform;
    };

},{"./quantize":23}],22:[function(require,module,exports){
    module.exports = function(topology, options) {
        var verbose = false,
            objects = topology.objects,
            oldArcs = topology.arcs,
            oldArcCount = oldArcs.length,
            newArcs = topology.arcs = [],
            newArcCount = 0,
            newIndexByOldIndex = new Array(oldArcs.length);

        if (options)
            "verbose" in options && (verbose = !!options["verbose"]);

        function pruneGeometry(geometry) {
            if (geometry && pruneGeometryType.hasOwnProperty(geometry.type)) pruneGeometryType[geometry.type](geometry);
        }

        var pruneGeometryType = {
            GeometryCollection: function(o) { o.geometries.forEach(pruneGeometry); },
            LineString: function(o) { pruneArcs(o.arcs); },
            MultiLineString: function(o) { o.arcs.forEach(pruneArcs); },
            Polygon: function(o) { o.arcs.forEach(pruneArcs); },
            MultiPolygon: function(o) { o.arcs.forEach(pruneMultiArcs); }
        };

        function pruneArcs(arcs) {
            for (var i = 0, n = arcs.length; i < n; ++i) {
                var oldIndex = arcs[i],
                    oldReverse = oldIndex < 0 && (oldIndex = ~oldIndex, true),
                    newIndex;

                // If this is the first instance of this arc,
                // record it under its new index.
                if ((newIndex = newIndexByOldIndex[oldIndex]) == null) {
                    newIndexByOldIndex[oldIndex] = newIndex = newArcCount++;
                    newArcs[newIndex] = oldArcs[oldIndex];
                }

                arcs[i] = oldReverse ? ~newIndex : newIndex;
            }
        }

        function pruneMultiArcs(arcs) {
            arcs.forEach(pruneArcs);
        }

        for (var key in objects) {
            pruneGeometry(objects[key]);
        }

        if (verbose) console.warn("prune: retained " + newArcCount + " / " + oldArcCount + " arcs (" + Math.round(newArcCount / oldArcCount * 100) + "%)");

        return topology;
    };

    function noop() {}

},{}],23:[function(require,module,exports){
    module.exports = function(dx, dy, kx, ky) {

        function quantizePoint(coordinates) {
            coordinates[0] = Math.round((coordinates[0] + dx) * kx);
            coordinates[1] = Math.round((coordinates[1] + dy) * ky);
            return coordinates;
        }

        function quantizeLine(coordinates) {
            var i = 0,
                j = 1,
                n = coordinates.length,
                pi = quantizePoint(coordinates[0]),
                pj,
                px = pi[0],
                py = pi[1],
                x,
                y;

            while (++i < n) {
                pi = quantizePoint(coordinates[i]);
                x = pi[0];
                y = pi[1];
                if (x !== px || y !== py) { // skip coincident points
                    pj = coordinates[j++];
                    pj[0] = px = x;
                    pj[1] = py = y;
                }
            }

            coordinates.length = j;
        }

        return {
            point: quantizePoint,
            line: quantizeLine,
            transform: {
                scale: [1 / kx, 1 / ky],
                translate: [-dx, -dy]
            }
        };
    };

},{}],24:[function(require,module,exports){
    var type = require("./type");

    module.exports = function(topology, options) {
        var width,
            height,
            margin = 0,
            invert = true;

        if (options)
            "width" in options && (width = +options["width"]),
            "height" in options && (height = +options["height"]),
            "margin" in options && (margin = +options["margin"]),
            "invert" in options && (invert = !!options["invert"]);

        var bx = topology.bbox,
            dx = bx[2] - bx[0],
            dy = bx[3] - bx[1],
            cx = (bx[2] + bx[0]) / 2,
            cy = (bx[3] + bx[1]) / 2,
            kx;

        width = Math.max(0, width - margin * 2);
        height = Math.max(0, height - margin * 2);

        if (width && height) {
            kx = Math.min(width / dx, height / dy);
        } else if (width) {
            kx = width / dx;
            height = kx * dy;
        } else {
            kx = height / dy;
            width = kx * dx;
        }

        var ky = invert ? -kx : kx,
            lt = scalePoint([bx[0], bx[1]]),
            rb = scalePoint([bx[2], bx[3]]),
            tx;

        topology.bbox = invert
            ? [lt[0], rb[1], rb[0], lt[1]]
            : [lt[0], lt[1], rb[0], rb[1]];

        function scalePoint(point) {
            return [
                point[0] * kx + (width / 2 - cx * kx) + margin,
                point[1] * ky + (height / 2 - cy * ky) + margin
            ];
        }

        if (tx = topology.transform) {
            tx.scale[0] *= kx;
            tx.scale[1] *= ky;
            tx.translate[0] = width / 2 + margin - (cx - tx.translate[0]) * kx;
            tx.translate[1] = height / 2 + margin - (cy - tx.translate[1]) * ky;
        } else {
            var scale = type({
                LineString: noop,
                MultiLineString: noop,
                Point: function(point) { point.coordinates = scalePoint(point.coordinates); },
                MultiPoint: function(multipoint) { multipoint.coordinates = multipoint.coordinates.map(scalePoint); },
                Polygon: noop,
                MultiPolygon: noop
            });

            for (var key in topology.objects) {
                scale.object(topology.objects[key]);
            }

            topology.arcs = topology.arcs.map(function(arc) {
                return arc.map(scalePoint);
            });
        }

        return topology;
    };

    function noop() {}

},{"./type":39}],25:[function(require,module,exports){
    var topojson = require("../../"),
        systems = require("./coordinate-systems");

    module.exports = function(topology, options) {
        var minimumArea = 0,
            retainProportion,
            verbose = false,
            system = null,
            N = topology.arcs.reduce(function(p, v) { return p + v.length; }, 0),
            M = 0;

        if (options)
            "minimum-area" in options && (minimumArea = +options["minimum-area"]),
            "coordinate-system" in options && (system = systems[options["coordinate-system"]]),
            "retain-proportion" in options && (retainProportion = +options["retain-proportion"]),
            "verbose" in options && (verbose = !!options["verbose"]);

        topojson.presimplify(topology, system.triangleArea);

        if (retainProportion) {
            var areas = [];
            topology.arcs.forEach(function(arc) {
                arc.forEach(function(point) {
                    if (isFinite(point[2])) areas.push(point[2]); // ignore endpoints
                });
            });
            options["minimum-area"] = minimumArea = N ? areas.sort(function(a, b) { return b - a; })[Math.ceil((N - 1) * retainProportion)] : 0;
            if (verbose) console.warn("simplification: effective minimum area " + minimumArea.toPrecision(3));
        }

        topology.arcs.forEach(topology.transform ? function(arc) {
            var dx = 0,
                dy = 0, // accumulate removed points
                i = -1,
                j = -1,
                n = arc.length,
                source,
                target;

            while (++i < n) {
                source = arc[i];
                if (source[2] >= minimumArea) {
                    target = arc[++j];
                    target[0] = source[0] + dx;
                    target[1] = source[1] + dy;
                    dx = dy = 0;
                } else {
                    dx += source[0];
                    dy += source[1];
                }
            }

            arc.length = ++j;
        } : function(arc) {
            var i = -1,
                j = -1,
                n = arc.length,
                point;

            while (++i < n) {
                point = arc[i];
                if (point[2] >= minimumArea) {
                    arc[++j] = point;
                }
            }

            arc.length = ++j;
        });

        // Remove computed area (z) for each point, and remove coincident points.
        // This is done as a separate pass because some coordinates may be shared
        // between arcs (such as the last point and first point of a cut line).
        // If the entire arc is empty, retain at least two points (per spec).
        topology.arcs.forEach(topology.transform ? function(arc) {
            var i = 0,
                j = 0,
                n = arc.length,
                p = arc[0];
            p.length = 2;
            while (++i < n) {
                p = arc[i];
                p.length = 2;
                if (p[0] || p[1]) arc[++j] = p;
            }
            M += arc.length = (j || 1) + 1;
        } : function(arc) {
            var i = 0,
                j = 0,
                n = arc.length,
                p = arc[0],
                x0 = p[0],
                y0 = p[1],
                x1,
                y1;
            p.length = 2;
            while (++i < n) {
                p = arc[i], x1 = p[0], y1 = p[1];
                p.length = 2;
                if (x0 !== x1 || y0 !== y1) arc[++j] = p, x0 = x1, y0 = y1;
            }
            M += arc.length = (j || 1) + 1;
        });

        if (verbose) console.warn("simplification: retained " + M + " / " + N + " points (" + Math.round((M / N) * 100) + "%)");

        return topology;
    };

},{"../../":"topojson","./coordinate-systems":16}],26:[function(require,module,exports){
    var π = Math.PI,
        π_4 = π / 4,
        radians = π / 180;

    exports.name = "spherical";
    exports.formatDistance = formatDistance;
    exports.ringArea = ringArea;
    exports.absoluteArea = absoluteArea;
    exports.triangleArea = triangleArea;
    exports.distance = haversinDistance; // XXX why two implementations?

    function formatDistance(k) {
        var km = k * radians * 6371;
        return (km > 1 ? km.toFixed(3) + "km" : (km * 1000).toPrecision(3) + "m") + " (" + k.toPrecision(3) + "°)";
    }

    function ringArea(ring) {
        if (!ring.length) return 0;
        var area = 0,
            p = ring[0],
            λ = p[0] * radians,
            φ = p[1] * radians / 2 + π_4,
            λ0 = λ,
            cosφ0 = Math.cos(φ),
            sinφ0 = Math.sin(φ);

        for (var i = 1, n = ring.length; i < n; ++i) {
            p = ring[i], λ = p[0] * radians, φ = p[1] * radians / 2 + π_4;

            // Spherical excess E for a spherical triangle with vertices: south pole,
            // previous point, current point.  Uses a formula derived from Cagnoli’s
            // theorem.  See Todhunter, Spherical Trig. (1871), Sec. 103, Eq. (2).
            var dλ = λ - λ0,
                cosφ = Math.cos(φ),
                sinφ = Math.sin(φ),
                k = sinφ0 * sinφ,
                u = cosφ0 * cosφ + k * Math.cos(dλ),
                v = k * Math.sin(dλ);
            area += Math.atan2(v, u);

            // Advance the previous point.
            λ0 = λ, cosφ0 = cosφ, sinφ0 = sinφ;
        }

        return 2 * (area > π ? area - 2 * π : area < -π ? area + 2 * π : area);
    }

    function absoluteArea(a) {
        return a < 0 ? a + 4 * π : a;
    }

    function triangleArea(t) {
        var a = distance(t[0], t[1]),
            b = distance(t[1], t[2]),
            c = distance(t[2], t[0]),
            s = (a + b + c) / 2;
        return 4 * Math.atan(Math.sqrt(Math.max(0, Math.tan(s / 2) * Math.tan((s - a) / 2) * Math.tan((s - b) / 2) * Math.tan((s - c) / 2))));
    }

    function distance(a, b) {
        var Δλ = (b[0] - a[0]) * radians,
            sinΔλ = Math.sin(Δλ),
            cosΔλ = Math.cos(Δλ),
            sinφ0 = Math.sin(a[1] * radians),
            cosφ0 = Math.cos(a[1] * radians),
            sinφ1 = Math.sin(b[1] * radians),
            cosφ1 = Math.cos(b[1] * radians),
            _;
        return Math.atan2(Math.sqrt((_ = cosφ1 * sinΔλ) * _ + (_ = cosφ0 * sinφ1 - sinφ0 * cosφ1 * cosΔλ) * _), sinφ0 * sinφ1 + cosφ0 * cosφ1 * cosΔλ);
    }

    function haversinDistance(x0, y0, x1, y1) {
        x0 *= radians, y0 *= radians, x1 *= radians, y1 *= radians;
        return 2 * Math.asin(Math.sqrt(haversin(y1 - y0) + Math.cos(y0) * Math.cos(y1) * haversin(x1 - x0)));
    }

    function haversin(x) {
        return (x = Math.sin(x / 2)) * x;
    }

},{}],27:[function(require,module,exports){
    var type = require("./type");

    module.exports = function(objects, transform) {
        var ε = 1e-2,
            x0 = -180, x0e = x0 + ε,
            x1 = 180, x1e = x1 - ε,
            y0 = -90, y0e = y0 + ε,
            y1 = 90, y1e = y1 - ε;

        if (transform) {
            var kx = transform.scale[0],
                ky = transform.scale[1],
                dx = transform.translate[0],
                dy = transform.translate[1];

            x0 = Math.round((x0 - dx) / kx);
            x1 = Math.round((x1 - dx) / kx);
            y0 = Math.round((y0 - dy) / ky);
            y1 = Math.round((y1 - dy) / ky);
            x0e = Math.round((x0e - dx) / kx);
            x1e = Math.round((x1e - dx) / kx);
            y0e = Math.round((y0e - dy) / ky);
            y1e = Math.round((y1e - dy) / ky);
        }

        function normalizePoint(y) {
            return y <= y0e ? [0, y0] // south pole
                : y >= y1e ? [0, y1] // north pole
                : [x0, y]; // antimeridian
        }

        function stitchPolygons(polygons) {
            var fragments = [];

            for (var p = 0, np = polygons.length; p < np; ++p) {
                var polygon = polygons[p];

                // For each ring, detect where it crosses the antimeridian or pole.
                for (var j = 0, m = polygon.length; j < m; ++j) {
                    var ring = polygon[j];
                    ring.polygon = polygon;

                    // By default, assume that this ring doesn’t need any stitching.
                    fragments.push(ring);

                    for (var i = 0, n = ring.length; i < n; ++i) {
                        var point = ring[i],
                            x = point[0],
                            y = point[1];

                        // If this is an antimeridian or polar point…
                        if (x <= x0e || x >= x1e || y <= y0e || y >= y1e) {

                            // Advance through any antimeridian or polar points…
                            for (var k = i + 1; k < n; ++k) {
                                var pointk = ring[k],
                                    xk = pointk[0],
                                    yk = pointk[1];
                                if (xk > x0e && xk < x1e && yk > y0e && yk < y1e) break;
                            }

                            // If this was just a single antimeridian or polar point,
                            // we don’t need to cut this ring into a fragment;
                            // we can just leave it as-is.
                            if (k === i + 1) continue;

                            // Otherwise, if this is not the first point in the ring,
                            // cut the current fragment so that it ends at the current point.
                            // The current point is also normalized for later joining.
                            if (i) {
                                var fragmentBefore = ring.slice(0, i + 1);
                                fragmentBefore.polygon = polygon;
                                fragmentBefore[fragmentBefore.length - 1] = normalizePoint(y);
                                fragments[fragments.length - 1] = fragmentBefore;
                            }

                            // If the ring started with an antimeridian fragment,
                            // we can ignore that fragment entirely.
                            else {
                                fragments.pop();
                            }

                            // If the remainder of the ring is an antimeridian fragment,
                            // move on to the next ring.
                            if (k >= n) break;

                            // Otherwise, add the remaining ring fragment and continue.
                            fragments.push(ring = ring.slice(k - 1));
                            ring[0] = normalizePoint(ring[0][1]);
                            ring.polygon = polygon;
                            i = -1;
                            n = ring.length;
                        }
                    }
                }
                polygon.length = 0;
            }

            // Now stitch the fragments back together into rings.
            // To connect the fragments start-to-end, create a simple index by end.
            var fragmentByStart = {},
                fragmentByEnd = {};

            // For each fragment…
            for (var i = 0, n = fragments.length; i < n; ++i) {
                var fragment = fragments[i],
                    start = fragment[0],
                    end = fragment[fragment.length - 1];

                // If this fragment is closed, add it as a standalone ring.
                if (start[0] === end[0] && start[1] === end[1]) {
                    fragment.polygon.push(fragment);
                    fragments[i] = null;
                    continue;
                }

                fragment.index = i;
                fragmentByStart[start] = fragmentByEnd[end] = fragment;
            }

            // For each open fragment…
            for (var i = 0; i < n; ++i) {
                var fragment = fragments[i];
                if (fragment) {

                    var start = fragment[0],
                        end = fragment[fragment.length - 1],
                        startFragment = fragmentByEnd[start],
                        endFragment = fragmentByStart[end];

                    delete fragmentByStart[start];
                    delete fragmentByEnd[end];

                    // If this fragment is closed, add it as a standalone ring.
                    if (start[0] === end[0] && start[1] === end[1]) {
                        fragment.polygon.push(fragment);
                        continue;
                    }

                    if (startFragment) {
                        delete fragmentByEnd[start];
                        delete fragmentByStart[startFragment[0]];
                        startFragment.pop(); // drop the shared coordinate
                        fragments[startFragment.index] = null;
                        fragment = startFragment.concat(fragment);
                        fragment.polygon = startFragment.polygon;

                        if (startFragment === endFragment) {
                            // Connect both ends to this single fragment to create a ring.
                            fragment.polygon.push(fragment);
                        } else {
                            fragment.index = n++;
                            fragments.push(fragmentByStart[fragment[0]] = fragmentByEnd[fragment[fragment.length - 1]] = fragment);
                        }
                    } else if (endFragment) {
                        delete fragmentByStart[end];
                        delete fragmentByEnd[endFragment[endFragment.length - 1]];
                        fragment.pop(); // drop the shared coordinate
                        fragment = fragment.concat(endFragment);
                        fragment.polygon = endFragment.polygon;
                        fragment.index = n++;
                        fragments[endFragment.index] = null;
                        fragments.push(fragmentByStart[fragment[0]] = fragmentByEnd[fragment[fragment.length - 1]] = fragment);
                    } else {
                        fragment.push(fragment[0]); // close ring
                        fragment.polygon.push(fragment);
                    }
                }
            }
            // TODO remove empty polygons.
        }

        var stitch = type({
            Polygon: function(polygon) { stitchPolygons([polygon.coordinates]); },
            MultiPolygon: function(multiPolygon) { stitchPolygons(multiPolygon.coordinates); }
        });

        for (var key in objects) {
            stitch.object(objects[key]);
        }
    };

},{"./type":39}],28:[function(require,module,exports){
    var type = require("./type"),
        stitch = require("./stitch"),
        systems = require("./coordinate-systems"),
        topologize = require("./topology/index"),
        delta = require("./delta"),
        geomify = require("./geomify"),
        prequantize = require("./pre-quantize"),
        postquantize = require("./post-quantize"),
        bounds = require("./bounds"),
        computeId = require("./compute-id"),
        transformProperties = require("./transform-properties");

    var ε = 1e-6;

    module.exports = function(objects, options) {
        var Q0 = 1e4, // precision of pre-quantization
            Q1 = 1e4, // precision of post-quantization (must be divisor of Q0)
            id = function(d) { return d.id; }, // function to compute object id
            propertyTransform = function() {}, // function to transform properties
            transform,
            minimumArea = 0,
            stitchPoles = true,
            verbose = false,
            system = null;

        if (options)
            "verbose" in options && (verbose = !!options["verbose"]),
            "stitch-poles" in options && (stitchPoles = !!options["stitch-poles"]),
            "coordinate-system" in options && (system = systems[options["coordinate-system"]]),
            "minimum-area" in options && (minimumArea = +options["minimum-area"]),
            "quantization" in options && (Q0 = Q1 = +options["quantization"]),
            "pre-quantization" in options && (Q0 = +options["pre-quantization"]),
            "post-quantization" in options && (Q1 = +options["post-quantization"]),
            "id" in options && (id = options["id"]),
            "property-transform" in options && (propertyTransform = options["property-transform"]);

        if (Q0 / Q1 % 1) throw new Error("post-quantization is not a divisor of pre-quantization");
        if (Q0 && !Q1) throw new Error("post-quantization is required when input is already quantized");

        // Compute the new feature id and transform properties.
        computeId(objects, id);
        transformProperties(objects, propertyTransform);

        // Convert to geometry objects.
        geomify(objects);

        // Compute initial bounding box.
        var bbox = bounds(objects);

        // For automatic coordinate system determination, consider the bounding box.
        var oversize = bbox[0] < -180 - ε
            || bbox[1] < -90 - ε
            || bbox[2] > 180 + ε
            || bbox[3] > 90 + ε;
        if (!system) {
            system = systems[oversize ? "cartesian" : "spherical"];
            if (options) options["coordinate-system"] = system.name;
        }

        if (system === systems.spherical) {
            if (oversize) throw new Error("spherical coordinates outside of [±180°, ±90°]");

            // When near the spherical coordinate limits, clamp to nice round values.
            // This avoids quantized coordinates that are slightly outside the limits.
            if (bbox[0] < -180 + ε) bbox[0] = -180;
            if (bbox[1] < -90 + ε) bbox[1] = -90;
            if (bbox[2] > 180 - ε) bbox[2] = 180;
            if (bbox[3] > 90 - ε) bbox[3] = 90;
        }

        if (verbose) {
            console.warn("bounds: " + bbox.join(" ") + " (" + system.name + ")");
        }

        // Pre-topology quantization.
        if (Q0) {
            transform = prequantize(objects, bbox, Q0, Q1);
            if (verbose) {
                console.warn("pre-quantization: " + transform.scale.map(function(k) { return system.formatDistance(k); }).join(" "));
            }
        }

        // Remove any antimeridian cuts and restitch.
        if (system === systems.spherical && stitchPoles) {
            stitch(objects, transform);
        }

        // Compute the topology.
        var topology = topologize(objects);
        if (Q0) topology.transform = transform;
        topology.bbox = bbox;
        if (verbose) {
            console.warn("topology: " + topology.arcs.length + " arcs, " + topology.arcs.reduce(function(p, v) { return p + v.length; }, 0) + " points");
        }

        // Post-topology quantization.
        if (Q1 && Q1 !== Q0) {
            postquantize(topology, Q0, Q1);
            transform = topology.transform;
            if (verbose) {
                console.warn("post-quantization: " + transform.scale.map(function(k) { return system.formatDistance(k); }).join(" "));
            }
        }

        // Convert to delta-encoding.
        if (Q1) {
            delta(topology);
        }

        return topology;
    };

},{"./bounds":12,"./compute-id":15,"./coordinate-systems":16,"./delta":17,"./geomify":19,"./post-quantize":20,"./pre-quantize":21,"./stitch":27,"./topology/index":34,"./transform-properties":38,"./type":39}],29:[function(require,module,exports){
    var join = require("./join");

// Given an extracted (pre-)topology, cuts (or rotates) arcs so that all shared
// point sequences are identified. The topology can then be subsequently deduped
// to remove exact duplicate arcs.
    module.exports = function(topology) {
        var junctions = join(topology),
            coordinates = topology.coordinates,
            lines = topology.lines,
            rings = topology.rings;

        for (var i = 0, n = lines.length; i < n; ++i) {
            var line = lines[i],
                lineMid = line[0],
                lineEnd = line[1];
            while (++lineMid < lineEnd) {
                if (junctions.has(coordinates[lineMid])) {
                    var next = {0: lineMid, 1: line[1]};
                    line[1] = lineMid;
                    line = line.next = next;
                }
            }
        }

        for (var i = 0, n = rings.length; i < n; ++i) {
            var ring = rings[i],
                ringStart = ring[0],
                ringMid = ringStart,
                ringEnd = ring[1],
                ringFixed = junctions.has(coordinates[ringStart]);
            while (++ringMid < ringEnd) {
                if (junctions.has(coordinates[ringMid])) {
                    if (ringFixed) {
                        var next = {0: ringMid, 1: ring[1]};
                        ring[1] = ringMid;
                        ring = ring.next = next;
                    } else { // For the first junction, we can rotate rather than cut.
                        rotateArray(coordinates, ringStart, ringEnd, ringEnd - ringMid);
                        coordinates[ringEnd] = coordinates[ringStart];
                        ringFixed = true;
                        ringMid = ringStart; // restart; we may have skipped junctions
                    }
                }
            }
        }

        return topology;
    };

    function rotateArray(array, start, end, offset) {
        reverse(array, start, end);
        reverse(array, start, start + offset);
        reverse(array, start + offset, end);
    }

    function reverse(array, start, end) {
        for (var mid = start + ((end-- - start) >> 1), t; start < mid; ++start, --end) {
            t = array[start], array[start] = array[end], array[end] = t;
        }
    }

},{"./join":35}],30:[function(require,module,exports){
    var join = require("./join"),
        hashmap = require("./hashmap"),
        hashPoint = require("./point-hash"),
        equalPoint = require("./point-equal");

// Given a cut topology, combines duplicate arcs.
    module.exports = function(topology) {
        var coordinates = topology.coordinates,
            lines = topology.lines,
            rings = topology.rings,
            arcCount = lines.length + rings.length;

        delete topology.lines;
        delete topology.rings;

        // Count the number of (non-unique) arcs to initialize the hashmap safely.
        for (var i = 0, n = lines.length; i < n; ++i) {
            var line = lines[i]; while (line = line.next) ++arcCount;
        }
        for (var i = 0, n = rings.length; i < n; ++i) {
            var ring = rings[i]; while (ring = ring.next) ++arcCount;
        }

        var arcsByEnd = hashmap(arcCount * 2 * 1.4, hashPoint, equalPoint),
            arcs = topology.arcs = [];

        for (var i = 0, n = lines.length; i < n; ++i) {
            var line = lines[i];
            do {
                dedupLine(line);
            } while (line = line.next);
        }

        for (var i = 0, n = rings.length; i < n; ++i) {
            var ring = rings[i];
            if (ring.next) { // arc is no longer closed
                do {
                    dedupLine(ring);
                } while (ring = ring.next);
            } else {
                dedupRing(ring);
            }
        }

        function dedupLine(arc) {
            var startPoint,
                endPoint,
                startArcs,
                endArcs;

            // Does this arc match an existing arc in order?
            if (startArcs = arcsByEnd.get(startPoint = coordinates[arc[0]])) {
                for (var i = 0, n = startArcs.length; i < n; ++i) {
                    var startArc = startArcs[i];
                    if (equalLine(startArc, arc)) {
                        arc[0] = startArc[0];
                        arc[1] = startArc[1];
                        return;
                    }
                }
            }

            // Does this arc match an existing arc in reverse order?
            if (endArcs = arcsByEnd.get(endPoint = coordinates[arc[1]])) {
                for (var i = 0, n = endArcs.length; i < n; ++i) {
                    var endArc = endArcs[i];
                    if (reverseEqualLine(endArc, arc)) {
                        arc[1] = endArc[0];
                        arc[0] = endArc[1];
                        return;
                    }
                }
            }

            if (startArcs) startArcs.push(arc); else arcsByEnd.set(startPoint, [arc]);
            if (endArcs) endArcs.push(arc); else arcsByEnd.set(endPoint, [arc]);
            arcs.push(arc);
        }

        function dedupRing(arc) {
            var endPoint,
                endArcs;

            // Does this arc match an existing line in order, or reverse order?
            // Rings are closed, so their start point and end point is the same.
            if (endArcs = arcsByEnd.get(endPoint = coordinates[arc[0]])) {
                for (var i = 0, n = endArcs.length; i < n; ++i) {
                    var endArc = endArcs[i];
                    if (equalRing(endArc, arc)) {
                        arc[0] = endArc[0];
                        arc[1] = endArc[1];
                        return;
                    }
                    if (reverseEqualRing(endArc, arc)) {
                        arc[0] = endArc[1];
                        arc[1] = endArc[0];
                        return;
                    }
                }
            }

            // Otherwise, does this arc match an existing ring in order, or reverse order?
            if (endArcs = arcsByEnd.get(endPoint = coordinates[arc[0] + findMinimumOffset(arc)])) {
                for (var i = 0, n = endArcs.length; i < n; ++i) {
                    var endArc = endArcs[i];
                    if (equalRing(endArc, arc)) {
                        arc[0] = endArc[0];
                        arc[1] = endArc[1];
                        return;
                    }
                    if (reverseEqualRing(endArc, arc)) {
                        arc[0] = endArc[1];
                        arc[1] = endArc[0];
                        return;
                    }
                }
            }

            if (endArcs) endArcs.push(arc); else arcsByEnd.set(endPoint, [arc]);
            arcs.push(arc);
        }

        function equalLine(arcA, arcB) {
            var ia = arcA[0], ib = arcB[0],
                ja = arcA[1], jb = arcB[1];
            if (ia - ja !== ib - jb) return false;
            for (; ia <= ja; ++ia, ++ib) if (!equalPoint(coordinates[ia], coordinates[ib])) return false;
            return true;
        }

        function reverseEqualLine(arcA, arcB) {
            var ia = arcA[0], ib = arcB[0],
                ja = arcA[1], jb = arcB[1];
            if (ia - ja !== ib - jb) return false;
            for (; ia <= ja; ++ia, --jb) if (!equalPoint(coordinates[ia], coordinates[jb])) return false;
            return true;
        }

        function equalRing(arcA, arcB) {
            var ia = arcA[0], ib = arcB[0],
                ja = arcA[1], jb = arcB[1],
                n = ja - ia;
            if (n !== jb - ib) return false;
            var ka = findMinimumOffset(arcA),
                kb = findMinimumOffset(arcB);
            for (var i = 0; i < n; ++i) {
                if (!equalPoint(coordinates[ia + (i + ka) % n], coordinates[ib + (i + kb) % n])) return false;
            }
            return true;
        }

        function reverseEqualRing(arcA, arcB) {
            var ia = arcA[0], ib = arcB[0],
                ja = arcA[1], jb = arcB[1],
                n = ja - ia;
            if (n !== jb - ib) return false;
            var ka = findMinimumOffset(arcA),
                kb = n - findMinimumOffset(arcB);
            for (var i = 0; i < n; ++i) {
                if (!equalPoint(coordinates[ia + (i + ka) % n], coordinates[jb - (i + kb) % n])) return false;
            }
            return true;
        }

        // Rings are rotated to a consistent, but arbitrary, start point.
        // This is necessary to detect when a ring and a rotated copy are dupes.
        function findMinimumOffset(arc) {
            var start = arc[0],
                end = arc[1],
                mid = start,
                minimum = mid,
                minimumPoint = coordinates[mid];
            while (++mid < end) {
                var point = coordinates[mid];
                if (point[0] < minimumPoint[0] || point[0] === minimumPoint[0] && point[1] < minimumPoint[1]) {
                    minimum = mid;
                    minimumPoint = point;
                }
            }
            return minimum - start;
        }

        return topology;
    };

},{"./hashmap":32,"./join":35,"./point-equal":36,"./point-hash":37}],31:[function(require,module,exports){
// Extracts the lines and rings from the specified hash of geometry objects.
//
// Returns an object with three properties:
//
// * coordinates - shared buffer of [x, y] coordinates
// * lines - lines extracted from the hash, of the form [start, end]
// * rings - rings extracted from the hash, of the form [start, end]
//
// For each ring or line, start and end represent inclusive indexes into the
// coordinates buffer. For rings (and closed lines), coordinates[start] equals
// coordinates[end].
//
// For each line or polygon geometry in the input hash, including nested
// geometries as in geometry collections, the `coordinates` array is replaced
// with an equivalent `arcs` array that, for each line (for line string
// geometries) or ring (for polygon geometries), points to one of the above
// lines or rings.
    module.exports = function(objects) {
        var index = -1,
            lines = [],
            rings = [],
            coordinates = [];

        function extractGeometry(geometry) {
            if (geometry && extractGeometryType.hasOwnProperty(geometry.type)) extractGeometryType[geometry.type](geometry);
        }

        var extractGeometryType = {
            GeometryCollection: function(o) { o.geometries.forEach(extractGeometry); },
            LineString: function(o) { o.arcs = extractLine(o.coordinates); delete o.coordinates; },
            MultiLineString: function(o) { o.arcs = o.coordinates.map(extractLine); delete o.coordinates; },
            Polygon: function(o) { o.arcs = o.coordinates.map(extractRing); delete o.coordinates; },
            MultiPolygon: function(o) { o.arcs = o.coordinates.map(extractMultiRing); delete o.coordinates; }
        };

        function extractLine(line) {
            for (var i = 0, n = line.length; i < n; ++i) coordinates[++index] = line[i];
            var arc = {0: index - n + 1, 1: index};
            lines.push(arc);
            return arc;
        }

        function extractRing(ring) {
            for (var i = 0, n = ring.length; i < n; ++i) coordinates[++index] = ring[i];
            var arc = {0: index - n + 1, 1: index};
            rings.push(arc);
            return arc;
        }

        function extractMultiRing(rings) {
            return rings.map(extractRing);
        }

        for (var key in objects) {
            extractGeometry(objects[key]);
        }

        return {
            type: "Topology",
            coordinates: coordinates,
            lines: lines,
            rings: rings,
            objects: objects
        };
    };

},{}],32:[function(require,module,exports){
    module.exports = function(size, hash, equal, keyType, keyEmpty, valueType) {
        if (arguments.length === 3) {
            keyType = valueType = Array;
            keyEmpty = null;
        }

        var keystore = new keyType(size = 1 << Math.max(4, Math.ceil(Math.log(size) / Math.LN2))),
            valstore = new valueType(size),
            mask = size - 1,
            free = size;

        for (var i = 0; i < size; ++i) {
            keystore[i] = keyEmpty;
        }

        function set(key, value) {
            var index = hash(key) & mask,
                matchKey = keystore[index],
                collisions = 0;
            while (matchKey != keyEmpty) {
                if (equal(matchKey, key)) return valstore[index] = value;
                if (++collisions >= size) throw new Error("full hashmap");
                matchKey = keystore[index = (index + 1) & mask];
            }
            keystore[index] = key;
            valstore[index] = value;
            --free;
            return value;
        }

        function maybeSet(key, value) {
            var index = hash(key) & mask,
                matchKey = keystore[index],
                collisions = 0;
            while (matchKey != keyEmpty) {
                if (equal(matchKey, key)) return valstore[index];
                if (++collisions >= size) throw new Error("full hashmap");
                matchKey = keystore[index = (index + 1) & mask];
            }
            keystore[index] = key;
            valstore[index] = value;
            --free;
            return value;
        }

        function get(key, missingValue) {
            var index = hash(key) & mask,
                matchKey = keystore[index],
                collisions = 0;
            while (matchKey != keyEmpty) {
                if (equal(matchKey, key)) return valstore[index];
                if (++collisions >= size) break;
                matchKey = keystore[index = (index + 1) & mask];
            }
            return missingValue;
        }

        function keys() {
            var keys = [];
            for (var i = 0, n = keystore.length; i < n; ++i) {
                var matchKey = keystore[i];
                if (matchKey != keyEmpty) keys.push(matchKey);
            }
            return keys;
        }

        return {
            set: set,
            maybeSet: maybeSet, // set if unset
            get: get,
            keys: keys
        };
    };

},{}],33:[function(require,module,exports){
    module.exports = function(size, hash, equal, type, empty) {
        if (arguments.length === 3) {
            type = Array;
            empty = null;
        }

        var store = new type(size = 1 << Math.max(4, Math.ceil(Math.log(size) / Math.LN2))),
            mask = size - 1,
            free = size;

        for (var i = 0; i < size; ++i) {
            store[i] = empty;
        }

        function add(value) {
            var index = hash(value) & mask,
                match = store[index],
                collisions = 0;
            while (match != empty) {
                if (equal(match, value)) return true;
                if (++collisions >= size) throw new Error("full hashset");
                match = store[index = (index + 1) & mask];
            }
            store[index] = value;
            --free;
            return true;
        }

        function has(value) {
            var index = hash(value) & mask,
                match = store[index],
                collisions = 0;
            while (match != empty) {
                if (equal(match, value)) return true;
                if (++collisions >= size) break;
                match = store[index = (index + 1) & mask];
            }
            return false;
        }

        function values() {
            var values = [];
            for (var i = 0, n = store.length; i < n; ++i) {
                var match = store[i];
                if (match != empty) values.push(match);
            }
            return values;
        }

        return {
            add: add,
            has: has,
            values: values
        };
    };

},{}],34:[function(require,module,exports){
    var hashmap = require("./hashmap"),
        extract = require("./extract"),
        cut = require("./cut"),
        dedup = require("./dedup");

// Constructs the TopoJSON Topology for the specified hash of geometries.
// Each object in the specified hash must be a GeoJSON object,
// meaning FeatureCollection, a Feature or a geometry object.
    module.exports = function(objects) {
        var topology = dedup(cut(extract(objects))),
            coordinates = topology.coordinates,
            indexByArc = hashmap(topology.arcs.length * 1.4, hashArc, equalArc);

        objects = topology.objects; // for garbage collection

        topology.arcs = topology.arcs.map(function(arc, i) {
            indexByArc.set(arc, i);
            return coordinates.slice(arc[0], arc[1] + 1);
        });

        delete topology.coordinates;
        coordinates = null;

        function indexGeometry(geometry) {
            if (geometry && indexGeometryType.hasOwnProperty(geometry.type)) indexGeometryType[geometry.type](geometry);
        }

        var indexGeometryType = {
            GeometryCollection: function(o) { o.geometries.forEach(indexGeometry); },
            LineString: function(o) { o.arcs = indexArcs(o.arcs); },
            MultiLineString: function(o) { o.arcs = o.arcs.map(indexArcs); },
            Polygon: function(o) { o.arcs = o.arcs.map(indexArcs); },
            MultiPolygon: function(o) { o.arcs = o.arcs.map(indexMultiArcs); }
        };

        function indexArcs(arc) {
            var indexes = [];
            do {
                var index = indexByArc.get(arc);
                indexes.push(arc[0] < arc[1] ? index : ~index);
            } while (arc = arc.next);
            return indexes;
        }

        function indexMultiArcs(arcs) {
            return arcs.map(indexArcs);
        }

        for (var key in objects) {
            indexGeometry(objects[key]);
        }

        return topology;
    };

    function hashArc(arc) {
        var i = arc[0], j = arc[1], t;
        if (j < i) t = i, i = j, j = t;
        return i + 31 * j;
    }

    function equalArc(arcA, arcB) {
        var ia = arcA[0], ja = arcA[1],
            ib = arcB[0], jb = arcB[1], t;
        if (ja < ia) t = ia, ia = ja, ja = t;
        if (jb < ib) t = ib, ib = jb, jb = t;
        return ia === ib && ja === jb;
    }

},{"./cut":29,"./dedup":30,"./extract":31,"./hashmap":32}],35:[function(require,module,exports){
    var hashset = require("./hashset"),
        hashmap = require("./hashmap"),
        hashPoint = require("./point-hash"),
        equalPoint = require("./point-equal");

// Given an extracted (pre-)topology, identifies all of the junctions. These are
// the points at which arcs (lines or rings) will need to be cut so that each
// arc is represented uniquely.
//
// A junction is a point where at least one arc deviates from another arc going
// through the same point. For example, consider the point B. If there is a arc
// through ABC and another arc through CBA, then B is not a junction because in
// both cases the adjacent point pairs are {A,C}. However, if there is an
// additional arc ABD, then {A,D} != {A,C}, and thus B becomes a junction.
//
// For a closed ring ABCA, the first point A’s adjacent points are the second
// and last point {B,C}. For a line, the first and last point are always
// considered junctions, even if the line is closed; this ensures that a closed
// line is never rotated.
    module.exports = function(topology) {
        var coordinates = topology.coordinates,
            lines = topology.lines,
            rings = topology.rings,
            indexes = index(),
            visitedByIndex = new Int32Array(coordinates.length),
            leftByIndex = new Int32Array(coordinates.length),
            rightByIndex = new Int32Array(coordinates.length),
            junctionByIndex = new Int8Array(coordinates.length),
            junctionCount = 0; // upper bound on number of junctions

        for (var i = 0, n = coordinates.length; i < n; ++i) {
            visitedByIndex[i] = leftByIndex[i] = rightByIndex[i] = -1;
        }

        for (var i = 0, n = lines.length; i < n; ++i) {
            var line = lines[i],
                lineStart = line[0],
                lineEnd = line[1],
                previousIndex,
                currentIndex = indexes[lineStart],
                nextIndex = indexes[++lineStart];
            ++junctionCount, junctionByIndex[currentIndex] = 1; // start
            while (++lineStart <= lineEnd) {
                sequence(i, previousIndex = currentIndex, currentIndex = nextIndex, nextIndex = indexes[lineStart]);
            }
            ++junctionCount, junctionByIndex[nextIndex] = 1; // end
        }

        for (var i = 0, n = coordinates.length; i < n; ++i) {
            visitedByIndex[i] = -1;
        }

        for (var i = 0, n = rings.length; i < n; ++i) {
            var ring = rings[i],
                ringStart = ring[0] + 1,
                ringEnd = ring[1],
                previousIndex = indexes[ringEnd - 1],
                currentIndex = indexes[ringStart - 1],
                nextIndex = indexes[ringStart];
            sequence(i, previousIndex, currentIndex, nextIndex);
            while (++ringStart <= ringEnd) {
                sequence(i, previousIndex = currentIndex, currentIndex = nextIndex, nextIndex = indexes[ringStart]);
            }
        }

        function sequence(i, previousIndex, currentIndex, nextIndex) {
            if (visitedByIndex[currentIndex] === i) return; // ignore self-intersection
            visitedByIndex[currentIndex] = i;
            var leftIndex = leftByIndex[currentIndex];
            if (leftIndex >= 0) {
                var rightIndex = rightByIndex[currentIndex];
                if ((leftIndex !== previousIndex || rightIndex !== nextIndex)
                    && (leftIndex !== nextIndex || rightIndex !== previousIndex)) {
                    ++junctionCount, junctionByIndex[currentIndex] = 1;
                }
            } else {
                leftByIndex[currentIndex] = previousIndex;
                rightByIndex[currentIndex] = nextIndex;
            }
        }

        function index() {
            var indexByPoint = hashmap(coordinates.length * 1.4, hashIndex, equalIndex, Int32Array, -1, Int32Array),
                indexes = new Int32Array(coordinates.length);

            for (var i = 0, n = coordinates.length; i < n; ++i) {
                indexes[i] = indexByPoint.maybeSet(i, i);
            }

            return indexes;
        }

        function hashIndex(i) {
            return hashPoint(coordinates[i]);
        }

        function equalIndex(i, j) {
            return equalPoint(coordinates[i], coordinates[j]);
        }

        visitedByIndex = leftByIndex = rightByIndex = null;

        var junctionByPoint = hashset(junctionCount * 1.4, hashPoint, equalPoint);

        // Convert back to a standard hashset by point for caller convenience.
        for (var i = 0, n = coordinates.length, j; i < n; ++i) {
            if (junctionByIndex[j = indexes[i]]) {
                junctionByPoint.add(coordinates[j]);
            }
        }

        return junctionByPoint;
    };

},{"./hashmap":32,"./hashset":33,"./point-equal":36,"./point-hash":37}],36:[function(require,module,exports){
    module.exports = function(pointA, pointB) {
        return pointA[0] === pointB[0] && pointA[1] === pointB[1];
    };

},{}],37:[function(require,module,exports){
// TODO if quantized, use simpler Int32 hashing?

    var buffer = new ArrayBuffer(16),
        floats = new Float64Array(buffer),
        uints = new Uint32Array(buffer);

    module.exports = function(point) {
        floats[0] = point[0];
        floats[1] = point[1];
        var hash = uints[0] ^ uints[1];
        hash = hash << 5 ^ hash >> 7 ^ uints[2] ^ uints[3];
        return hash & 0x7fffffff;
    };

},{}],38:[function(require,module,exports){
// Given a hash of GeoJSON objects, transforms any properties on features using
// the specified transform function. The function is invoked for each existing
// property on the current feature, being passed the new properties hash, the
// property name, and the property value. The function is then expected to
// assign a new value to the given property hash if the feature is to be
// retained and return true. Or, to skip the property, do nothing and return
// false. If no properties are propagated to the new properties hash, the
// properties hash will be deleted from the current feature.
    module.exports = function(objects, propertyTransform) {
        if (arguments.length < 2) propertyTransform = function() {};

        function transformObject(object) {
            if (object && transformObjectType.hasOwnProperty(object.type)) transformObjectType[object.type](object);
        }

        function transformFeature(feature) {
            if (feature.properties) {
                var properties0 = feature.properties,
                    properties1 = {},
                    empty = true;

                for (var key0 in properties0) {
                    if (propertyTransform(properties1, key0, properties0[key0])) {
                        empty = false;
                    }
                }

                if (empty) delete feature.properties;
                else feature.properties = properties1;
            }
        }

        var transformObjectType = {
            Feature: transformFeature,
            FeatureCollection: function(collection) { collection.features.forEach(transformFeature); }
        };

        for (var key in objects) {
            transformObject(objects[key]);
        }

        return objects;
    };

},{}],39:[function(require,module,exports){
    module.exports = function(types) {
        for (var type in typeDefaults) {
            if (!(type in types)) {
                types[type] = typeDefaults[type];
            }
        }
        types.defaults = typeDefaults;
        return types;
    };

    var typeDefaults = {

        Feature: function(feature) {
            if (feature.geometry) this.geometry(feature.geometry);
        },

        FeatureCollection: function(collection) {
            var features = collection.features, i = -1, n = features.length;
            while (++i < n) this.Feature(features[i]);
        },

        GeometryCollection: function(collection) {
            var geometries = collection.geometries, i = -1, n = geometries.length;
            while (++i < n) this.geometry(geometries[i]);
        },

        LineString: function(lineString) {
            this.line(lineString.coordinates);
        },

        MultiLineString: function(multiLineString) {
            var coordinates = multiLineString.coordinates, i = -1, n = coordinates.length;
            while (++i < n) this.line(coordinates[i]);
        },

        MultiPoint: function(multiPoint) {
            var coordinates = multiPoint.coordinates, i = -1, n = coordinates.length;
            while (++i < n) this.point(coordinates[i]);
        },

        MultiPolygon: function(multiPolygon) {
            var coordinates = multiPolygon.coordinates, i = -1, n = coordinates.length;
            while (++i < n) this.polygon(coordinates[i]);
        },

        Point: function(point) {
            this.point(point.coordinates);
        },

        Polygon: function(polygon) {
            this.polygon(polygon.coordinates);
        },

        object: function(object) {
            return object == null ? null
                : typeObjects.hasOwnProperty(object.type) ? this[object.type](object)
                : this.geometry(object);
        },

        geometry: function(geometry) {
            return geometry == null ? null
                : typeGeometries.hasOwnProperty(geometry.type) ? this[geometry.type](geometry)
                : null;
        },

        point: function() {},

        line: function(coordinates) {
            var i = -1, n = coordinates.length;
            while (++i < n) this.point(coordinates[i]);
        },

        polygon: function(coordinates) {
            var i = -1, n = coordinates.length;
            while (++i < n) this.line(coordinates[i]);
        }
    };

    var typeGeometries = {
        LineString: 1,
        MultiLineString: 1,
        MultiPoint: 1,
        MultiPolygon: 1,
        Point: 1,
        Polygon: 1,
        GeometryCollection: 1
    };

    var typeObjects = {
        Feature: 1,
        FeatureCollection: 1
    };

},{}],40:[function(require,module,exports){
    !function() {
        var topojson = {
            version: "1.6.8",
            mesh: function(topology) { return object(topology, meshArcs.apply(this, arguments)); },
            meshArcs: meshArcs,
            merge: function(topology) { return object(topology, mergeArcs.apply(this, arguments)); },
            mergeArcs: mergeArcs,
            feature: featureOrCollection,
            neighbors: neighbors,
            presimplify: presimplify
        };

        function stitchArcs(topology, arcs) {
            var stitchedArcs = {},
                fragmentByStart = {},
                fragmentByEnd = {},
                fragments = [],
                emptyIndex = -1;

            // Stitch empty arcs first, since they may be subsumed by other arcs.
            arcs.forEach(function(i, j) {
                var arc = topology.arcs[i < 0 ? ~i : i], t;
                if (arc.length < 3 && !arc[1][0] && !arc[1][1]) {
                    t = arcs[++emptyIndex], arcs[emptyIndex] = i, arcs[j] = t;
                }
            });

            arcs.forEach(function(i) {
                var e = ends(i),
                    start = e[0],
                    end = e[1],
                    f, g;

                if (f = fragmentByEnd[start]) {
                    delete fragmentByEnd[f.end];
                    f.push(i);
                    f.end = end;
                    if (g = fragmentByStart[end]) {
                        delete fragmentByStart[g.start];
                        var fg = g === f ? f : f.concat(g);
                        fragmentByStart[fg.start = f.start] = fragmentByEnd[fg.end = g.end] = fg;
                    } else {
                        fragmentByStart[f.start] = fragmentByEnd[f.end] = f;
                    }
                } else if (f = fragmentByStart[end]) {
                    delete fragmentByStart[f.start];
                    f.unshift(i);
                    f.start = start;
                    if (g = fragmentByEnd[start]) {
                        delete fragmentByEnd[g.end];
                        var gf = g === f ? f : g.concat(f);
                        fragmentByStart[gf.start = g.start] = fragmentByEnd[gf.end = f.end] = gf;
                    } else {
                        fragmentByStart[f.start] = fragmentByEnd[f.end] = f;
                    }
                } else {
                    f = [i];
                    fragmentByStart[f.start = start] = fragmentByEnd[f.end = end] = f;
                }
            });

            function ends(i) {
                var arc = topology.arcs[i < 0 ? ~i : i], p0 = arc[0], p1;
                if (topology.transform) p1 = [0, 0], arc.forEach(function(dp) { p1[0] += dp[0], p1[1] += dp[1]; });
                else p1 = arc[arc.length - 1];
                return i < 0 ? [p1, p0] : [p0, p1];
            }

            function flush(fragmentByEnd, fragmentByStart) {
                for (var k in fragmentByEnd) {
                    var f = fragmentByEnd[k];
                    delete fragmentByStart[f.start];
                    delete f.start;
                    delete f.end;
                    f.forEach(function(i) { stitchedArcs[i < 0 ? ~i : i] = 1; });
                    fragments.push(f);
                }
            }

            flush(fragmentByEnd, fragmentByStart);
            flush(fragmentByStart, fragmentByEnd);
            arcs.forEach(function(i) { if (!stitchedArcs[i < 0 ? ~i : i]) fragments.push([i]); });

            return fragments;
        }

        function meshArcs(topology, o, filter) {
            var arcs = [];

            if (arguments.length > 1) {
                var geomsByArc = [],
                    geom;

                function arc(i) {
                    var j = i < 0 ? ~i : i;
                    (geomsByArc[j] || (geomsByArc[j] = [])).push({i: i, g: geom});
                }

                function line(arcs) {
                    arcs.forEach(arc);
                }

                function polygon(arcs) {
                    arcs.forEach(line);
                }

                function geometry(o) {
                    if (o.type === "GeometryCollection") o.geometries.forEach(geometry);
                    else if (o.type in geometryType) geom = o, geometryType[o.type](o.arcs);
                }

                var geometryType = {
                    LineString: line,
                    MultiLineString: polygon,
                    Polygon: polygon,
                    MultiPolygon: function(arcs) { arcs.forEach(polygon); }
                };

                geometry(o);

                geomsByArc.forEach(arguments.length < 3
                    ? function(geoms) { arcs.push(geoms[0].i); }
                    : function(geoms) { if (filter(geoms[0].g, geoms[geoms.length - 1].g)) arcs.push(geoms[0].i); });
            } else {
                for (var i = 0, n = topology.arcs.length; i < n; ++i) arcs.push(i);
            }

            return {type: "MultiLineString", arcs: stitchArcs(topology, arcs)};
        }

        function mergeArcs(topology, objects) {
            var polygonsByArc = {},
                polygons = [],
                components = [];

            objects.forEach(function(o) {
                if (o.type === "Polygon") register(o.arcs);
                else if (o.type === "MultiPolygon") o.arcs.forEach(register);
            });

            function register(polygon) {
                polygon.forEach(function(ring) {
                    ring.forEach(function(arc) {
                        (polygonsByArc[arc = arc < 0 ? ~arc : arc] || (polygonsByArc[arc] = [])).push(polygon);
                    });
                });
                polygons.push(polygon);
            }

            function exterior(ring) {
                return cartesianRingArea(object(topology, {type: "Polygon", arcs: [ring]}).coordinates[0]) > 0; // TODO allow spherical?
            }

            polygons.forEach(function(polygon) {
                if (!polygon._) {
                    var component = [],
                        neighbors = [polygon];
                    polygon._ = 1;
                    components.push(component);
                    while (polygon = neighbors.pop()) {
                        component.push(polygon);
                        polygon.forEach(function(ring) {
                            ring.forEach(function(arc) {
                                polygonsByArc[arc < 0 ? ~arc : arc].forEach(function(polygon) {
                                    if (!polygon._) {
                                        polygon._ = 1;
                                        neighbors.push(polygon);
                                    }
                                });
                            });
                        });
                    }
                }
            });

            polygons.forEach(function(polygon) {
                delete polygon._;
            });

            return {
                type: "MultiPolygon",
                arcs: components.map(function(polygons) {
                    var arcs = [];

                    // Extract the exterior (unique) arcs.
                    polygons.forEach(function(polygon) {
                        polygon.forEach(function(ring) {
                            ring.forEach(function(arc) {
                                if (polygonsByArc[arc < 0 ? ~arc : arc].length < 2) {
                                    arcs.push(arc);
                                }
                            });
                        });
                    });

                    // Stitch the arcs into one or more rings.
                    arcs = stitchArcs(topology, arcs);

                    // If more than one ring is returned,
                    // at most one of these rings can be the exterior;
                    // this exterior ring has the same winding order
                    // as any exterior ring in the original polygons.
                    if ((n = arcs.length) > 1) {
                        var sgn = exterior(polygons[0][0]);
                        for (var i = 0, t; i < n; ++i) {
                            if (sgn === exterior(arcs[i])) {
                                t = arcs[0], arcs[0] = arcs[i], arcs[i] = t;
                                break;
                            }
                        }
                    }

                    return arcs;
                })
            };
        }

        function featureOrCollection(topology, o) {
            return o.type === "GeometryCollection" ? {
                type: "FeatureCollection",
                features: o.geometries.map(function(o) { return feature(topology, o); })
            } : feature(topology, o);
        }

        function feature(topology, o) {
            var f = {
                type: "Feature",
                id: o.id,
                properties: o.properties || {},
                geometry: object(topology, o)
            };
            if (o.id == null) delete f.id;
            return f;
        }

        function object(topology, o) {
            var absolute = transformAbsolute(topology.transform),
                arcs = topology.arcs;

            function arc(i, points) {
                if (points.length) points.pop();
                for (var a = arcs[i < 0 ? ~i : i], k = 0, n = a.length, p; k < n; ++k) {
                    points.push(p = a[k].slice());
                    absolute(p, k);
                }
                if (i < 0) reverse(points, n);
            }

            function point(p) {
                p = p.slice();
                absolute(p, 0);
                return p;
            }

            function line(arcs) {
                var points = [];
                for (var i = 0, n = arcs.length; i < n; ++i) arc(arcs[i], points);
                if (points.length < 2) points.push(points[0].slice());
                return points;
            }

            function ring(arcs) {
                var points = line(arcs);
                while (points.length < 4) points.push(points[0].slice());
                return points;
            }

            function polygon(arcs) {
                return arcs.map(ring);
            }

            function geometry(o) {
                var t = o.type;
                return t === "GeometryCollection" ? {type: t, geometries: o.geometries.map(geometry)}
                    : t in geometryType ? {type: t, coordinates: geometryType[t](o)}
                    : null;
            }

            var geometryType = {
                Point: function(o) { return point(o.coordinates); },
                MultiPoint: function(o) { return o.coordinates.map(point); },
                LineString: function(o) { return line(o.arcs); },
                MultiLineString: function(o) { return o.arcs.map(line); },
                Polygon: function(o) { return polygon(o.arcs); },
                MultiPolygon: function(o) { return o.arcs.map(polygon); }
            };

            return geometry(o);
        }

        function reverse(array, n) {
            var t, j = array.length, i = j - n; while (i < --j) t = array[i], array[i++] = array[j], array[j] = t;
        }

        function bisect(a, x) {
            var lo = 0, hi = a.length;
            while (lo < hi) {
                var mid = lo + hi >>> 1;
                if (a[mid] < x) lo = mid + 1;
                else hi = mid;
            }
            return lo;
        }

        function neighbors(objects) {
            var indexesByArc = {}, // arc index -> array of object indexes
                neighbors = objects.map(function() { return []; });

            function line(arcs, i) {
                arcs.forEach(function(a) {
                    if (a < 0) a = ~a;
                    var o = indexesByArc[a];
                    if (o) o.push(i);
                    else indexesByArc[a] = [i];
                });
            }

            function polygon(arcs, i) {
                arcs.forEach(function(arc) { line(arc, i); });
            }

            function geometry(o, i) {
                if (o.type === "GeometryCollection") o.geometries.forEach(function(o) { geometry(o, i); });
                else if (o.type in geometryType) geometryType[o.type](o.arcs, i);
            }

            var geometryType = {
                LineString: line,
                MultiLineString: polygon,
                Polygon: polygon,
                MultiPolygon: function(arcs, i) { arcs.forEach(function(arc) { polygon(arc, i); }); }
            };

            objects.forEach(geometry);

            for (var i in indexesByArc) {
                for (var indexes = indexesByArc[i], m = indexes.length, j = 0; j < m; ++j) {
                    for (var k = j + 1; k < m; ++k) {
                        var ij = indexes[j], ik = indexes[k], n;
                        if ((n = neighbors[ij])[i = bisect(n, ik)] !== ik) n.splice(i, 0, ik);
                        if ((n = neighbors[ik])[i = bisect(n, ij)] !== ij) n.splice(i, 0, ij);
                    }
                }
            }

            return neighbors;
        }

        function presimplify(topology, triangleArea) {
            var absolute = transformAbsolute(topology.transform),
                relative = transformRelative(topology.transform),
                heap = minAreaHeap(),
                maxArea = 0,
                triangle;

            if (!triangleArea) triangleArea = cartesianTriangleArea;

            topology.arcs.forEach(function(arc) {
                var triangles = [];

                arc.forEach(absolute);

                for (var i = 1, n = arc.length - 1; i < n; ++i) {
                    triangle = arc.slice(i - 1, i + 2);
                    triangle[1][2] = triangleArea(triangle);
                    triangles.push(triangle);
                    heap.push(triangle);
                }

                // Always keep the arc endpoints!
                arc[0][2] = arc[n][2] = Infinity;

                for (var i = 0, n = triangles.length; i < n; ++i) {
                    triangle = triangles[i];
                    triangle.previous = triangles[i - 1];
                    triangle.next = triangles[i + 1];
                }
            });

            while (triangle = heap.pop()) {
                var previous = triangle.previous,
                    next = triangle.next;

                // If the area of the current point is less than that of the previous point
                // to be eliminated, use the latter's area instead. This ensures that the
                // current point cannot be eliminated without eliminating previously-
                // eliminated points.
                if (triangle[1][2] < maxArea) triangle[1][2] = maxArea;
                else maxArea = triangle[1][2];

                if (previous) {
                    previous.next = next;
                    previous[2] = triangle[2];
                    update(previous);
                }

                if (next) {
                    next.previous = previous;
                    next[0] = triangle[0];
                    update(next);
                }
            }

            topology.arcs.forEach(function(arc) {
                arc.forEach(relative);
            });

            function update(triangle) {
                heap.remove(triangle);
                triangle[1][2] = triangleArea(triangle);
                heap.push(triangle);
            }

            return topology;
        };

        function cartesianRingArea(ring) {
            var i = -1,
                n = ring.length,
                a,
                b = ring[n - 1],
                area = 0;

            while (++i < n) {
                a = b;
                b = ring[i];
                area += a[0] * b[1] - a[1] * b[0];
            }

            return area * .5;
        }

        function cartesianTriangleArea(triangle) {
            var a = triangle[0], b = triangle[1], c = triangle[2];
            return Math.abs((a[0] - c[0]) * (b[1] - a[1]) - (a[0] - b[0]) * (c[1] - a[1]));
        }

        function compareArea(a, b) {
            return a[1][2] - b[1][2];
        }

        function minAreaHeap() {
            var heap = {},
                array = [],
                size = 0;

            heap.push = function(object) {
                up(array[object._ = size] = object, size++);
                return size;
            };

            heap.pop = function() {
                if (size <= 0) return;
                var removed = array[0], object;
                if (--size > 0) object = array[size], down(array[object._ = 0] = object, 0);
                return removed;
            };

            heap.remove = function(removed) {
                var i = removed._, object;
                if (array[i] !== removed) return; // invalid request
                if (i !== --size) object = array[size], (compareArea(object, removed) < 0 ? up : down)(array[object._ = i] = object, i);
                return i;
            };

            function up(object, i) {
                while (i > 0) {
                    var j = ((i + 1) >> 1) - 1,
                        parent = array[j];
                    if (compareArea(object, parent) >= 0) break;
                    array[parent._ = i] = parent;
                    array[object._ = i = j] = object;
                }
            }

            function down(object, i) {
                while (true) {
                    var r = (i + 1) << 1,
                        l = r - 1,
                        j = i,
                        child = array[j];
                    if (l < size && compareArea(array[l], child) < 0) child = array[j = l];
                    if (r < size && compareArea(array[r], child) < 0) child = array[j = r];
                    if (j === i) break;
                    array[child._ = i] = child;
                    array[object._ = i = j] = object;
                }
            }

            return heap;
        }

        function transformAbsolute(transform) {
            if (!transform) return noop;
            var x0,
                y0,
                kx = transform.scale[0],
                ky = transform.scale[1],
                dx = transform.translate[0],
                dy = transform.translate[1];
            return function(point, i) {
                if (!i) x0 = y0 = 0;
                point[0] = (x0 += point[0]) * kx + dx;
                point[1] = (y0 += point[1]) * ky + dy;
            };
        }

        function transformRelative(transform) {
            if (!transform) return noop;
            var x0,
                y0,
                kx = transform.scale[0],
                ky = transform.scale[1],
                dx = transform.translate[0],
                dy = transform.translate[1];
            return function(point, i) {
                if (!i) x0 = y0 = 0;
                var x1 = (point[0] - dx) / kx | 0,
                    y1 = (point[1] - dy) / ky | 0;
                point[0] = x1 - x0;
                point[1] = y1 - y0;
                x0 = x1;
                y0 = y1;
            };
        }

        function noop() {}

        if (typeof define === "function" && define.amd) define(topojson);
        else if (typeof module === "object" && module.exports) module.exports = topojson;
        else this.topojson = topojson;
    }();

},{}],41:[function(require,module,exports){
    module.exports = extend

    function extend() {
        var target = {}

        for (var i = 0; i < arguments.length; i++) {
            var source = arguments[i]

            for (var key in source) {
                if (source.hasOwnProperty(key)) {
                    target[key] = source[key]
                }
            }
        }

        return target
    }

},{}],42:[function(require,module,exports){
    module.exports = function(hostname) {
        // Settings for geojson.io
        L.mapbox.accessToken = 'pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXFhYTA2bTMyeW44ZG0ybXBkMHkifQ.gUGbDOPUN1v1fTs5SeOR4A';
        if (hostname === 'geojson.io') {
            L.mapbox.config.FORCE_HTTPS = true;
            return {
                client_id: '62c753fd0faf18392d85',
                gatekeeper_url: 'https://geojsonioauth.herokuapp.com'
            };
            // Customize these settings for your own development/deployment
            // version of geojson.io.
        } else {
            L.mapbox.config.HTTP_URL = 'http://a.tiles.mapbox.com/v4';
            L.mapbox.config.HTTPS_URL = 'https://a.tiles.mapbox.com/v4';
            L.mapbox.config.FORCE_HTTPS = true;
            L.mapbox.config.REQUIRE_ACCESS_TOKEN = true;
            return {
                GithubAPI: null,
                client_id: 'bb7bbe70bd1f707125bc',
                gatekeeper_url: 'https://localhostauth.herokuapp.com'
            };
        }
    };

},{}],43:[function(require,module,exports){
    var clone = require('clone'),
        xtend = require('xtend'),
        config = require('../config.js')(location.hostname);

    function _getData() {
        return {
            map: {
                type: 'FeatureCollection',
                features: []
            },
            dirty: false,
            source: null,
            meta: null,
            type: 'local'
        };
    }

    module.exports = function(context) {

        var _data = _getData();

        var data = {};

        data.hasFeatures = function() {
            return !!(_data.map && _data.map.features && _data.map.features.length);
        };

        data.set = function(obj, src, createdLayer) {
            for (var k in obj) {
                _data[k] = (typeof obj[k] === 'object') ? clone(obj[k], false) : obj[k];
            }
            if (obj.dirty !== false) data.dirty = true;

            if(!createdLayer){
                context.dispatch.change({
                    obj: obj,
                    source: src
                });
            }

            return data;
        };

        data.setJSONData = function(obj, src) {

            data.set(obj, src);

            data.dirty = false;

            var map = context.map, json = data.get('map');
            if(context && context.imageOverlay){
                map.removeLayer(context.imageOverlay);
                context.imageOverlay = null;
            }

            //这里需要根据json的样式来setCRS的样式
            if(context.mapType == context.CONST.IMAGE_MAP){

                var bounds = [[0,0], [json.imageHeight, json.imageWidth]];

                context.imageOverlay = L.imageOverlay(json.imageUrl, bounds).addTo(map);

                L.setOptions(map, {crs: L.CRS.Simple});

            }else{
                L.setOptions(map, {crs: L.CRS.EPSG3857});
            }

            var bounds = context.mapLayer.getBounds();

            if (bounds.isValid()){
                context.map.fitBounds(bounds);
            }else if(context.mapType == context.CONST.IMAGE_MAP){
                context.map.fitBounds([[0,0], [json.imageHeight, json.imageWidth]]);
            }

            return data;
        };

        data.clear = function() {
            data.set(_getData());
        };

        data.mergeFeatures = function(features, src) {
            function coerceNum(feature) {
                var props = feature.properties,
                    keys = Object.keys(props),
                    length = keys.length;

                for (var i = 0; i < length; i++) {
                    var key = keys[i];
                    var value = props[key];
                    feature.properties[key] = losslessNumber(value);
                }

                return feature;
            }

            function losslessNumber(x) {
                var fl = parseFloat(x);
                if (fl.toString() === x) return fl;
                else return x;
            }

            _data.map.features = (_data.map.features || []).concat(features.map(coerceNum));
            return data.set({ map: _data.map }, src);
        };

        data.get = function(k) {
            return _data[k];
        };

        data.all = function() {
            return clone(_data, false);
        };

        return data;
    };

},{"../config.js":42,"clone":4,"xtend":41}],44:[function(require,module,exports){
    window.GEOJSON_UI = require('./ui')();

//d3.select('.geojsonio').call(GEOJSON_UI.initGeographicMapUI);


//var ui = GEOJSON_UI.initImageMapUI(d3.select('.geojsonio'));

//ui.initJSONState();

// ui.initJSONState();





},{"./ui":47}],45:[function(require,module,exports){
    var qs = require('qs-hash');
    require('leaflet-hash');

    L.Hash.prototype.parseHash = function(hash) {
        var query = qs.stringQs(hash.substring(1));
        var map = query.map || '';
        var args = map.split('/');
        if (args.length == 3) {
            var zoom = parseInt(args[0], 10),
                lat = parseFloat(args[1]),
                lon = parseFloat(args[2]);
            if (isNaN(zoom) || isNaN(lat) || isNaN(lon)) {
                return false;
            } else {
                return {
                    center: new L.LatLng(lat, lon),
                    zoom: zoom
                };
            }
        } else {
            return false;
        }
    };

    L.Hash.prototype.formatHash = function(map) {
        var query = qs.stringQs(location.hash.substring(1)),
            center = map.getCenter(),
            zoom = map.getZoom(),
            precision = Math.max(0, Math.ceil(Math.log(zoom) / Math.LN2));

        query.map = [zoom,
            center.lat.toFixed(precision),
            center.lng.toFixed(precision)
        ].join('/');

        return '#' + qs.qsString(query);
    };

},{"leaflet-hash":9,"qs-hash":10}],46:[function(require,module,exports){
    module.exports = function(context) {
        return function(e) {
            var sel = d3.select(e.popup._contentNode);

            sel.selectAll('.cancel')
                .on('click', clickClose);

            sel.selectAll('.save')
                .on('click', saveFeature);

            sel.selectAll('.add')
                .on('click', addRow);

            sel.selectAll('.delete-invert')
                .on('click', removeFeature);

            function clickClose() {
                context.map.closePopup(e.popup);
            }

            function removeFeature() {
                if (e.popup._source && context.mapLayer.hasLayer(e.popup._source)) {

                    if(window.MAP_EDITOR_CONTROL){
                        MAP_EDITOR_CONTROL.removeListTableItem(e.popup._source.feature);
                    }

                    context.mapLayer.removeLayer(e.popup._source);
                    context.data.set({map: context.mapLayer.toGeoJSON()}, 'popup');
                }
            }

            function losslessNumber(x) {
                var fl = parseFloat(x);
                if (fl.toString() === x) return fl;
                else return x;
            }

            function saveFeature() {
                var obj = {};
                var table = sel.select('table.marker-properties');
                table.selectAll('tr').each(collectRow);
                function collectRow() {
                    var name = FR.i18nText('BI-FS-Module_Map_Editor_Name'), center = FR.i18nText('BI-FS-Module_Map_Editor_Center');
                    var inter = {};
                    inter[name] = 'name';
                    inter[center] = 'center';

                    if (d3.select(this).selectAll('input')[0][0].value) {
                        var key = d3.select(this).selectAll('input')[0][0].value;
                        key = inter[key] ? inter[key] : key;
                        var value = losslessNumber(d3.select(this).selectAll('input')[0][1].value);

                        if(key == 'center'){
                            if(value && value.charAt(0) == '['){
                                value = value.substring(1, value.length - 1);
                            }

                            var centerPoint = value.split(',').map(function(positon){
                                return parseFloat(positon);
                            });

                            if(centerPoint && centerPoint.length == 2 && !isNaN(+centerPoint[0]) && !isNaN(+centerPoint[1])){
                                obj[key] = centerPoint;
                            }

                        }else{
                            obj[key] = value;
                        }
                    }
                }

                if(window.MAP_EDITOR_CONTROL){
                    MAP_EDITOR_CONTROL.updateListTableItem(e.popup._source.feature, obj.name);
                }

                e.popup._source.feature.properties = obj;

                context.data.set({map: context.mapLayer.toGeoJSON()}, 'popup');
                context.map.closePopup(e.popup);
            }

            function addRow() {
                var tr = sel.select('table.marker-properties tbody')
                    .append('tr');

                tr.append('th')
                    .append('input')
                    .attr('type', 'text');

                tr.append('td')
                    .append('input')
                    .attr('type', 'text');
            }
        };
    };

},{}],47:[function(require,module,exports){
    module.exports = ui;
    var map = require('./ui/map'), data = require('./core/data');

    var CONST = {
        AREA_MAP:"area_map",
        POINT_MAP:"point_map",
        IMAGE_MAP:"image_map"
    };

    var TAB_SELECTED = '#1d8cd5';
    var TAB_UNSELECTED = "#e4e8ed";

    function getLightBoxDom(){
        var w, d;
        try {
            d = window.top.document;
            w = window.top;
        } catch (e) {
            w = window;
            d = w.document;
        }

        var body = d.body;

        var currentMaxZ = 3330;
        var boxDiv = d.createElement('div');
        boxDiv.style.cssText =
            'position: fixed;' +
            'top:0;' +
            'left:0;' +
            'width:100%;' +
            'height:100%;' +
            'z-index:' + (currentMaxZ + 1) + ';';

        boxDiv.style.background = 'rgba(0,0,0,0.3)';

        body.appendChild(boxDiv);

        var container = d.createElement('div');
        var leftPx = (document.body.clientWidth - 600)/2;
        container.style.cssText =
            'position: fixed;' +
            'top:5%;' +
            'left:' + leftPx + 'px;' +
            'width:600px;' +
            'height:500px;' +
            'z-index:' + (currentMaxZ + 2) + ';' +
            'background:#ffffff;' +
            'box-shadow:0px 4px 50px rgba(0,0,0,0.5);';

        // for ie quirks mode
        try {
            container.style.setExpression("top", "( ignoreMe = document.body.scrollTop + this.previousSibling.clientHeight * 0.05) + 'px' ");
            boxDiv.style.setExpression("top", "( ignoreMe = document.body.scrollTop ) + 'px' ");
            container.style.position = 'absolute';
            boxDiv.style.position = 'absolute';
        } catch (e) {

        }
        body.appendChild(container);

        container.boxDiv = boxDiv;

        return container;
    }

    function ui(){

        function getContext(mapType) {
            var context = {
                CONST:CONST,
                mapType:mapType
            };
            context.dispatch = d3.dispatch('change', 'route');
            context.map = map(context);
            context.data = data(context);
            return context;
        }

        function initGeographicMapUI(selection) {

            selection.append("div").attr("class", "saveButton").text(FR.i18nText('BI-FS-Module_Map_Editor_Save')).on("click", function(){
                MAP_EDITOR_CONTROL.saveGeographicMap();
            });

            selection.append("div").attr("class", "resetButton").text(FR.i18nText("BI-FS-Module_Map_Editor_Reset")).on("click", function(){
                MAP_EDITOR_CONTROL.cancelGeographicMap();
            });

            var tabs = selection.append('div'), areaTab = tabs.append('div'), pointTab = tabs.append('div');

            registerTabActions(areaTab);

            registerTabActions(pointTab);

            var areaContext = createAreaMap(areaTab, selection);

            var pointContext = createPointMap(pointTab, selection);

            function registerTabActions(tab){
                tab.on("click", function(){
                    var tabCls = tab.attr("tabName");
                    var container = tabCls == "point_map_tab" ? pointContext.container : areaContext.container;
                    var other = tabCls == "point_map_tab" ? areaContext.container : pointContext.container;
                    var otherTab = tabCls == "point_map_tab" ? areaTab : pointTab;

                    if(container.style("display") != 'none'){
                        return;
                    }

                    container.style("display", "block");
                    other.style("display", "none");

                    tab.style("background", TAB_SELECTED).style('color', 'white');
                    otherTab.style("background", TAB_UNSELECTED).style('color', 'black');
                });
            }

            function createAreaMap(tab){

                tab.text(FR.i18nText('BI-FS-Module_Map_Editor_Area')).attr("class", "areaTab").attr("tabName", "area_map_tab").style('color', 'white');

                var context = getContext(CONST.AREA_MAP);

                var map = selection.append('div').attr("class", "mapContainer").call(context.map);

                context.container = map;

                return context;
            }

            function createPointMap(tab){

                tab.text(FR.i18nText("BI-FS-Module_Map_Editor_Point")).attr("class", "pointTab").attr("tabName", "point_map_tab").style('color', 'black');

                var context = getContext(CONST.POINT_MAP);

                selection = selection.append("div").attr("class", "mapContainer");

                selection.append('div').attr("class", "pointMap").call(context.map);

                var tableHeadContainer = selection.append('div').attr('class', 'pointTableHead');
                tableHeadContainer.append('table').append("thead").append("tr").selectAll('th').data(['BI-FS-Module_Map_Editor_Point_Name', 'BI-FS-Module_Map_Editor_Longitude', 'BI-FS-Module_Map_Editor_Latitude']).enter()
                    .append("th").attr("class", "tableHead").text(function(d){return FR.i18nText(d)});

                var tableContainer = selection.append('div').attr("class", "pointTable");
                var table = tableContainer.append("table");

                function setRowColor(tr, color){
                    var items = listTable.items, count = items.length;
                    var item;
                    for(var i = 0; i < count; i++){
                        if(items[i].elm == tr){

                            item = items[i];

                            d3.select(tr).selectAll('td').each(function(d, i){
                                this.style.borderLeftColor = color;
                                this.style.borderBottomColor = color;
                                if(i == 2){
                                    this.style.borderRightColor = color;
                                }
                            });

                            if(items[i - 1]){
                                d3.select(items[i - 1].elm).selectAll('td').each(function(d, i){
                                    this.style.borderBottomColor = color;
                                });
                            }
                        }
                    }

                    return item;
                }

                function excelPreView(jsonData){

                    var dom = getLightBoxDom();

                    d3.select(dom).append("div").attr("class", "previewTitle").text(FR.i18nText('BI-FS-Module_Map_Editor_Preview_Data'));

                    var previewTableContainer = d3.select(dom).append("div").attr("class", "previewTable");

                    var previewTable = previewTableContainer.append("table").style("border-collapse", "collapse");

                    previewTable.append("thead").append("tr").selectAll('th').data(['BI-FS-Module_Map_Editor_Point_Name', 'BI-FS-Module_Map_Editor_Longitude', 'BI-FS-Module_Map_Editor_Latitude']).enter()
                        .append("th").attr("class", "previewTableHead").style("border", "1px solid #d4dadd").text(function(d){return FR.i18nText(d)});

                    previewTable.append('tbody').style("border", "1px solid #d4dadd").attr("class", "list");

                    var rowOptions = {
                        valueNames: [ 'name', 'lng', 'lat' ],
                        item:'<tr><td class="name" style="border:1px solid #d4dadd; padding:0px; height:32px; width:187px; position:relative;"></td>' +
                        '<td class="lng" style="border:1px solid #d4dadd; padding:0px;height:32px; width:187px; position: relative;"></td>' +
                        '<td class="lat" style="border:1px solid #d4dadd; padding:0px;height:32px; width:187px; position: relative;"></td></tr>'
                    };

                    var previewListTable = new List(previewTable.node(), rowOptions);
                    previewListTable.add(jsonData);

                    d3.select(dom).append('div').text(FR.i18nText("FR-Chart-Dialog_OK")).attr("class", "previewConfirmButton").on('click', function(){
                        listTable.add(jsonData);
                        updateWithListTable();

                        if(dom.parentNode){
                            if(dom.boxDiv){
                                dom.parentNode.removeChild(dom.boxDiv);
                            }
                            dom.parentNode.removeChild(dom);
                        }

                    });

                    d3.select(dom).append('div').text(FR.i18nText('BI-FS-Module_Map_Editor_Cancel')).attr('class', "previewCancelButton").on('click', function(){

                        if(dom.parentNode){
                            if(dom.boxDiv){
                                dom.parentNode.removeChild(dom.boxDiv);
                            }
                            dom.parentNode.removeChild(dom);
                        }

                    });
                }

                var _keydown, _selected = [];

                function unselectedAll(){
                    _selected.forEach(function(row){
                        if(row && row.elm){
                            setRowColor(row.elm, '#c5c5c5');
                        }
                    });
                    _selected = [];
                }

                d3.select(document).on('keydown', function(){

                    var keycode = d3.event.keyCode || d3.event.which;

                    _keydown = (keycode == 17 || keycode == 91);
                }).on('keyup', function(){
                    var keycode = d3.event.keyCode || d3.event.which;

                    _keydown = !(keycode == 17 || keycode == 91);
                });

                table.append('tbody').attr("class", "list").attr("contenteditable", "true")
                    .on("click", function(){

                        var tr = d3.event.srcElement.parentNode.parentNode;

                        if(!_keydown){
                            unselectedAll();
                        }

                        _selected.push(setRowColor(tr, '#009ce2'));

                        updateWithListTable();
                    });

                var rowOptions = {
                    valueNames: [ 'name', 'lng', 'lat' ],
                    item:'<tr><td style="border-left:1px solid #c5c5c5; border-bottom:1px solid #c5c5c5;padding:0px;height:32px; width:117px; position:relative;"><input class="name" style="text-align:center;border:none;padding:0px;width:100%;height:100%"></input></td>' +
                    '<td style="border-left:1px solid #c5c5c5; border-bottom:1px solid #c5c5c5; padding:0px;height:32px; width:117px; position: relative;"><input class="lng" style="text-align:center;border:none;padding:0px;width:100%;height:100%"></input></td>' +
                    '<td style="border-left:1px solid #c5c5c5; border-bottom:1px solid #c5c5c5;border-right:1px solid #c5c5c5; padding:0px;height:32px; width:117px; position: relative;"><input class="lat" style="text-align:center;border:none;padding:0px;width:100%;height:100%"></input></td></tr>'
                };

                var listTable = new List(table.node(), rowOptions);

                var importButton = selection.append("div").text(FR.i18nText('BI-FS-Module_Map_Editor_Import_Data')).attr("class", "importButton")
                    .on("click", function(){
                        var form = d3.select('body').append("form");
                        var input = form.append('input')
                            .attr('type', 'file').attr("name", "background_image")
                            .style('visibility', 'hidden').style('position', 'absolute').style('height', '0')
                            .on('change', function() {
                                if(this.files && this.files.length){
                                    var fileName = this.files[0].name.toLowerCase();
                                    if(fileName.indexOf('xls') == -1 && fileName.indexOf('xlsx') == -1){
                                        FR.Msg.alert(FR.i18nText("FS-Generic-Simple_Alert"), FR.i18nText("BI-FS-Module_Map_Editor_Not_Excel"));

                                        form.remove();

                                        return;
                                    }

                                    MAP_EDITOR_CONTROL.importExcelData(form.node(), excelPreView, function(){
                                        form.remove();
                                    });
                                }

                            });
                        input.node().click();
                    });

                selection.append("div").text(FR.i18nText('BI-FS-Module_Map_Editor_Add_Line')).attr("class", "addButton")
                    .on("click", function(){
                        var added = listTable.add({'name':'', 'lng':'', 'lat':''})[0];

                        unselectedAll();

                        _selected.push(setRowColor(added.elm, '#009ce2'));

                        added.elm.getElementsByTagName("td")[0].getElementsByTagName("input")[0].focus();

                        updateWithListTable();
                    });

                selection.append("div").text(FR.i18nText('BI-FS-Module_Map_Editor_Delete_Line')).attr("class", "deleteButton")
                    .on("click", function(){

                        _selected.forEach(function(row){
                            if(row && row.elm){
                                setRowColor(row.elm, '#c5c5c5');
                                listTable.removeItem(row.values());
                            }
                        });

                        _selected = [];

                        updateWithListTable();

                    });

                context.listTable = listTable;
                context.container = selection;

                selection.style("display", "none");

                return context;
            }

            function setJSONData(jsonArray){

                var areaJSONData = jsonArray[0] || jsonArray[2];
                areaJSONData ? areaContext.data.setJSONData({map: areaJSONData}, 'map') : areaContext.data.clear();

                jsonArray[1] ? pointContext.data.setJSONData({map:jsonArray[1]}, 'map') : pointContext.data.clear();

                pointContext.listTable.clear();
                var features = pointContext.data.get("map").features;
                var items = [];
                for(var i = 0, len = features.length; i < len; i++){
                    var feature = features[i];
                    if(feature.geometry.type == 'Point'){
                        items.push({
                            name:feature.properties.name || '',
                            lng:feature.geometry.coordinates[0],
                            lat:feature.geometry.coordinates[1]
                        });
                    }
                }
                pointContext.listTable.add(items);
            }

            function initJSONState(){
                areaContext.data.clear();
                pointContext.data.clear();
                pointContext.listTable.clear();
            }

            function getMapJSONData(){
                return [areaContext.data.get("map"), pointContext.data.get("map")];
            }

            function updateWithListTable(){
                var items = pointContext.listTable.items;
                var features = [];
                for(var i = items.length - 1; i >= 0; i--){
                    var item = items[i];

                    var values = item.init(['name', 'lng', 'lat'], item.elm);

                    if(values.lng && values.lat){
                        features.push({
                            "geometry": {
                                "coordinates": [values.lng, values.lat],
                                "type": "Point"
                            },
                            "type": "Feature",
                            "properties": {
                                "name": values.name || ''
                            }
                        });
                    }
                }
                pointContext.data.set({"map":{
                    type: 'FeatureCollection',
                    features: features
                }}, 'map');
            }

            function clear(){
                initJSONState();
            }

            function isDirty(){
                return areaContext.data.dirty || pointContext.data.dirty;
            }

            function cleanContext(){
                areaContext.data.dirty = pointContext.data.dirty = false;
            }

            return {
                setJSONData:setJSONData,
                initJSONState:initJSONState,
                getMapJSONData:getMapJSONData,
                clear:clear,
                isDirty:isDirty,
                cleanContext:cleanContext,
                listTable:pointContext.listTable,
                updateWithListTable:updateWithListTable
            };
        }

        function initImageMapUI(selection){

            selection.append("div").attr("class", "saveButton").text(FR.i18nText('BI-FS-Module_Map_Editor_Save')).on("click", function(){
                MAP_EDITOR_CONTROL.saveImageMap();
            });

            selection.append("div").attr("class", "resetButton").text(FR.i18nText("BI-FS-Module_Map_Editor_Reset")).on("click", function(){
                MAP_EDITOR_CONTROL.cancelImageMap();
            });

            var context = getContext(CONST.IMAGE_MAP);

            var map = selection.append('div').attr("class", "mapContainer").call(context.map);

            function setJSONData(jsonArray){

                jsonArray[2] ? context.data.setJSONData({map:jsonArray[2]}, 'map') : context.data.clear();

            }

            function initJSONState(){

                var background = selection.append("div").style("position","absolute").style("width","100%").style("height","100%").style("top", "30px").style("background", "#ffffff");

                var inputButton = background.append("div").attr("class", "importImage").text(FR.i18nText("BI-FS-Module_Map_Editor_Import_Image"));

                background.append('div').attr("class", "importMsg").text(FR.i18nText("BI-FS-Module_Map_Editor_Supported_Format"));

                inputButton.on("click", function(){
                    var form = d3.select('body').append("form");
                    var input = form.append('input')
                        .attr('type', 'file').attr("name", "background_image")
                        .style('visibility', 'hidden').style('position', 'absolute').style('height', '0')
                        .on('change', function() {
                            MAP_EDITOR_CONTROL.saveImageBackground(form.node(), function(){
                                background.remove();
                                form.remove();
                            });
                        });
                    input.node().click();
                });
            }

            function getMapJSONData(){
                return [context.data.get("map")];
            }

            function clear(){
                context.data.clear();
                if(context && context.imageOverlay){
                    context.map.removeLayer(context.imageOverlay);
                    context.imageOverlay = null;
                }
            }

            function isDirty(){
                return context.data.dirty;
            }

            function cleanContext(){
                context.data.dirty = false;
            }

            return {
                setJSONData:setJSONData,
                initJSONState:initJSONState,
                clear:clear,
                isDirty:isDirty,
                cleanContext:cleanContext,
                getMapJSONData:getMapJSONData
            };
        }

        return {
            initGeographicMapUI: initGeographicMapUI,
            initImageMapUI: initImageMapUI
        };
    }
},{"./core/data":43,"./ui/map":48}],48:[function(require,module,exports){
    require('qs-hash');
    require('../lib/custom_hash.js');

    var popup = require('../lib/popup'),
        escape = require('escape-html'),
        geojsonRewind = require('geojson-rewind');

    module.exports = function(context) {

        function map(selection) {
            if(context.mapType == context.CONST.IMAGE_MAP){
                context.map = L.map(selection.node(), {crs: L.CRS.Simple, minZoom:-10}).setView([20, 0], 2);
            }else{
                context.map = L.map(selection.node()).setView([20, 0], 2);
                context.map.addLayer(L.mapbox.tileLayer('mapbox.streets'));
            }

            context.map.zoomControl.setPosition('topright');

            context.mapLayer = L.featureGroup().addTo(context.map);

            var draw = {
                circle:false,
                polyline:false,
                polygon: {metric: (navigator.language !== 'en-us' && navigator.language !== 'en-US')},
                marker: {
                    icon: L.mapbox.marker.icon({})
                }
            };

            if(context.mapType == context.CONST.AREA_MAP){
                draw.marker = false;
            }else if(context.mapType == context.CONST.POINT_MAP){
                draw.polygon = false;
                draw.rectangle = false;
            }

            context.drawControl = new L.Control.Draw({
                position: 'topright',
                edit: { featureGroup: context.mapLayer },
                draw: draw
            }).addTo(context.map);

            context.map
                .on('draw:edited', update)
                .on('draw:deleted', update);

            context.map
                .on('draw:created', created)
                .on('popupopen', popup(context));

            function update(createdLayer) {
                var geojson = context.mapLayer.toGeoJSON();
                geojson = geojsonRewind(geojson);
                geojsonToLayer(geojson, context.mapLayer, createdLayer);

                context.data.set({map: layerToGeoJSON(context.mapLayer)}, 'map', createdLayer);
            }

            context.dispatch.on('change.map', function() {

                var json = context.data.get('map');

                geojsonToLayer(json, context.mapLayer);
            });

            function created(e) {
                context.mapLayer.addLayer(e.layer);

                e.layer.feature = e.layer.toGeoJSON();

                update(e.layer);
            }
        }

        function layerToGeoJSON(layer) {
            var features = [];
            layer.eachLayer(collect);
            function collect(l) { if ('toGeoJSON' in l) features.push(l.toGeoJSON()); }
            return {
                type: 'FeatureCollection',
                features: features
            };
        }

        return map;
    };

    function geojsonToLayer(geojson, layer, createdLayer) {

        if(createdLayer){
            bindPopup(createdLayer);

            var latlng = createdLayer.getBounds ? createdLayer.getBounds().getCenter() : (createdLayer.getLatLng ? createdLayer.getLatLng() : '');
            if(latlng){
                createdLayer.openPopup(latlng);
                var container = createdLayer._map._popup && createdLayer._map._popup._container;
                if(container){
                    d3.select('.area_name_input').node().focus();
                }
            }

        }else{
            layer.clearLayers();

            L.geoJson(geojson, {
                style: L.mapbox.simplestyle.style,
                pointToLayer: function(feature, latlon) {
                    if (!feature.properties) feature.properties = {};
                    return L.mapbox.marker.style(feature, latlon);
                }
            }).eachLayer(add);

            function add(l) {
                bindPopup(l);
                l.addTo(layer);
            }
        }


    }

    function bindPopup(l) {

        var geoJSON = l.toGeoJSON();
        var props = JSON.parse(JSON.stringify(geoJSON.properties)) || {},
            table = '';

        props.center = props.center || '';
        props.name = props.name || '';

        table += '<tr><th><input style="border:none;text-align:center;" type="text" value="' + FR.i18nText('BI-FS-Module_Map_Editor_Name') + '"' + (' readonly') + ' /></th>' +
            '<td><input class="area_name_input" style="border:none;text-align:center" type="text" value="' + props.name + '"' + ' /></td></tr>';

        if(geoJSON.geometry && geoJSON.geometry.type != "Point"){
            table += '<tr><th><input style="border:none;text-align:center;" type="text" value="' + FR.i18nText('BI-FS-Module_Map_Editor_Center') + '"' + (' readonly') + ' /></th>' +
                '<td><input style="border:none;text-align:center" type="text" value="' + props.center + '"' + ' /></td></tr>';
        }

        for (var key in props) {
            if(key != 'name' && key != 'center'){
                table += '<tr><th><input style="border:none;text-align:center;" type="text" value="' + key + '"' + (' readonly') + ' /></th>' +
                    '<td><input style="border:none;text-align:center;" type="text" value="' + props[key] + '"' + ' /></td></tr>';
            }
        }

        var tabs = '<div class="pad1 tabs-ui clearfix col12">' +
            '<div class="tab col12">' +
            '<input style="border:none;" class="hide" type="radio" id="properties" name="tab-group" checked="true">' +
            '<label></label>' +
            '<div class="space-bottom1 col12 content">' +
            '<table class="space-bottom0 marker-properties">' + table + '</table>' +
            '</div>' +
            '</div>' +
            '</div>';

        var content = tabs + '<div class="clearfix col12 pad1 keyline-top">' +
            '<div class="pill col6">' +
            '<button class="save col6 major">'+ FR.i18nText('BI-FS-Module_Map_Editor_Save') +'</button> ' +
            '<button class="minor col6 cancel">'+ FR.i18nText('BI-FS-Module_Map_Editor_Cancel') +'</button>' +
            '</div>' +
            '<button class="col6 text-right pad0 delete-invert">' + FR.i18nText('BI-FS-Module_Map_Editor_Delete') +'</button></div>';

        l.bindPopup(L.popup({
            closeButton: false,
            maxWidth: 500,
            maxHeight: 400,
            autoPanPadding: [5, 45],
            className: 'geojsonio-feature'
        }, l).setContent(content));
    }

},{"../lib/custom_hash.js":45,"../lib/popup":46,"escape-html":5,"geojson-rewind":6,"qs-hash":10}],"topojson":[function(require,module,exports){
    var topojson = module.exports = require("./topojson");
    topojson.topology = require("./lib/topojson/topology");
    topojson.simplify = require("./lib/topojson/simplify");
    topojson.clockwise = require("./lib/topojson/clockwise");
    topojson.filter = require("./lib/topojson/filter");
    topojson.prune = require("./lib/topojson/prune");
    topojson.bind = require("./lib/topojson/bind");
    topojson.stitch = require("./lib/topojson/stitch");
    topojson.scale = require("./lib/topojson/scale");

},{"./lib/topojson/bind":11,"./lib/topojson/clockwise":14,"./lib/topojson/filter":18,"./lib/topojson/prune":22,"./lib/topojson/scale":24,"./lib/topojson/simplify":25,"./lib/topojson/stitch":27,"./lib/topojson/topology":28,"./topojson":40}]},{},[44]);
