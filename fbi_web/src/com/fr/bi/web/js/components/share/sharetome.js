/**
 * Created by Young's on 2016/6/4.
 */
BI.ShareToMe = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ShareToMe.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-share-to-me"
        })
    },

    _init: function () {
        BI.ShareToMe.superclass._init.apply(this, arguments);
        var self = this;
        this.viewSwitcher = BI.createWidget({
            type: "bi.segment",
            cls: "reports-view-switcher",
            items: BI.createItems([{
                cls: "folder-list-view view-button",
                value: BI.ShareToMe.LIST_VIEW
            }, {
                cls: "folder-card-view view-button",
                value: BI.ShareToMe.CARD_VIEW
            }], {
                type: "bi.icon_button",
                width: 25,
                height: 25
            }),
            width: 60,
            height: 25
        });
        this.viewSwitcher.on(BI.Segment.EVENT_CHANGE, function(){
             self._onSwitchView();
        });
        this.viewSwitcher.setValue(BI.ShareToMe.LIST_VIEW);
        this.wrapper = BI.createWidget({
            type: "bi.vertical",
            cls: "all-shared-reports",
            hgap: 10,
            vgap: 10
        });
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.right_vertical_adapt",
                    items: [this.viewSwitcher],
                    hgap: 20
                },
                height: 40
            }, {
                el: this.wrapper
            }]
        })
    },

    _onSwitchView: function(){
        var viewType = this.viewSwitcher.getValue()[0];
        BI.each(this.reportList, function(i, reports){
            reports.populate(viewType);
        });
    },

    populate: function (reports) {
        var self = this;
        var viewType = this.viewSwitcher.getValue()[0];
        this.reportList = [];
        //分用户
        var userMap = {};
        BI.each(reports, function (i, report) {
            BI.isNull(userMap[report.userName]) && (userMap[report.userName] = []);
            userMap[report.userName].push(report);
        });
        var users = BI.keys(userMap);
        users = BI.sortBy(users);
        BI.each(users, function (i, userName) {
            var singleUserReports = BI.createWidget({
                type: "bi.share_to_me_single_user",
                userName: userName,
                reports: userMap[userName]
            });
            singleUserReports.populate(viewType);
            self.reportList.push(singleUserReports);
        });
        this.wrapper.addItems(this.reportList);
        if(this.reportList.length === 0 ){
            this.wrapper.addItem({
                type: "bi.label",
                text: BI.i18nText("BI-No_Selected_Item"),
                cls: "empty-tip"
            });
        }
    }
});
BI.extend(BI.ShareToMe, {
    LIST_VIEW: 1,
    CARD_VIEW: 2
});
$.shortcut("bi.share_to_me", BI.ShareToMe);