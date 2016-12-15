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
    }
});