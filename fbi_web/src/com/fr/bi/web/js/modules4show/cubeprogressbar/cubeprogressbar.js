/**
 * 实时报表进度条
 *
 * Created by GUY on 2016/5/11.
 * @class BI.CubeProgressBar
 * @extends BI.Widget
 */
BI.CubeProgressBar = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeProgressBar.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-progress-bar"
        });
    },

    _init: function () {
        BI.CubeProgressBar.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.progressbar = BI.createWidget({
            type: "bi.progress_bar",
            width: 600,
            height: 30
        });

        BI.createWidget({
            type: "bi.absolute_center_adapt",
            element: this.element,
            items: [this.progressbar]
        })
    },

    _reqState: function () {
        var self = this;
        BI.requestAsync("fr_bi_dezi", "get_temp_cube_generating_status", {}, function (state) {
            self.progressbar.setValue(state.percent);
            if (state.percent < 100) {
                BI.delay(BI.bind(self._reqState, self), 300);
            } else {
                BI.delay(function () {
                    self.fireEvent(BI.CubeProgressBar.EVENT_COMPLETE);
                }, 1000);
            }
        })
    },

    populate: function () {
        var self = this;
        this._reqState();
    }
});
BI.CubeProgressBar.EVENT_COMPLETE = "CubeProgressBar.EVENT_COMPLETE";
$.shortcut('bi.cube_progress_bar', BI.CubeProgressBar);