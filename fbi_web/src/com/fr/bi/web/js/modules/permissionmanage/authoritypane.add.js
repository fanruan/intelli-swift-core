/**
 * Created by wuk on 16/4/28.
 */
/**
 * @class BI.AuthorityPaneAdd
 * @extend BI.Widget
 * 单个数据连接中所有表 tab
 */
BI.AuthorityPaneAdd = BI.inherit(BI.Widget, {


    _defaultConfig: function () {
        return BI.extend(BI.AuthorityPaneAdd.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-table-pane"
        })
    },

    _init: function () {
        BI.AuthorityPaneAdd.superclass._init.apply(this, arguments);
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
            text: '',
            handler: function () {
                self.fireEvent(BI.AuthorityPaneInitMain.EVENT_CHANGE, arguments);
            }
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
                var selectedRole = self.tab.getValue();
                Data.SharingPool.put("selectedRole", BI.union(Data.SharingPool.get("selectedRole"), selectedRole));
                var unselectedRole = Data.SharingPool.get("unselectedRole");
                var unselectedRoleTemp = [];
                BI.each(unselectedRole, function (i, item) {
                    if (!BI.contains(selectedRole, item)) {
                        unselectedRoleTemp.push(item);
                    }
                })
                Data.SharingPool.put("unselectedRole", unselectedRoleTemp);
                self.fireEvent(BI.AuthorityPaneInitMain.EVENT_CHANGE, arguments);

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

    populate: function () {
        this.tab.populate(Data.SharingPool.get("unselectedRole"));
    }
});
BI.AuthorityPaneAdd.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.authority_pane_add", BI.AuthorityPaneAdd);
;
