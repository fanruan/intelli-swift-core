/**
 * Created by 小灰灰 on 2016/3/16.
 */
BI.ETLFilterViewItemFactory = {
    _createItems : function (itemsArray, title){
        var items = [];
        if (BI.isWidget(title)){
            items.push(title);
        } else if (BI.isNotNull(title)){
            items.push(BI.createWidget({
                type : 'bi.label',
                cls : 'title',
                textAlign : 'left',
                text : title,
                title : title
            }));
        }
        BI.each(itemsArray, function(i, item){
            if(BI.isNull(item)) {
                item = ""
            }
            if (BI.isArray(item)){
                var vertical = BI.createWidget({
                    type : 'bi.vertical_adapt'
                });
                BI.each(item, function(i, itm){
                    if (BI.isWidget(itm)){
                        vertical.addItem(itm);
                    } else {
                        vertical.addItem(
                            BI.createWidget({
                                type : 'bi.label',
                                cls : 'content',
                                textAlign : 'left',
                                text : itm,
                                title : itm
                            })
                        )
                    }
                })
                items.push(vertical);
            } else {
                items.push(BI.createWidget({
                    type : 'bi.label',
                    cls : 'content',
                    textAlign : 'left',
                    text : item,
                    title : item
                }))
            }
        })
        return items;
    },

    _createMultiChooseItems : function (value, text){
        var v =[];
        if (BI.isNotNull(value)){
            v = value.type === BI.Selection.All ? value.assist : value.value;
        }
        return this._createItems(v, BI.createWidget({
            type : 'bi.left_right_vertical_adapt',
            items : {
                left: [BI.createWidget({
                    type : 'bi.label',
                    cls : 'title',
                    text : text
                })],
                right: [BI.createWidget({
                    type : 'bi.label',
                    cls : 'title',
                    text : v.length +BI.i18nText("BI-Tiao") + BI.i18nText("BI-Data")
                })]
            }
        }));
    },

    _createDateMultiChooseItems : function (value, text){
        var v =[];
        if (BI.isNotNull(value)){
            BI.each(value.type === BI.Selection.All ? value.assist : value.value, function(i, item){
                if(item === "") {
                    v.push(item)
                } else {
                    var d = FR.str2Date(item, 'yyyy-mm-dd');
                    v.push(d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate())
                }
            });
        }

        return this._createItems(v, BI.createWidget({
            type : 'bi.left_right_vertical_adapt',
            items : {
                left: [BI.createWidget({
                    type : 'bi.label',
                    cls : 'title',
                    text : text
                })],
                right: [BI.createWidget({
                    type : 'bi.label',
                    cls : 'title',
                    text : v.length +BI.i18nText("BI-Tiao") + BI.i18nText("BI-Data")
                })]
            }
        }));
    },


    _createNumberRange : function (value, fieldName){
        value = value || {max : '', min:''};
        return [value.min ,( value.closemin ? this._createItemByCls('less-equal-font') : this._createItemByCls('less-font')) , fieldName , (value.closemax ? this._createItemByCls('less-equal-font') : this._createItemByCls('less-font')) , value.max];
    },

    _createNumberGroupText : function (filterValue){
        if (filterValue.type !== BICst.ETL_FILTER_NUMBER_VALUE.AVG){
            return filterValue.value || '';
        }
        return filterValue.groupType == BICst.ETL_FILTER_NUMBER_AVG_TYPE.ALL ? BI.i18nText("BI-ETL_Number_Avg_All") :  BI.i18nText("BI-ETL_Number_Avg_Inner");
    },

    _createNumberNTitleText : function (filterValue){
        return filterValue.type !== BICst.ETL_FILTER_NUMBER_N_TYPE.INNER_GROUP ?  BI.i18nText("BI-ETL_Number_N_All") + ' :' : BI.i18nText("BI-ETL_Number_N_Inner") + ' :';
    },

    _createDateRange : function (value, fieldName){
        value = value || {};
        return [this._getDateText(value.start) ,this._createItemByCls('less-equal-font') , fieldName , this._createItemByCls('less-equal-font'), this._getDateText(value.end)];
    },

    _getDateText : function(d){
        if (BI.isNotNull(d)){
            var date = new Date(d)
            return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
        }
        return '-';
    },

    _createItemByCls : function (cls){
        return BI.createWidget({
            type : "bi.center_adapt",
            cls : cls + ' bi-filter-show-view-icon',
            items :[{
                type: "bi.icon",
                width : 20,
                height : 20
            }]
        })
    },

    createViewItems : function (value, fieldName, fieldItems){
        var type = value.filter_type, filterValue = value.filter_value;
        switch (type){
            case BICst.FILTER_TYPE.FORMULA :
                return this._createItems([BI.Utils.getTextFromFormulaValue(filterValue, fieldItems)], BI.i18nText("BI-Formula"));
            case BICst.TARGET_FILTER_STRING.BELONG_VALUE :
                return this._createMultiChooseItems(filterValue, BI.i18nText("BI-In"));
            case BICst.TARGET_FILTER_STRING.NOT_BELONG_VALUE :
                return this._createMultiChooseItems(filterValue, BI.i18nText("BI-Not_In"));
            case BICst.TARGET_FILTER_STRING.CONTAIN :
                return this._createItems([filterValue], BI.i18nText("BI-Contain"));
            case BICst.TARGET_FILTER_STRING.NOT_CONTAIN :
                return this._createItems([filterValue], BI.i18nText("BI-Not_Contain"));
            case BICst.TARGET_FILTER_STRING.BEGIN_WITH :
                return this._createItems([filterValue], BI.i18nText("BI-Begin_With"));
            case BICst.TARGET_FILTER_STRING.NOT_BEGIN_WITH :
                return this._createItems([filterValue], BI.i18nText("BI-Not_Begin_With"));
            case BICst.TARGET_FILTER_STRING.END_WITH :
                return this._createItems([filterValue], BI.i18nText("BI-End_With"));
            case BICst.TARGET_FILTER_STRING.NOT_END_WITH :
                return this._createItems([filterValue], BI.i18nText("BI-Not_End_With"));
            case BICst.TARGET_FILTER_NUMBER.CONTAINS :
                return this._createMultiChooseItems(filterValue, BI.i18nText("BI-ETL_Filter_Belongs"));
            case BICst.FILTER_DATE.CONTAINS_DAY:
                return this._createDateMultiChooseItems(filterValue, BI.i18nText("BI-ETL_Filter_Belongs"));
            case BICst.TARGET_FILTER_NUMBER.BELONG_VALUE :
                return this._createItems([this._createNumberRange(filterValue, fieldName)], BI.i18nText("BI-ETL_Number_IN"));
            case BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE :
                return this._createItems([this._createNumberRange(filterValue, fieldName)], BI.i18nText("BI-Not") + BI.i18nText("BI-ETL_Number_IN"));
            case BICst.TARGET_FILTER_NUMBER.EQUAL_TO :
                return this._createItems([fieldName + ' = ' + (filterValue || '')]);
            case  BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO :
                return this._createItems([[fieldName , this._createItemByCls('not-equal-font'), filterValue]]);
            case BICst.TARGET_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE :
                filterValue = filterValue || {};
                return this._createItems([[fieldName , (filterValue.close ? this._createItemByCls('more-equal-font') : this._createItemByCls('more-font')) , this._createNumberGroupText(filterValue)]]);
            case BICst.TARGET_FILTER_NUMBER.SMALL_OR_EQUAL_CAL_LINE :
                filterValue = filterValue || {};
                return this._createItems([[fieldName , (filterValue.close ? this._createItemByCls('less-equal-font') : this._createItemByCls('less-font')) , this._createNumberGroupText(filterValue)]]);
            case  BICst.TARGET_FILTER_NUMBER.TOP_N :
                filterValue = filterValue || {};
                return this._createItems([BI.i18nText("BI-ETL_Top_N", filterValue.value || ' ')], this._createNumberNTitleText(filterValue));
            case  BICst.TARGET_FILTER_NUMBER.BOTTOM_N :
                filterValue = filterValue || {};
                return this._createItems([BI.i18nText("BI-ETL_Bottom_N", filterValue.value || ' ')], this._createNumberNTitleText(filterValue));
            case BICst.FILTER_DATE.BELONG_DATE_RANGE :
                return this._createItems([this._createDateRange(filterValue, fieldName)], BI.i18nText("BI-ETL_Date_In_Range"));
            case BICst.FILTER_DATE.MORE_THAN :
                return this._createItems([[fieldName ,  this._createItemByCls('more-equal-font') , this._getDateText(filterValue)]], BI.i18nText("BI-More_Than") + ' :');
            case BICst.FILTER_DATE.LESS_THAN :
                return this._createItems([[fieldName ,  this._createItemByCls('less-equal-font') , this._getDateText(filterValue)]], BI.i18nText("BI-Less_Than") + ' :');
            case BICst.FILTER_DATE.DAY_EQUAL_TO :
                return this._createItems([fieldName + ' = ' +this._getDateText(filterValue)]);
            case BICst.FILTER_DATE.DAY_NOT_EQUAL_TO :
                return this._createItems([[fieldName , this._createItemByCls('not-equal-font') , this._getDateText(filterValue)]]);
            case BICst.TARGET_FILTER_STRING.IS_NULL :
            case BICst.TARGET_FILTER_NUMBER.IS_NULL :
            case BICst.FILTER_DATE.IS_NULL :
                return this._createItems([BI.i18nText("BI-Is_Null")]);
            case BICst.TARGET_FILTER_STRING.NOT_NULL :
            case BICst.TARGET_FILTER_NUMBER.NOT_NULL :
            case BICst.FILTER_DATE.NOT_NULL :
                return this._createItems([BI.i18nText("BI-Not_Null")]);
            default :
                return[];
        }
    }
}
