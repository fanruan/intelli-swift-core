/**
 * Created by Young's on 2016/3/18.
 */
BI.AddDataLink = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend(BI.AddDataLink.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-add-data-link"
        })
    },

    _init: function(){
        BI.AddDataLink.superclass._init.apply(this, arguments);
        this.model = new BI.AddDataLinkModel({
            info: this.options.info
        })
    },

    rebuildNorth: function (north) {
        var database = this.model.getDatabase();
        var comment = "";
        if(BI.isNotNull(database)){
            comment = BI.i18nText("BI-New_Add_Connection") + "(" + database + ")";
        } else {
            database = this.model.getDatabaseByDriver();
            comment = BI.i18nText(this.model.isCopy() === true ? "BI-Copy_Data_Link" : "BI-Modify_Data_Connection") + "(" + database + ")";
        }
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: comment,
            textAlign: "left",
            height: 50,
            hgap: 10
        });
        return true;
    },

    rebuildCenter: function (center) {
        var self = this;
        //数据库连接名
        this.linkName = BI.createWidget({
            type: "bi.sign_editor",
            value: this.model.getName(),
            height: 28,
            width: 460,
            hgap: 10,
            cls: "item-input",
            allowBlank: false,
            errorText: BI.i18nText("BI-Illegal_Data_Connection_Name"),
            validationChecker: function(v){
                var isValid = self.model.checkDataLinkName(v);
                self._refreshButtonsStatus(isValid);
                return isValid;
            }
        });
        this.linkName.on(BI.SignEditor.EVENT_CHANGE, function(){
            self.model.setName(this.getValue());
        });
        var linkNameWrapper = this._createItemWrap( BI.i18nText("BI-Database_Connection_Name"), this.linkName);

        var driverItems = BICst.DATA_LINK_MANAGE.DRIVERS[this.model.getDatabaseKey()];
        //驱动器
        var driverCombo = BI.createWidget({
            type: "bi.editor_icon_check_combo",
            items: driverItems,
            width: 460,
            height: 28
        });
        var driver = BI.isEmptyString(self.model.getDriver()) ? driverItems[0].value : self.model.getDriver();
        driverCombo.setValue(driver);
        self.model.setDriver(driver);
        driverCombo.on(BI.EditorIconCheckCombo.EVENT_CHANGE, function(){
            urlInput.setValue(BICst.DATA_LINK_MANAGE.URLS[this.getValue()[0]]);
            self.model.setDriver(this.getValue());
            self.model.setURL(BICst.DATA_LINK_MANAGE.URLS[this.getValue()[0]]);
        });
        var driverWrapper = this._createItemWrap( BI.i18nText("BI-Driver"), driverCombo);

        //URL
        var urlInput = BI.createWidget({
            type: "bi.sign_editor",
            value: this.model.getURL(),
            height: 28,
            width: 460,
            hgap: 10,
            cls: "item-input",
            allowBlank: false
        });
        var url = BI.isEmptyString(this.model.getURL()) ? BICst.DATA_LINK_MANAGE.URLS[driver] : this.model.getURL();
        urlInput.setValue(url);
        this.model.setURL(url);
        urlInput.on(BI.SignEditor.EVENT_CHANGE, function(){
            self.model.setURL(this.getValue());
        });
        var urlInputWrapper = this._createItemWrap("URL", urlInput);

        //用户名
        var userName = BI.createWidget({
            type: "bi.sign_editor",
            value: this.model.getUser(),
            height: 28,
            width: 460,
            hgap: 10,
            cls: "item-input",
            allowBlank: true
        });
        userName.on(BI.SignEditor.EVENT_CHANGE, function(){
            self.model.setUser(this.getValue());
        });
        var userNameWrapper = this._createItemWrap(BI.i18nText("BI-Username"), userName);

        //密码
        var password = BI.createWidget({
            type: "bi.editor",
            value: this.model.getPassword(),
            inputType: "password",
            height: 28,
            width: 460,
            hgap: 10,
            cls: "item-input",
            allowBlank: true
        });
        password.on(BI.Editor.EVENT_CHANGE, function(){
            self.model.setPassword(this.getValue());
        });
        var passwordWrapper = this._createItemWrap(BI.i18nText("BI-Password"), password);

        //编码
        var codeLabel = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Code_Conversion"),
            textAlign: "left",
            height: 28,
            width: "100%",
            cls: "data-link-code"
        });

        //原始编码
        var oldCodeCombo = BI.createWidget({
            type: "bi.text_value_check_combo",
            items: BICst.DATA_LINK_MANAGE.CODES,
            width: 460,
            height: 28
        });
        oldCodeCombo.on(BI.TextValueCheckCombo.EVENT_CHANGE, function(){
            self.model.setOriginalCharsetName(this.getValue());
        });
        var oldCode = this._createItemWrap(BI.i18nText("BI-Original_Code"), oldCodeCombo);

        //新编码
        var newCodeCombo = BI.createWidget({
            type: "bi.text_value_check_combo",
            items: BICst.DATA_LINK_MANAGE.CODES,
            width: 460,
            height: 28
        });
        newCodeCombo.on(BI.TextValueCheckCombo.EVENT_CHANGE, function(){
            self.model.setNewCharsetName(this.getValue());
        });
        var newCode = this._createItemWrap(BI.i18nText("BI-New_Code"), newCodeCombo);
        BI.createWidget({
            type: "bi.vertical",
            element: center,
            cls: "bi-add-data-link",
            items: [
                linkNameWrapper,
                driverWrapper,
                urlInputWrapper,
                userNameWrapper,
                passwordWrapper,
                codeLabel,
                oldCode,
                newCode
            ],
            lgap: 10,
            vgap: 10
        });
    },

    _createItemWrap: function(name, widget){
        return BI.createWidget({
            type: "bi.left",
            cls: "add-data-link-wrap",
            items: [{
                type: "bi.label",
                text: name,
                textAlign: "left",
                cls: "item-label",
                height: 30,
                width: 100
            }, widget]
        })
    },

    rebuildSouth: function(south){
        var self = this;
        this.testButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Test_Connection"),
            title: BI.i18nText("BI-Test_Connection"),
            level: "ignore",
            height: 28,
            handler: function () {
                BI.createWidget({
                    type: "bi.test_link_loading_mask",
                    masker: BICst.BODY_ELEMENT,
                    link: self.model.getValue()
                });
            }
        });
        this.saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Sure"),
            title: BI.i18nText("BI-Sure"),
            height: 28,
            handler: function(){
                var data = self.model.getValue();
                BI.Utils.saveDataLink({actionType: "update", linkData: data, oldName: self.model.getOldName()}, function(){
                    self.fireEvent(BI.AddDataLink.EVENT_SAVE, data);
                    self.close();
                });
            }
        });
        this._refreshButtonsStatus(BI.isNotEmptyString(this.linkName.getValue()));
        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: south,
            items: {
                left: [this.testButton],
                right: [{
                    type: "bi.right",
                    items: [this.saveButton, {
                        type: "bi.button",
                        text: BI.i18nText("BI-Cancel"),
                        level: "ignore",
                        height: 28,
                        handler: function(){
                            self.close();
                        }
                    }],
                    hgap: 10
                }]
            }
        })
    },

    _refreshButtonsStatus: function(isValid){
        if(isValid === false){
            this.saveButton.setEnable(false);
            this.saveButton.setWarningTitle(BI.i18nText("BI-Illegal_Data_Connection_Name"));
            this.testButton.setEnable(false);
            this.testButton.setWarningTitle(BI.i18nText("BI-Illegal_Data_Connection_Name"));
        } else {
            this.saveButton.setEnable(true);
            this.testButton.setEnable(true);
        }
    }
});
BI.AddDataLink.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.add_data_link", BI.AddDataLink);