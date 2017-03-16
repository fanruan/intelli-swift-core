/**
 * @class BIConf.AddDataLinkView 新增数据连接的悬浮框
 * @extends BI.BarFloatSection
 * @type {*|void|Object}
 */
BIConf.AddDataLinkView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function(){
        return BI.extend( BIConf.AddDataLinkView.superclass._defaultConfig.apply(this, arguments), {
            btns:[BI.i18nText("BI-Sure"), BI.i18nText("BI-Test_Connection")]
        })
    },

    _init: function () {
        BIConf.AddDataLinkView.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.isNull(this.model.get("url")) ? BI.i18nText("BI-New_Add_Connection"): BI.i18nText("BI-Modify_Data_Connection"),
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
            value: this.model.get("name"),
            height: 28,
            width: 460,
            hgap: 10,
            cls: "item-input",
            allowBlank: false,
            errorText: BI.i18nText("BI-Illegal_Data_Connection_Name"),
            validationChecker: function(v){
                var isValid = self.model.get("checkDataLinkName", v);
                self._refreshButtonsStatus(isValid);
                return isValid;
            }
        });
        var linkName = this._createItemWrap( BI.i18nText("BI-Database_Connection_Name"), this.linkName);

        //数据库
        this.databaseCombo = BI.createWidget({
            type: "bi.text_value_check_combo",
            items: self.model.get("databaseItems"),
            width: 460,
            height: 30
        });
        this.databaseCombo.on(BI.TextValueCombo.EVENT_CHANGE, function(){
            self._onDatabaseChange(self.databaseCombo.getValue()[0]);
        });
        var database = this._createItemWrap( BI.i18nText("BI-Database"), this.databaseCombo);

        //驱动器
        this.driverCombo = BI.createWidget({
            type: "bi.text_value_check_combo",
            items: [],
            width: 460,
            height: 30
        });
        this.driverCombo.on(BI.TextValueCombo.EVENT_CHANGE, function(){
            self._onDriverChange(self.driverCombo.getValue()[0]);
        });
        var driver = this._createItemWrap( BI.i18nText("BI-Driver"), this.driverCombo);

        //URL
        this.urlInput = BI.createWidget({
            type: "bi.sign_editor",
            value: this.model.get("url"),
            height: 30,
            width: 460,
            hgap: 10,
            cls: "item-input",
            allowBlank: false
        });
        var urlInput = this._createItemWrap("URL", this.urlInput);

        //用户名
        this.userName = BI.createWidget({
            type: "bi.sign_editor",
            value: this.model.get("user"),
            height: 30,
            width: 460,
            hgap: 10,
            cls: "item-input",
            allowBlank: true
        });
        var userName = this._createItemWrap(BI.i18nText("BI-Username"), this.userName);

        //密码
        this.password = BI.createWidget({
            type: "bi.editor",
            value: this.model.get("password"),
            inputType: "password",
            height: 28,
            width: 460,
            hgap: 10,
            cls: "item-input",
            allowBlank: true
        });
        var password = this._createItemWrap(BI.i18nText("BI-Password"), this.password);

        //编码
        var codeLabel = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Code_Conversion"),
            textAlign: "left",
            height: 30,
            width: "100%",
            cls: "data-link-code"
        });

        //原始编码
        this.oldCodeCombo = BI.createWidget({
            type: "bi.text_value_check_combo",
            items: self.model.get("codeItems"),
            width: 460,
            height: 30
        });
        var oldCode = this._createItemWrap(BI.i18nText("BI-Original_Code"), this.oldCodeCombo);

        //新编码
        this.newCodeCombo = BI.createWidget({
            type: "bi.text_value_check_combo",
            items: self.model.get("codeItems"),
            width: 460,
            height: 30
        });
        var newCode = this._createItemWrap(BI.i18nText("BI-New_Code"), this.newCodeCombo);
        BI.createWidget({
            type: "bi.vertical",
            element: center,
            cls: "bi-add-data-link-center",
            items: [
                linkName,
                database,
                driver,
                urlInput,
                userName,
                password,
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
                self.model.set("test", true);
            }
        });
        this.saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Sure"),
            title: BI.i18nText("BI-Sure"),
            height: 28,
            handler: function(){
                self.model.set(self._getLinkValue());
                self.notifyParentEnd();
            }
        });
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
                            self.notifyParentEnd();
                        }
                    }],
                    hgap: 10
                }]
            }
        })
    },

    _onDatabaseChange: function (newValue) {
        //重新设置driverCombo的下拉框值,并触发事件
        this.driverCombo.populate(this.model.get("drivers")[newValue]);
        this.driverCombo.setValue(this.model.get("drivers")[newValue][0].value);
        this._onDriverChange(this.driverCombo.getValue()[0])
    },

    _onDriverChange: function (driver) {
        this.urlInput.setValue(this.model.get("urls")[driver]);
    },

    _getLinkValue: function(){
        return {
            name: this.linkName.getValue(),
            driver: this.driverCombo.getValue()[0],
            url: this.urlInput.getValue(),
            user: this.userName.getValue(),
            password: this.password.getValue(),
            originalCharsetName: this.oldCodeCombo.getValue()[0],
            newCharsetName: this.newCodeCombo.getValue()[0]
        }
    },

    _testDataLink: function(){
        BI.createWidget({
            type: "bi.test_link_loading_mask",
            masker: BICst.BODY_ELEMENT,
            link: this._getLinkValue()
        });
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
    },

    local: function(){
        if(this.model.has("test")){
            this.model.get("test");
            this._testDataLink();
            return true;
        }
        return false;
    },

    refresh: function(){
        if(BI.isNotNull(this.model.get("url"))){
            var driver = this.model.get("driver");
            var database = "";
            this.databaseCombo.setValue(database);
            BI.each(this.model.get("drivers"), function(key, drivers){
                BI.any(drivers, function(i, d){
                    return driver == d.value;
                }) && (database = key);
            });
            this.driverCombo.populate(this.model.get("drivers")[database] || []);
            this.driverCombo.setValue(driver);
            this.driverCombo.getValue();
            this.oldCodeCombo.setValue(this.model.get("originalCharsetName"));
            this.newCodeCombo.setValue(this.model.get("newCharsetName"));
        } else {
            this.driverCombo.populate(this.model.get("drivers")["MySQL"]);
            this.databaseCombo.setValue("MySQL");
            this.driverCombo.setValue(this.model.get("drivers")["MySQL"][0].value);
            this.urlInput.setValue(this.model.get("urls")[this.driverCombo.getValue()[0]]);
            this.oldCodeCombo.setValue("");
            this.newCodeCombo.setValue("");
        }
        this._refreshButtonsStatus(BI.isNotNull(this.model.get("name")));
    }
});