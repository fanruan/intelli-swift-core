/**
 * Created by GUY on 2015/6/24.
 */
BIDezi.Model = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.Model.superclass._defaultConfig.apply(this), {
            popConfig: {}
        });
    },

    _init: function () {
        BIDezi.Model.superclass._init.apply(this, arguments);
        Data.SharingPool.put("createBy", this.get('createBy'));
        Data.SharingPool.put("sessionID", this.get('sessionID'));
        Data.SharingPool.put("edit", this.get('edit') === '_bi_edit_');
        Data.SharingPool.put("description", this.get('description'));
        Data.SharingPool.put("reg", this.get('reg'));
        Data.SharingPool.put("reportName", this.get('reportName'));
        Data.SharingPool.put("reportId", this.get('reportId'));
        Data.SharingPool.put("sessionID", this.get('sessionID'));
        this._initSessionBeater();
        this.request = BI.debounce(BI.bind(this.update, this), 1000);
    },

    change: function () {
        this.request();
    },

    _initSessionBeater: function () {
        setInterval(function () {
            BI.requestAsync("fr_bi_dezi", "update_session", {
                sessionID: Data.SharingPool.get("sessionID"),
                _t: new Date()
            }, BI.emptyFn);
        }, 30000);
        window.onbeforeunload = function () {
            BI.requestSync("closesessionid", "", {
                sessionID: Data.SharingPool.get("sessionID"),
                _t: new Date()
            })
        };
    },

    updateURL: function () {
        return this.cmd("report_saving");
    }
});