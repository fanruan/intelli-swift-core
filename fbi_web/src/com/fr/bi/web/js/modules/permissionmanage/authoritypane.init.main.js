/**
 * Created by wuk on 16/4/29.
 * 初始页面,分批量和单独编辑两种情况
 */
BI.AuthorityPaneInitMain = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AuthorityPaneInitMain.superclass._defaultConfig.apply(this, arguments), {
            baseCls: '',
            items: []
        })
    },
    _init: function () {
        BI.AuthorityPaneInitMain.superclass._init.apply(this, arguments);
        this.packageIDs =[];
        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            tab: null,
            defaultShowIndex: BI.AuthorityPaneInitMain.SelectPane.paneSingleShow,
            cardCreator: BI.bind(this._createTabs, this)
        });
    },
    _createTabs: function (type) {
        var self=this;
        switch (type) {
            case BI.AuthorityPaneInitMain.SelectPane.paneSingleShow:
                this.paneSingleShow = BI.createWidget({
                    type: "bi.authority_pane_single"
                });
                return this.paneSingleShow;
            case BI.AuthorityPaneInitMain.SelectPane.paneMultiShow:
                this.paneMultiShow = BI.createWidget({
                    type: "bi.authority_pane_multi"
                });
                this.paneMultiShow.on(BI.AuthorityPaneMulti.EVENT_CHANGE, function () {
                    self.fireEvent(BI.AuthorityPaneInitMain.EVENT_CHANGE);
                });
                return this.paneMultiShow;
        }
    },
    populate: function (packageIds,selectType) {
        this.packageIDs=packageIds;
        this._switchPane(selectType);
    },
    /*批量设置的情况下,点击'批量设置'按钮进入权限管理页面*/
    _switchPane: function (v) {
        this.tab.setSelect(v);
        switch (v) {
            case BI.AuthorityPaneInitMain.SelectPane.paneSingleShow:
                this.paneSingleShow.populate();
                break;
            case BI.AuthorityPaneInitMain.SelectPane.paneMultiShow:
                this.paneMultiShow.populate(this.packageIDs);
                break;
        }
    }

});

BI.AuthorityPaneInitMain.EVENT_CHANGE = "EVENT_CHANGE";
BI.AuthorityPaneInitMain.SelectPane = {
    paneSingleShow:0,
    paneMultiShow: 1
};
$.shortcut('bi.authority_pane_init_main', BI.AuthorityPaneInitMain)
