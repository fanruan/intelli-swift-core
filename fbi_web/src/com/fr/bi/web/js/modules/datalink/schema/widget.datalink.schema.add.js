/**
 * Created by Young's on 2016/3/18.
 */
BI.AddSchemaDataLink = BI.inherit(BI.BarPopoverSection, {

    constants: {
        INPUT_WIDTH: 384,
        INPUT_HEIGHT: 28,
        LINK_NAME_WIDTH: 452
    },

    _defaultConfig: function () {
        return BI.extend(BI.AddSchemaDataLink.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-add-schema-data-link"
        })
    },

    _init: function () {
        BI.AddSchemaDataLink.superclass._init.apply(this, arguments);
        this.model = new BI.AddSchemaDataLinkModel({
            info: this.options.info
        })
    },

    rebuildNorth: function (north) {
        var database = this.model.getDatabase();
        var comment = "";
        if (BI.isNotNull(database)) {
            comment = BI.i18nText("BI-New_Add_Connection") + "(" + database + ")";
        } else {
            database = this.model.getDatabaseByDriver();
            comment = BI.i18nText("BI-Modify_Data_Connection") + "(" + database + ")";
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
            height: this.constants.INPUT_HEIGHT,
            width: this.constants.LINK_NAME_WIDTH,
            hgap: 10,
            cls: "item-input",
            allowBlank: false,
            errorText: BI.i18nText("BI-Illegal_Data_Connection_Name"),
            validationChecker: function (v) {
                var isValid = self.model.checkDataLinkName(v);
                self._refreshButtonsStatus(isValid);
                return isValid;
            }
        });
        this.linkName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.model.setName(this.getValue());
        });
        var linkNameWrapper = BI.createWidget({
            type: "bi.left",
            cls: "bi-wrapper-bottom",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Database_Connection_Name"),
                textAlign: "left",
                cls: "item-label",
                height: 30,
                width: 100
            }, this.linkName],
            vgap: 5
        });

        var driverItems = BICst.DATA_LINK_MANAGE.DRIVERS[this.model.getDatabaseKey()];
        //驱动器
        var driverCombo = BI.createWidget({
            type: "bi.editor_icon_check_combo",
            items: driverItems,
            width: this.constants.INPUT_WIDTH,
            height: this.constants.INPUT_HEIGHT
        });
        var driver = BI.isEmptyString(this.model.getDriver()) ? driverItems[0].value : this.model.getDriver();
        driverCombo.setValue(driver);
        this.model.setDriver(driver);
        driverCombo.on(BI.EditorIconCheckCombo.EVENT_CHANGE, function () {
            schemaCombo.setEnable(false);
            urlInput.setValue(BICst.DATA_LINK_MANAGE.URLS[this.getValue()[0]]);
            self.model.setDriver(this.getValue());
            self.model.setURL(BICst.DATA_LINK_MANAGE.URLS[this.getValue()[0]]);
        });
        var driverWrapper = this._createItemsWrapper(BI.i18nText("BI-First_Step"), BI.i18nText("BI-Driver"), driverCombo);

        //URL
        var urlInput = BI.createWidget({
            type: "bi.sign_editor",
            value: this.model.getURL(),
            height: this.constants.INPUT_HEIGHT,
            width: this.constants.INPUT_WIDTH,
            hgap: 10,
            cls: "item-input",
            allowBlank: false
        });
        var url = BI.isEmptyString(this.model.getURL()) ? BICst.DATA_LINK_MANAGE.URLS[driver] : this.model.getURL();
        urlInput.setValue(url);
        this.model.setURL(url);
        urlInput.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.model.setURL(this.getValue());
            schemaCombo.setEnable(false);
        });
        var urlInputWrapper = this._createItemsWrapper("", "URL", urlInput);

        //用户名
        var userName = BI.createWidget({
            type: "bi.sign_editor",
            value: this.model.getUser(),
            height: this.constants.INPUT_HEIGHT,
            width: this.constants.INPUT_WIDTH,
            hgap: 10,
            cls: "item-input",
            allowBlank: true
        });
        userName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.model.setUser(this.getValue());
            schemaCombo.setEnable(false);
        });
        var userNameWrapper = this._createItemsWrapper("", BI.i18nText("BI-Username"), userName);

        //密码
        var password = BI.createWidget({
            type: "bi.editor",
            value: this.model.getPassword(),
            inputType: "password",
            height: this.constants.INPUT_HEIGHT,
            width: this.constants.INPUT_WIDTH,
            hgap: 10,
            cls: "item-input",
            allowBlank: true
        });
        password.on(BI.Editor.EVENT_CHANGE, function () {
            self.model.setPassword(this.getValue());
            schemaCombo.setEnable(false);
        });
        var passwordWrapper = this._createItemsWrapper("", BI.i18nText("BI-Password"), password);

        //编码
        var codeLabel = BI.createWidget({
            type: "bi.left",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Code_Conversion"),
                textAlign: "left",
                height: this.constants.INPUT_HEIGHT,
                width: this.constants.LINK_NAME_WIDTH,
                cls: "data-link-code"
            }],
            hgap: 100
        });

        //原始编码
        var oldCodeCombo = BI.createWidget({
            type: "bi.text_icon_check_combo",
            items: BICst.DATA_LINK_MANAGE.CODES,
            width: this.constants.INPUT_WIDTH,
            height: this.constants.INPUT_HEIGHT
        });
        oldCodeCombo.on(BI.TextIconCheckCombo.EVENT_CHANGE, function () {
            self.model.setOriginalCharsetName(this.getValue());
            schemaCombo.setEnable(false);
        });
        var oldCode = this._createItemsWrapper("", BI.i18nText("BI-Original_Code"), oldCodeCombo);

        //新编码
        var newCodeCombo = BI.createWidget({
            type: "bi.text_icon_check_combo",
            items: BICst.DATA_LINK_MANAGE.CODES,
            width: this.constants.INPUT_WIDTH,
            height: this.constants.INPUT_HEIGHT
        });
        newCodeCombo.on(BI.TextIconCheckCombo.EVENT_CHANGE, function () {
            self.model.setNewCharsetName(this.getValue());
            schemaCombo.setEnable(false);
        });
        var newCode = this._createItemsWrapper("", BI.i18nText("BI-New_Code"), newCodeCombo);

        this.testButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Test_Connection"),
            title: BI.i18nText("BI-Test_Connection"),
            level: "ignore",
            height: this.constants.INPUT_HEIGHT,
            handler: function () {
                var testLoading = BI.createWidget({
                    type: "bi.test_link_loading_mask",
                    masker: BICst.BODY_ELEMENT,
                    link: self.model.getValue()
                });
                testLoading.on(BI.TestLinkLoadingMask.EVENT_SCHEMA_SUCCESS, function (schemas) {
                    var items = [];
                    BI.each(schemas, function (i, schema) {
                        items.push({text: schema, value: schema});
                    });
                    schemaCombo.setEnable(true);
                    schemaCombo.populate(items);
                })
            }
        });
        var testLinkWrapper = BI.createWidget({
            type: "bi.left",
            cls: "bi-wrapper-top",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Second_Step"),
                textAlign: "left",
                cls: "item-label",
                height: 30,
                width: 170
            }, this.testButton],
            vgap: 5
        });

        var schemaCombo = BI.createWidget({
            type: "bi.text_icon_check_combo",
            items: [],
            width: this.constants.INPUT_WIDTH,
            height: this.constants.INPUT_HEIGHT
        });
        schemaCombo.on(BI.TextIconCheckCombo.EVENT_CHANGE, function () {
            self.model.setSchema(this.getValue()[0]);
        });
        if (BI.isNotEmptyString(this.model.getSchema())) {
            schemaCombo.setEnable(true);
            BI.Utils.getSchemasByLink(self.model.getValue(), function (data) {
                var items = [];
                BI.each(data, function (i, s) {
                    items.push({text: s, value: s});
                });
                schemaCombo.populate(items);
                schemaCombo.setValue(self.model.getSchema());
            });
        } else {
            schemaCombo.setEnable(true);
        }
        var schemaWrapper = this._createItemsWrapper(BI.i18nText("BI-Third_Step"), BI.i18nText("BI-Mode"), schemaCombo);

        BI.createWidget({
            type: "bi.vertical",
            element: center,
            cls: "bi-add-schema-data-link",
            items: [
                linkNameWrapper,
                driverWrapper,
                urlInputWrapper,
                userNameWrapper,
                passwordWrapper,
                codeLabel,
                oldCode,
                newCode,
                testLinkWrapper,
                schemaWrapper
            ],
            hgap: 10
        });
    },

    _createItemsWrapper: function (name1, name2, widget) {
        return {
            type: "bi.left",
            cls: "add-data-link-wrap",
            items: [{
                type: "bi.label",
                text: name1,
                textAlign: "left",
                cls: "item-label",
                height: 30,
                width: 100
            }, {
                type: "bi.label",
                text: name2,
                textAlign: "left",
                cls: "item-label",
                height: 30,
                width: 70
            }, widget],
            vgap: 3
        }
    },

    rebuildSouth: function (south) {
        var self = this;
        this.saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Sure"),
            title: BI.i18nText("BI-Sure"),
            height: 28,
            handler: function () {
                var data = self.model.getValue();
                BI.Utils.saveDataLink({
                    actionType: "update",
                    linkData: data,
                    oldName: self.model.getOldName()
                }, function () {
                    self.fireEvent(BI.AddDataLink.EVENT_SAVE, data);
                    self.close();
                });
            }
        });
        this._refreshButtonsStatus(BI.isNotEmptyString(this.linkName.getValue()));
        BI.createWidget({
            type: "bi.right_vertical_adapt",
            element: south,
            items: [{
                type: "bi.button",
                text: BI.i18nText("BI-Cancel"),
                level: "ignore",
                height: 28,
                handler: function () {
                    self.close();
                }
            }, this.saveButton],
            hgap: 10
        });
    },

    _refreshButtonsStatus: function (isValid) {
        if (isValid === false) {
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
BI.AddSchemaDataLink.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.add_schema_data_link", BI.AddSchemaDataLink);