/**
 * 选择表界面
 * Created by wuk on 16/4/20.
 * @class BI.AuthorityPaneRoleShow
 * @extend BI.Widget
 */
BI.AuthorityPaneRoleShow = BI.inherit(BI.Widget, {


    _defaultConfig: function () {
        return BI.extend(BI.AuthorityPaneRoleShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-table-pane"
        })
    },

    _init: function () {
        BI.AuthorityPaneRoleShow.superclass._init.apply(this, arguments);
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                north: {el: this._createNorth(), height: 40},
                center: this._createCenter(),
                south: {
                    el: this._createSouth(),
                    height: 60
                }
            }
        });
    },
    _createNorth: function () {
        var self = this;
        this.button = BI.createWidget({
            type: "bi.button",
            text: '新增',
            width:80,
            handler: function () {
                self.fireEvent(BI.AuthorityPaneRoleShow.EVENT_CHANGE);
            }
        });
        return this.button;
    },
    _createCenter: function () {
        this.tab = BI.createWidget({
            type: "bi.authority_tabs"
        });
        this.tab.on(BI.AuthorityTabs.EVENT_CHANGE, function () {
        });
        return this.tab;
    },

    _createSouth: function () {
        var self=this;
        this.saveButton = BI.createWidget({
            type: "bi.button",
            level: "common",
            text: BI.i18nText("BI-Save"),
            height: 20,
            handler: function () {
                self.fireEvent(BI.AuthorityPaneRoleShow.EVENT_SAVE);
            }
        });
        this.cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: 20
        });
        return BI.createWidget({
            type: "bi.center",
            items: [{
                type: "bi.left_right_vertical_adapt",
                cls: "bi-data-link-pane-south",
                items: {
                    left: [this.cancelButton],
                    right: [
                        this.saveButton
                    ]
                },
                lhgap: 20,
                rhgap: 20
            }]
        })
    },

    populate: function (items) {
        var self=this;
        self.tab.populate(items)
    }
})
;
BI.AuthorityPaneRoleShow.EVENT_CHANGE = "EVENT_CHANGE";
BI.AuthorityPaneRoleShow.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.authority_pane_role_show", BI.AuthorityPaneRoleShow);

