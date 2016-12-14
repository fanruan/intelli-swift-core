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
        this._initSessionBeater();
        this.router = new AppRouter;
        BI.history.start();
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