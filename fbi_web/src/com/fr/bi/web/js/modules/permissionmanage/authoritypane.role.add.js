/**
 * Created by wuk on 16/4/28.
 */
/**
 * @class BI.AuthorityPaneRoleAddView
 * @extend BI.Widget
 * 单个数据连接中所有表 tab
 */
BI.AuthorityPaneRoleAddView = BI.inherit(BI.Widget, {


    _defaultConfig: function () {
        return BI.extend(BI.AuthorityPaneRoleAddView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-table-pane"
        })
    },

    _init: function () {
        BI.AuthorityPaneRoleAddView.superclass._init.apply(this, arguments);
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
            type: "bi.default",
            text: ''
        });
        return this.button;
    },
    _createCenter: function () {
        this.tab = BI.createWidget({
            type: "bi.authority_tabs"
        });
        return this.tab;
    },

    _createSouth: function () {
        var self = this;
        this.saveButton = BI.createWidget({
            type: "bi.button",
            level: "common",
            text: '添加',
            height: 20,
            handler: function () {
                self.fireEvent(BI.AuthorityPaneRoleAddView.EVENT_CHANGE, arguments);
            }
        });
        return BI.createWidget({
            type: "bi.center",
            items: [{
                type: "bi.left_right_vertical_adapt",
                cls: "bi-data-link-pane-south",
                items: {
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
        var self = this;
        self.tab.populate(items)
    }
});
BI.AuthorityPaneRoleAddView.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.authority_pane_role_add", BI.AuthorityPaneRoleAddView);
;
