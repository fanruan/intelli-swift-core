/**
 * 选择表界面
 * Created by wuk on 16/4/20.
 * @class BI.AuthorityPaneShow
 * @extend BI.Widget
 */
BI.AuthorityPaneShow = BI.inherit(BI.LoadingPane, {


    _defaultConfig: function () {
        return BI.extend(BI.AuthorityPaneShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-table-pane"
        })
    },

    _init: function () {
        BI.AuthorityPaneShow.superclass._init.apply(this, arguments);
        this.packageIds=[];
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
            text: 'add',
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
        this.tab.on(BI.AuthorityTabs.EVENT_CHANGE, function () {
        });
        return this.tab;
    },

    _createSouth: function () {
        var self = this;
        this.saveButton = BI.createWidget({
            type: "bi.button",
            level: "common",
            text: BI.i18nText("BI-Save"),
            height: 20,
            handler: function () {
                var data=new Array();
                var allRoles=Data.SharingPool.get("allAuthorityInfo")
                BI.each(Data.SharingPool.get("selectedRole"), function(a,item) {
                    for (var i = 0; i < allRoles.length; i++) {
                        if (allRoles[i].text == item) {
                            data.push(allRoles[i].id);
                            break;
                        }
                    }
                });
                BI.Utils.updatePackageAuthority({packageId:Data.SharingPool.get("packageId"),roles:data}, function(){
                    self.fireEvent(BI.AuthorityPaneInitMain.EVENT_SAVE, arguments);
                });

            }
        });
        this.cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: 20
        });
        this.cancelButton.on(BI.Button.EVENT_CHANGE, function () {
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
        this.packageIds=items;
        alert(this.packageIds);
        this.tab.populate(Data.SharingPool.get("selectedRole"));
    }
})
;
BI.AuthorityPaneShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.authority_pane_show", BI.AuthorityPaneShow);

