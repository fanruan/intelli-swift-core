/**
 * 对数组对象的扩展
 * @class Array
 */
$.extend(Array.prototype, {
    /**
     * 检查指定的值是否在数组中
     * @param {Object} o 要检查的值
     * @return {Number}  o在数组中的索引（如果不在数组中则返回-1）
     */
    indexOf: function (o) {
        for (var i = 0, len = this.length; i < len; i++) {
            if (FR.equals(o, this[i])) {
                return i;
            }
        }
        return -1;
    },

    /**
     * 检查指定的值是否在数组中
     * ie67不支持数组的这个方法
     * @param {Object} o 要检查的值
     * @return {Number}  o在数组中的索引（如果不在数组中则返回-1）
     */
    lastIndexOf: function (o) {
        for (var len = this.length, i = len - 1; i >= 0; i--) {
            if (FR.equals(o, this[i])) {
                return i;
            }
        }
        return -1;
    },

    /**
     * 从数组中移除指定的值，如果值不在数组中，则不产生任何效果
     * @param {Object} o 要移除的值
     * @return {Array} 移除制定值后的数组
     */
    remove: function (o) {
        var index = this.indexOf(o);
        if (index != -1) {
            this.splice(index, 1);
        }
        return this;
    },
    /**
     * 移除数组中的所有元素
     */
    clear: function () {
        while (this.length > 0) {
            this.pop();
        }
    }
});

/**
 * 对jQuery对象方法的补充
 * @class jQuery
 */
(function () {
    /**
     * 使用指定的class替代jQuery对象的原始class
     * @param {jQuery} c1  原始的class
     * @param {jQuery} c2  新的class
     * @return {jQuery}  jQuery对象
     */
    $.fn.swapClass = function (c1, c2) {
        // removeClass(undefined): jquery1.6删除当前class  1.9则不会删除 会导致单参数的switchClass失败
        return c1 == undefined ? this.removeClass().addClass(c2) : this.removeClass(c1).addClass(c2);
    };
    /**
     * 让jQuery对象的class在两个指定的class中交换，如果当前是c1，调用后则将class变为c2，如果当前是c2，调用后则将class变为c1
     * @param {jQuery} c1  第一个class
     * @param {jQuery} c2  第二个class
     * @return {jQuery} jQuery对象
     */
    $.fn.switchClass = function (c1, c2) {
        if (this.hasClass(c1)) {
            return (c2 || c2 === 0) ? this.swapClass(c1, c2) : false;
        } else {
            return this.swapClass(c2, c1);
        }
    };
    /**
     * 判断当前元素是否为指定元素的子元素
     * @param {jQuery} b  需要比较的元素
     * @return {Boolean}   如果当前元素为指定元素的子元素，返回true，否则返回false
     */
    $.fn.isChildOf = function (b) {
        var self = this;
        var compare1 = self[0];
        var compare2 = b[0];
        var parent = compare1.parentNode;
        while (parent) {
            if (parent == compare2) {
                return true;
            }
            else if (!parent.tagName || parent.tagName.toUpperCase() == "HTML") {
                return false;
            }
            parent = parent.parentNode;
        }
        return false;
    };
    /**
     * 判断制定元素是否为当前元素或则当前元素的子元素
     * @param {jQuery} b 需要比较的元素
     * @return {Boolean}  是则返回true，否则返回false
     */
    $.fn.isChildAndSelfOf = function (b) {
        return (this.closest(b).length > 0);
    };
    $.fn.mousewheel = function (Func) {
        return this.each(function () {
            var _self = this;
            _self.D = 0;
            if (FR.Browser.isIE() || FR.Browser.r.safari || FR.Browser.r.chrome) {
                _self.onmousewheel = function () {
                    _self.D = event.wheelDelta;
                    event.returnValue = false;
                    Func && Func.call(_self);
                };
            } else {
                _self.addEventListener("DOMMouseScroll", function (e) {
                    _self.D = e.detail > 0 ? -1 : 1;
                    e.preventDefault();
                    Func && Func.call(_self);
                }, false);
            }
        });
    };
    /*
     * 给jQuery.Event对象添加的工具方法
     */
    $.extend($.Event.prototype, {
        // event.stopEvent
        stopEvent: function () {
            this.stopPropagation();
            this.preventDefault();
        }
    });

})();

/**
 * 对字符串对象的扩展
 * @class String
 */
