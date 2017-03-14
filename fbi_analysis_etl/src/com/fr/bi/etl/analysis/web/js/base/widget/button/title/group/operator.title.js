BI.AnalysisOperatorTitle = FR.extend(BI.Widget, {

    _defaultConfig: function() {
        var conf = BI.AnalysisOperatorTitle.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-analysis-etl-operator-title",
            height:40,
            buttonHeight:30,
            warningTitle: BI.i18nText("BI-Current_Editing")
        })
    },

    _init : function() {
        BI.AnalysisOperatorTitle.superclass._init.apply(this, arguments);
        var o = this.options;

        this.operator = BI.createWidget({
            type:"bi.analysis_operator_button",
            height: o.buttonHeight
        })
        var self = this;
        this.operator.on(BI.AnalysisOperatorButton.EVENT_OPERATOR_CHANGE, function(v){
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, arguments)
        })

        var pane = BI.createWidget({
            type: "bi.etl_rename_pane",
            renameChecker : function (v) {
                return !BI.Utils.getAllETLTableNames().contains(v);
            }
        });
        pane.on(BI.ETLNamePane.EVENT_VALID, function(){
            confirmButton.setEnable(true);
        });
        pane.on(BI.ETLNamePane.EVENT_ERROR, function(v){
            if (BI.isEmptyString(v)) {
                confirmButton.setWarningTitle(BI.i18nText("BI-Report_Name_Not_Null"));
            } else {
                confirmButton.setWarningTitle(BI.i18nText("BI-Template_Name_Already_Exist"));
            }
            confirmButton.setEnable(false);
        });
        var confirmButton = BI.createWidget({
            type: "bi.button",
            height: 30,
            value: BI.i18nText(BI.i18nText("BI-Sure")),
            handler: function () {
                self.fireEvent(BI.AnalysisOperatorTitle.EVENT_SAVE, pane.getValue(), pane.getDesc());
                confirmCombo.hideView();
            }
        });

        this.saveButton = BI.createWidget({
            type: "bi.button",
            level: 'ignore',
            height: o.buttonHeight,
            text: BI.i18nText('BI-Save_To_Package'),
            title: BI.i18nText('BI-Save_To_Package')
        });

        var confirmCombo = BI.createWidget({
            type: "bi.bubble_combo",
            cls: "select-bubble-combo",
            el: {
                type: "bi.button",
                level: 'ignore',
                height: o.buttonHeight,
                text: BI.i18nText('BI-Save_To_Package'),
                title: BI.i18nText('BI-Save_To_Package')
            },
            popup: {
                type: "bi.bubble_bar_popup_view",
                buttons: [confirmButton, {
                    value: BI.i18nText("BI-Cancel"),
                    level: "ignore",
                    handler: function () {
                        confirmCombo.hideView();
                    }
                }],
                el: {
                    type: "bi.vertical_adapt",
                    items: [pane],
                    width: 400,
                    height: 300,
                    hgap: 20
                },
                maxHeight: 340,
                minWidth: 400
            }
        })

        confirmCombo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function(){
            self.fireEvent(BI.AnalysisOperatorTitle.CLICK_SAVE);
            pane.populate(BI.Utils.createDistinctName(BI.Utils.getAllETLTableNames(), o.tableName));
        });

        confirmCombo.on(BI.Combo.EVENT_AFTER_POPUPVIEW, function(){
            pane.setTemplateNameFocus();
        })
        BI.createWidget({
            type:"bi.left_right_vertical_adapt",
            element: this.element,
            height: o.height,
            items: {
                left: [{
                    el: this.operator
                }],
                right: [confirmCombo]
            }
        })
    },

    setTableName: function(v){
        this.options.tableName = v;
    },

    getTitleButtons: function(){
        return this.operator.getAllButtons();
    },

    getTitleCombos: function(){
        return this.operator.getAllCombos();
    },

    setEnable : function(v, txt){
        txt = txt || this.options.warningTitle;
        this.operator.setEnable(v, txt)
        this.setSaveButtonEnabled(v, txt)
        this.fireEvent(BI.AnalysisOperatorTitle.EVENT_STATE_CHANGE,v)
    },

    isEnabled : function(){
        return this.operator.isEnabled();
    },

    clearAllSelected : function () {
        this.operator.setValue()
    },

    getValue : function () {
        return this.operator.getValue();
    },

    getElementByValue : function(v) {
        return this.operator.getElementByValue(v)
    },

    setSaveButtonEnabled : function (v, txt) {
        this.saveButton.setEnable(v)
        this.saveButton.setWarningTitle(txt)
    }
})
BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE = "EVENT_OPERATOR_CHANGE";
BI.AnalysisOperatorTitle.EVENT_STATE_CHANGE = "EVENT_STATE_CHANGE";
BI.AnalysisOperatorTitle.EVENT_SAVE = "ANALYSIS_EVENT_SAVE";
BI.AnalysisOperatorTitle.CLICK_SAVE = "CLICK_SAVE";
$.shortcut("bi.analysis_operator_title", BI.AnalysisOperatorTitle)