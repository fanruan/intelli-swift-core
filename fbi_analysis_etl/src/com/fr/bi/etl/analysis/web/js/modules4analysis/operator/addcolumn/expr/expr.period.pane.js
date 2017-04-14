/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnPeriodPane  = BI.inherit(BI.Widget, {
    _constants: {
        GAP : 10,
        HEIGHT: 30,
        WIDTH : 200,
        DETAIL_WIDTH : 230,
        LIST_HEIGHT : 164,
        FIRST_DETAIL_HEIGHT : 25,
        SECOND_DETAIL_HEIGHT : 60,
        LIST_DOWN_HEIGHT : 138,
        LABEL_WIDTH : 60
    },

    render: function(){
        var self = this;
        this.model = new BI.AnalysisETLOperatorAddColumnExprNumberFieldsModel({});
        var items = [
            {
                el : {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : BI.i18nText('BI-ETL_Add_Column_EXPR_Warning'),
                    textAlign : 'left'
                },
                left : 10,
                top : 15
            }, {
                el : {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : BI.i18nText('BI-Value_From'),
                    textAlign : 'left'
                },
                left : 10,
                top : 45
            },{
                el : {
                    type: "bi.text_value_combo",
                    height : self._constants.HEIGHT,
                    width : self._constants.WIDTH,
                    items : [],
                    ref: function(_ref){
                        self.combo = _ref;
                    },
                    listeners: [{
                        eventName: BI.TextValueCombo.EVENT_CHANGE,
                        action: function(v){
                            self.setValueField(v);
                        }
                    }]
                },
                left : 75,
                top : 40
            },{
                el : {
                    type : 'bi.vertical',
                    cls : 'group-list',
                    width : self._constants.WIDTH,
                    height : self._constants.LIST_HEIGHT,
                    scrolly : false,
                    items : [
                        {
                            type : 'bi.label',
                            text : BI.i18nText('BI-Group_Detail_Setting'),
                            cls : 'first-label label-name',
                            textAlign : 'center',
                            height : self._constants.HEIGHT
                        },
                        {
                            el : {
                                type : 'bi.button_group',
                                height : 133,
                                ref: function(_ref){
                                    self.listContainer = _ref;
                                },
                                layouts: [{
                                    type: "bi.vertical"
                                }]
                            }
                        }
                    ]
                },
                left : 285,
                top : 10
            },{
                el : {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : BI.i18nText('BI-Formula_Time_Field') + BI.i18nText('BI-Basic_Setting'),
                    textAlign : 'left'
                },
                left : 585,
                top : 15
            },{
                el : this._createDetail(),
                left : 785,
                top : 10
            }

        ];
        items = BI.concat(items, this.createUniqItems());
        return {
            type : 'bi.absolute',
            ref: function(_ref){
                self.layout = _ref;
            },
            items : items,
            height : self._constants.HEIGHT
        }
    },

    createUniqItems: function(){
        return [];
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

    _createDetail: function () {
        var self = this;
        return {
            type : 'bi.vertical',
            width : self._constants.DETAIL_WIDTH,
            height : self._constants.LIST_HEIGHT,
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
                    el : {
                        type : 'bi.button_group',
                        cls : 'detail-view',
                        height : self._constants.LIST_DOWN_HEIGHT,
                        ref: function(_ref){
                            self.labels = _ref;
                        },
                        layouts: [{
                            type: "bi.vertical",
                            lgap : self._constants.GAP,
                            tgap : self._constants.GAP
                        }]
                    }
                }
            ]
        };
    },

    //原controller
    _checkCanSave : function () {
        if (BI.isNull(this.model.get('field'))){
            this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Value_From')));
        } else {
            this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    _createLeftItems : function(){
        var group = this.model.get('group') || [];
        return BI.filter(this.model.getNumberFields(), function (i, item) {
            return BI.indexOf(group, item.value) === -1;
        })
    },

    _afterValueSetted : function () {
        this._refreshGroup();
        this._populateLabel();
        this._checkCanSave();
    },

    _refreshCombo : function () {
        this.combo.populate(this._createLeftItems());
        this.combo.setValue(this.model.get('field'));
    },

    _getSelectedFields : function () {
        var fields = [];
        if (BI.isNotNull(this.model.get('field'))){
            fields.push(this.model.get('field'));
        }
        return fields;
    },
    _refreshGroup : function () {
        var items = [];
        var group = this.model.getCopyValue('group')||[];
        var selectedFields = this._getSelectedFields();
        BI.each(selectedFields, function (i ,item) {
            group.remove(item);
        })
        BI.each(group, function (i, item) {
            items.push({
                text : item,
                value : item,
                title : item,
                selected : true
            });
        })
        BI.each(this.model.get(ETLCst.FIELDS), function (i, item) {
            if (BI.indexOf(group, item.value) === -1 && BI.indexOf(selectedFields, item.value) === -1){
                items.push({text : item.text, value : item.value, title : item.text});
            }
        })
        this.refreshGroup(items);
    },

    _refreshComboValue : function (combo, oldValue) {
        if (combo !== this.combo && combo.getValue()[0] === this.model.get('field')){
            this.combo.setValue(oldValue);
            this.model.set('field', oldValue);
        }
    },

    setGroup : function (group) {
        this.model.set('group' , group);
        this._populateLabel();
        this._checkCanSave();
        this._refreshCombo();
    },

    setValueField : function (value) {
        var oldValue = this.model.get('field');
        this.model.set('field', value);
        this._refreshComboValue(this.combo, oldValue)
        this._afterValueSetted();
    },

    _populate: function(){
        var fields = this.model.getNumberFields();
        if (BI.isNull(this.model.get('field')) && BI.isNotEmptyArray(fields)){
            this.model.set('field', fields[0].value)
        }
        this.combo.populate(this._createLeftItems());
        this.combo.setValue(this.model.get('field'));
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