$.extend(String, {

    /**
     * 对字符串中的'和\做编码处理
     * @static
     * @param {String} string 要做编码处理的字符串
     * @return {String} 编码后的字符串
     */
    escape: function (string) {
        return string.replace(/('|\\)/g, "\\$1");
    },

    /**
     * 让字符串通过指定字符做补齐的函数
     *
     *      var s = String.leftPad('123', 5, '0');//s的值为：'00123'
     *
     * @static
     * @param {String} val 原始值
     * @param {Number} size 总共需要的位数
     * @param {String} ch 用于补齐的字符
     * @return {String}  补齐后的字符串
     */
    leftPad: function (val, size, ch) {
        var result = String(val);
        if (!ch) {
            ch = " ";
        }
        while (result.length < size) {
            result = ch + result;
        }
        return result.toString();
    },

    /**
     * 对字符串做替换的函数
     *
     *      var cls = 'my-class', text = 'Some text';
     *      var res = String.format('<div class="{0}>{1}</div>"', cls, text);
     *      //res的值为：'<div class="my-class">Some text</div>';
     *
     * @static
     * @param {String} format 要做替换的字符串，替换字符串1，替换字符串2...
     * @return {String} 做了替换后的字符串
     */
    format: function (format) {
        var args = Array.prototype.slice.call(arguments, 1);
        return format.replace(/\{(\d+)\}/g, function (m, i) {
            return args[i];
        });
    }
});

/**
 * 对字符串对象的扩展
 * @class String
 */
$.extend(String.prototype, {

    /**
     * 判断字符串是否已指定的字符串开始
     * @param {String} startTag   指定的开始字符串
     * @return {Boolean}  如果字符串以指定字符串开始则返回true，否则返回false
     */
    startWith: function (startTag) {
        if (startTag == null || startTag == "" || this.length === 0 || startTag.length > this.length) {
            return false;
        }
        return this.substr(0, startTag.length) == startTag;
    },
    /**
     * 判断字符串是否以指定的字符串结束
     * @param {String} endTag 指定的字符串
     * @return {Boolean}  如果字符串以指定字符串结束则返回true，否则返回false
     */
    endWith: function (endTag) {
        if (endTag == null || endTag == "" || this.length === 0 || endTag.length > this.length) {
            return false;
        }
        return this.substring(this.length - endTag.length) == endTag;
    },

    /**
     * 获取url中指定名字的参数
     * @param {String} name 参数的名字
     * @return {String} 参数的值
     */
    getQuery: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = this.substr(this.indexOf("?") + 1).match(reg);
        if (r) {
            return unescape(r[2]);
        }
        return null;
    },

    /**
     * 给url加上给定的参数
     * @param {Object} paras 参数对象，是一个键值对对象
     * @return {String} 添加了给定参数的url
     */
    appendQuery: function (paras) {
        if (!paras) {
            return this;
        }
        var src = this;
        // 没有问号说明还没有参数
        if (src.indexOf("?") === -1) {
            src += "?";
        }
        // 如果以问号结尾，说明没有其他参数
        if (src.endWith("?") !== false) {
        } else {
            src += "&";
        }
        $.each(paras, function (name, value) {
            if (typeof(name) === 'string') {
                src += name + "=" + value + "&";
            }
        });
        src = src.substr(0, src.length - 1);
        return src;
    },
    /**
     * 将所有符合第一个字符串所表示的字符串替换成为第二个字符串
     * @param {String} s1 要替换的字符串的正则表达式
     * @param {String} s2 替换的结果字符串
     * @returns {String} 替换后的字符串
     */
    replaceAll: function (s1, s2) {
        return this.replace(new RegExp(s1, "gm"), s2);
    },
    /**
     * 总是让字符串以指定的字符开头
     * @param {String} start 指定的字符
     * @returns {String} 以指定字符开头的字符串
     */
    perfectStart: function (start) {
        if (this.startWith(start)) {
            return this;
        } else {
            return start + this;
        }
    },

    /**
     * 获取字符串中某字符串的所有项位置数组
     * @param {String} sub 子字符串
     * @return {Number[]} 子字符串在父字符串中出现的所有位置组成的数组
     */
    allIndexOf: function (sub) {
        if (typeof sub != 'string') {
            return [];
        }
        var str = this;
        var location = [];
        var offset = 0;
        while (str.length > 0) {
            var loc = str.indexOf(sub);
            if (loc === -1) {
                break;
            }
            location.push(offset + loc);
            str = str.substring(loc + sub.length, str.length);
            offset += loc + sub.length;
        }
        return location;
    }
});

/**
 * 对函数对象的扩展
 * @class Function
 */
$.extend(Function.prototype, {

    /**
     * 将函数绑定到全局域上
     *
     *      @example
     *      function t(a) {
     *          alert(a);
     *      }
     *      var x = t.createCallback("abc");
     *      window.x();
     * @returns {Function}
     */
    createCallback: function (/*args...*/) {
        // make args available, in function below
        var args = arguments;
        var method = this;
        return function () {
            return method.apply(window, args);
        };
    },

    /**
     * 函数的代理，更改原函数的参数和this作用域
     *
     *      @example
     *      var $div = $("#test1");
     *      var $div2 = $("#test2")
     *      $div.bind("click", function(){
     *          alert($(this) == $div2);  // 这一句将会输出true
     *      }.createDelegate($div2));
     *
     * @param {Object} obj 函数内部this作用域的范围
     * @param {Array} args 参数数组
     * @param {Array} appendArgs appendArgs是"Boolean或Number",
     * 如果appendArgs是 Boolean型的且值为true,那么args参数将跟在调用代理方法时传入的参数后面组成数组一起传入当前方法,
     * 否则只传入args,如果 appendArgs为Number型,那么args将插入到appendArgs指定的位置。
     * @returns {Function}
     */
    createDelegate: function (obj) {
        var method = this;
        var args = arguments[1];
        var appendArgs = arguments[2];
        return function () {
            var callArgs = args || arguments;
            if (appendArgs === true) {
                callArgs = Array.prototype.slice.call(arguments, 0);
                callArgs = callArgs.concat(args);
            } else if (typeof appendArgs == "number") {
                callArgs = Array.prototype.slice.call(arguments, 0);
                // copy arguments first
                var applyArgs = [appendArgs, 0].concat(args);
                // create method call params
                Array.prototype.splice.apply(callArgs, applyArgs);
                // splice them in
            }
            return method.apply(obj || window, callArgs);
        };
    },

    /**
     * 创建阻断方法,如果fcn返回false,原方法将不会被执行
     * @param {Function} fcn 阻断函数
     * @param {Object} scope 作用域
     * @returns {*}
     */
    createInterceptor: function (fcn, scope) {
        if (typeof fcn != "function") {
            return this;
        }
        var method = this;
        return function () {
            fcn.target = this;
            fcn.method = method;
            if (fcn.apply(scope || this || window, arguments) === false) {
                return;
            }
            return method.apply(this || window, arguments);
        };
    },

    /**
     * 让函数延迟执行
     * @param {Number} millis 延迟的毫秒数
     * @param {Object} obj 函数的this作用域
     * @param {Array} args 参数
     * @param {Array} appendArgs 同createDelegate函数的最后一个参数说明
     * @returns {Number}
     */
    defer: function (millis, obj, args, appendArgs) {
        var fn = this.createDelegate(obj, args, appendArgs);
        if (millis || millis === 0) {
            return setTimeout(fn, millis);
        }
        fn();
        return 0;
    },

    /**
     * 创建组合函数，将执行原函数以及fcn函数
     * @param {Function} fcn  组合添加的fcn函数
     * @param {Object} scope 函数作用域
     * @returns {*}
     */
    createSequence: function (fcn, scope) {
        if (typeof fcn != "function") {
            return this;
        }
        var method = this;
        return function () {
            var retval = method.apply(this || window, arguments);
            fcn.apply(scope || this || window, arguments);
            return retval;
        };
    },

    /**
     * 通过函数名获取函数的参数列表
     * @returns {String[]} 函数的参数列表组成的数组
     */
    getNameArguments: function () {
        var s = this.toString();
        /*
         * alex:通过正则表达式取到参数列表
         * function(a, b) -> a, b
         * function fn(a, b, c) -> a, b, c
         */
        var match = /function[^\(]*\(([^\)]*)\)/.exec(s);
        var a = [];
        if (match != null) {
            a = match[1].split(",");
        }

        return $.map(a, function (item) {
            return $.trim(item)
        });
    },

    /**
     * @private
     */
    arguments2Json: function () {
        var args = arguments;
        var nameArgs = this.getNameArguments();
        var retJo = {};
        $.each(nameArgs, function (idx, item) {
            if (args.length > idx) {
                retJo[item] = args[idx];
            }
        });

        return retJo;
    }
});


if (window.FR == null) {
    window.FR = {};
}
if (window.FR.servletURL == null) {
    window.FR.servletURL = {};
}
/**
 * 插件接口判断方法
 */
$.extend(FR, {

    Plugin : {
        /**
         * 是否是合适的插件等级
         * @param provider 接口
         * @param plugin 插件实现
         * @returns {boolean} 是否是符合版本需求的插件实现
         */
        validLevel : function(provider, plugin) {
            var bv = provider.version || 1.0;
            var pv = plugin.version || 1.0;
            return bv >= pv;
        }
    }
});
/**
 * 浏览器类型、模式以及版本判断处理
 * @class FR.Browser
 */
var browser = function () {
    var agent = navigator.userAgent.toLowerCase(),
        opera = window.opera,
        browser = {
            /**
             * @property {boolean} ie 检测当前浏览器是否为IE
             *
             *      @example
             *      if (FR.Browser.r.ie ) {
             *              console.log( '当前浏览器是IE' );
             *      }
             */
            ie: /(msie\s|trident.*rv:)([\w.]+)/.test(agent),

            /**
             * @property {boolean} opera 检测当前浏览器是否为Opera
             *
             *      @example
             *      if (FR.Browser.r.opera ) {
             *          console.log( '当前浏览器是Opera' );
             *      }
             */
            opera: ( !!opera && opera.version ),

            /**
             * @property {boolean} webkit 检测当前浏览器是否是webkit内核的浏览器
             *
             *     @example
             *     if (FR.Browser.r.webkit ) {
             *         console.log( '当前浏览器是webkit内核浏览器' );
             *     }
             */
            webkit: ( agent.indexOf(' applewebkit/') > -1 ),

            /**
             * @property {boolean} mac 检测当前浏览器是否是运行在mac平台下
             *
             *      @example
             *      if (FR.Browser.r.mac ) {
             *          console.log( '当前浏览器运行在mac平台下' );
             *      }
             */
            mac: ( agent.indexOf('macintosh') > -1 ),

            /**
             * @property {boolean} quirks 检测当前浏览器是否处于“怪异模式”下
             *
             *      @example
             *      if (FR.Browser.r.quirks) {
             *          console.log( '当前浏览器运行处于“怪异模式”' );
             *      }
             */
            quirks: ( document.compatMode == 'BackCompat' )
        };

    /**
     * @property {boolean} gecko 检测当前浏览器内核是否是火狐内核
     *
     *      @example
     *      if (FR.Browser.r.gecko) {
     *          console.log( '当前浏览器内核是火狐内核' );
     *      }
     */
    browser.gecko = ( navigator.product == 'Gecko' && !browser.webkit && !browser.opera && !browser.ie);

    var version = 0;
    browser.isInnerHtmlSuitable = true;
    // Internet Explorer 6.0+
    if (browser.ie) {

        var v1 = agent.match(/(?:msie\s([\w.]+))/);
        var v2 = agent.match(/(?:trident.*rv:([\w.]+))/);
        if (v1 && v2 && v1[1] && v2[1]) {
            version = Math.max(v1[1] * 1, v2[1] * 1);
        } else if (v1 && v1[1]) {
            version = v1[1] * 1;
        } else if (v2 && v2[1]) {
            version = v2[1] * 1;
        } else {
            version = 0;
        }

        browser.ie11Compat = document.documentMode === 11;
        /**
         * @property { boolean } ie9Compat 检测浏览器模式是否为 IE9 兼容模式，如果浏览器不是IE， 则该值为undefined
         *
         *      @example
         *      if (FR.Browser.r.ie9Compat) {
         *          console.log( '当前浏览器运行在IE9兼容模式下' );
         *      }
         */
        browser.ie9Compat = document.documentMode === 9;

        /**
         * @property { boolean } ie10Compat 检测浏览器模式是否为 IE10 兼容模式，如果浏览器不是IE， 则该值为undefined
         *
         *      @example
         *      if (FR.Browser.r.ie10Compat) {
         *          console.log( '当前浏览器运行在IE10兼容模式下' );
         *      }
         */
        browser.ie10Compat = document.documentMode === 10;

        /**
         * @property { boolean } ie8 检测浏览器是否是IE8浏览器，如果浏览器不是IE， 则该值为undefined
         *
         *      @example
         *      if (FR.Browser.r.ie8) {
         *          console.log( '当前浏览器是IE8浏览器' );
         *      }
         */
        browser.ie8 = !!document.documentMode;

        /**
         * @property { boolean } ie8Compat 检测浏览器模式是否为 IE8 兼容模式，如果浏览器不是IE， 则该值为undefined
         *
         *      @example
         *      if (FR.Browser.r.ie8Compat) {
         *          console.log( '当前浏览器运行在IE8兼容模式下' );
         *      }
         */
        browser.ie8Compat = document.documentMode === 8;

        /**
         * @property { boolean } ie7Compat 检测浏览器模式是否为 IE7 兼容模式，如果浏览器不是IE， 则该值为undefined
         *
         *      @example
         *      if (FR.Browser.r.ie7Compat) {
         *          console.log( '当前浏览器运行在IE7兼容模式下' );
         *      }
         */
        browser.ie7Compat = ( ( version === 7 && !document.documentMode )
        || document.documentMode === 7 );
        /**
         *
         * @type {boolean} 当前浏览器是否可使用用innerhtml替换html以提高性能而没有bug.(bug:77528)
         *
         *      @example
         *      if (FR.Browser.r.isInnerHtmlSuitable) {
         *          console.log( '当前浏览器建议使用.innerHtml替换.html' );
         *      }
         */
        browser.isInnerHtmlSuitable = (version === 9 && browser.ie9Compat)
        ||(version === 10 && browser.ie10Compat)
        ||(browser.ie && version === 11.0 && browser.ie11Compat)
        ||(version === 8);  //added by loy on 20160913

        /**
         * @property { boolean } ie6Compat 检测浏览器模式是否为 IE6 模式 或者怪异模式，如果浏览器不是IE， 则该值为undefined
         *
         *      @example
         *      if (FR.Browser.r.ie6Compat) {
         *           console.log( '当前浏览器运行在IE6模式或者怪异模式下' );
         *      }
         */
        browser.ie6Compat = version < 7 ;

        browser.ie8bellow = version < 8;

        browser.ie9above = version > 8;

        browser.ie9below = version < 9;

        browser.ie11above = version > 10;

        browser.ie11below = version < 11;

    }

    // Gecko.
    if (browser.gecko) {
        var geckoRelease = agent.match(/rv:([\d\.]+)/);
        if (geckoRelease) {
            geckoRelease = geckoRelease[1].split('.');
            version = geckoRelease[0] * 10000 + ( geckoRelease[1] || 0 ) * 100 + ( geckoRelease[2] || 0 ) * 1;
        }
    }

    /**
     * @property { Number } chrome 检测当前浏览器是否为Chrome, 如果是，则返回Chrome的大版本号，如果浏览器不是chrome， 则该值为undefined
     *
     *      @example
     *      if (FR.Browser.r.chrome ) {
     *          console.log( '当前浏览器是Chrome' );
     *      }
     */
    if (/chrome\/(\d+\.\d)/i.test(agent)) {
        browser.chrome = +RegExp['\x241'];
    }

    /**
     * @property { Number } safari 检测当前浏览器是否为Safari, 如果是，则返回Safari的大版本号，如果浏览器不是safari， 则该值为undefined
     *
     *      @example
     *      if (FR.Browser.r.safari) {
     *          console.log( '当前浏览器是Safari' );
     *      }
     */
    if (/(\d+\.\d)?(?:\.\d)?\s+safari\/?(\d+\.\d+)?/i.test(agent) && !/chrome/i.test(agent)) {
        browser.safari = +(RegExp['\x241'] || RegExp['\x242']);
    }


    // Opera 9.50+
    if (browser.opera) {
        version = parseFloat(opera.version());
    }

    // WebKit 522+ (Safari 3+)
    if (browser.webkit) {
        version = parseFloat(agent.match(/ applewebkit\/(\d+)/)[1]);
    }

    /**
     * @property { Number } version 检测当前浏览器版本号
     * <ul>
     *     <li>IE系列返回值为5,6,7,8,9,10等</li>
     *     <li>gecko系列会返回10900，158900等</li>
     *     <li>webkit系列会返回其build号 (如 522等)</li>
     * </ul>
     *
     *      @example
     *      console.log( '当前浏览器版本号是： ' + FR.Browser.r.version );
     */
    browser.version = version;

    return browser;
}();

$.extend(FR, {
    Browser: {

        /**
         * 浏览器细节
         */
        r: browser,

        /**
         * 判断浏览器是否是IE6
         * @returns {Boolean} 浏览器是IE6则返回true，否则返回false
         */
        isIE6: function () {
            return browser.ie6Compat;
        },
        isIE7: function () {
            return browser.ie7Compat;
        },
        isIE8: function () {
            return this.getIEVersion() === 8.0;
        },
        isIE9: function () {
            return this.getIEVersion() === 9.0;
        },
        isIE10: function () {
            return this.getIEVersion() === 10.0;
        },
        isIE11: function () {
            return browser.ie && browser.version === 11.0
        },
        isIE8Before: function () {
            return this.getIEVersion() < 9.0;
        },
        isIE6Before: function () {
            return this.getIEVersion() < 7.0;
        },
        isIE7Before: function () {
            return this.getIEVersion() < 8.0;
        },
        isIE9Later: function () {
            return browser.ie9above;
        },
        getIEVersion: function () {
            return browser.ie ? browser.version : undefined;
        },
        isIE: function () {
            return browser.ie;
        }

    }
});

(function () {
    /**
     * FR工具类
     * @class FR
     */
    $.extend(FR, {
        /**
         * 对指定的键值对对象做中日韩文编码处理
         *
         * @static
         * @param {Object} o 键值对对象
         * @return {Object} 经过了中日韩文编码处理的键值对
         */
        cjkEncodeDO: function (o) {
            if ($.isPlainObject(o)) {
                var result = {};
                $.each(o, function (k, v) {
                    if (!(typeof v == "string")) {
                        v = FR.jsonEncode(v);
                    }
                    //wei:bug 43338，如果key是中文，cjkencode后o的长度就加了1，ie9以下版本死循环，所以新建对象result。
                    k = FR.cjkEncode(k);
                    result[k] = FR.cjkEncode(v);
                });
                return result;
            }
            return o;
        },

        /**
         * 封装过的jQuery.ajax()函数，对data参数做了中日韩文编码处理
         *
         * @static
         * @param {Object} options ajax参数
         */
        ajax: function (options) {
            if (options) {
                options.data = FR.cjkEncodeDO(options.data);
            }

            $.ajax(options);
        },

        /**
         * 封装过的jQuery.get()函数，对data参数做了中日航文编码处理
         *
         * @static
         * @param url 异步请求的地址
         * @param data 异步请求的参数
         * @param callback 异步请求的回调函数
         * @param type 返回值的类型
         */
        get: function (url, data, callback, type) {
            // shift arguments if data argument was omitted
            if ($.isFunction(data)) {
                type = type || callback;
                callback = data;
                data = undefined;
            }

            if (data) {
                data = FR.cjkEncodeDO(data);
            }

            $.get(url, data, callback, type);
        },
        /**
         * 封装过的jQuery.post()函数，对data参数做了中日韩文编码处理
         *
         * @static
         * @param url 异步请求的地址
         * @param data 异步请求的参数
         * @param callback 异步请求的回调函数
         * @param type 返回值的类型
         */
        post: function (url, data, callback, type) {
            // shift arguments if data argument was omitted
            if ($.isFunction(data)) {
                type = type || callback;
                callback = data;
                data = undefined;
            }

            if (data) {
                data = FR.cjkEncodeDO(data);
            }

            $.post(url, data, callback, type);
        },

        /**
         * 封装过的jQuery.param()函数，其中的值做了中日韩文编码处理
         * @static
         * @param a  参数集合
         * @return 编码后的参数集合
         */
        param: function (a) {
            a = FR.cjkEncodeDO(a);

            return $.param(a);
        },

        /**
         * 对url做参数添加和编码处理
         * @static
         * @param url 原始地址
         * @param data  要添加的参数键值对对象
         * @return {String} 经过了编码处理和参数添加的地址
         */
        url: function (url, data) {
            if ($.isPlainObject(data)) {
                return url + "?" + FR.param(data);
            }

            return FR.cjkEncode(url);
        },

        /**
         * 生成一个当前服务器下使用的地址，可以开启和关闭缓存
         * @static
         * @param data  生成的url所使用的参数，键值对对象
         * @param nocache  true表示启用缓存，否则表示关闭缓存
         * @return {String} 结果地址
         */
        buildServletUrl: function (data, nocache) {
            if (nocache) {
                data = $.extend({_: ('' + new Date().getTime())}, data);
            }
            return FR.url(FR.servletURL, data);
        },

        /**
         * 生成一个获取内部资源的url
         * @static
         * 例如：name为/com/fr/web/jquery.js
         * 则生成一个指向jquery.js这个文件的地址
         * @param name 资源文件的名称
         * @return {String} 指向特性资源文件的
         */
        resource: function (name) {
            return FR.buildServletUrl({op: 'resource', resource: name});
        },

        /**
         * 对指定的函数执行异常检测
         * @static
         * @param fn  要执行的函数
         * @param context 函数执行上下文
         * @param args 参数
         * @return {*}
         */
        tc: function (fn, context, args) {
            try {
                return fn.apply(context, args);
            } catch (e) {
                FR.Msg.toast(e.toString());
                throw e;
            }
        },

        /**
         * 返回参数列表中第一个有效的参数（不为null，不为undefined）
         * @returns {*} 有效的参数
         */
        pick: function () {
            var arg, length = arguments.length;
            for (var i = 0; i < length; i++) {
                arg = arguments[i];
                if (typeof arg !== 'undefined' && arg !== null) {
                    return arg;
                }
            }
            return null;
        }
    });
})();

(function () {
    $.extend(FR, /**@class FR */{
        /**
         * 客户端计算公式
         * @static
         * @param {String} formula 要计算的公式
         * @param {Object} initValue 初始值
         * @param {Boolean} must  是否必须总是计算
         * @returns {Object} 计算后的值
         */
        formulaEvaluator: function (formula, initValue, must) {
            var lazyValue = initValue;
            return function (nocache) {
                //如果后台没有计算出公式结果initValue, 那再去remoteEval
                //这边之前写!lazyValue , 遇到lazyValue = 0时就会重复计算
                if (nocache || lazyValue == undefined || must) {
                    try {
                        if (FR.SessionMgr.getSessionID()) {// kunsnat: BI在sessionID为空时, 不需要此解析公式.
                            lazyValue = FR.remoteEvaluate(formula);
                        }
                    } catch (e) {
                        // 说明不需要到服务器端取数
                    }
                }

                return lazyValue;
            }
        },

        /**
         * @private
         * @static
         * @param {String} formula
         * @returns {String} 经过计算的公式的结果
         */
        remoteEvaluate: function (formula) {
            var result = null;

            //post只需要encode一次, get两次
            formula = encodeURIComponent(formula);

            FR.ajax({
                url: FR.servletURL,
                type: 'POST',
                async: false,
                data: {
                    op: 'fr_base',
                    cmd: 'evaluate_formula',
                    sessionID: FR.SessionMgr.getSessionID(),
                    expression: formula
                },
                timeout: 5000,
                complete: function (res, status) {
                    result = FR.jsonDecode(res.responseText);
                    result = result["result"];
                }
            });

            return result;
        }
    });
})();

(function ($) {
    var COL_IDS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * FR的一些常用方法
     * @class FR
     */
    $.extend(FR, {

        /**
         * 取数据时一次取的数据量
         */
        limitData: 500,

        /**
         * @property {Object} 常量
         */
        constant: {
            /**
             * 成功
             */
            success: "success",
            /**
             * 失败
             */
            failure: "failure"
        },

        /**
         * 空函数
         * @static
         */
        emptyFn: function () {
        },

        /**
         * ie8对0开头的字串默认为8进制,需要加进制参数
         * @static
         * @param str 需要解析的字串
         * @returns {Number} 整数
         */
        parseINT:function(str){
            return parseInt(str, 10);
        },


        /**
         * 判断一个对象是否可能是宽度或者高度
         * 例如：100， 'auto', '40px'都会被识别为高度或者宽度
         * @static
         * @param {Object} o 待判断的对象
         * @return {Boolean}  该对象可以表示高度或者宽度则返回true，否则返回false
         */
        isWidthOrHeight: function (o) {
            if (typeof o == 'number') {
                return o >= 0;
            } else if (typeof o == 'string') {
                return /^\d{1,3}%$/.exec(o) || o == 'auto' || /^\d+px$/.exec(o);
            }
        },

        /**
         * 将数字转换成以百分比的形式显示
         * @static
         * @param {Number} number  数字
         * @return {String}  百分比形式的值
         */
        number2Percentage: function (number) {
            return (~~(number * 100)) + "%";	// to integer
        },

        /**
         * 判断一个指定对象是否为一个数组对象
         * @deprecated
         * @static
         * @param {Object} a 指定对象
         * @return {Boolean}  该对象昂如果是数组对象则返回true，否则返回false
         */
        isArray: function (a) {
            return Object.prototype.toString.call(a) == '[object Array]' || a instanceof jQuery
        },

        /**
         * 给指定的jQuery对象添加样式
         *
         *      @example
         *      var $div = $("<div></div>").css({width:100, height:100}).appendTo("body");
         *      FR.applyStyles($div, "color:red; border:1px"); //$div的样式将会变成$nbsp;div style='color:red; border:1px'>;
         *      FR.applyStyles($div, {color:#4eff2，border:1px}）; //$div的样式将会变成<div style='color:#4eef2; border:1px'>;
         *
         * @static
         * @param {jQuery} $el jQuery对象
         * @param {String} styles 样式
         */
        applyStyles: function ($el, styles) {
            if (styles) {
                if (typeof styles == "string") {
                    var re = /\s?([a-z\-]*)\:\s?([^;]*);?/gi;
                    var matches;
                    while ((matches = re.exec(styles)) != null) {
                        $el.css(matches[1], matches[2]);
                    }
                } else if (typeof styles == "object") {
                    $el.css(styles);
                } else if (typeof styles == "function") {
                    FR.applyStyles(dom, styles.call());
                }
            }
        },

        /**
         * 根据配置属性以及evt生成菜单
         * @static
         * @param {Object} o 菜单的配置属性
         * @param evt 用于计算菜单弹出位置的事件对象
         * @param evtPosition true表示根据evt的位置进行定位，false表示根据evt的target的位置进行定位
         */
        showMenuByEvent: function (o, evt, evtPosition) {
            if (evtPosition === true) {
                FR.showMenuByLocation(o, {
                    left: evt.clientX,
                    top: evt.clientY
                }, {
                    // 让鼠标位置在menu内,防止menu显示后不消失
                    left: -5,
                    top: -5
                });
            }
            // alex:如果根据evt.target的位置进行定位
            else if (evt.target) {
                FR.showMenuByEl(o, $(evt.target));
            } else {
                // james:IE中这里得到的evt不是mouseEvent,只是一个普通的Event，结果就没有target了
                FR.showMenuByLocation(o, {
                    left: evt.clientX,
                    top: evt.clientY + 10
                })
            }
        },

        /**
         * 判断是否是空数组
         * @static
         * @param {Array} array 数组
         * @returns {Boolean} 返回是否是空数组
         */
        isEmptyArray: function (array) {
            if ($.isArray(array)) {
                if (array.length === 0) {
                    return true;
                } else if (array.length === 1) {
                    return array[0] === "";
                }
            }
            return false;
        },

        /**
         * 代理执行指定的函数
         * @static
         * @param {Object} obj 原始对象
         * @param {Function} func 执行函数
         * @param {Object} param 函数执行的参数
         * @param {Object} defaultValue 默认值
         * @returns {Object} 函数执行后的值
         */
        applyFunc: function (obj, func, param, defaultValue) {
            if ((typeof func) == "function") {
                return func.apply(obj, param ? param : []);
            }
            return defaultValue;
        },

        /**
         * URLdecode时候+和%要特别处理
         * @static
         * @param {String} s 原始字符串
         * @return {String} 编码后的字符串
         */
        encodePrecentPlus: function (s) {
            if (typeof(s) == "string") {
                s = s.replace(/%/gi, "%25");
                s = s.replace(/\+/gi, "%2B");
            }
            else if (FR.isArray(s)) {
                for (var i = 0; i < s.length; i++) {
                    s[i] = FR.encodePrecentPlus(s[i]);
                }
            }
            return s;
        },

        /**
         * @private
         * @static
         */
        showMenuByEl: function (o, $el) {
            var menu;
            if ($el.fr_menu && $el.fr_menu.menu && (FR.equals($.extend({
                    xxxMMenu: true
                }, o), $el.fr_menu.opts) || $el.fr_menu.menu.close())) {
                menu = $el.fr_menu.menu;
            } else {
                o.$el = $el;
                menu = FR.createShortMenu(o);
                delete o.$el;
            }
            var modify = 1;
            if ($el.offset().top < menu.$menuRoot.height() + modify
                || $el.offset().top + $el.height() + modify
                + menu.$menuRoot.height() < document.body.clientHeight) {
                var offsetTop = $el.offset().top + $el.height() + modify
            } else {
                var offsetTop = $el.offset().top - menu.$menuRoot.height() - modify
            }
            o.xxxMMenu = true;
            menu.show();
            menu.position($el.offset().left, offsetTop);
            $el.fr_menu = {
                //防止点击多次生成，menu remove的时候会清除掉，想不到更好的写法。
                "menu": menu,
                "opts": o
            };
        },

        /**
         * @private
         * @static
         */
        showMenuByLocation: function (o, location, modify) {
            if (!modify) {
                modify = {
                    left: 0,
                    top: 0
                }
            }
            var menu = FR.createShortMenu(o);
            FR.showWithMenu(menu, location, modify);
        },

        /**
         * @private
         * @static
         */
        showWithMenu: function (menu, location, modify) {
            if (location.left < menu.$menuRoot.width() + modify.left
                || location.left + modify.left + menu.$menuRoot.width() < document.body.clientWidth) {
                var left = location.left + modify.left;
            } else {
                var left = location.left - menu.$menuRoot.width() - modify.left;
            }
            if (location.top < menu.$menuRoot.height() + modify.top
                || location.top + modify.top + menu.$menuRoot.height() < document.body.clientHeight) {
                var top = location.top + modify.top
            } else {
                var top = location.top - menu.$menuRoot.height() - modify.top
            }
            menu.show();
            //40565 ie里面, 如果鼠标在原点0,0, 快速移动鼠标, 会导致menu不消失, 往右下偏一点
            left = FR.Browser.isIE() ? left - 5 : left;
            top = FR.Browser.isIE() ? top - 5 : top;
            menu.position(left, top);
        },

        versionRemind: function (o) {
            var jo = FR.jsonDecode(o);
            if (jo.exception == "failpass") {
                var msg = FR.i18nText("FR_FS-FailPass_ImproveYourVersion");
                if (jo.func) {
                    msg = jo.func + ", " + msg;
                }
                FR.Msg.toast(msg);
                return false;
            }
            return true;
        },

        createShortMenu: function (o) {
            o = $.extend({
                minWidth: 80,
                destroyOnClose: true
            }, o);
            return new FR.frMenu(o);
        },

        /*
         * 超链,如果只有一个链接,直接链过去,如果有多个链接,要弹出菜单
         */
        doHyperlink: function () {
            function _doHyperlink(data) {
                // carl:这里不decode了吧,否则传的都是明码了
                new Function(unescape(data))();
            }

            /*
             * evt是事件,用来定位菜单弹出的位置
             * obj [{url, targetFrame, features}] 关于超链的配置属性
             */
            // carl:多加个evtPosition确定menu弹出策略
            return function (evt, obj, evtPosition) {
                if ($.isArray(obj)) {
                    switch (obj.length) {
                        case 0:
                            return;
                        // alex:如果是数组,且长度只有1,不需要以弹出menu的方式处理超链
                        case 1:
                            FR.doHyperlink(evt, obj[0]);
                            break;
                        // alex:如果是数组,且长度只有1,不需要以弹出menu的方式处理超链
                        default:
                            var _noMenuHyperlink = function (data) {
                                //目前只有图表块和报表块是需要直接联动
                                return data && (data.indexOf("changeParameter") >= 0 ||
                                    data.indexOf("_g().name_widgets[") >= 0);
                            }
                            // frank 图表关联时候不需要弹Menu,直接变化
                            var noMenuHyperlink = [];
                            var otherHyperlink = [];
                            for (var i = 0; i < obj.length; i++) {
                                var data = obj[i].data;
                                if (_noMenuHyperlink(data)) {
                                    noMenuHyperlink[noMenuHyperlink.length] = obj[i];
                                } else {
                                    otherHyperlink[otherHyperlink.length] = obj[i];
                                }
                            }

                            for (var i = 0; i < noMenuHyperlink.length; i++) {
                                try {
                                    _doHyperlink(noMenuHyperlink[i].data);
                                } catch (e) {
                                    FR.Msg.toast(e.message)
                                    //do nth 某一个出错的, 不影响其他的
                                }
                            }

                            switch (otherHyperlink.length) {
                                case 0:
                                    return;
                                case 1:
                                    FR.doHyperlink(evt, otherHyperlink[0]);
                                    break;
                                default:
                                    FR.showMenuByEvent({
                                        items: $.map(otherHyperlink, function (cfg) {
                                            return {
                                                src: cfg.name,
                                                handler: _doHyperlink.createCallback(cfg.data)
                                            }
                                        })
                                    }, evt, evtPosition || false);
                            }
                    }
                } else {
                    _doHyperlink(obj.data);
                }
            }
        }(),

        doHyperlinkByGet: function (url, para, target, feature) {
            if (arguments.length < 1) {
                return;
            }
            var options = arguments[0];
            if (typeof options === 'object') {
                url = options.url || '';
                para = options.para || {};
                target = options.target;
                feature = options.feature;
            }
            // alex:加一个当前时间参数,保证超链不从缓存中取
            para = $.extend({
                _: new Date().getTime()
            }, para);

            var pString = "";
            var paras = {};
            $.each(para, function (pName, pValue) {
                if (FR.isArray(pValue) && !FR.isEmptyArray(pValue) && FR.isArray(pValue[0])) {
                    pValue = pValue.join(";");
                }
                // kunsnat: 先cjdDeCode处理, 都是统一在java端做过cjkEncode处理
                var tmpName = FR.cjkDecode(pName);
                var tmpValue = FR.cjkDecode(pValue);
                paras[pName] = pValue;
                if (tmpName != "__LOCALE__" && tmpName != "REPORTLET" && tmpName != "OP") {
                    // neil:第一次encode是去掉特殊字符, 变成ascii字符串(STR_ENC1), 第二次encode是因为web容器得到后会去自动解一次,
                    // 容器req.getParameter自动解的这一次，不管是按 GBK 还是 UTF-8 还是 ISO-8859-1 都好，都能够正确的得到 [STR_ENC1],
                    // 如果只encode一次, 那么容器那边只能按照utf-8解, 否则乱码妥妥的, 然后java端再去URLDecoder.decode
                    pString += "&" + encodeURIComponent(encodeURIComponent(tmpName)) + "=" + encodeURIComponent(encodeURIComponent(tmpValue));
                }
            });

            if (url.indexOf("?") == -1) {
                url += "?";
                pString = pString.substring(1);
            }

            if (target && target == "_dialog") {
                this.showHyperlinkDialog(url + pString, feature);
            } else {
                var targetUrl = url + pString;
                //Sean: IE下Get方式的url最大限制为256字节，此时改用POST
                if (targetUrl.length >= 2048 && FR.Browser.isIE()) {
                    var submitForm = $("<form method='post' target='" + target + "' action='" + url + "'></form>").hide().appendTo($('body'));
                    for (var i in paras) {
                        $("<input type=\"hidden\" name=\"" + i + "\" value=\"\"/>")
                            .attr("value", paras[i]).appendTo(submitForm);
                    }
                    submitForm.submit();
                } else {
                    window.open(targetUrl, target);
                }
            }
        },

        showHyperlinkDialog: function (url, feature) {
            //39363 ie7下iframe的滚动条只显示一半
            var isIEUnder8 = FR.Browser.isIE8Before();
            var iframeWidth = isIEUnder8 ? '99%' : '100%';
            var iframeHeight = isIEUnder8 ? '99%' : '100%';

            var iframeDialog = "<iframe src='' width=" + iframeWidth + " height= " +
                iframeHeight + " scrolling='no' frameborder='0'></iframe>";

            iframeDialog = $(iframeDialog).attr('src', url)[0];
            if (feature.split(',').length > 2) {
                var width = feature.split(',')[0];
                if (width.indexOf('=') != -1) {
                    width = width.substring(width.indexOf('=') + 1, width.length);
                }
                var height = feature.split(',')[1];
                if (height.indexOf('=') != -1) {
                    height = height.substring(height.indexOf('=') + 1, height.length);
                }
            }

            FR.showDialog(" ", parseInt(width) || 600, parseInt(height) || 400, iframeDialog);
        },

        //marro:这个方法和下面的 doHyperlinkByPost调用对象只是网络报表，所以用__parameter__=xxx来传递，这样我们处理的时候可以对数组参数
        //也进行处理
        doHyperlinkByGet4Reportlet: function (url, para, target, feature) {
            para = $.extend({
                _: new Date().getTime()
            }, para);

            $.each(para, function (pName, pValue) {
                if (pName != "__LOCALE__" && pName != "REPORTLET" && pName != "OP" && !$.isPlainObject(pValue)) {
                    if (FR.isArray(pValue)) {
                        para[pName] = pValue;
                    } else {
                        para[pName] = encodeURIComponent(encodeURIComponent(pValue));
                    }
                }
            });

            var pString = "__parameters__=" + FR.cjkEncode(FR.jsonEncode(para));

            url += url.indexOf("?") == -1 ? "?" : "&";

            if (target && target == "_dialog") {
                this.showHyperlinkDialog(url + pString, feature);
            } else {
                window.open(url + pString, target);
            }

        },

        doHyperlinkByPost: function (url, para, target, feature) {
            var isGet = target && target == "_dialog";
            $.each(para, function (pName, pValue) {
                if (pName != "__LOCALE__" && pName != "REPORTLET" && pName != "OP" && !$.isPlainObject(pValue)) {
                    if (FR.isArray(pValue)) {
                        para[pName] = pValue;
                    } else {
                        para[pName] = isGet ? encodeURIComponent(encodeURIComponent(pValue)) : encodeURIComponent(pValue);
                    }
                }
            });
            if (this.$hyperlinkForm) {
                this.$hyperlinkForm.html("");
            } else {
                this.$hyperlinkForm = ($("<div>").appendTo($(document.body))).css({
                    "display": "none",
                    "position": "absolute"
                });
            }
            if (target == null) {
                target = "formresult";
            }
            para = FR.cjkEncode(FR.jsonEncode(para));
            // 这里注意写法$("<form></form>"),否则IE里面无法提交
            var submitForm = $("<form method='post' target='" + target + "' action='" + url + "'></form>").appendTo(this.$hyperlinkForm);
            var input = $("<input type=\"hidden\" name=\"__parameters__\" value=\"\"/>");
            input.attr("value", para);// kunsnat: 都是在java端先做的cjk处理, 所以这边只做json
            submitForm.append(input);
            if (isGet) {
                var pString = "__parameters__=" + para;
                url += url.indexOf("?") == -1 ? "?" : "&";
                this.showHyperlinkDialog(url + pString, feature);
            } else {
                //window.open("about:blank", target, feature);
                submitForm.submit();
            }
        },

        isEmpty: function (value) {
            // 判断是否为空值
            var result = value === "" || value === null || value === undefined;
            return result;
        },

        /**
         * 判断是否为{},undefined,null,'',"" 
         * @param obj
         * @returns {boolean}
         */
        isEmptyObj: function (obj) {
            return _.isEmpty(obj);
        },
        /*
         * 转成正则表达式的String
         * {abc}变成str = \{abc\},这样new Regex(str)就是匹配{abc}的正则表达式
         */
        toRE: function (str) {
            return str.replace(/\\/g, "\\\\")
                .replace(/\[/g, "\\[")
                .replace(/\]/g, "\\]")
                .replace(/\(/g, "\\(")
                .replace(/\)/g, "\\)")
                .replace(/\{/g, "\\{")
                .replace(/\}/g, "\\}")
                .replace(/\*/g, "\\*")
                .replace(/\|/g, "\\|")
                .replace(/\?/g, "\\?")
                .replace(/\$/g, "\\$")
                .replace(/\^/g, "\\^")
        },

        //执行html中的带的script代码
        //非常不严谨,目前只用于特定地方，有其他需求再完善
        _executeScriptFromHtml: function (html) {
            var html_script = html.substring(0, 500);
            var html_script_low = html_script.toLowerCase();
            var begin = html_script_low.indexOf("<" + "script>");
            if (begin < 0) {
                return;
            }
            var end = html_script_low.indexOf("</" + "script>");
            html_script = html_script.substring(begin + 8, end);
            try {
                eval(html_script);
            } catch (e) {

            }
        },

        extend: function () {
            return function (sb, sp, overrides) {
                if (typeof sp == 'object') {
                    overrides = sp;
                    sp = sb;
                    sb = function () {
                        sp.apply(this, arguments);
                    };
                }
                if (sp == null) {
                    console && console.log("empty");
                }
                var F = function () {
                }, spp = sp.prototype;
                F.prototype = spp;
                sb.prototype = new F();
                sb.superclass = spp;
                $.extend(sb.prototype, overrides);
                return sb;
            };
        }(),

        /*
         * 管理该html页面中所有的SessionID -> ReportPane
         */
        SessionMgr: function () {
            var sid, cp;

            /*
             * alex:unload的时候关掉session TODO 但上次没加async:false + unbind的时候导致了ie浏览器的死掉
             */
            $(window).unload(function () {
                $(window).unbind('unload', arguments.callee);
                // carl：打印预览的时候不需要close, bi也不需要关闭
                if (sid && cp && FR.servletURL && cp.rtype != 'preview' && cp.rtype != 'bi' && !cp.noClossSession) {
                    var closeSession = function (sid) {
                        FR.ajax({
                            async: false,
                            url: FR.servletURL,
                            data: {
                                op: 'closesessionid',
                                sessionID: sid
                            }
                        });
                    };
                    closeSession(sid);
                }
            });

            if (FR.shouldSendHeartBeat) {
                // alex:每30秒通知服务器端i am alive
                setInterval(function () {
                    if (sid && cp && FR.servletURL) {
                        FR.ajax({
                            url: FR.servletURL,
                            data: {
                                sessionID: sid,
                                _: new Date().getTime()
                            }
                        });
                    }
                }, 30000);
            }

            return {
                getSessionID: function () {
                    return sid;
                },
                getContentPane: function () {
                    return cp;
                },
                //wei : 兼容
                get: function () {
                    return cp;
                },
                register: function (id, s) {
                    sid = id;
                    cp = s;
                }
            }
        }(),

        CookieInfor: {
            getCookies: function () {
                var cookieString = document.cookie
                return unescape(cookieString).split(";");
            },

            addCookie: function (name, value, temp) {
                var newCookie = name + "=" + escape(value);
                //生存的天数
                var liveDays = temp ? temp : 30;
                var expires = new Date();
                expires.setDate(expires.getDate() + liveDays);
                newCookie += ";expires=" + expires.toGMTString();
                document.cookie = newCookie;
            },

            removeCookie: function (name) {
                var expires = new Date();
                expires.setTime(expires.getTime() - 1);
                document.cookie = name + '=' + ';expires=' + expires.toGMTString();
            },

            clearCookie: function () {
                var self = this;
                var $cookies = this.getCookies();
                $.each($cookies, function (i) {
                    var cookieName = $cookies[i].split('=')[0];
                    self.removeCookie(cookieName);
                });
            },

            getCookieByName: function (name) {
                var $cookies = this.getCookies();
                var re = new RegExp(name);
                var value = null;
                $.each($cookies, function (i) {
                    if ($cookies[i].match(re)) {
                        value = $cookies[i].split(name + '=')[1];
                        return false;
                    }
                });
                return value;
            }
        },

        /**
         * 返回对中日韩问做了特殊转换的字符串
         *
         * @static
         * @param text 需要做编码的字符串
         * @return {String} 编码后的字符串
         */
        cjkEncode: function (text) {
            // alex:如果非字符串,返回其本身(cjkEncode(234) 返回 ""是不对的)
            if (typeof text !== 'string') {
                return text;
            }

            var newText = "";
            for (var i = 0; i < text.length; i++) {
                var code = text.charCodeAt(i);
                if (code >= 128 || code === 91 || code === 93) {//91 is "[", 93 is "]".
                    newText += "[" + code.toString(16) + "]";
                } else {
                    newText += text.charAt(i);
                }
            }

            return newText
        },
        /**
         * 将cjkEncode处理过的字符串转化为原始字符串
         *
         * @static
         * @param text 需要做解码的字符串
         * @return {String} 解码后的字符串
         */
        cjkDecode: function (text) {
            if (text == null) {
                return "";
            }
            //查找没有 "[", 直接返回.  kunsnat:数字的时候, 不支持indexOf方法, 也是直接返回.
            if (!isNaN(text) || text.indexOf('[') == -1) {
                return text;
            }

            var newText = "";
            for (var i = 0; i < text.length; i++) {
                var ch = text.charAt(i);
                if (ch == '[') {
                    var rightIdx = text.indexOf(']', i + 1);
                    if (rightIdx > i + 1) {
                        var subText = text.substring(i + 1, rightIdx);
                        //james：主要是考虑[CDATA[]]这样的值的出现
                        if (subText.length > 0) {
                            ch = String.fromCharCode(eval("0x" + subText));
                        }

                        i = rightIdx;
                    }
                }

                newText += ch;
            }

            return newText;
        },

        //replace the space(&nbsp;) of html with " "
        //Only "&nbsp;" need to be decoded, because "&amp;", "&lt;", "&gt;", "&apos;" and "&quot;"
        //can be paresed correctly by the org.w3c.dom to the eaxctly values '&', "<", ">", "'", """
        htmlSpaceDecode: function (text) {
            return (text == null) ? '' : String(text).replace(/&nbsp;/, ' ');
        },
        //replace the html special tags
        htmlEncode: function (text) {
            return (text == null) ? '' : String(text).replace(/&/g, '&amp;').replace(/\"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        },
        //html decode
        htmlDecode: function (text) {
            return (text == null) ? '' : String(text).replace(/&amp;/g, '&').replace(/&quot;/g, '\"').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&nbsp;/g, ' ');
        },
        //json encode
        jsonEncode: function (o) {
            //james:这个Encode是抄的EXT的
            var useHasOwn = {}.hasOwnProperty ? true : false;

            // crashes Safari in some instances
            //var validRE = /^("(\\.|[^"\\\n\r])*?"|[,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t])+?$/;

            var m = {
                "\b": '\\b',
                "\t": '\\t',
                "\n": '\\n',
                "\f": '\\f',
                "\r": '\\r',
                '"': '\\"',
                "\\": '\\\\'
            };

            var encodeString = function (s) {
                if (/["\\\x00-\x1f]/.test(s)) {
                    return '"' + s.replace(/([\x00-\x1f\\"])/g, function (a, b) {
                            var c = m[b];
                            if (c) {
                                return c;
                            }
                            c = b.charCodeAt();
                            return "\\u00" +
                                Math.floor(c / 16).toString(16) +
                                (c % 16).toString(16);
                        }) + '"';
                }
                return '"' + s + '"';
            };

            var encodeArray = function (o) {
                var a = ["["], b, i, l = o.length, v;
                for (i = 0; i < l; i += 1) {
                    v = o[i];
                    switch (typeof v) {
                        case "undefined":
                        case "function":
                        case "unknown":
                            break;
                        default:
                            if (b) {
                                a.push(',');
                            }
                            a.push(v === null ? "null" : FR.jsonEncode(v));
                            b = true;
                    }
                }
                a.push("]");
                return a.join("");
            };

            if (typeof o == "undefined" || o === null) {
                return "null";
            } else if ($.isArray(o)) {
                return encodeArray(o);
            } else if (o instanceof Date) {
                /*
                 * alex:原来只是把年月日时分秒简单地拼成一个String,无法decode
                 * 现在这么处理就可以decode了,但是JS.jsonDecode和Java.JSONObject也要跟着改一下
                 */
                return FR.jsonEncode({
                    __time__: o.getTime()
                })
            } else if (typeof o == "string") {
                return encodeString(o);
            } else if (typeof o == "number") {
                return isFinite(o) ? String(o) : "null";
            } else if (typeof o == "boolean") {
                return String(o);
            } else if ($.isFunction(o)) {
                return String(o);
            } else {
                var a = ["{"], b, i, v;
                for (i in o) {
                    if (!useHasOwn || o.hasOwnProperty(i)) {
                        v = o[i];
                        switch (typeof v) {
                            case "undefined":
                            case "unknown":
                                break;
                            default:
                                if (b) {
                                    a.push(',');
                                }
                                a.push(FR.jsonEncode(i), ":",
                                    v === null ? "null" : FR.jsonEncode(v));
                                b = true;
                        }
                    }
                }
                a.push("}");
                return a.join("");
            }
        },
        /**
         * hiram 优化了一下，但仅为了找o.__time__还是多耗了点时间
         * richie:为了找__time__，需要把整个JSON树都遍历一遍，耗时不少，应该要想办法优化
         */
        jsonDecode: function (text) {

            try {
                // 注意0啊
                //var jo = $.parseJSON(text) || {};
                var jo = $.parseJSON(text);
                if (jo == null) {
                    jo = {};
                }
            } catch (e) {
                /*
                 * richie:浏览器只支持标准的JSON字符串转换，而jQuery会默认调用浏览器的window.JSON.parse()函数进行解析
                 * 比如：var str = "{'a':'b'}",这种形式的字符串转换为JSON就会抛异常
                 */
                try {
                    jo = new Function("return " + text)() || {};
                } catch (e) {
                    //do nothing
                }
                if (jo == null) {
                    jo = [];
                }
            }
            if (!FR._hasDateInJson(text)) {
                return jo;
            }
            return (function (o) {
                if (typeof o === "string") {
                    return o;
                }
                if (o && o.__time__ != null) {
                    return new Date(o.__time__);
                }
                for (var a in o) {
                    if (o[a] == o || typeof o[a] == 'object' || $.isFunction(o[a])) {
                        break;
                    }
                    o[a] = arguments.callee(o[a]);
                }

                return o;
            })(jo);
        },

        _hasDateInJson: function (json) {
            if (!json || typeof json !== "string") {
                return false;
            }
            return json.indexOf("__time__") != -1;
        },


        /**
         * 设置背景 尤其是ie兼容的处理
         * @param $dom
         * @param bg
         */
        setBackground: function ($dom, bg, height) {
            var type = bg["type"];
            // 4自适应背景  2拉伸
            if ((type === 4 || type === 2) && FR.Browser.isIE()) {
                // IE8杂项模式会出现里面的控件点不了的问题
                // 自适应要保持长宽比 下面的是拉伸 todo
                if (FR.ieForFixBackground()) {
                    var url = bg["url"];
                    var bgImage = $("<img src='" + url + "' id='para-bg'>");
                    bgImage.appendTo($dom);
                    setSize($dom);
                    $(window).resize(function () {
                        setSize($dom);
                    })
                    function setSize($dom) {
                        var $img = $('#para-bg', $dom);
                        setTimeout(function () {
                            if ($img.isVisible()) {
                                $img.attr('width', $dom.width());
                                // ie8计算工具栏的height总是不对 直接传过来了
                                //if (height) {
                                //    $img.attr('height', height);
                                //} else {
                                //    $img.attr('height', $dom.height());
                                //}
                                //zack:上述代码会导致bug67421的第二个问题，
                                // resize之后高度会变化但是上面的代码只会记住初始高度，这显然不对啊，
                                // 不过看上去注释代码传height是有原因的先写成||吧，有问题后续再看看
                                $img.attr('height', $dom.height() || height);

                            }
                        }, 20);
                    }
                } else {
                    $.each(['background-image', 'background-repeat', 'filter', "background"], function (idx, item) {
                        if (bg[item] != null) {
                            $dom.css(item, bg[item]);
                        }
                    });
                }
            } else {
                $.each(['background', 'background-size', 'background-image',
                    'background-repeat'], function (idx, item) {
                    if (bg[item] != null) {
                        $dom.css(item, bg[item]);
                    }
                });
            }
        },


        // 不能用filter花背景图的ie版本 ie7 和 ie8杂项
        //wikky:ie8标准也不能用
        // jim:ie6不支持filter的
        ieForFixBackground: function () {
//            return $.browser.version == '7.0' || ($.browser.version == '8.0' && !$.support.boxModel);
            return FR.Browser.isIE8Before()
        },

        //james：判断c是否包含在p中，p==c的时候，也表示包含
        contains: function (p, c) {
            return FR.isAncestor(p, c) ? true : ((!p || !c) ? false : p == c);
        },

        //james：判断p是不是c的祖先,p,c都是DOM对象
        isAncestor: function (p, c) {
            //p = $(p);
            //c = $(c);
            if (!p || !c) {
                return false;
            }

            if (p.contains && !(FR.Browser.r.safari || FR.Browser.r.chrome)) {
                try {
                    return p.contains(c);//james:IE中会报“no such interface supported”
                } catch (e) {
                    //do nothing
                }
            }
            if (p.compareDocumentPosition) {
                return !!(p.compareDocumentPosition(c) & 16);
            } else {
                var parent = c.parentNode;
                while (parent) {
                    if (parent == p) {
                        return true;
                    }
                    else if (!parent.tagName || parent.tagName.toUpperCase() == "HTML") {
                        return false;
                    }
                    parent = parent.parentNode;
                }
                return false;
            }
        },

        isNavKeyPress: function (event) {
            if (!event) {
                return false;
            }
            var safariKeys = {
                63234: 37, // left
                63235: 39, // right
                63232: 38, // up
                63233: 40, // down
                63276: 33, // page up
                63277: 34, // page down
                63272: 46, // delete
                63273: 36, // home
                63275: 35  // end
            };
            var k = event.keyCode;
            k = (FR.Browser.r.safari || FR.Browser.r.chrome) ? (safariKeys[k] || k) : k;
            return (k >= 33 && k <= 40) || k === 13 || k == FR.keyCode.TAB || k == FR.keyCode.ESCAPE;
        },

        isSpecialKey: function (e) {
            if (!e) {
                return false;
            }
            var k = e.keyCode;
            return (e.type == 'keypress' && e.ctrlKey) || k === 9 || k === 13 || k === 40 || k === 27 ||
                (k === 16) || (k === 17) ||
                (k >= 18 && k <= 20) ||
                (k >= 33 && k <= 35) ||
                (k >= 36 && k <= 39) ||
                k === 44 || (k === 13 || k === 229);//richer:中文输入法可以在数字编辑器中输入非数字的bug
        },

        equals: function (v1, v2) {
            if (FR.isNull(v1) || FR.isNull(v2)) {
                return v1 == undefined && v2 == undefined || v1 == null && v2 == null
            }
            if (v1 === v2) {
                return true;
            } else if ($.isArray(v1)) {
                if (v1.length == v2.length) {
                    for (var i = 0; i < v1.length; i++) {
                        if (!FR.equals(v1[i], v2[i])) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            } else if (typeof v1 == 'object') {
                var lv1 = 0, lv2 = 0;
                for (var i in v1) {
                    lv1++;
                }
                for (var i in v2) {
                    lv2++;
                }
                if (lv1 != lv2) {
                    return false;
                }
                for (var a in v1) {
                    if (!FR.equals(v1[a], v2[a])) {
                        return false;
                    }
                }
            } else {
                return v1 === v2
            }

            return true;
        },

        isNull: function (v) {
            return v == undefined || v == null
        },

        // 给文件名后面加一块,用于重名处理,没有扩展名的话直接加在后面
        lengthenFileName: function (name, s) {
            if (!name) {
                return;
            }
            if (name.lastIndexOf('.') == -1) {
                return name + s;
            }
            var leftName = name.substring(0, name.lastIndexOf('.'));
            var rightName = name.substring(name.lastIndexOf('.'), name.length);
            return leftName + s + rightName;
        },

        /**
         * 获取光标在文本框文本中的位置
         * @param input
         * @return {*}
         */
        getCursorPosition: function (input) {
            try {
                if (input.value == "") {
                    return 0;
                }
                var cursurPosition = -1;
                if (input.selectionStart) { //非IE
                    cursurPosition = input.selectionStart;
                } else {                   //IE
                    var rngSel = document.selection.createRange();//建立选择域
                    var rngTxt = input.createTextRange();//建立文本域
                    var flag = rngSel.getBookmark();//用选择域建立书签
                    rngTxt.collapse();//瓦解文本域到开始位,以便使标志位移动
                    rngTxt.moveToBookmark(flag);//使文本域移动到书签位
                    rngTxt.moveStart('character', -input.value.length);//获得文本域左侧文本
//                    cursurPosition = rngTxt.text.replace(/\r\n/g, '').length;//替换回车换行符
                    // ie和非ie的都把换行符算在内
                    cursurPosition = rngTxt.text.length;
                }
                return cursurPosition;
            }
            catch (e) {
                return input.value.length;
            }
        },

        /**
         * 设置光标位置
         * @param input
         * @param pos
         */
        setCursorPosition: function (input, pos) {
            if (input.setSelectionRange) {
                input.focus();
                input.setSelectionRange(pos, pos);
            } else if (input.createTextRange) {
                var lineChangeNum = input.value.length - input.value.replace(/\n/g, '').length;
                pos = pos - lineChangeNum + 1;
                var range = input.createTextRange();
                range.collapse(true);
                range.moveEnd('character', pos);
                range.moveStart('character', pos);
                range.select();
            }
        },

        /**
         * 将表示id的字符串转换成表示格子的字符串，如"A3-1-0"->"A3"
         * @param id
         * @return {String}
         */
        id2ColumnRowStr: function (id) {
            return id.replace(/([A-Z]+\d+)-\d+-\d+/, "$1");
        },

        /**
         * 将表示id的字符串转换成一个表示列和行的对象，如"A3-1-0"->{column, row}
         * @param id
         * @return {*}
         */
        id2ColumnRow: function (id) {
            return FR.cellStr2ColumnRow(FR.id2ColumnRowStr(id));
        },

        /**
         * 将表示id的字符串转换成一个表示页数、列和行的对象，如"A3-4-5"->{rptIdx, column, row}
         * @param id
         * @return {*}
         */
        id2Location: function (id) {
            var rt = FR.id2ColumnRow(id);
            var arr = id.split("-");
            rt.rptIdx = arr[1];
            return rt
        },

        /**
         * 将表示行列的字符串转换成表示列和行的对象，如"A3"->{column, row}
         * @param name
         * @return {*}
         */
        cellStr2ColumnRow: function (name) {
            name = $.trim(name.toUpperCase());
            if (!FR.isValidCellStr(name)) {
                return null;
            }
            var colStr = name.replace(/([A-Z]+)\d+/, "$1")
            var rowStr = name.replace(/[A-Z]+(\d+)/, "$1")
            var col = FR.letter2Digit(colStr) - 1;

            var row = parseInt(rowStr) - 1;
            return {
                col: col,
                row: row
            }
        },

        /**
         * 将表示列和行的对象转化成表示行列的字符串，如{column, row}->"A3"
         * @param cr
         * @return {*}
         */
        columnRow2CellStr: function (cr) {
            var col = parseInt(cr.col);
            var row = parseInt(cr.row);

            if (typeof col != "number" || typeof row != "number") {
                return "";
            }

            return FR.digit2Letter(col + 1) + (row + 1);
        },

        /**
         * 判断是否是合法的单元格字符串，如A1
         * @param str 被判断的字符串
         */
        isValidCellStr: function (str) {
            if (str.match(/^[A-Z]+\d+$/) == null) {
                return false;
            } else {
                return true;
            }
        },

        // "A"->1
        letter2Digit: function (abc) {
            var len = abc.length;
            var col = 0;
            for (var i = len - 1; i >= 0; i--) {
                var c = abc.charAt(i);
                col += (COL_IDS.indexOf(c) + 1) * Math.pow(26, len - 1 - i);
            }
            return col;
        },

        // 1->"A"
        digit2Letter: function (k) {
            var c, abcBuf = "";

            if (typeof k != 'number' || k === 0 || isNaN(k) || !isFinite(k)) {
                return abcBuf;
            }

            for (; k !== 0;) {
                c = k % 26;
                if (c === 0) {
                    c = 26;
                }
                abcBuf = COL_IDS.charAt(c - 1) + abcBuf;
                k = (k - c) / 26;
            }

            return abcBuf;
        },

        // 获取字符串的byte长度，英文占1，中文占2
        byteLength: function (str) {
            var byteLen = 0, len = str.length;
            if (!str) {
                return 0;
            }
            for (var i = 0; i < len; i++) {
                byteLen += str.charCodeAt(i) > 255 ? 2 : 1;
            }
            return byteLen;
        },
        keyCode: {
            BACKSPACE: 8,
            COMMA: 188,
            DELETE: 46,
            DOWN: 40,
            END: 35,
            ENTER: 13,
            ESCAPE: 27,
            HOME: 36,
            LEFT: 37,
            NUMPAD_ADD: 107,
            NUMPAD_DECIMAL: 110,
            NUMPAD_DIVIDE: 111,
            NUMPAD_ENTER: 108,
            NUMPAD_MULTIPLY: 106,
            NUMPAD_SUBTRACT: 109,
            PAGE_DOWN: 34,
            PAGE_UP: 33,
            PERIOD: 190,
            RIGHT: 39,
            SPACE: 32,
            TAB: 9,
            UP: 38
        },

        location: function (callback) {
            callback.call(this, "fail", FR.i18nText("FR-Basic_Fail_Location"));
        }

    });
})(jQuery);



(function ($) {

    var kv = {}; // alex:键(编辑器简称,如text)值(也是一个字符串,如FR.TextEditor)对
    $.shortcut = function (xtype, cls) {
        if (kv[xtype] != null) {
            throw ("shortcut:[" + xtype + "] has been registed");
        }
        kv[xtype] = cls;
        $.extend(cls.prototype, {
            xtype: xtype
        })
    };
    // 根据配置属性生成widget
    FR.createWidget = function (config, throwError) {
        // alex:如果是一个jquery对象,就在外面套一层,变成一个FR.Widget
        if (config instanceof $) {
            return new FR.Widget({
                renderEl: config
            });
        }
        if (config['classType']) {
            return new (new Function('return ' + config['classType'] + ';')())(config);
        }

        if (!config.type) {
            if (throwError !== true) {
                return new FR.ErrorMarkWidget({
                    renderEl : config.renderEl,
                    message : 'config type cannot be null.',
                    width : config.width,
                    height : config.height
                });
            }
        }
        var xtype = config.type.toLowerCase();
        var cls = kv[xtype];
        try {
            return new cls(config);
        } catch (e) {
            if (throwError === true) {
                throw e;
            } else {
                return new FR.ErrorMarkWidget({
                    renderEl : config.renderEl,
                    message : FR.i18nText('FR-Base_Widget_Error_Mark_Text') + ":" + config.type + ",message:" + e,
                    width : config.width,
                    height : config.height});
            }
        }
    };
    // 兼容$el.asComponent(config)的写法
    $.fn.extend({
        asComponent: function (config) {
            config = config || {};
            config.renderEl = $(this);
            return FR.createWidget(config);
        }
    })
})(jQuery);

// 简化FR.SessionMgr.getContentPane
if (window._g == null) {
    window._g = FR.SessionMgr.getContentPane;
}

/*
 * 加减乘除的精确计算的东西
 * 
 * marks:为了精确计算
 */
$.extend(FR, {
    /*
     * 把2, 4, 7, 3-6, 8-9, 12转成int[]
     */
    string2ints: function (str) {
        var res_array = []
        var ints_str_array = str.split(",");
        for (var i = 0; i < ints_str_array.length; i++) {
            var ints_str = ints_str_array[i]; // 3-6 or 7
            var se = ints_str.split("-");
            var start = parseInt(se[0]), end = start
            if (se.length > 1) {
                end = parseInt(se[1])
            }

            for (var x = start; x <= end; x++) {
                res_array.push(x);
            }
        }

        return res_array
    }
});

//b:迁移至base下，暂时做为内置函数用来处理process
$.extend(FR, {
    activeBranches: function (branches, parameters, processid, taskName, serverURL, needClose) {
        if (processid && taskName) {
            var it = {
                "id": processid,
                "taskname": taskName
            }
        } else {
            var it = this.getProcessIDAndTaskName();
            if (!it.processid) {
                //wei : 说明任务已经关闭
                FR.Msg.toast(FR.i18nText('FR_FS-Task_Has_Been_Closed'));
                return;
            }
        }
        //wei : 支持不选分支，只选参数，这样就是不跳转只入库。所以branches未定义的时候不return
        if (!it) {
            return;
        }
        if (!$.isArray(branches)) {
            branches = [branches];
        }
        if (parameters != null && !$.isArray(parameters)) {
            parameters = [parameters];
        }
        var data = $.extend(it, {
            branches: branches
        });
        if (parameters) {
            $.each(parameters, function (idx, item) {
                if (item.value.toString().startWith('eval')) {
                    var evalValue = item.value.toString().substring(5, item.value.toString().length - 1);
                    item.value = eval(evalValue);
                }
            });
            data.parameters = parameters;
        }
        this.ajax({
            url: (serverURL ? serverURL : FR.servletURL) + '?op=process_exe&cmd=continue',
            data: data,
            type: 'post',
            complete: function (res, status) {
                if (res.responseText == 'close') {
                    FR.Msg.toast(FR.i18nText('FR_FS-Task_Has_Been_Closed'));
                    return;
                }
//				if (res.responseText != 'success') {
//					FR.Msg.toast('操作失败!');
//				}
                this.refreshUserTask(it);
            }.createDelegate(this)
        });
    },

    getProcessIDAndTaskName: function () {
        var o = {};
        this.ajax({
            url: FR.servletURL + '?op=process_exe',
            async: false,
            data: {
                sessionID: FR.SessionMgr.getSessionID(),
                cmd: 'values'
            },
            complete: function (res, status) {
                o = FR.jsonDecode(res.responseText);
            }
        });
        return o;
    },

    refreshUserTask: function (it) {
        var pa = window.parent;
        if (!pa || !pa.FS || !pa.FS.Process) {
            //wei : 不是在FS里访问的页面，直接刷新
            location.reload(true);
            return;
        }
        pa.FS.Process.refreshTask();
        var src = "?op=process_exe&cmd=seejob&taskname=" + it.taskname + "&processexecutorid=" + it.id
            + "&processid=" + it.processid;
        pa.FS.tabPane.addItem({
            'title': FR.i18nText('FR_FS-Handle_Affairs') + it.id,
            src: src
        });
    }
});

// 在pc端 重置js移动端特有方法
$.extend(FR, {
    logoutApp: function() {

    }
});

// 用全局变量保存浏览器尺寸,参数面板,工具栏高度
(function($){
    $(document).ready(function () {
        FR.windowHeight = $(window).height();
        FR.windowWidth = $(window).width();
        if (FR.windowHeight === 0 || FR.windowWidth === 0) {
            FR.windowHeight = document.body.clientHeight;
            FR.windowWidth = document.body.clientWidth;
        }
        $(window).resize(function() {
            if (FR.Browser.isIE()) {
                setTimeout(function () {
                    FR.windowHeight = $(window).height();
                    FR.windowWidth = $(window).width();
                    if (FR.windowHeight === 0 || FR.windowWidth === 0) {
                        FR.windowHeight = document.body.clientHeight;
                        FR.windowWidth = document.body.clientWidth;
                    }
                }, 0);
            }
            else {
                FR.windowHeight = $(window).height();
                FR.windowWidth = $(window).width();
            }
        });
        FR.parameterContainerHeight = 0;
        FR.toolbarHeight = 0;
    });
})($);