/**
 * Created by GUY on 2015/6/24.
 */
BIShow.View = BI.inherit(BI.View, {

    _init: function () {
        BIShow.View.superclass._init.apply(this, arguments);
        var self = this;
        if (BI.Utils.isRealTime()) {
            var popConfig = this.model.cat("popConfig");
            var tableId;
            BI.some(popConfig.widgets, function (wid, widget) {
                return BI.some(widget.dimensions, function (did, dimension) {
                    var fid = dimension._src.field_id;
                    var tid = BI.Utils.getTableIdByFieldID(fid);
                    if (BI.isNotEmptyString(tid)) {
                        tableId = tid;
                        return true;
                    }
                });
            });

            if (BI.isNotEmptyString(tableId)) {
                BI.requestAsync("fr_bi_dezi", "start_generate_temp_cube", {
                    table_id: tableId
                }, function (state) {
                    if (state.percent >= 100) {
                        self._showIndicator();
                        self.populate();
                    } else {
                        self._showProgressBar();
                    }
                });
            } else {
                BI.Msg.alert("没有拖任何字段");
            }
        } else {
            this.populate();
        }
    },

    _showProgressBar: function () {
        var self = this;
        var mask = BI.Maskers.make(this.getName(), "body");
        this.progressbar = BI.createWidget({
            type: "bi.cube_progress_bar",
            element: mask
        });
        this.progressbar.on(BI.CubeProgressBar.EVENT_COMPLETE, function () {
            BI.Maskers.remove(self.getName());
            self.populate();
        });
        BI.Maskers.show(this.getName());
        this.progressbar.populate();
    },

    _showIndicator: function () {
        var self = this;
        this.indicator = BI.createWidget({
            type: "bi.cube_progress_indicator"
        });
        this.indicator.populate();
    },

    _render: function (vessel) {
        vessel.css("z-index", 0);
    },

    refresh: function () {
        this.skipTo("pane", "", "popConfig");
    }
});
