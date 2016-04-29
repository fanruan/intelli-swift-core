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
        Data.SharingPool.put("_createby", this.get('createby'));
        Data.SharingPool.put("sessionID", this.get('sessionID'));
        Data.SharingPool.put("edit", this.get('edit') === '_bi_edit_');
        Data.SharingPool.put("description", this.get('description'));
        Data.SharingPool.put("reg", this.get('reg'));
        Data.SharingPool.put("reportName", this.get('reportName'));
        Data.SharingPool.put("reportId", this.get('reportId'));
        Data.SharingPool.put("sessionID", this.get('sessionID'));
    }


});
