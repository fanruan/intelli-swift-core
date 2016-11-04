/**
 * Created by GUY on 2015/6/24.
 */
BIShow.Model = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.Model.superclass._defaultConfig.apply(this), {
            popConfig: {}
        });
    },

    _init: function () {
        BIShow.Model.superclass._init.apply(this, arguments);
        var reportName = this.get('reportName') || {};
        Data.SharingPool.put("reportName", reportName.name);
        Data.SharingPool.put("createBy", this.get('createBy'));
        Data.SharingPool.put("sessionID", this.get('sessionID'));
        Data.SharingPool.put("edit", this.get('edit') === '_bi_edit_');
        Data.SharingPool.put("description", this.get('description'));
        Data.SharingPool.put("reg", this.get('reg'));
        Data.SharingPool.put("reportId", this.get('reportId'));
        Data.SharingPool.put("sessionID", this.get('sessionID'));
        Data.SharingPool.put("show", this.get('show') === '_bi_show_');
        Data.SharingPool.put("lockedBy", this.get('lockedBy'));
        Data.SharingPool.put("hideTop", this.get('hideTop'));
        Data.SharingPool.put("plateConfig", this.get('plateConfig'));
        
        this._initSessionBeater();
    },
    _initSessionBeater: function () {
        setInterval(function () {
            BI.requestAsync("fr_bi_dezi", "update_session", {
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
    },
});
