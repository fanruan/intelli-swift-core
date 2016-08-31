if (!window.FR) {
    window.FR = {};
}

_.extend(FR, {
    isEmpty: function (value) {
        // 判断是否为空值
        var result = value === "" || value === null || value === undefined;
        return result;
    },

    contentFormat: function (cv, fmt) {
        if (FR.isEmpty(cv)) {
            //原值为空，返回空字符
            return '';
        }
        var text = cv.toString();
        if (FR.isEmpty(fmt)) {
            //格式为空，返回原字符
            return text;
        }
        if (fmt.match(/^T/)) {
            //T - 文本格式
            return text;
        } else if (fmt.match(/^D/)) {
            //D - 日期(时间)格式
            if (!(cv instanceof Date)) {
                if (typeof cv === 'number') {
                    //毫秒数类型
                    cv = new Date(cv);
                } else {
                    //字符串类型，如yyyyMMdd、MMddyyyy等这样无分隔符的结构
                    cv = Date.parseDate(cv + "", Date.patterns.ISO8601Long);
                }
            }
            if (!FR.isNull(cv)) {
                var needTrim = fmt.match(/^DT/);
                text = FR.date2Str(cv, fmt.substring(needTrim ? 2 : 1));
            }
        } else if (fmt.match(/E/)) {
            //科学计数格式
            text = FR._eFormat(text, fmt);
        } else {
            //数字格式
            text = FR._numberFormat(text, fmt);
        }
        //¤ - 货币格式
        text = text.replace(/¤/g, '￥');
        return text;
    },

    /**
     * 数字格式
     */
    _numberFormat: function (text, format) {
        var text = text + '';
        //数字格式，区分正负数
        var numMod = format.indexOf(';');
        if (numMod > -1) {
            if (text >= 0) {
                return FR._numberFormat(text + "", format.substring(0, numMod));
            } else {
                return FR._numberFormat((-text) + "", format.substr(numMod + 1));
            }
        }
        var tp = text.split('.'), fp = format.split('.'),
            tleft = tp[0] || '', fleft = fp[0] || '',
            tright = tp[1] || '', fright = fp[1] || '';
        //百分比,千分比的小数点移位处理
        if (/[%‰]$/.test(format)) {
            var paddingZero = /[%]$/.test(format) ? '00' : '000';
            tright += paddingZero;
            tleft += tright.substr(0, paddingZero.length);
            tleft = tleft.replace(/^0+/gi, '');
            tright = tright.substr(paddingZero.length).replace(/0+$/gi, '');
        }
        var right = FR._dealWithRight(tright, fright);
        if (right.leftPlus) {
            //小数点后有进位
            tleft = parseInt(tleft) + 1 + '';

            tleft = isNaN(tleft) ? '1' : tleft;
        }
        right = right.num;
        var left = FR._dealWithLeft(tleft, fleft);
        if (!(/[0-9]/.test(left))) {
            left = left + '0';
        }
        if (!(/[0-9]/.test(right))) {
            return left + right;
        } else {
            return left + '.' + right;
        }
    },
    /**
     * 处理小数点右边小数部分
     * @param tright 右边内容
     * @param fright 右边格式
     * @returns {JSON} 返回处理结果和整数部分是否需要进位
     * @private
     */
    _dealWithRight: function (tright, fright) {
        var right = '', j = 0, i = 0;
        for (var len = fright.length; i < len; i++) {
            var ch = fright.charAt(i);
            var c = tright.charAt(j);
            switch (ch) {
                case '0':
                    if (FR.isEmpty(c)) {
                        c = '0';
                    }
                    right += c;
                    j++;
                    break;
                case '#':
                    right += c;
                    j++;
                    break;
                default :
                    right += ch;
                    break;
            }
        }
        var rll = tright.substr(j);
        var result = {};
        if (!FR.isEmpty(rll) && rll.charAt(0) > 4) {
            //有多余字符，需要四舍五入
            result.leftPlus = true;
            var numReg = right.match(/^[0-9]+/);
            if (numReg) {
                var num = numReg[0];
                var orilen = num.length;
                var newnum = FR.parseINT(num) + 1 + '';
                //进位到整数部分
                if (newnum.length > orilen) {
                    newnum = newnum.substr(1);
                } else {
                    newnum = FR.leftPad(newnum, orilen, '0');
                    result.leftPlus = false;
                }
                right = right.replace(/^[0-9]+/, newnum);
            }
        }
        result.num = right;
        return result;
    },

    parseINT: function (str) {
        return parseInt(str, 10);
    },

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
     * 处理小数点左边整数部分
     * @param tleft 左边内容
     * @param fleft 左边格式
     * @returns {string} 返回处理结果
     * @private
     */
    _dealWithLeft: function (tleft, fleft) {
        var left = '';
        var j = tleft.length - 1;
        var combo = -1, last = -1;
        var i = fleft.length - 1;
        for (; i >= 0; i--) {
            var ch = fleft.charAt(i);
            var c = tleft.charAt(j);
            switch (ch) {
                case '0':
                    if (FR.isEmpty(c)) {
                        c = '0';
                    }
                    last = -1;
                    left = c + left;
                    j--;
                    break;
                case '#':
                    last = i;
                    left = c + left;
                    j--;
                    break;
                case ',':
                    if (!FR.isEmpty(c)) {
                        //计算一个,分隔区间的长度
                        var com = fleft.match(/,[#0]+/);
                        if (com) {
                            combo = com[0].length - 1;
                        }
                        left = ',' + left;
                    }
                    break;
                default :
                    left = ch + left;
                    break;
            }
        }
        if (last > -1) {
            //处理剩余字符
            var tll = tleft.substr(0, j + 1);
            left = left.substr(0, last) + tll + left.substr(last);
        }
        if (combo > 0) {
            //处理,分隔区间
            var res = left.match(/[0-9]+,/);
            if (res) {
                res = res[0];
                var newstr = '', n = res.length - 1 - combo;
                for (; n >= 0; n = n - combo) {
                    newstr = res.substr(n, combo) + ',' + newstr;
                }
                var lres = res.substr(0, n + combo);
                if (!FR.isEmpty(lres)) {
                    newstr = lres + ',' + newstr;
                }
            }
            left = left.replace(/[0-9]+,/, newstr);
        }
        return left;
    },

    object2Number: function (value) {
        if (value == null) {
            return 0;
        }
        if (typeof value == 'number') {
            return value;
        } else {
            var str = value + "";
            if (str.indexOf(".") === -1) {
                return parseInt(str);
            } else {
                return parseFloat(str);
            }
        }
    },

    object2Date: function (obj) {
        if (obj == null) {
            return new Date();
        }
        if (obj instanceof Date) {
            return obj;
        } else if (typeof obj == 'number') {
            return new Date(obj);
        } else {
            var str = obj + "";
            str = str.replace(/-/g, '/');
            var dt = new Date(str);
            if (!FR.isInvalidDate(dt)) {
                return dt;
            }

            return new Date();
        }
    },

    object2Time: function (obj) {
        if (obj == null) {
            return new Date();
        }
        if (obj instanceof Date) {
            return obj;
        } else {
            var str = obj + "";
            str = str.replace(/-/g, '/');
            var dt = new Date(str);
            if (!FR.isInvalidDate(dt)) {
                return dt;
            }
            if (str.indexOf('/') === -1 && str.indexOf(':') !== -1) {
                dt = new Date("1970/01/01 " + str);
                if (!FR.isInvalidDate(dt)) {
                    return dt;
                }
            }
            dt = FR.str2Date(str, "HH:mm:ss");
            if (!FR.isInvalidDate(dt)) {
                return dt;
            }
            return new Date();
        }
    }
})

/**
 * 科学计数格式
 */
FR._eFormat = function (text, fmt) {
    var e = fmt.indexOf("E");
    var eleft = fmt.substr(0, e), eright = fmt.substr(e + 1);
    if (/^[0\.-]+$/.test(text)) {
        text = FR._numberFormat(0.0, eleft) + 'E' + FR._numberFormat(0, eright)
    } else {
        var isNegative = text < 0;
        if (isNegative) {
            text = text.substr(1);
        }
        var elvl = (eleft.split('.')[0] || '').length;
        var point = text.indexOf(".");
        if (point < 0) {
            point = text.length;
        }
        var i = 0; //第一个不为0的数的位置
        text = text.replace('.', '');
        for (var len = text.length; i < len; i++) {
            var ech = text.charAt(i);
            if (ech <= '9' && ech >= '1') {
                break;
            }
        }
        var right = point - i - elvl;
        var left = text.substr(i, elvl);
        var dis = i + elvl - text.length;
        if (dis > 0) {
            //末位补全0
            for (var k = 0; k < dis; k++) {
                left += '0';
            }
        } else {
            left += '.' + text.substr(i + elvl);
        }
        left = left.replace(/^[0]+/, '');
        if (right < 0 && eright.indexOf('-') < 0) {
            eright += ';-' + eright;
        }
        text = FR._numberFormat(left, eleft) + 'E' + FR._numberFormat(right, eright);
        if (isNegative) {
            text = '-' + text;
        }
    }
    return text;
}

FR.serverURL = document.location.href;
FR.servletURL = "";

FR.i18n = BI.i18n = {
    Monday: 'Monday',
    Tuesday: 'Tuesday',
    Wednesday: 'Wednesday',
    Thursday: 'Thursday',
    Friday: 'Friday',
    Saturday: 'Saturday',
    Sunday: 'Sunday',
    Mon: 'Mon',
    Tue: 'Tue',
    Wed: 'Wed',
    Thu: 'Thu',
    Fri: 'Fri',
    Sat: 'Sat',
    Sun: 'Sun',
    January: 'January',
    February: 'February',
    March: 'March',
    April: 'April',
    May: 'May',
    June: 'June',
    July: 'July',
    August: 'August',
    September: 'September',
    October: 'October',
    November: 'November',
    December: 'December',
    Jan: 'Jan',
    Feb: 'Feb',
    Mar: 'Mar',
    Apr: 'Apr',
    Short_May: 'Short_May',
    Jun: 'Jun',
    Jul: 'Jul',
    Aug: 'Aug',
    Sep: 'Sep',
    Oct: 'Oct',
    Nov: 'Nov',
    Dec: 'Dec',
    Prev_Year: 'Prev_Year',
    Prev_Month: 'Prev_Month',
    Next_Month: 'Next_Month',
    Next_Year: 'Next_Year',
    Select_Date: 'Select_Date',
    Drag_To_Move: 'Drag_To_Move',
    Today: 'Today',
    OK: 'OK',
    Clear: 'Clear',
    Cancel: 'Cancel',
    First_Of_Week: 'First_Of_Week',
    Close: 'Close',
    Click_To_Change_Value: 'Click_To_Change_Value',
    Week: 'Week',
    Time: 'Time'

};

BI.i18nText = function (key) {
    return key;
};