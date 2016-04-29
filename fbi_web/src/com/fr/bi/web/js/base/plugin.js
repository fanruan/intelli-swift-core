BI.Plugin = BI.Plugin || {};
;
(function () {
    var _WidgetsPlugin = {};
    BI.extend(BI.Plugin, {

        getWidget: function (type, options) {
            if (_WidgetsPlugin[type]) {
                var res;
                for (var i = 0, len = _WidgetsPlugin[type].length; i < len; i++) {
                    if (res = _WidgetsPlugin[type][i](options)) {
                        return res;
                    }
                }
            }
            return options;
        },

        registerWidget: function (type, fn) {
            if (!_WidgetsPlugin[type]) {
                _WidgetsPlugin[type] = [];
            }
            if (_WidgetsPlugin[type].length > 0) {
                console.log("组件已经注册过了!");
            }
            _WidgetsPlugin[type].push(fn);
        },

        relieveWidget: function (type) {
            delete _WidgetsPlugin[type];
        }
    });
})();