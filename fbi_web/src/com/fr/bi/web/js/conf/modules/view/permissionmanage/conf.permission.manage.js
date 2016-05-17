/**
 * Created by wuk on 16/4/18.
 */
BIConf.PermissionManageView = BI.inherit(BI.View, {

    _defaultConfig: function () {
        return BI.extend(BIConf.PermissionManageView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-permission-manage"
        })
    },

    _init: function () {
        BIConf.PermissionManageView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this._buildUserField(),
                top: -40,
                left: 220
            }, {
                el: this._builtPackageTree(),
                top: 0,
                left: 0,
                bottom: 0
            }, {
                el: this._buildAuthorityPane(),
                top: 0,
                left: 220,
                right: 20,
                bottom: 0
            }]
        })
    },
    change: function (changed) {
        if (changed.isShow){

        }
    },
    load: function () {
        this._refreshLoginInfo();
    },

    local: function () {
        return false;
    },

    refresh: function () {
        this.readData(true);
    },

    _buildUserField: function(){
        var self = this;
        this.selectField = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Please_Select"),
            height: 30,
            cls: "select-field"
        });
        this.selectField.on(BI.TextButton.EVENT_CHANGE, function(){
            var selectTablePane = BI.createWidget({
                type: "bi.select_one_table_pane",
                element: BI.Layers.create(BICst.SELECT_ONE_TABLE_LAYER, BICst.BODY_ELEMENT),
                translations: Data.SharingPool.get("translations")
            });
            BI.Layers.show(BICst.SELECT_ONE_TABLE_LAYER);
            selectTablePane.on(BI.SelectOneTablePane.EVENT_CHANGE, function (tables) {
                if (BI.isNotEmptyArray(tables)) {
                    self._openLoginInfoPane(tables[0]);
                }
            });
        });

        this.loginInfo = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText(),
            height: 30,
            cls: "login-info-text"
        });

        this.setButton = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Setting"),
            height: 30,
            cls: "select-field"
        });
        this.setButton.on(BI.Button.EVENT_CHANGE, function(){
            var loginInfo = BI.Utils.getAuthorityLoginInfo();
            self._openLoginInfoPane(loginInfo.table, loginInfo.field_name);
        });

        return BI.createWidget({
            type: "bi.left",
            cls: "login-info",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Field_Of_Login_Name"),
                height: 30,
                cls: "login-info-text"
            }, this.selectField, this.loginInfo, this.setButton]
        });
    },

    _openLoginInfoPane: function(table, fieldName){
        var self = this;
        BI.Popovers.remove(BICst.LOGIN_INFO_POPOVER);
        var loginPane = BI.createWidget({
            type: "bi.authority_login_info_pane",
            table: table,
            field_name: fieldName
        });
        loginPane.on(BI.AuthorityLoginInfoPane.EVENT_CANCEL, function(){
            BI.Popovers.remove(BICst.LOGIN_INFO_POPOVER);
        });
        loginPane.on(BI.AuthorityLoginInfoPane.EVENT_CLEAR, function(){
            var authoritySettings = Data.SharingPool.get("authority_settings");
            delete authoritySettings.login_info;
            Data.SharingPool.put("authority_settings", authoritySettings);
            self._refreshLoginInfo();
            BI.Popovers.remove(BICst.LOGIN_INFO_POPOVER);
        });
        loginPane.on(BI.AuthorityLoginInfoPane.EVENT_SAVE, function(){
            var authoritySettings = Data.SharingPool.get("authority_settings");
            authoritySettings.login_info = this.getValue();
            Data.SharingPool.put("authority_settings", authoritySettings);
            self._refreshLoginInfo();
            BI.Popovers.remove(BICst.LOGIN_INFO_POPOVER);
        });
        BI.Popovers.create(BICst.LOGIN_INFO_POPOVER, loginPane).open(BICst.LOGIN_INFO_POPOVER);
    },

    _refreshLoginInfo: function(){
        var loginInfo = BI.Utils.getAuthorityLoginInfo();
        if(BI.isNull(loginInfo) || BI.isNull(loginInfo.field_name)) {
            this.selectField.setVisible(true);
            this.loginInfo.setVisible(false);
            this.setButton.setVisible(false);
        } else {
            this.selectField.setVisible(false);
            this.loginInfo.setVisible(true);
            this.setButton.setVisible(true);
            this.loginInfo.setText(loginInfo.table.table_name + "." + loginInfo.field_name);
        }
    },

    _builtPackageTree: function () {
        var self = this;
        this.packageTree = BI.createWidget({
            type: "bi.authority_packages_tree"
        });
        this.packageTree.on(BI.AuthorityPackagesTree.EVENT_TYPE_CHANGE, function () {
            self.model.set("packageIds", this.getValue());
            self.model.set("selectType", this.getSelectType());
            self.authorityTab.setSelect(this.getSelectType());
        });
        this.packageTree.on(BI.AuthorityPackagesTree.EVENT_CHANGE, function(){
             self.authorityTab.setValue(this.getValue());
        });
        return this.packageTree;
    },

    _buildAuthorityPane: function () {
        var self=this;
        this.authorityTab = BI.createWidget({
            type: "bi.tab",
            tab: "",
            direction: "custom",
            cardCreator: function(v){
                switch (v) {
                    case BI.SwitchTree.SelectType.MultiSelect:
                        self.batchSet = BI.createWidget({
                            type: "bi.authority_batch_set_pane"
                        });
                        return self.batchSet;
                    case BI.SwitchTree.SelectType.SingleSelect:
                        self.singleSet = BI.createWidget({
                            type: "bi.authority_single_set_pane"
                        });
                        return self.singleSet;
                }
            }
        });
        this.authorityTab.setSelect(BI.SwitchTree.SelectType.SingleSelect);
        return this.authorityTab;
    }
});
