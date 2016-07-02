BIShow = FR.BIShow = BI.Show = {};
BI.extend(BIShow, {
    _init: function (options) {
        options["popConfig"] || (options["popConfig"] = {});
        BI.Cache.setUsername(options["createBy"]);

        var AppRouter = BI.Router.extend({
            routes: {
                "": "index"
            },
            index: function () {
                BI.Factory.createView("", BIShow.Views.get("/"), BI.extend({}, options, BIShow.Models.get("")), {
                    element: "#wrapper"
                }, null);
            }
        });
        this.router = new AppRouter;
        BI.history.start();
    }
});

//TODO show界面不做缓存
BI.Cache.setItem = function(){
    
};
