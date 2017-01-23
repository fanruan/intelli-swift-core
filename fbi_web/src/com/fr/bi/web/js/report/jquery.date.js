;
/**
 * 日期的扩展函数
 * @class Date
 */
$.extend(Date.prototype, {
    /**
     * 获取时区
     * @returns {String}
     */
    getTimezone: function () {
        return this.toString().replace(/^.* (?:\((.*)\)|([A-Z]{1,4})(?:[\-+][0-9]{4})?(?: -?\d+)?)$/, "$1$2").replace(/[^A-Z]/g, "");
    },

    /**
     * 获取每月的最后一天
     * @returns {Date}
     */
    getLastDateOfMonth: function () {
        return new Date(this.getFullYear(), this.getMonth(), this.getMonthDays());
    },

    /**
     * 获取该月总天数
     * @param {Number} month 有参数表示指定月份，无参数表示当前月份
     * @returns {*}
     */
    getMonthDays: function (month) {
        var MD = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
        var year = this.getFullYear();
        if (typeof month == "undefined") {
            month = this.getMonth();
        }
        if (((0 === (year % 4)) && ( (0 !== (year % 100)) || (0 === (year % 400)))) && month === 1) {
            return 29;
        } else {
            return MD[month];
        }
    },

    /**
     * 获取当前日期属于该月的第几周
     * @returns {Number}
     */
    getWeekNumber: function () {
        var d = new Date(this.getFullYear(), this.getMonth(), this.getDate(), 0, 0, 0);
        var DoW = d.getDay();
        d.setDate(d.getDate() - (DoW + 6) % 7 + 3);
        var ms = d.valueOf();
        d.setMonth(0);
        d.setDate(4);
        return Math.round((ms - d.valueOf()) / (7 * 864e5)) + 1;
    }

});
(function () {
    var compileJFmt = function (jfmt, date) {
        var str = jfmt.str, len = jfmt.len, ch = jfmt['char'];
        switch(ch){
            case 'E': //星期
                //跟java的效果一致
                str = len < 4 ? Date._SDN[date.getDay()] : Date._DN[date.getDay()];
                break;
            case 'y': //年
                if(len <= 3){
                    str = (date.getFullYear()+'').slice(2,4);
                }else{
                    str = date.getFullYear();
                }
                break;
            case 'M': //月
                if(len > 2){
                    str = len > 3 ? Date._MN[date.getMonth()] : Date._SMN[date.getMonth()];
                }else if(len < 2){
                    str = date.getMonth() + 1;
                }else{
                    str = String.leftPad(date.getMonth() + 1 +'',2,'0');
                }
                break;
            case 'd': //日
                if(len > 1){
                    str = String.leftPad(date.getDate()+'',2,'0');
                }else{
                    str = date.getDate();
                }
                break;
            case 'h': //时(12)
                var hour = date.getHours()%12;
                if(hour === 0){
                    hour = 12;
                }
                if(len > 1){
                    str = String.leftPad(hour +'',2,'0');
                }else{
                    str = hour;
                }
                break;
            case 'H': //时(24)
                if(len > 1){
                    str = String.leftPad(date.getHours()+'',2,'0');
                }else{
                    str = date.getHours();
                }
                break;
            case 'm':
                if(len > 1){
                    str = String.leftPad(date.getMinutes()+'',2,'0');
                }else{
                    str = date.getMinutes();
                }
                break;
            case 's':
                if(len > 1){
                    str = String.leftPad(date.getSeconds()+'',2,'0');
                }else{
                    str = date.getSeconds();
                }
                break;
            case 'a':
                str = date.getHours()<12 ? Date._NN[0] : Date._NN[1];
                break;
            case 'z':
                str = date.getTimezone();
                break;
            default:
                str = jfmt.str;
                break;
        }
        return str;
    };

    /**
     * 将Java提供的日期格式字符串装换为JS识别的日期格式字符串
     * @class FR.parseFmt
     * @param fmt 日期格式
     * @returns {String}
     */
    FR.parseFmt = function (fmt) {
        if (!fmt) {
            return '';
        }
        //日期
        fmt = String(fmt)
            //年
            .replace(/y{4,}/g, '%Y')//yyyy的时候替换为Y
            .replace(/y{2}/g, '%y')//yy的时候替换为y
            //月
            .replace(/M{4,}/g, '%b')//MMMM的时候替换为b，八
            .replace(/M{3}/g, '%B')//MMM的时候替换为M，八月
            .replace(/M{2}/g, '%X')//MM的时候替换为X，08
            .replace(/M{1}/g, '%x')//M的时候替换为x，8
            .replace(/a{1}/g, '%p')
        //天
        if (new RegExp('d{2,}', 'g').test(fmt)) {
            fmt = fmt.replace(/d{2,}/g, '%d');//dd的时候替换为d
        } else {
            fmt = fmt.replace(/d{1}/g, '%e');//d的时候替换为j
        }
        //时
        if (new RegExp('h{2,}', 'g').test(fmt)) {//12小时制
            fmt = fmt.replace(/h{2,}/g, '%I');
        } else {
            fmt = fmt.replace(/h{1}/g, '%I');
        }
        if (new RegExp('H{2,}', 'g').test(fmt)) {//24小时制
            fmt = fmt.replace(/H{2,}/g, '%H');
        } else {
            fmt = fmt.replace(/H{1}/g, '%H');
        }
        fmt = fmt.replace(/m{2,}/g, '%M')//分
            //秒
            .replace(/s{2,}/g, '%S')

        return fmt;
    };

    /**
     * 这个主要是针对IE支持的日期格式太少没法构造现成的日期进行判断的情况
     *
     *      @example
     *      format:yyyy-MM-dd  yyyyMM  M/yyyy  yyyy-M-d h:m    yyyyMd  yyyyMd
     *      date: 2012-01-01  201412  1/2013  2013-1-1 5:30    201311  2013111
     *
     * @class FR.matchDateFormat
     * @param date 日期
     * @param format 格式
     * @returns {*}
     */
    FR.matchDateFormat = function (date, format) {
        if (typeof date != 'string' || typeof format != 'string' || !date || !format) {
            return false;
        }

        //中文的年月日间隔符处理
        var zh_yMd = "[" + String.fromCharCode(24180) + String.fromCharCode(26376) + String.fromCharCode(26085) + "]";
        if (date.match(zh_yMd)) {
            if (format.match(zh_yMd)) {
                date = date.replaceAll(zh_yMd, '-');
                format = format.replaceAll(zh_yMd, '-');
                if (date.endWith('-') && format.endWith('-')) {
                    date = date.substring(0, date.length - 1);
                    format = format.substring(0, format.length - 1);
                }
            } else {
                return false;
            }
        } else if (format.match(zh_yMd)) {
            return false;
        }

        //首先判断上午下午a
        var isAM = null;
        if (format.endWith(' a')) {
            format = format.substring(0, format.length-2);
            if (date.endWith(' am') || date.endWith(' pm')) {
                isAM = date.endWith(' am');
                date = date.substring(0, date.length-3);
            } else {
                return false;
            }
        }

        //比较分隔符
        if (date.replace(/[0-9]/g, '') != format.replace(/[YyMmDdHhSsa]/g, '')) {
            return false;
        }

        var formatStr = format;
        var dateStr = date;
        var formatArr = [];
        var dateArr = [];
        if (format.indexOf(' ') == -1) {
            dateArr = shortMatch(dateStr, formatStr);
            if (!dateArr) {
                return false;
            }
        } else {
            var ff = format.split(' ');
            var dd = date.split(' ');
            for (var i = 0; i < ff.length; i++) {
                var temp = shortMatch(dd[i], ff[i]);
                if (!temp) {
                    return false;
                } else {
                    dateArr = dateArr.concat(temp);
                }
            }
        }
        function shortMatch(str, fmt) {
            var da = [];
            var fa = [];
            if (fmt.match(/[-/:.]/)) {
                fa = fmt.split(/[-/:.]/);
                formatArr = formatArr.concat(fa);
                var da = str.split(/[-/:.]/);
                return da;
            }
            while (fmt.length > 0) {
                var reg = new RegExp(fmt.substring(0, 1) + "+");
                var f = fmt.match(reg).toString();
                fa.push(f);
                formatArr.push(f);
                fmt = fmt.substring(f.length);
            }
            var len = 0;
            for (var i = 0; i < fa.length; i++) {
                var tmpLen = 0;
                if (fa[i].length === 1) {
                    if (i == fa.length - 1) {
                        tmpLen = str.length - len;
                    } else if (i == fa.length - 2) {
                        switch (str.length - len) {
                            case 2 :
                                tmpLen = 1;
                                break;
                            case 3 :
                                if (invalidStr(str.substring(len, len + 2), fa[i]) || fa[i + 1].length === 2) {
                                    tmpLen = 1;
                                } else if (fa[i + 1].length === 1 && invalidStr(str.substring(len + 1, len + 3), fa[i + 1])) {
                                    tmpLen = 2;
                                } else {
                                    tmpLen = 1;
                                }
                                break;
                            case 4 :
                                tmpLen = 2;
                                break;
                            default :
                                return false;
                        }
                    }
                } else {
                    tmpLen = fa[i].length;
                }
                da[i] = str.substring(len, len + tmpLen);
                len += tmpLen;
            }
            return da;
        }

        function invalidStr(str, fm) {
            return str.match(/[Mh]/) && parseInt(fm, 10) > 12 ||
                str.match(/[d]/) && parseInt(fm, 10) > 31 ||
                str.match(/[H]/) && parseInt(fm, 10) > 23 ||
                str.match(/[ms]/) && parseInt(fm, 10) > 59;
        }

        if (formatArr.length != dateArr.length) {
            return false;
        } else {
            for (var i = 0; i < formatArr.length; i++) {
                var ff = formatArr[i].replace(/ /g, '');
                var vv = dateArr[i].replace(/ /g, '');
                if (ff.length === 1 && ((vv.length === 2 && vv.startWith('0')) || vv.length > 2) ||
                    ff.length === 2 && vv.length !== 2 ||
                    ff.length === 4 && vv.length !== 4) {
                    return false;
                }
            }
            // 先把年月日的顺序调整下
            var que = ['y', 'M', 'd', 'h', 'H', 'm', 's'];
            for (var i = 0; i < formatArr.length; i++) {
                if (formatArr[i].match(/[yMd]/)) {
                    for (var j = i + 1; j < formatArr.length; j++) {
                        if (formatArr[j].match(/[yMd]/) &&
                            que.indexOf(formatArr[i].substring(0, 1)) > que.indexOf(formatArr[j].substring(0, 1))) {
                            var tmp = formatArr[i];
                            formatArr[i] = formatArr[j];
                            formatArr[j] = tmp;
                            tmp = dateArr[i];
                            dateArr[i] = dateArr[j];
                            dateArr[j] = tmp;
                        }
                    }
                }
            }
            // 如果是match的 返回对应的日期
            if (!format.match(/[hHms]/)) {
                var dt = "";
                // 年份如果是两位改成四位
                for (var i = 0; i < dateArr.length; i++) {
                    var yMd = formatArr[i] == 'yy' ? "20" + dateArr[i] : dateArr[i];
                    dt = dt.length === 0 ? yMd : dt + '/' + yMd;
                }
                var res = new Date(dt);
                if (!FR.isInvalidDate(res)) {
                    /*
                     * new Date('2013/02/28')   --Thu Feb 28 2013 00:00:00 GMT+0800 (中国标准时间)
                     * new Date('2013/02/31')   --Mon Mar 03 2014 00:00:00 GMT+0800 (中国标准时间)
                     * new Date('2013/02/33')   --Invalid Date
                     */
                    if (!inMonthRange(res)) {
                        return false;
                    }
                    return res;
                } else if (!FR.isInvalidDate(new Date(dt + '/01'))) {
                    return new Date(dt + '/01');
                } else if (!FR.isInvalidDate(new Date(dt + '/01' + '/01'))) {
                    return new Date(dt + '/01' + '/01');
                }
            } else {
                if(checkHms(date)){
                    return date;
                }

                var dt = '';
                var spt = false;
                for (var i = 0; i < formatArr.length; i++) {
                    if (formatArr[i].match(/[yMd]/)) {
                        var yMd = formatArr[i] == 'yy' ? "20" + dateArr[i] : dateArr[i];
                        dt = dt.length === 0 ? yMd : dt + '/' + yMd;
                    } else {
                        var hms = dateArr[i];
                        if (formatArr[i].indexOf('h') != -1 && isAM === false && parseInt(hms) < 12) {
                            hms = parseInt(hms) + 12;
                        }
                        if (!spt) {
                            dt = dt + ' ' + hms;
                            spt = true;
                        } else {
                            dt = dt + ':' + hms;
                        }
                    }
                }
                var res = new Date(dt);
                if (!inMonthRange(res)) {
                    return false;
                }
                return res;
            }
        }
        function checkHms(timeStr){
            var parts;
            if(FR.isEmpty(timeStr)){
                return false;
            }
            parts = timeStr.split(':');

            if(FR.isEmpty(parts)){
                return false;
            }
            for (i = 0; i < parts.length; i++) {
                if (isNaN(parts[i])) {
                    return false;
                }
            }
            if (parts.length === 2) {
                h = parts[0]; //小時
                m = parts[1]; //分
                if(format.match(/[s]/)){

                    if (h < 0 || h > 59) {
                        return false;
                    }
                    if (m < 0 || m > 59) {
                        return false;
                    }
                }else{
                    if (h < 0 || h > 23) {
                        return false;
                    }
                    if (m < 0 || m > 59) {
                        return false;
                    }
                }
              return true;
            }else if (parts.length === 3){
                h = parts[0]; //小時
                m = parts[1]; //分
                s = parts[3];
                if (h < 0 || h > 23) {
                    return false;
                }
                if (m < 0 || m > 59) {
                    return false;
                }
                if(s < 0 || s > 59){
                    return false;
                }
            }
            return true;
        }
        function inMonthRange(res) {
            var d, m;
            for (var i=0; i<formatArr.length; i++) {
                if (formatArr[i].indexOf('d') != -1) {
                    d = dateArr[i].replace(/^[0]/, "");
                } else if (formatArr[i].indexOf('M') != -1) {
                    m = dateArr[i].replace(/^[0]/, "");
                }
            }
            if (d == null || m == null) {
                return true;
            }
            return parseInt(d) == res.getDate() && parseInt(m) == res.getMonth() + 1;
        }
    };

    /**
     * 把字符串按照对应的格式转化成日期对象
     *
     *      @example
     *      var result = FR.str2Date('2013-12-12', 'yyyy-MM-dd');//Thu Dec 12 2013 00:00:00 GMT+0800
     *
     * @class FR.str2Date
     * @param str 字符串
     * @param format 日期格式
     * @returns {*}
     */
    FR.str2Date = function (str, format) {
        if (typeof str != 'string' || typeof format != 'string') {
            return null;
        }
        var fmt = FR.parseFmt(format);
        return Date.parseDateTime(str, fmt);
    };

    /**
     * 把日期对象按照指定格式转化成字符串
     *
     *      @example
     *      var date = new Date('Thu Dec 12 2013 00:00:00 GMT+0800');
     *      var result = FR.date2Str(date, 'yyyy-MM-dd');//2013-12-12
     *
     * @class FR.date2Str
     * @param date 日期
     * @param format 日期格式
     * @returns {String}
     */
    FR.date2Str = function(date, format){
        if(!date){
            return '';
        }
        // O(len(format))
        var len = format.length, result = '';
        if (len > 0) {
            var flagch = format.charAt(0), start = 0, str = flagch;
            for (var i = 1; i < len; i++) {
                var ch = format.charAt(i);
                if (flagch !== ch) {
                    result += compileJFmt({
                        'char': flagch,
                        'str': str,
                        'len': i - start
                    }, date);
                    flagch = ch;
                    start = i;
                    str = flagch;
                }else{
                    str +=ch;
                }
            }
            result += compileJFmt({
                'char': flagch,
                'str': str,
                'len': len - start
            }, date);
        }
        return result;
    };

    Date.patterns = {
        ISO8601Long: "Y-m-d H:i:s",
        ISO8601Short: "Y-m-d",
        ShortDate: "n/j/Y",
        LongDate: "l, F d, Y",
        FullDateTime: "l, F d, Y g:i:s A",
        MonthDay: "F d",
        ShortTime: "g:i A",
        LongTime: "g:i:s A",
        SortableDateTime: "Y-m-d\\TH:i:s",
        UniversalSortableDateTime: "Y-m-d H:i:sO",
        YearMonth: "F, Y"
    };
    Date.parseFunctions = {count: 0};
    Date.parseRegexes = [];
    Date.formatFunctions = {count: 0};

    Date._MN = [FR.i18nText("FR-Base_January"),
        FR.i18nText("FR-Base_February"),
        FR.i18nText("FR-Base_March"),
        FR.i18nText("FR-Base_April"),
        FR.i18nText("FR-Base_May"),
        FR.i18nText("FR-Base_June"),
        FR.i18nText("FR-Base_July"),
        FR.i18nText("FR-Base_August"),
        FR.i18nText("FR-Base_September"),
        FR.i18nText("FR-Base_October"),
        FR.i18nText("FR-Base_November"),
        FR.i18nText("FR-Base_December")];

    Date._SMN = [
        FR.i18nText("FR-Base_Short_Jan"),
        FR.i18nText("FR-Base_Short_Feb"),
        FR.i18nText("FR-Base_Short_Mar"),
        FR.i18nText("FR-Base_Short_Apr"),
        FR.i18nText("FR-Base_Short_May"),
        FR.i18nText("FR-Base_Short_Jun"),
        FR.i18nText("FR-Base_Short_Jul"),
        FR.i18nText("FR-Base_Short_Aug"),
        FR.i18nText("FR-Base_Short_Sep"),
        FR.i18nText("FR-Base_Short_Oct"),
        FR.i18nText("FR-Base_Short_Nov"),
        FR.i18nText("FR-Base_Short_Dec")
    ];

    Date._DN=[
        FR.i18nText("FR-Base_Sche_Sunday"),
        FR.i18nText("FR-Base_Sche_Monday"),
        FR.i18nText("FR-Base_Sche_Tuesday"),
        FR.i18nText("FR-Base_Sche_Wednesday"),
        FR.i18nText("FR-Base_Sche_Thursday"),
        FR.i18nText("FR-Base_Sche_Friday"),
        FR.i18nText("FR-Base_Sche_Saturday")
    ];

    Date._SDN=[
        FR.i18nText("FR-Base_Sun"),
        FR.i18nText("FR-Base_Mon"),
        FR.i18nText("FR-Base_Tue"),
        FR.i18nText("FR-Base_Wed"),
        FR.i18nText("FR-Base_Thu"),
        FR.i18nText("FR-Base_Fri"),
        FR.i18nText("FR-Base_Sat")
    ];

    Date._NN=[
        FR.i18nText("FR-Base_Ante_Meridian"),
        FR.i18nText("FR-Base_Post_Meridian")
    ];


    Date.y2kYear = 50;


    Date.monthNumbers = {
        Jan: 0,
        Feb: 1,
        Mar: 2,
        Apr: 3,
        May: 4,
        Jun: 5,
        Jul: 6,
        Aug: 7,
        Sep: 8,
        Oct: 9,
        Nov: 10,
        Dec: 11};

    Date.getMonthNumber = function (name) {
        return Date.monthNumbers[name.substring(0, 1).toUpperCase() + name.substring(1, 3).toLowerCase()];
    };

    Date.parseDate = function (input, format) {
        if (Date.parseFunctions[format] == null) {
            Date.createParser(format);
        }
        var func = Date.parseFunctions[format];
        return Date[func](input);
    };

    Date.createParser = function (format) {
        var funcName = "parse" + Date.parseFunctions.count++;
        var regexNum = Date.parseRegexes.length;
        var currentGroup = 1;
        Date.parseFunctions[format] = funcName;

        var code = "Date." + funcName + " = function(input){\n"
            + "var y = -1, m = -1, d = -1, h = -1, i = -1, s = -1, ms = -1, o, z, u, v;\n"
            + "var d = new Date();\n"
            + "y = d.getFullYear();\n"
            + "m = d.getMonth();\n"
            + "d = d.getDate();\n"
            + "var results = input.match(Date.parseRegexes[" + regexNum + "]);\n"
            + "if (results && results.length > 0) {";
        var regex = "";

        var special = false;
        var ch = '';
        for (var i = 0; i < format.length; ++i) {
            ch = format.charAt(i);
            if (!special && ch == "\\") {
                special = true;
            }
            else if (special) {
                special = false;
                regex += String.escape(ch);
            }
            else {
                var obj = Date.formatCodeToRegex(ch, currentGroup);
                currentGroup += obj.g;
                regex += obj.s;
                if (obj.g && obj.c) {
                    code += obj.c;
                }
            }
        }

        code += "if (u)\n"
            + "{v = new Date(u * 1000);}" // give top priority to UNIX time
            + "else if (y >= 0 && m >= 0 && d > 0 && h >= 0 && i >= 0 && s >= 0 && ms >= 0)\n"
            + "{v = new Date(y, m, d, h, i, s, ms);}\n"
            + "else if (y >= 0 && m >= 0 && d > 0 && h >= 0 && i >= 0 && s >= 0)\n"
            + "{v = new Date(y, m, d, h, i, s);}\n"
            + "else if (y >= 0 && m >= 0 && d > 0 && h >= 0 && i >= 0)\n"
            + "{v = new Date(y, m, d, h, i);}\n"
            + "else if (y >= 0 && m >= 0 && d > 0 && h >= 0)\n"
            + "{v = new Date(y, m, d, h);}\n"
            + "else if (y >= 0 && m >= 0 && d > 0)\n"
            + "{v = new Date(y, m, d);}\n"
            + "else if (y >= 0 && m >= 0)\n"
            + "{v = new Date(y, m);}\n"
            + "else if (y >= 0)\n"
            + "{v = new Date(y);}\n"
            + "}return (v && (z || o))?\n" // favour UTC offset over GMT offset
            + "    (z ? v.add(Date.SECOND, (v.getTimezoneOffset() * 60) + (z*1)) :\n" // reset to UTC, then add offset
            + "        v.add(Date.HOUR, (v.getGMTOffset() / 100) + (o / -100))) : v\n" // reset to GMT, then add offset
            + ";}";

        Date.parseRegexes[regexNum] = new RegExp("^" + regex + "$", "i");
        eval(code);
    };

    Date.parseDateTime = function(str, fmt) {
        var today = new Date();
        var y = 0;
        var m = 0;
        var d = 1;
        //wei : 对于fmt为‘YYYYMM’或者‘YYYYMMdd’的格式，str的值为类似'201111'的形式，因为年月之间没有分隔符，所以正则表达式分割无效，导致bug7376。
        var a = str.split(/\W+/);
        if(fmt.toLowerCase() == '%y%x' || fmt.toLowerCase() == '%y%x%d') {
            var tempStr = a.join('');
            var yearlength = 4;
            var otherlength = 2;
            a[0] = tempStr.substring(0, yearlength);
            a[1] = tempStr.substring(yearlength, yearlength+otherlength);
            a[2] = tempStr.substring(yearlength+otherlength, yearlength+otherlength*2);
        }
        var b = fmt.match(/%./g);
        var i = 0, j = 0;
        var hr = 0;
        var min = 0;
        var sec = 0;
        for (i = 0; i < a.length; ++i) {
            if (!a[i]){
                continue;
            }
            switch (b[i]) {
                case "%d":
                case "%e":
                    d = parseInt(a[i], 10);
                    break;

                case "%X":
                    m = parseInt(a[i], 10) - 1;
                    break;
                case "%x":
                    m = parseInt(a[i], 10) - 1;
                    break;

                case "%Y":
                case "%y":
                    y = parseInt(a[i], 10);
                    (y < 100) && (y += (y > 29) ? 1900 : 2000);
                    break;

                case "%b":
                case "%B":
                    for (j = 0; j < 12; ++j) {
                        if (Date._MN[j].substr(0, a[i].length).toLowerCase() == a[i].toLowerCase()) { m = j; break; }
                    }
                    break;

                case "%H":
                case "%I":
                case "%k":
                case "%l":
                    hr = parseInt(a[i], 10);
                    break;

                case "%P":
                case "%p":
                    if (/pm/i.test(a[i]) && hr < 12){
                        hr += 12;
                    }
                    else if (/am/i.test(a[i]) && hr >= 12){
                        hr -= 12;
                    }
                    break;

                case "%M":
                    min = parseInt(a[i], 10);
                case "%S":
                    sec = parseInt(a[i], 10);
                    break;
            }
        }
        if (isNaN(y)) {
            y = today.getFullYear();
        }
        if (isNaN(m)) {
            m = today.getMonth();
        }
        if (isNaN(d)) {
            d = today.getDate();
        }
        if (isNaN(hr)) {
            hr = today.getHours();
        }
        if (isNaN(min)) {
            min = today.getMinutes();
        }
        if (isNaN(sec)) {
            sec = today.getSeconds();
        }
        if (y !== 0){
            return new Date(y, m, d, hr, min, sec);
        }
        y = 0; m = -1; d = 0;
        for (i = 0; i < a.length; ++i) {
            if (a[i].search(/[a-zA-Z]+/) != -1) {
                var t = -1;
                for (j = 0; j < 12; ++j) {
                    if (Date._MN[j].substr(0, a[i].length).toLowerCase() == a[i].toLowerCase()) { t = j; break; }
                }
                if (t != -1) {
                    if (m != -1) {
                        d = m+1;
                    }
                    m = t;
                }
            } else if (parseInt(a[i], 10) <= 12 && m == -1) {
                m = a[i]-1;
            } else if (parseInt(a[i], 10) > 31 && y === 0) {
                y = parseInt(a[i], 10);
                (y < 100) && (y += (y > 29) ? 1900 : 2000);
            } else if (d === 0) {
                d = a[i];
            }
        }
        if (y === 0){
            y = today.getFullYear();
        }
        if (m !== -1 && d !== 0){
            return new Date(y, m, d, hr, min, sec);
        }
        return today;
    };

    Date.formatCodeToRegex = function (character, currentGroup) {
        switch (character) {
            case "d":
                return {g: 1,
                    c: "d = parseInt(results[" + currentGroup + "], 10);\n",
                    s: "(\\d{2})"}; // day of month with leading zeroes (01 - 31)
            case "D":
                for (var a = [], i = 0; i < 7; a.push(Date._SDN[i]), ++i) {
                }
                ; // get L10n short day names
                return {g: 0,
                    c: null,
                    s: "(?:" + a.join("|") + ")"};
            case "j":
                return {g: 1,
                    c: "d = parseInt(results[" + currentGroup + "], 10);\n",
                    s: "(\\d{1,2})"}; // day of month without leading zeroes (1 - 31)
            case "l":
                return {g: 0,
                    c: null,
                    s: "(?:" + Date.dayNames.join("|") + ")"};
            case "N":
                return {g: 0,
                    c: null,
                    s: "[1-7]"}; // ISO-8601 day number (1 (monday) - 7 (sunday))
            case "S":
                return {g: 0,
                    c: null,
                    s: "(?:st|nd|rd|th)"};
            case "w":
                return {g: 0,
                    c: null,
                    s: "[0-6]"}; // javascript day number (0 (sunday) - 6 (saturday))
            case "z":
                return {g: 0,
                    c: null,
                    s: "(?:\\d{1,3}"}; // day of the year (0 - 364 (365 in leap years))
            case "W":
                return {g: 0,
                    c: null,
                    s: "(?:\\d{2})"}; // ISO-8601 week number (with leading zero)
            case "F":
                return {g: 1,
                    c: "m = parseInt(Date.getMonthNumber(results[" + currentGroup + "]), 10);\n", // get L10n month number
                    s: "(" + Date._MN.join("|") + ")"};
            case "m":
                return {g: 1,
                    c: "m = parseInt(results[" + currentGroup + "], 10) - 1;\n",
                    s: "(\\d{2})"}; // month number with leading zeros (01 - 12)
            case "M":
                for (var a = [], i = 0; i < 12; a.push(Date._SMN[i]), ++i) {
                }
                ; // get L10n short month names
                return {g: 1,
                    c: "m = parseInt(Date.getMonthNumber(results[" + currentGroup + "]), 10);\n", // get L10n month number
                    s: "(" + a.join("|") + ")"};
            case "n":
                return {g: 1,
                    c: "m = parseInt(results[" + currentGroup + "], 10) - 1;\n",
                    s: "(\\d{1,2})"}; // month number without leading zeros (1 - 12)
            case "t":
                return {g: 0,
                    c: null,
                    s: "(?:\\d{2})"}; // no. of days in the month (28 - 31)
            case "L":
                return {g: 0,
                    c: null,
                    s: "(?:1|0)"};
            case "o":
            case "Y":
                return {g: 1,
                    c: "y = parseInt(results[" + currentGroup + "], 10);\n",
                    s: "(\\d{4})"}; // 4-digit year
            case "y":
                return {g: 1,
                    c: "var ty = parseInt(results[" + currentGroup + "], 10);\n"
                        + "y = ty > Date.y2kYear ? 1900 + ty : 2000 + ty;\n",
                    s: "(\\d{1,2})"}; // 2-digit year
            case "a":
                return {g: 1,
                    c: "if (results[" + currentGroup + "] == 'am') {\n"
                        + "if (h == 12) { h = 0; }\n"
                        + "} else { if (h < 12) { h += 12; }}",
                    s: "(am|pm)"};
            case "A":
                return {g: 1,
                    c: "if (results[" + currentGroup + "] == 'AM') {\n"
                        + "if (h == 12) { h = 0; }\n"
                        + "} else { if (h < 12) { h += 12; }}",
                    s: "(AM|PM)"};
            case "g":
            case "G":
                return {g: 1,
                    c: "h = parseInt(results[" + currentGroup + "], 10);\n",
                    s: "(\\d{1,2})"}; // 24-hr format of an hour without leading zeroes (0 - 23)
            case "h":
            case "H":
                return {g: 1,
                    c: "h = parseInt(results[" + currentGroup + "], 10);\n",
                    s: "(\\d{2})"}; //  24-hr format of an hour with leading zeroes (00 - 23)
            case "i":
                return {g: 1,
                    c: "i = parseInt(results[" + currentGroup + "], 10);\n",
                    s: "(\\d{2})"}; // minutes with leading zeros (00 - 59)
            case "s":
                return {g: 1,
                    c: "s = parseInt(results[" + currentGroup + "], 10);\n",
                    s: "(\\d{2})"}; // seconds with leading zeros (00 - 59)
            case "u":
                return {g: 1,
                    c: "ms = parseInt(results[" + currentGroup + "], 10);\n",
                    s: "(\\d{3})"}; // milliseconds with leading zeros (000 - 999)
            case "O":
                return {g: 1,
                    c: [
                        "o = results[", currentGroup, "];\n",
                        "var sn = o.substring(0,1);\n", // get + / - sign
                        "var hr = o.substring(1,3)*1 + Math.floor(o.substring(3,5) / 60);\n", // get hours (performs minutes-to-hour conversion also, just in case)
                        "var mn = o.substring(3,5) % 60;\n", // get minutes
                        "o = ((-12 <= (hr*60 + mn)/60) && ((hr*60 + mn)/60 <= 14))?\n", // -12hrs <= GMT offset <= 14hrs
                        "    (sn + String.leftPad(hr, 2, 0) + String.leftPad(mn, 2, 0)) : null;\n"
                    ].join(""),
                    s: "([+\\-]\\d{4})"}; // GMT offset in hrs and mins
            case "P":
                return {g: 1,
                    c: [
                        "o = results[", currentGroup, "];\n",
                        "var sn = o.substring(0,1);\n", // get + / - sign
                        "var hr = o.substring(1,3)*1 + Math.floor(o.substring(4,6) / 60);\n", // get hours (performs minutes-to-hour conversion also, just in case)
                        "var mn = o.substring(4,6) % 60;\n", // get minutes
                        "o = ((-12 <= (hr*60 + mn)/60) && ((hr*60 + mn)/60 <= 14))?\n", // -12hrs <= GMT offset <= 14hrs
                        "    (sn + String.leftPad(hr, 2, 0) + String.leftPad(mn, 2, 0)) : null;\n"
                    ].join(""),
                    s: "([+\\-]\\d{2}:\\d{2})"}; // GMT offset in hrs and mins (with colon separator)
            case "T":
                return {g: 0,
                    c: null,
                    s: "[A-Z]{1,4}"}; // timezone abbrev. may be between 1 - 4 chars
            case "Z":
                return {g: 1,
                    c: "z = results[" + currentGroup + "] * 1;\n" // -43200 <= UTC offset <= 50400
                        + "z = (-43200 <= z && z <= 50400)? z : null;\n",
                    s: "([+\\-]?\\d{1,5})"}; // leading '+' sign is optional for UTC offset
            case "c":
                var df = Date.formatCodeToRegex, calc = [];
                var arr = [df("Y", 1), df("m", 2), df("d", 3), df("h", 4), df("i", 5), df("s", 6), df("P", 7)];
                for (var i = 0, l = arr.length; i < l; ++i) {
                    calc.push(arr[i].c);
                }
                return {g: 1,
                    c: calc.join(""),
                    s: arr[0].s + "-" + arr[1].s + "-" + arr[2].s + "T" + arr[3].s + ":" + arr[4].s + ":" + arr[5].s + arr[6].s};
            case "U":
                return {g: 1,
                    c: "u = parseInt(results[" + currentGroup + "], 10);\n",
                    s: "(-?\\d+)"}; // leading minus sign indicates seconds before UNIX epoch
            default:
                return {g: 0,
                    c: null,
                    s: character.replace(/([.*+?^$}{()|[\]\/\\])/g, "\\$1")};
        }
    };

    // safari setMonth is broken
    if (FR.Browser.r.safari) {
        Date.brokenSetMonth = Date.prototype.setMonth;
        Date.prototype.setMonth = function (num) {
            if (num <= -1) {
                var n = Math.ceil(-num);
                var back_year = Math.ceil(n / 12);
                var month = (n % 12) ? 12 - n % 12 : 0;
                this.setFullYear(this.getFullYear() - back_year);
                return Date.brokenSetMonth.call(this, month);
            } else {
                return Date.brokenSetMonth.apply(this, arguments);
            }
        };
    }
})();