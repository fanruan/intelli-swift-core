/**
 * Created by wuk on 16/4/29.
 * 初始页面,分批量和单独编辑两种情况
 */
BI.AuthorityPaneRoleMain = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AuthorityPaneRoleMain.superclass._defaultConfig.apply(this, arguments), {
            baseCls: '',
            items: []
        })
    },
    _init: function () {
        BI.AuthorityPaneRoleMain.superclass._init.apply(this, arguments);
        this.packageIDs =[];
        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            tab: null,
            defaultShowIndex: 0,
            cardCreator: BI.bind(this._createTabs, this)
        });
    },
    _createTabs: function (type) {
        var self=this;
        switch (type) {
            case 0:
                this.roleShow = BI.createWidget({
                    type: "bi.authority_pane_role_show"
                });
                this.roleShow.on(BI.AuthorityPaneRoleAddView.EVENT_CHANGE, function () {
                    self._switchPane(1);
                });
                this.roleShow.on(BI.AuthorityPaneRoleAddView.EVENT_CHANGE, function () {
                    self.fireEvent(BI.AuthorityPaneRoleMain.EVENT_CHANGE);
                });

                return this.roleShow;
            case 1:
                this.roleAdd = BI.createWidget({
                    type: "bi.authority_pane_role_add"
                });
                this.roleAdd.on(BI.AuthorityPaneRoleAddView.EVENT_CHANGE, function () {
                    self._switchPane(0);
                });
                return this.roleAdd;
        }
    },
    populate: function (packageIds) {
    },

    /*批量设置的情况下,点击'批量设置'按钮进入权限管理页面*/
    _switchPane: function (v) {
        this.tab.setSelect(v);
        switch (v) {
            case 0:
                this.roleShow.populate(this.packageIDs);
                break;
            case 1:
                this.roleAdd.populate(this.packageIDs);
                break;
        }
    }

});

BI.AuthorityPaneRoleMain.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.authority_pane_role_main', BI.AuthorityPaneRoleMain)
