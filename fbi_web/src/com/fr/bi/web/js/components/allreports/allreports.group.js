/**
 * Created by Young's on 2016/5/31.
 */
BI.AllReportsGroup = BI.inherit(BI.Widget, {

    _constant: {
        PAGE_SIZE: 30
    },

    _defaultConfig: function () {
        return BI.extend(BI.AllReportsGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-all-reports-group"
        })
    },

    _init: function () {
        BI.AllReportsGroup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.viewType = BI.AllReports.SHOW_LIST;
        this.reportGroup = BI.createWidget({
            type: "bi.button_group",
            layouts: [{
                type: "bi.vertical"
            }]
        });
        this.pager = BI.createWidget({
            type: "bi.pager",
            dynamicShow: false,
            dynamicShowFirstLast: true,
            height: 50,
            pages: o.pages,
            groups: 5,
            curr: 1,
            first: 1,
            last: o.pages,
            prev: {
                type: "bi.icon_button",
                cls: "pre-page-font previous-next-page",
                title: BI.i18nText("BI-Previous_Page"),
                value: "prev",
                once: false,
                height: 23,
                width: 23
            },
            next: {
                type: "bi.icon_button",
                cls: "next-page-font previous-next-page",
                title: BI.i18nText('BI-Next_Page'),
                value: "next",
                once: false,
                height: 23,
                width: 23
            },
            layouts: [{
                type: "bi.horizontal",
                hgap: 3,
                vgap: 10
            }]
        });
        this.pager.on(BI.Pager.EVENT_CHANGE, function () {
            self._populateByPage();
        });

        var viewType = BI.createWidget({
            type: "bi.segment",
            cls: "folder-report-view",
            items: BI.createItems([{
                cls: "folder-list-view folder-view",
                value: BI.AllReports.SHOW_LIST
            }, {
                cls: "folder-card-view folder-view",
                value: BI.AllReports.SHOW_CARD
            }], {
                type: "bi.icon_button",
                width: 25,
                height: 25
            }),
            width: 60,
            height: 25
        });
        viewType.on(BI.Segment.EVENT_CHANGE, function(v){
            self.viewType = v;
            self._onViewTypeChange();
        });
        viewType.setValue(BI.AllReports.SHOW_LIST);

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.reportGroup,
                top: 0,
                left: 0,
                right: 0,
                bottom: 50
            }, {
                el: this.pager,
                right: 30,
                bottom: 0
            }, {
                el: viewType,
                left: 30,
                bottom: 20
            }]
        })
    },

    _onViewTypeChange: function () {
        switch (this.viewType) {
            case BI.AllReports.SHOW_LIST:
                this.reportGroup.attr("layouts", [{
                    type: "bi.vertical"
                }]);
                break;
            case BI.AllReports.SHOW_CARD:
                this.reportGroup.attr("layouts", [{
                    type: "bi.left",
                    hgap: 10,
                    vgap: 10
                }]);
                break;
        }
        this.pager.setValue(1);
        this._populateByPage();
    },

    _populateByPage: function(){
        var self = this;
        var page = this.pager.getCurrentPage();
        var reports = this.reports.slice((page - 1) * this._constant.PAGE_SIZE, page * this._constant.PAGE_SIZE);
        var items = [];
        BI.each(reports, function(i, report){
            items.push({
                type: self.viewType === BI.AllReports.SHOW_LIST ? "bi.all_reports_list_item" : "bi.all_reports_card_item",
                report: report,
                users: self.users,
                roles: self.roles
            })
        });
        this.reportGroup.populate(items);
    },

    populate: function (reports, roles, users) {
        this.reports = reports;
        this.roles = roles;
        this.users = users;
        this.pager.setAllPages(Math.ceil(this.reports.length / this._constant.PAGE_SIZE));
        this.pager.setValue(1);

        this._populateByPage();
        
    }
});
$.shortcut("bi.all_reports_group", BI.AllReportsGroup);