BIDezi = FR.BIDezi = BI.Dezi = {};
BI.extend(BIDezi, {
    _init: function (options) {
        options["popConfig"] || (options["popConfig"] = {});
        BI.Cache.setUsername(options["_createby"]);

        var AppRouter = BI.Router.extend({
            routes: {
                "": "index"
            },
            index: function () {
                BI.Factory.createView("", BIDezi.Views.get("/"), BI.extend({}, options, BIDezi.Models.get("")), {
                    element: "body"
                }, null);
            }
        });
        this.router = new AppRouter;
        BI.history.start();
    }
});