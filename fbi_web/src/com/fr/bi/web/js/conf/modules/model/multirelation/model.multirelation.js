/**
 *
 */
BIConf.MultiRelationModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIConf.MultiRelationModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    change: function (changed) {

    },

    local: function () {
        return false;
    },

    load: function (data) {
        this.set({
            relations: data.relations || [],
            disabledRelations: data.disabledRelations || [],
            availableRelations: data.availableRelations || [],
            translations: data.translations || {},
            cubeEnd: data.cubeEnd
        }, {
            silent: true
        })
    },


    readURL: function () {
        return this.cmd("get_multi_path");
    },

    updateURL: function () {
        return this.cmd("update_multi_path");
    },


});