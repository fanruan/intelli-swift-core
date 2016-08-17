!(function () {
    var expressionCache = new BI.LRU(1000);

    var allowedKeywords =
        'Math,Date,this,true,false,null,undefined,Infinity,NaN,' +
        'isNaN,isFinite,decodeURI,decodeURIComponent,encodeURI,' +
        'encodeURIComponent,parseInt,parseFloat'
    var allowedKeywordsRE =
        new RegExp('^(' + allowedKeywords.replace(/,/g, '\\b|') + '\\b)')

    // keywords that don't make sense inside expressions
    var improperKeywords =
        'break,case,class,catch,const,continue,debugger,default,' +
        'delete,do,else,export,extends,finally,for,function,if,' +
        'import,in,instanceof,let,return,super,switch,throw,try,' +
        'var,while,with,yield,enum,await,implements,package,' +
        'protected,static,interface,private,public'
    var improperKeywordsRE =
        new RegExp('^(' + improperKeywords.replace(/,/g, '\\b|') + '\\b)')

    var wsRE = /\s/g
    var newlineRE = /\n/g
    var saveRE = /[\{,]\s*[\w\$_]+\s*:|('(?:[^'\\]|\\.)*'|"(?:[^"\\]|\\.)*"|`(?:[^`\\]|\\.)*\$\{|\}(?:[^`\\]|\\.)*`|`(?:[^`\\]|\\.)*`)|new |typeof |void /g
    var restoreRE = /"(\d+)"/g
    var pathTestRE = /^[A-Za-z_$][\w$]*(?:\.[A-Za-z_$][\w$]*|\['.*?'\]|\[".*?"\]|\[\d+\]|\[[A-Za-z_$][\w$]*\])*$/
    var identRE = /[^\w$\.](?:[A-Za-z_$][\w$]*)/g
    var literalValueRE = /^(?:true|false|null|undefined|Infinity|NaN)$/

    function noop() {
    }

    /**
     * Save / Rewrite / Restore
     *
     * When rewriting paths found in an expression, it is
     * possible for the same letter sequences to be found in
     * strings and Object literal property keys. Therefore we
     * remove and store these parts in a temporary array, and
     * restore them after the path rewrite.
     */

    var saved = []

    /**
     * Save replacer
     *
     * The save regex can match two possible cases:
     * 1. An opening object literal
     * 2. A string
     * If matched as a plain string, we need to escape its
     * newlines, since the string needs to be preserved when
     * generating the function body.
     *
     * @param {String} str
     * @param {String} isString - str if matched as a string
     * @return {String} - placeholder with index
     */

    function save(str, isString) {
        var i = saved.length
        saved[i] = isString
            ? str.replace(newlineRE, '\\n')
            : str
        return '"' + i + '"'
    }

    /**
     * Path rewrite replacer
     *
     * @param {String} raw
     * @return {String}
     */

    function rewrite(raw) {
        var c = raw.charAt(0)
        var path = raw.slice(1)
        if (allowedKeywordsRE.test(path)) {
            return raw
        } else {
            path = path.indexOf('"') > -1
                ? path.replace(restoreRE, restore)
                : path
            return c + 'scope.' + path
        }
    }

    /**
     * Restore replacer
     *
     * @param {String} str
     * @param {String} i - matched save index
     * @return {String}
     */

    function restore(str, i) {
        return saved[i]
    }

    /**
     * Rewrite an expression, prefixing all path accessors with
     * `scope.` and generate getter/setter functions.
     *
     * @param {String} exp
     * @return {Function}
     */

    function compileGetter(exp) {
        if (improperKeywordsRE.test(exp)) {
            BI.warn(
                'Avoid using reserved keywords in expression: ' + exp
            )
        }
        // reset state
        saved.length = 0
        // save strings and object literal keys
        var body = exp
            .replace(saveRE, save)
            .replace(wsRE, '')
        // rewrite all paths
        // pad 1 space here because the regex matches 1 extra char
        body = (' ' + body)
            .replace(identRE, rewrite)
            .replace(restoreRE, restore)
        return makeGetterFn(body)
    }

    /**
     * Build a getter function. Requires eval.
     *
     * We isolate the try/catch so it doesn't affect the
     * optimization of the parse function when it is not called.
     *
     * @param {String} body
     * @return {Function|undefined}
     */

    function makeGetterFn(body) {
        try {
            /* eslint-disable no-new-func */
            return new Function('scope', 'return ' + body + ';')
            /* eslint-enable no-new-func */
        } catch (e) {
            /* istanbul ignore if */
            if (e.toString().match(/unsafe-eval|CSP/)) {
                BI.warn(
                    'It seems you are using the default build of Vue.js in an environment ' +
                    'with Content Security Policy that prohibits unsafe-eval. ' +
                    'Use the CSP-compliant build instead: ' +
                    'http://vuejs.org/guide/installation.html#CSP-compliant-build'
                )
            } else {
                BI.warn(
                    'Invalid expression. ' +
                    'Generated function body: ' + body
                )
            }
            return noop
        }
    }

    /**
     * Compile a setter function for the expression.
     *
     * @param {String} exp
     * @return {Function|undefined}
     */

    function compileSetter(exp) {
        var path = BI.parsePath(exp)
        if (path) {
            return function (scope, val, cb) {
                BI.setPath(scope, path, val, cb)
            }
        } else {
            BI.warn(
                'Invalid setter expression: ' + exp
            )
        }
    }

    /**
     * Parse an expression into re-written getter/setters.
     *
     * @param {String} exp
     * @param {Boolean} needSet
     * @return {Function}
     */

    function parseExpression(exp, needSet) {
        exp = exp.trim()
        // try cache
        var hit = expressionCache.get(exp)
        if (hit) {
            if (needSet && !hit.set) {
                hit.set = compileSetter(hit.exp)
            }
            return hit
        }
        var res = {exp: exp}
        res.get = isSimplePath(exp) && exp.indexOf('[') < 0
            // optimized super simple getter
            ? makeGetterFn('scope.' + exp)
            // dynamic getter
            : compileGetter(exp)
        if (needSet) {
            res.set = compileSetter(exp)
        }
        expressionCache.put(exp, res)
        return res
    }

    /**
     * Check if an expression is a simple path.
     *
     * @param {String} exp
     * @return {Boolean}
     */

    function isSimplePath(exp) {
        return pathTestRE.test(exp) &&
                // don't treat literal values as paths
            !literalValueRE.test(exp) &&
                // Math constants e.g. Math.PI, Math.E etc.
            exp.slice(0, 5) !== 'Math.'
    }

    BI.parseExpression = parseExpression;

})();