/**
 * Created by GUY on 2015/6/24.
 */
BIDezi.View = BI.inherit(BI.View, {
    
    _defaultConfig: function () {
        return BI.extend(BIDezi.PaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dezi-view"
        })
    },
    
    _init: function () {
        BIDezi.View.superclass._init.apply(this, arguments);
        this.populate();
    },

    _render: function (vessel) {
        var self = this;
        vessel.css("z-index", 0);
        var subvessel = BI.createWidget();

        var saveAs = BI.createWidget({
            type: "bi.bubble_combo",
            el: {
                type: "bi.icon_text_item",
                invisible: !!this.model.get('hideTop'),
                cls: "toolbar-save-font save-as",
                text: BI.i18nText("BI-Save_As"),
                height: 30,
                width: 70
            },
            popup: {
                el: BI.createWidget({
                    type: "bi.saveas_pupup",
                    width: 340,
                    height: 140,
                    cancel: function () {
                        saveAs.hideView();
                    },
                    confirm: function (data) {
                        saveAs.hideView();
                        BI.requestAsync("fr_bi", "report_save_as", {
                            report_id: self.model.get("reportId"),
                            report_name: data.report_name,
                            create_by: self.model.get("createBy"),
                            report_location: data.report_location,
                            real_time: self.model.get("description")
                        }, function (res, model) {
                            if (BI.isNotNull(res) && BI.isNotNull(res.reportId)) {
                                BI.Msg.toast(BI.i18nText("BI-Save_As_Success"));
                            }
                        });
                    }
                }),
                maxHeight: 142,
                minWidth: 340
            }
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: subvessel,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }, {
                el: saveAs,
                left: 20,
                top: 0
            }]
        });
        this.addSubVessel("pane", subvessel);
    },

    refresh: function () {
        this.skipTo("pane", "pane", "popConfig");
    }
});