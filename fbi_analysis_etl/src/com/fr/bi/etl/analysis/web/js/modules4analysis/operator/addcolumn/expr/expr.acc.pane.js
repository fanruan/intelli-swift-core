/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumnAccPane  = BI.inherit(BI.Widget, {
    _constants: {
        TGAP : 20,
        LGAP : 10,
        HEIGHT: 30,
        WIDTH : 200,
        COMBO_PANE_WIDTH : 270,
        PANE_HEIGHT : 200,
        GROUP_HEIGHT : 158,
        DETAIL_WIDTH : 230,
        LIST_HEIGHT : 144,
        LIST_DOWN_HEIGHT : 128
    },

    render: function(){
        var self = this;
        this.model = new BI.AnalysisETLOperatorAddColumnExprNumberFieldsModel({});
        this.fieldCombo = this.listContainer = this.labels = this.rule = this.groupPane = null;
        return {
            type : 'bi.horizontal',
            tgap : self._constants.TGAP,
            lgap : self._constants.LGAP,
            items : [this._createComboPane(), {
                type : 'bi.absolute',
                width : self._constants.WIDTH,
                height : self._constants.PANE_HEIGHT,
                items : [{el : BI.createWidget({
                        type : 'bi.vertical',
                        cls : 'group-list',
                        width : self._constants.WIDTH,
                        height : self._constants.GROUP_HEIGHT,
                        scrolly : false,
                        items : [
                            {
                                el : BI.createWidget({
                                    type : 'bi.label',
                                    text : BI.i18nText('BI-Group_Detail_Setting'),
                                    cls : 'first-label label-name',
                                    textAlign : 'center',
                                    height : self._constants.HEIGHT
                                })
                            },
                            {
                                el : {
                                    type : 'bi.button_group',
                                    height : 127,
                                    ref: function(_ref){
                                        self.listContainer = _ref;
                                    },
                                    layouts: [{
                                        type: "bi.vertical"
                                    }]
                                }
                            }
                        ]
                    }

                )}],
                ref: function(_ref){
                    self.groupPane = _ref;
                }
            }, {
                type : 'bi.vertical',
                width : self._constants.DETAIL_WIDTH,
                height : self._constants.PANE_HEIGHT,
                cls :'group-detail',
                scrolly : false,
                items : [
                    {
                        type : 'bi.label',
                        cls : 'label-name',
                        text : BI.i18nText('BI-Group_Detail_Short'),
                        textAlign : 'left',
                        height : self._constants.HEIGHT
                    },
                    {
                        type : 'bi.button_group',
                        cls : 'detail-view',
                        height : self._constants.LIST_DOWN_HEIGHT,
                        ref: function(_ref){
                            self.labels = _ref;
                        },
                        layouts: [{
                            type: "bi.vertical",
                            lgap : self._constants.LGAP
                        }]
                    }
                ]
            }]
        }
    },

    _createComboPane : function () {
        var self = this;
        return {
            type : 'bi.absolute',
            width : self._constants.COMBO_PANE_WIDTH,
            height : self._constants.PANE_HEIGHT,
            items : [{el : {
                type : 'bi.label',
                cls : 'label-name',
                text : BI.i18nText('BI-Value_From'),
                textAlign : 'left'
            },
                left : 0,
                top : 5
            },{el : {
                type : 'bi.label',
                cls : 'label-name',
                text : BI.i18nText('BI-Value_Principle'),
                textAlign : 'left'
            },
                left : 0,
                top : 45
            },{
                el : {
                    type: "bi.text_value_combo",
                    height : self._constants.HEIGHT,
                    width : self._constants.WIDTH,
                    items : [],
                    ref: function(_ref){
                        self.fieldCombo = _ref;
                    },
                    listeners: [{
                        eventName: BI.TextValueCombo.EVENT_CHANGE,
                        action: function(v){
                            self.setValueField(v);
                        }
                    }]
                },
                left : 70,
                top : 0
            },{
                el : {
                    type: "bi.text_value_combo",
                    height : self._constants.HEIGHT,
                    width : self._constants.WIDTH,
                    items : [],
                    ref: function(_ref){
                        self.rule = _ref;
                    },
                    listeners: [{
                        eventName: BI.TextValueCombo.EVENT_CHANGE,
                        action: function(v){
                            self.setRule(v);
                        }
                    }]
                },
                left : 70,
                top : 40
            }
            ]
        };
    },

    refreshGroup : function(items){
        var self = this;
        var list = BI.createWidget({
            type : 'bi.etl_group_sortable_list',
            items : items
        });
        list.on(BI.ETLGroupSortableList.EVENT_CHANGE, function(){
            self.setGroup(list.getValue())
        })
        self.listContainer.populate([list]);
    },

    //原controller
    _checkCanSave : function () {
        if (BI.isNull(this.model.get('field'))){
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Value_From')));
        } else  if (BI.isNull(this.model.get('rule'))){
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Value_Principle')));
        } else {
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    _afterValueSetted : function () {
        this._checkRule();
        this._checkGroup();
        this._refreshLabel();
        this._checkCanSave();
    },

    _checkRule : function () {
        this.rule.setEnable(BI.isNotNull(this.model.get('field'))) ;
    },

    _isGroupWillShow: function () {
        return this.model.get('rule') === BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP;
    },

    _refreshLabel : function () {
        var self = this;
        var group = this.model.get('group') || [];
        var items = [];
        BI.each(group, function(i, item){
            var text = BI.i18nText('BI-Basic_Same') + BI.i18nText('BI-Brackets_Value', item) + (i === group.length - 1 ? BI.i18nText('BI-Relation_In') : '');
            items.push({
                type : 'bi.label',
                textAlign : 'left',
                height : 25,
                text : text,
                title : text
            });
        })
        var text = this._getLabelLastText();
        items.push({
            type : 'bi.label',
            textAlign : 'left',
            whiteSpace : 'normal',
            text : text,
            title : text
        });
        this.labels.populate(items);
    },

    _getLabelLastText : function () {
        return BI.i18nText(this._isGroupWillShow() ? 'BI-Calculate_Target_Sum_Above_Group_Get_Sum' : 'BI-Calculate_Target_Sum_Above_Logic', this.model.get('field'))
    },

    _checkGroup : function () {
        if (this._isGroupWillShow()){
            var items = [];
            var group = this.model.getCopyValue('group')||[];
            var field = this.model.get('field');
            group.remove(field);
            BI.each(group, function (i, item) {
                items.push({
                    text : item,
                    value : item,
                    title : item,
                    selected : true
                });
            })
            BI.each(this.model.get(ETLCst.FIELDS), function (i, item) {
                if (BI.indexOf(group, item.value) === -1 && item.value !== field){
                    items.push({text : item.text, value : item.value, title : item.text});
                }
            })
            this.refreshGroup(items);
            this.groupPane.setVisible(true);
        } else {
            this.groupPane.setVisible(false)
            this.model.unset('group');
        }
    },

    setGroup : function (group) {
        this.model.set('group' , group);
        this._refreshLabel();
        this._checkCanSave();
    },

    setValueField : function (value) {
        this.model.set('field', value);
        this._afterValueSetted();
    },

    setRule : function (value) {
        this.model.set('rule', value);
        this._afterValueSetted();
    },

    _populate: function(){
        var fields = this.model.getNumberFields();
        this.fieldCombo.populate(fields);
        if (BI.isNull(this.model.get('field')) && BI.isNotEmptyArray(fields)){
            this.model.set('field', fields[0].value)
        }
        this.fieldCombo.setValue(this.model.get('field'));
        this.rule.populate([{
            text: BI.i18nText("BI-Cumulative_Value"),
            value: BICst.TARGET_TYPE.SUM_OF_ABOVE
        }, {
            text: BI.i18nText("BI-Cumulative_Value_In_Group"),
            value: BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP
        }]);
        this.model.set('rule', this.model.get('rule') ||BICst.TARGET_TYPE.SUM_OF_ABOVE);
        this.rule.setValue(this.model.get('rule'));
        this._afterValueSetted();
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    update: function(){
        return this.model.update();
    }
});
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.EXPR_ACC, BI.AnalysisETLOperatorAddColumnAccPane);