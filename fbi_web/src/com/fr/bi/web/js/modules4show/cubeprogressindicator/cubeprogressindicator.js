/**
 * 实时报表进度条
 *
 * Created by GUY on 2016/5/11.
 * @class BI.CubeProgressIndicator
 * @extends BI.Widget
 */
BI.CubeProgressIndicator = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeProgressIndicator.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-progress-bar"
        });
    },

    _init: function () {
        BI.CubeProgressIndicator.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
    },

    _reqState: function () {
        var self = this;
        BI.requestAsync("fr_bi_dezi", "get_temp_cube_generating_status", {}, function (state) {
            BI.Msg.toast("已完成" + state.percent + "%");
            if (state.percent < 100) {
                BI.nextTick(BI.bind(self._reqState, self), 300);
            } else {
                self.fireEvent(BI.CubeProgressIndicator.EVENT_COMPLETE);
            }
        })
    },

    populate: function () {
        var self = this;
        this._reqState();
    }
});
BI.CubeProgressIndicator.EVENT_COMPLETE = "CubeProgressIndicator.EVENT_COMPLETE";
$.shortcut('bi.cube_progress_indicator', BI.CubeProgressIndicator);