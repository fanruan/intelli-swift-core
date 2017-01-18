BIDezi = FR.BIDezi = BI.Dezi = {};
BI.extend(BIDezi, {
    _init: function (options) {
        options["popConfig"] || (options["popConfig"] = {});
        BI.Cache.setUsername(options["createBy"]);

        var AppRouter = BI.Router.extend({
            routes: {
                "": "index"
            },
            index: function () {
                BI.Factory.createView("", BIDezi.Views.get("/"), BI.extend({}, options, BIDezi.Models.get("")), {
                    element: "#wrapper"
                }, null);
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
    }
});