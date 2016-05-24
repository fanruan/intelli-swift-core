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
                left: 242
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
            self._selectSingleField();
        });

        this.loginField = BI.createWidget({
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
            self._selectSingleField();
        });

        this.clearButton = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Clear"),
            height: 30,
            cls: "select-field"
        });
        this.clearButton.on(BI.TextButton.EVENT_CHANGE, function(){
            self._clearLoginField();
        });

        return BI.createWidget({
            type: "bi.left",
            cls: "login-info",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Field_Of_Login_Name"),
                height: 30,
                cls: "login-info-text"
            }, this.loginField, this.selectField, this.setButton, this.clearButton],
            rgap: 5
        });
    },

    _selectSingleField: function(){
        var self = this, maskId = BI.UUID();
        var mask = BI.Maskers.make(maskId, BICst.BODY_ELEMENT);
        BI.Maskers.show(maskId);
        var selectDataMask = BI.createWidget({
            type: "bi.login_info_select_data_with_mask",
            element: mask
        });
        selectDataMask.on(BI.SelectDataWithMask.EVENT_VALUE_CANCEL, function(){
            BI.Maskers.remove(maskId);
        });
        selectDataMask.on(BI.SelectDataWithMask.EVENT_CHANGE, function(v){
            self._updateLoginField(v.id);
            selectDataMask.destroy();
            BI.Maskers.remove(maskId);
        });
    },
    
    _clearLoginField: function(){
        var authoritySettings = Data.SharingPool.get("authority_settings");
        delete authoritySettings.login_field;
        Data.SharingPool.put("authority_settings", authoritySettings);
        this._refreshLoginInfo();
        BI.Utils.saveLoginField({}, function(){});
    },
    
    _updateLoginField: function(fieldId){
        var authoritySettings = Data.SharingPool.get("authority_settings");
        authoritySettings.login_field = fieldId;
        Data.SharingPool.put("authority_settings", authoritySettings);
        this._refreshLoginInfo();
        BI.Utils.saveLoginField({"login_field": fieldId}, function(){});
    },

    _refreshLoginInfo: function(){
        var loginField = BI.Utils.getAuthorityLoginField();
        var allFields = Data.SharingPool.get("fields");
        if(BI.isNotNull(loginField) && BI.isNotNull(allFields[loginField])) {
            this.loginField.setText(BI.Utils.getTableNameByFieldId4Conf(loginField) + "." + BI.Utils.getFieldNameByFieldId4Conf(loginField));
            this.loginField.setVisible(true);
            this.selectField.setVisible(false);
            this.setButton.setVisible(true);
            this.clearButton.setVisible(true);
        } else {
            this.loginField.setVisible(false);
            this.selectField.setVisible(true);
            this.setButton.setVisible(false);
            this.clearButton.setVisible(false);
        }
    },

    _builtPackageTree: function () {
        var self = this;
        this.packageTree = BI.createWidget({
            type: "bi.authority_packages_tree"
        });
        this.packageTree.on(BI.AuthorityPackagesTree.EVENT_TYPE_CHANGE, function () {
            self._onTreeTypeChange();
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
                        self.batchSet.on(BI.AuthorityBatchSetPane.EVENT_CHANGE, function(){
                            self.packageTree.setSelect(BI.SwitchTree.SelectType.SingleSelect);
                            self._onTreeTypeChange();
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
    },

    _onTreeTypeChange: function(){
        this.authorityTab.setSelect(this.packageTree.getSelectType());
        switch (this.packageTree.getSelectType()) {
            case BI.SwitchTree.SelectType.MultiSelect:
                this.batchSet.setValue();
                break;
            case BI.SwitchTree.SelectType.SingleSelect:
                this.singleSet.setValue();
                break;
        }
    }
});
