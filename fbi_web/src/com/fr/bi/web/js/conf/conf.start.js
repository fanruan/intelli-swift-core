BIConf = BI.Conf = FR.BIConfigure = {};

BICst.CONF_MAIN_CARD = "main_card";
BICst.CONF_CONNECTION_CARD = "connection_card";
BICst.CONF_PACKAGE_SET = "package_setting";
BICst.CONF_MULTI_PATH_SET = "multi_path_setting";
BICst.CONF_TABLE_SET = "table_setting";

BI.extend(BIConf, {
    _init: function (options) {
        var self = this;
        this.supportSheets = options.supportSheets;
        this.sessionID = options.sessionID;
        Data.SharingPool.put("sessionID", this.sessionID);

        var AppRouter = BI.Router.extend({
            routes: {
                "": "index",
                "/:task": "task",
                "task/:id": "task",
                "*acts": "tasklist"
            },
            index: function () {
                BI.Factory.createView("", BIConf.Views.get(""), BIConf.Models.get(""), {
                    element: "#wrapper"
                }, null);
            },
            tasklist: function (action) {

            },
            task: function (task) {

            }
        });

        this.router = new AppRouter;
        BI.history.start();

        Data.SharingPool.put("urlParameters", (function GetRequest() {
            var url = location.search; //获取url中"?"符后的字串
            var theRequest = {};
            if (url.indexOf("?") != -1) {
                var str = url.substr(1);
                var strs = str.split("&");
                for (var i = 0; i < strs.length; i++) {
                    theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
                }
            }
            return theRequest;
        })());

        this._initSessionBeater();
    },

    _initSessionBeater: function () {
        setInterval(function () {
            BI.requestAsync("fr_bi_configure", "update_session", {
                sessionID: Data.SharingPool.get("sessionID"),
                _t: new Date()
            }, BI.emptyFn);
        }, 30000);
        $(window).unload(function () {
            $(window).unbind('unload');
            FR.ajax({
                async: false,
                url: FR.servletURL,
                data: {
                    op: 'closesessionid',
                    sessionID: Data.SharingPool.get("sessionID"),
                    _t: new Date()
                }
            })
        })
    }
});