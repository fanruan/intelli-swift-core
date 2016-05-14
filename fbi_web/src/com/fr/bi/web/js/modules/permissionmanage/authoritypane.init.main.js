/**
 * Created by wuk on 16/4/29.
 * 初始页面,分批量和单独编辑两种情况显示
 */
BI.authorityPaneInitMain = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.authorityPaneInitMain.superclass._defaultConfig.apply(this, arguments), {
            baseCls: '',
            items: []
        })
    },
    _init: function () {
        BI.authorityPaneInitMain.superclass._init.apply(this, arguments);
        this.packageIDs =[];
        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            tab: null,
            defaultShowIndex: BI.authorityPaneInitMain.SelectPane.paneSingleShow,
            cardCreator: BI.bind(this._createTabs, this)
        });
    },
    _createTabs: function (type) {
        alert('初始界面:'+type);
        var self=this;
        switch (type) {
            case BI.authorityPaneInitMain.SelectPane.paneSingleShow:
                this.paneSingleShow = BI.createWidget({
                    type: "bi.authority_pane_single"
                });
                return this.paneSingleShow;
            case BI.authorityPaneInitMain.SelectPane.paneMultiShow:
                this.paneMultiShow = BI.createWidget({
                    type: "bi.authority_pane_multi"
                });
                this.paneMultiShow.on(BI.AuthorityPaneMulti.EVENT_CHANGE, function () {
                    self.fireEvent(BI.authorityPaneInitMain.EVENT_CHANGE);
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
            case BI.authorityPaneInitMain.SelectPane.paneSingleShow:
                this.paneSingleShow.populate();
                break;
            case BI.authorityPaneInitMain.SelectPane.paneMultiShow:
                this.paneMultiShow.populate(this.packageIDs);
                break;
        }
    }

});

BI.authorityPaneInitMain.EVENT_CHANGE = "EVENT_CHANGE";
BI.authorityPaneInitMain.SelectPane = {
    paneSingleShow:BI.Selection.Single,
    paneMultiShow: BI.Selection.Multi
};
$.shortcut('bi.authority_pane_init_main', BI.authorityPaneInitMain)
