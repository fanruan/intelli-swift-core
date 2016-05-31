/**
 * Created by Young's on 2016/5/30.
 */
BI.AllReports = BI.inherit(BI.Widget, {

    _constant: {
        SHOW_FILTER: 1,
        HIDE_FILTER: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.AllReports.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-all-reports"
        })
    },

    _init: function () {
        BI.AllReports.superclass._init.apply(this, arguments);
        this.hangout = BI.createWidget({
            type: "bi.label",
            text: "",
            height: 40
        });
        this.filterPane = BI.createWidget({
            type: "bi.all_reports_filter"
        });
        this.wrapper = BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: BI.createWidget({
                    type: "bi.left",
                    cls: "hangout-count",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Current_Apply_For_Hangout"),
                        height: 40
                    }, this.hangout]
                }),
                height: 40
            }, {
                el: this._createPushBar(),
                height: 40
            }, {
                el: this.filterPane,
                height: 92
            }, {
                el: BI.createWidget(),
                height: 20
            }, {
                el: this._createReports()
            }, {
                el: BI.createWidget(),
                height: 20
            }],
            hgap: 20
        })
    },

    _createPushBar: function () {
        var self = this;
        this.reportsCount = BI.createWidget({
            type: "bi.label",
            cls: "",
            text: ""
        });
        var pushButton = BI.createWidget({
            type: "bi.icon_change_button",
            cls: "push-button",
            width: 16,
            height: 16
        });
        pushButton.setIcon("report-filter-close-font");
        pushButton.on(BI.IconChangeButton.EVENT_CHANGE, function () {
            var filterShow = self.filterPane.isVisible();
            this.setIcon(filterShow ? "report-filter-open-font" : "report-filter-close-font");
            self.filterPane.setVisible(!filterShow);
            self.wrapper.attr("items")[2].height = filterShow ? 0 : 90;
            self.wrapper.resize();
        });
        return BI.createWidget({
            type: "bi.center_adapt",
            cls: "push-bar",
            items: [{
                type: "bi.left_right_vertical_adapt",
                items: {
                    left: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Filter_Report"),
                        cls: "filter-report-label",
                        height: 40,
                        width: 70
                    }, this.reportsCount],
                    right: [pushButton]
                },
                width: 130,
                height: 40
            }],
            height: 40
        });
    },

    _createReports: function () {
        return BI.createWidget({
            type: "bi.left",
            cls: "all-reports",
            items: []
        });
    },

    populate: function () {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getAllReportsData(function (data) {
            var departs = data.departs, roles = data.roles, users = data.users, reports = data.reports;
            self.filterPane.populate(departs, roles, users);
            mask.destroy();
        });
    }
});
$.shortcut("bi.all_reports", BI.AllReports);