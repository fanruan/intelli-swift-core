/**
 * Created by 小灰灰 on 2016/3/8.
 */
BI.ETLGroupSettingPane = BI.inherit(BI.Widget, {
    _constants: {
        BUTTON_HEIGHT : 30,
        BUTTON_WIDTH : 88,
        BUTTON_LEFT : 65,
        PANE_HEIGHT : 120,
    },

    _defaultConfig: function () {
        var conf = BI.ETLGroupSettingPane.superclass._defaultConfig.apply(this, arguments)
        return BI.extend(conf, {
            baseCls: "bi-etl-filter-group-setting"
        })
    },

    _init: function () {
        BI.ETLGroupSettingPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.storedValue = o.value || [];
        self.button = BI.createWidget({
            type :'bi.button',
            height : self._constants.BUTTON_HEIGHT,
            width : self._constants.BUTTON_WIDTH,
            text : BI.i18nText('BI-Edit') + BI.i18nText('BI-Grouping')
        });
        self.button.on(BI.Button.EVENT_CHANGE,function(){
            var op ={
                type: "bi.etl_filter_group_popup",
                field : o.field_name,
                value : self.storedValue,
                targetText : self._getTargetText()
            }
            op[ETLCst.FIELDS] = o[ETLCst.FIELDS];
            var groupPopOver = BI.createWidget(op);
            groupPopOver.on(BI.PopoverSection.EVENT_CLOSE, function () {
                BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
            })
            groupPopOver.on(BI.ETLFilterGroupPopup.EVENT_CHANGE, function (v) {
                BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)
                self.storedValue = groupPopOver.getValue();
                self.populate();
                self.fireEvent(BI.ETLGroupSettingPane.EVENT_VALUE_CHANGED);
            });
            BI.Popovers.remove("etlGroup");
            var layer = BI.Layers.create(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
            BI.Layers.show(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)
            BI.Popovers.create("etlGroup", groupPopOver, {container: layer}).open("etlGroup");
            groupPopOver.populate();
        });
        self.labels = BI.createWidget({
            type : 'bi.vertical',
            lgap : 10,
            cls : 'detail-view'
        });
        BI.createWidget({
            type : 'bi.vertical',
            element : self.element,
            height : self._constants.PANE_HEIGHT,
            items : [
                    BI.createWidget({
                        type : 'bi.absolute',
                        height : self._constants.BUTTON_HEIGHT,
                        items:[{
                            left : self._constants.BUTTON_LEFT,
                            el : self.button
                        }]
                    }),
                    self.labels
            ]
        })
    },

    _getTargetText : function () {
        var text;
        var nValue = BI.isFunction(this.options.nValueGetter) ? this.options.nValueGetter() : 'N'
        switch (this.options.filterType){
            case  BICst.TARGET_FILTER_NUMBER.TOP_N:
                text = BI.i18nText('BI-ETL_Top_N', nValue);
                break;
            case BICst.TARGET_FILTER_NUMBER.TOP_N :
                text = BI.i18nText('BI-ETL_Bottom_N', nValue);
                break;
            default :
                text = BI.i18nText('BI-Average_Value');
        }
        return BI.i18nText('BI-ETL_Group_Target_Name_In', this.options.field_name) + text;

    },

    populate : function (){
        var self = this;
        self.labels.empty();
        if (BI.isNotNull(self.storedValue) && self.storedValue.length !== 0){
            BI.each(self.storedValue, function(i, item){
                self.labels.addItem(BI.createWidget({
                    type : 'bi.label',
                    textAlign : 'left',
                    height : 25,
                    text : BI.i18nText('BI-ETL_Group_Field_Name_Same', item) + (i === self.storedValue.length - 1 ? BI.i18nText('BI-Relation_In') : '')
                }))
            })
            self.labels.addItem(BI.createWidget({
                type : 'bi.label',
                textAlign : 'left',
                height : 25,
                text : BI.i18nText('BI-De') +  self._getTargetText()
            }))
        } else {
            self.labels.addItem(BI.createWidget({
                type : 'bi.label',
                textAlign : 'center',
                height : 25,
                text : self._getTargetText()
            }))
        }
    },

    getValue: function () {
        return this.storedValue;
    }
});
BI.ETLGroupSettingPane.EVENT_VALUE_CHANGED = 'ETLGroupSettingPane.EVENT_VALUE_CHANGED';
$.shortcut('bi.filter_etl_group_setting', BI.ETLGroupSettingPane);