BIShow.Models = new (BI.inherit(BI.WRouter, {
    routes: {
        "": "index"
    },

    index: function () {
        return {
            current: ""
        }
    }
}));
