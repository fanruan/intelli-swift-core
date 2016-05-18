/**
 * Created by Young's on 2016/5/16.
 */
BI.AuthorityBatchSetPane = BI.inherit(BI.Widget, {

    _constants: {
        SHOW_EMPTY: 1,
        SHOW_PANE: 2
    },
    
    _defaultConfig: function(){
        return BI.extend(BI.AuthorityBatchSetPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-batch-set-pane"
        })
    },

    _init: function(){
        BI.AuthorityBatchSetPane.superclass._init.apply(this, arguments);
        this.packageName = BI.createWidget({
            type: "bi.label",
            text: "",
            height: 30,
            cls: "package-title"
        });

        this.rolesTab = BI.createWidget({
            type: "bi.tab",
            tab: "",
            direction: "custom",
            cardCreator: BI.bind(this._createTab, this)
        });
        this.rolesTab.setSelect(this._constants.SHOW_EMPTY);
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.left",
                    cls: "authority-set-title",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Muti_Auth_Set"),
                        height: 30,
                        cls: "package-title"
                    }, this.packageName],
                    vgap: 5,
                    hgap: 2
                },
                height: 40
            }, {
                el: this.rolesTab,
                height: "fill"
            }],
            hgap: 20
        });
    },

    _createTab: function(v) {
        var self = this;
        switch (v) {
            case this._constants.SHOW_EMPTY:
                this.batchSetButton = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Have_Set_Package_Auth", 0),
                    height: 30,
                    width: 200
                });
                this.batchSetButton.on(BI.Button.EVENT_CHANGE, function(){
                    self.rolesTab.setSelect(self._constants.SHOW_PANE);
                });
                return BI.createWidget({
                    type: "bi.horizontal_auto",
                    items: [this.batchSetButton, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Muti_Package_Auth_Hint"),
                        height: 30,
                        cls: ""
                    }],
                    vgap: 10
                });
            case this._constants.SHOW_PANE:
                this.roles = BI.createWidget({
                    type: "bi.authority_add_role_pane"
                });
                return this.roles;
        }
    },

    setValue: function(v){
        this.packageName.setText(BI.i18nText("BI-N_Packages", v.length));
        this.batchSetButton.setText(BI.i18nText("BI-Have_Set_Package_Auth", v.length));
        this.batchSetButton.setEnable(v.length !== 0);
    },

    populate: function(packageIds, roles){
        this.packageName.setText(BI.i18nText("BI-N_Packages", packageIds.length));
        if(packageIds.length === 0) {
            this.packageName.setEnable(false);
            this.rolesTab.setSelect(this._constants.SHOW_EMPTY);
            return;
        }
        this.packageName.setEnable(true);
        this.roles.setValue(roles);
        this.rolesTab.setSelect(this._constants.SHOW_PANE);
    }
});
$.shortcut("bi.authority_batch_set_pane", BI.AuthorityBatchSetPane);