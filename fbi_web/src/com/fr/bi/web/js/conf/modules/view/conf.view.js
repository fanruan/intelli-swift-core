/**
 * @class BIConf.View  主面板
 * @extends BI.View
 * @type {*|void|Object}
 */
BIConf.View = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIConf.View.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-conf-view"
        })
    },

    _init: function () {
        BIConf.View.superclass._init.apply(this, arguments);
        //translations relations fields放进sharing pool
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getTranslationsRelationsFields(function (data) {
            mask.destroy();
            Data.SharingPool.put("translations", data.translations);
            Data.SharingPool.put("relations", data.relations);
            Data.SharingPool.put("fields", data.fields);
            Data.SharingPool.put("update_settings", data.update_settings);
        });
        this.populate();
    },

    _createServiceTitlePane: function () {
        return BI.createWidget({
            type: "bi.label",
            cls: "left-nav-title",
            textAlign: BI.HorizontalAlign.Left,
            text: BI.i18nText("BI-Data_Setting"),
            height: 60,
            hgap: 20
        })
    },

    _createServicePane: function () {
        var self = this;
        var pane = BI.createWidget({
            type: "bi.finebi_service"
        });
        pane.on(BI.FineBIService.EVENT_CHANGE, function () {
            self.set("current", this.getValue()[0]);
        });

        return pane;
    },

    _createDisplayTitlePane: function () {
        return BI.createWidget({
            type: "bi.left",
            cls: "right-display-title",
            items: [this.title = BI.createWidget({
                type: "bi.label",
                textAlign: BI.HorizontalAlign.Left,
                height: 60
            })],
            hgap: 20
        });
    },

    _createRightDisplayPane: function () {
        var pane = BI.createWidget({
            type: "bi.layout",
            cls: "right-display-pane"
        });
        this.addSubVessel("rightDisplayPane", pane);
        return pane;
    },

    _render: function (vessel) {
        vessel.css("z-index", 0);
        this.service_title_pane = this._createServiceTitlePane();
        this.service_pane = this._createServicePane();
        this.display_title_pane = this._createDisplayTitlePane();
        this.display_pane = this._createRightDisplayPane();


        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.absolute",
                    items: [{
                        el: {
                            type: "bi.vtape",
                            cls: "conf-left-nav-pane",
                            items: [{
                                el: this.service_title_pane,
                                height: 60
                            }, {
                                el: this.service_pane
                            }]
                        },
                        top: 20,
                        left: 20,
                        right: 10,
                        bottom: 20
                    }]
                },
                width: 270
            }, {
                el: {
                    type: "bi.absolute",
                    items: [{
                        el: {
                            type: "bi.vtape",
                            cls: "conf-right-display-pane",
                            items: [{
                                el: this.display_title_pane,
                                height: 60
                            }, {
                                el: this.display_pane
                            }]
                        },
                        top: 20,
                        right: 20,
                        bottom: 20,
                        left: 10
                    }]
                }
            }]
        });
    },

    refresh: function (value) {
        this.title.setValue(BI.i18nText(value));
        this.service_pane.setValue(value);
        this.skipTo(value, "rightDisplayPane", {}, {}, {force: true});
    }
});