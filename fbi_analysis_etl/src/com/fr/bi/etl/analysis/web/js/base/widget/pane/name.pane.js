/**
 * Created by 小灰灰 on 2016/3/23.
 */
BI.ETLNamePane = BI.inherit(BI.Widget, {
    _constants:{
        NORTH_HEIGHT : 50,
        EDITOR_WIDTH : 258,
        EDITOR_HEIGHT : 28,
        LABEL_WIDTH : 100,
        LABEL_HEIGHT : 30,
        VGAP : 20,
        HGAP : 10
    },

    _defaultConfig: function () {
        var conf = BI.ETLNamePane.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            renameChecker: BI.emptyFn
        })
    },

    _init: function () {
        BI.ETLNamePane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        self.name = BI.createWidget({
            type: "bi.sign_editor",
            width: self._constants.EDITOR_WIDTH,
            height: self._constants.EDITOR_HEIGHT,
            cls: "rename-input",
            allowBlank: false,
            errorText: function (v) {
                if (v === "") {
                    return BI.i18nText("BI-Report_Name_Not_Null");
                } else {
                    return BI.i18nText("BI-Table_Name_Already_Exist");
                }
            },
            validationChecker: function (v) {
                if (v.indexOf("\'") > -1) {
                    return false;
                }
                return o.renameChecker.call(this, v);
            }
        });

        this.name.on(BI.SignEditor.EVENT_ERROR, function (v) {
            self.fireEvent(BI.ETLNamePane.EVENT_ERROR, v);
        });
        this.name.on(BI.SignEditor.EVENT_VALID, function () {
            self.fireEvent(BI.ETLNamePane.EVENT_VALID);
        });

        this.describe = BI.createWidget({
            type:"bi.textarea_editor",
            cls: "rename-input",
            width: self._constants.EDITOR_WIDTH,
            height: self._constants.EDITOR_HEIGHT * 3,
        });

        BI.createWidget({
            type: "bi.vertical",
            cls: "bi-etl-rename-center",
            element: this.element,
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Name") + ' :',
                    height: self._constants.LABEL_HEIGHT,
                    width: self._constants.LABEL_WIDTH,
                    textAlign: "left",
                    cls: "rename-label",
                    hgap: self._constants.HGAP
                }, this.name]
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Basic_Group") + ' :',
                    height: self._constants.LABEL_HEIGHT,
                    width: self._constants.LABEL_WIDTH,
                    textAlign: "left",
                    cls: "rename-label",
                    hgap: self._constants.HGAP
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Basic_MYETL"),
                    height: self._constants.EDITOR_HEIGHT,
                    width: self._constants.EDITOR_WIDTH,
                    textAlign: "left",
                    cls: "rename-label",
                    hgap: self._constants.HGAP
                }]
            },{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Basic_Describe") + ' :',
                    height: self._constants.LABEL_HEIGHT,
                    width: self._constants.LABEL_WIDTH,
                    textAlign: "left",
                    cls: "rename-label",
                    hgap: self._constants.HGAP
                }, this.describe]
            }],
            vgap: self._constants.VGAP
        })
    },

    populate: function (name, des) {
        this.name.setValue(name);
        this.describe.setValue(des)
    },

    setTemplateNameFocus: function () {
        this.name.focus();
    },

    getValue: function(){
        return this.name.getValue();
    },

    getDesc: function(){
        return this.describe.getValue();
    }

});
BI.ETLNamePane.EVENT_CHANGE = "EVENT_CHANGE";
BI.ETLNamePane.EVENT_ERROR = "EVENT_ERROR";
BI.ETLNamePane.EVENT_VALID = "EVENT_VALID";
BI.shortcut("bi.etl_rename_pane", BI.ETLNamePane);