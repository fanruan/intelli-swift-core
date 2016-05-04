/**
 * Created by 小灰灰 on 2016/3/4.
 */
BI.ETLNumberFilterOneSidePane = BI.inherit(BI.Widget, {
    _constants: {
        HEIGHT: 30,
        WIDTH: 30,
        BORDER : 1,
        GAP : 5
    },

    _defaultConfig: function () {
        var conf = BI.ETLNumberFilterOneSidePane.superclass._defaultConfig.apply(this, arguments)
        return BI.extend(conf, {
            baseCls: "bi-etl-filter-number-oneside",
            defaultValue : {type : BICst.ETL_FILTER_NUMBER_VALUE.SETTED, close : 0}
        })
    },

    _init: function () {
        BI.ETLNumberFilterOneSidePane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        self.storedValue = o.defaultValue;
        self.segment = BI.createWidget({
            type : 'bi.segment',
            items : BICst.ETL_FILTER_NUMBER_SEGMENT
        })
        self.segment.on(BI.Segment.EVENT_CHANGE, function () {
            self.storedValue.type = self.segment.getValue()[0];
            self.populate();
            self.fireEvent(BI.ETLNumberFilterOneSidePane.EVENT_CONFIRM);
        })
        self.editorPane = BI.createWidget({
            type : 'bi.absolute'
        })
        self.smallCombo = BI.createWidget({
            cls: "numerical-interval-small-combo",
            height: self._constants.HEIGHT - self._constants.BORDER * 2,
            type: "bi.icon_combo",
            items: o.filter_type === BICst.TARGET_FILTER_NUMBER.SMALL_OR_EQUAL_CAL_LINE ? [{
                text: "(" + BI.i18nText("BI-Less_Than") + ")",
                iconClass: "less-arrow-font",
                value: 0
            }, {
                text: "(" + BI.i18nText("BI-Less_And_Equal") + ")",
                value: 1,
                iconClass: "less-equal-arrow-font"
            }] : [{
                text: "(" + BI.i18nText("BI-More_Than") + ")",
                iconClass: "more-arrow-font",
                value: 0
            }, {
                text: "(" + BI.i18nText("BI-More_Than_And_Equal") + ")",
                value: 1,
                iconClass: "more-equal-arrow-font"
            }]
        });
        self.smallCombo.on(BI.IconCombo.EVENT_CHANGE, function () {
            self.storedValue.close = self.smallCombo.getValue()[0];
            self.fireEvent(BI.ETLNumberFilterOneSidePane.EVENT_CONFIRM);
        })
        self.groupContainer = BI.createWidget({
            type : 'bi.vertical'
        });
        BI.createWidget({
            type : 'bi.vertical',
            element : self.element,
            bgap : self._constants.GAP,
            items :[
                        {
                            type : 'bi.htape',
                            items : [
                                        {
                                            type : 'bi.label',
                                            text : BI.i18nText('BI-Get_Value'),
                                            width : self._constants.WIDTH,
                                            textAlign : 'left'
                                        },
                                        {
                                            el : self.segment,
                                            width : 'fill'
                                        }
                                    ],
                            height : self._constants.HEIGHT
                        },
                        {
                            type : 'bi.htape',
                            items : [
                                        {
                                            type : 'bi.label',
                                            width : self._constants.WIDTH,
                                            text : BI.i18nText('BI-Value'),
                                            textAlign : 'left'
                                        },
                                        {
                                            el : self.smallCombo,
                                            width : self._constants.WIDTH
                                        },
                                        {
                                            el : self.editorPane,
                                            width : 'fill'
                                        }
                            ],
                            height : self._constants.HEIGHT
                        },
                        {
                            el : self.groupContainer
                        }
                    ]
        });
    },

    populate : function (){
        var self = this, o = this.options;
        self.segment.setValue(self.storedValue.type);
        self.smallCombo.setValue(self.storedValue.close);
        self.editorPane.empty();
        if (self.storedValue.type == BICst.ETL_FILTER_NUMBER_VALUE.SETTED){
            self.editor = BI.createWidget({
                type: "bi.editor",
                allowBlank: true,
                level: "warning",
                tipType: "warning",
                validationChecker: function (v) {
                    return BI.isNumeric(v)
                }
            });
            self.editor.on(BI.TextEditor.EVENT_CONFIRM, function () {
                self.storedValue.value = self.editor.getValue();
                self.fireEvent(BI.ETLNumberFilterOneSidePane.EVENT_CONFIRM);
            })
            if (!BI.isNumeric(self.storedValue.value)){
                delete self.storedValue.value;
            }
            self.editor.setValue(self.storedValue.value);
            self.editorPane.addItem({
                el :self.editor,
                left : 0,
                right : 0,
                bottom : 0,
                top : 0,
            });
        } else {
            if (!BI.isObject(self.storedValue.value)){
                self.storedValue.value = {type : BICst.ETL_FILTER_NUMBER_AVG_TYPE.ALL};
            }
            self.editor = BI.createWidget({
                type: "bi.text_icon_combo",
                width:'',
                height:self._constants.HEIGHT,
                items :BICst.ETL_FILTER_NUMBER_AVG_ITEMS,
                left : self._constants.GAP,
                right : 0,
                bottom : 0,
                top : 0,
            });
            self.editorPane.addItem({
                el :self.editor,
                left : self._constants.GAP,
                right : 0,
                bottom : 0,
                top : 0,
            });
            self.editor.on(BI.TextIconCombo.EVENT_CHANGE, function(v){
                self.storedValue.value.type = v;
                self.populateGroupContainer();
                self.fireEvent(BI.ETLNumberFilterOneSidePane.EVENT_CONFIRM);
            });
            self.editor.setValue(self.storedValue.value.type)
        }
        self.populateGroupContainer();
    },
    populateGroupContainer: function () {
        var self = this, o = this.options;
        self.groupContainer.empty();
        if (self.editor.getValue() == BICst.ETL_FILTER_NUMBER_AVG_TYPE.INNER_GROUP){
            self.group = BI.createWidget({
                type :'bi.filter_etl_group_setting',
                fields : o.fields,
                field_name : o.field_name,
                filterType : o.filterType,
                value : self.storedValue.value.group
            })
            self.group.on(BI.ETLGroupSettingPane.EVENT_VALUE_CHANGED, function () {
                self.storedValue.value.group = self.group.getValue();
                self.fireEvent(BI.ETLNumberFilterOneSidePane.EVENT_CONFIRM);
            })
            self.group.populate();
            self.groupContainer.addItem(self.group);
        }
    },

    setValue : function (v) {
        this.storedValue = v || BI.deepClone(this.options.defaultValue);
    },

    getValue: function () {
        return this.storedValue;
    }
});
BI.ETLNumberFilterOneSidePane.EVENT_CONFIRM = 'ETLNumberFilterOneSidePane.EVENT_CONFIRM';
$.shortcut('bi.filter_number_oneside_etl', BI.ETLNumberFilterOneSidePane);