/**
 * Created by 小灰灰 on 2016/3/10.
 */
BI.ETLFilterGroupPopup = BI.inherit(BI.BarPopoverSection, {
    _constants:{
        NORTH_HEIGHT : 50,
        LABEL_HEIGHT : 30,
        REGION_WIDTH : 260,
        REGION_HEIGHT : 360,
        LIST_HEIGHT : 330,
        WEST_LEFT : 20,
        CENTER_LEFT : 300
    },

    _init: function () {
        BI.ETLFilterGroupPopup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        self.fields = o.fields;
        self.storedValue = [];
        BI.each(o.value, function (i ,item) {
            if (BI.find(self.fields, function (i, field) {
                    if (field.field_name === item){
                        return true;
                    }
                })){
                self.storedValue.push(item)
            }
        })
        self.list = BI.createWidget({
            type : 'bi.etl_group_sortable_list',

            height : self._constants.LIST_HEIGHT
        });
        self.list.on(BI.ETLGroupSortableList.EVENT_CHANGE, function(){
            self.storedValue = self.list.getValue();
            self._afterListChanged();
        })
        self.labels = BI.createWidget({
            type : 'bi.vertical',
            cls : 'detail-view',
            lgap : 10,
            height : self._constants.LIST_HEIGHT
        });
    },

    _afterListChanged : function(){
        var self = this, o = this.options;
        self.sure.setEnable(this.storedValue.length !== 0);
        self.labels.clear();
        if (self.storedValue.length !== 0){
            BI.each(self.storedValue, function (i, item) {
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
                text : o.targetText
            }))
        }
    },

    rebuildNorth: function (north) {
        var self = this, o = this.options;
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: o.title || BI.i18nText("BI-Grouping_Setting"),
            textAlign: "left",
            height: self._constants.NORTH_HEIGHT
        });
        return true
    },

    _createList: function () {
        var self = this;
        return BI.createWidget({
            type : 'bi.vertical',
            cls : 'bi-etl-filter-group-pop-list',
            width : self._constants.REGION_WIDTH,
            height : self._constants.REGION_HEIGHT,
            scrolly : false,
            items : [
                {
                    el : BI.createWidget({
                        type : 'bi.label',
                        text : BI.i18nText('BI-Group_Detail_Setting'),
                        cls : 'first-label',
                        textAlign : 'center',
                        height : self._constants.LABEL_HEIGHT
                    })
                },
                {
                    el : self.list
                }
            ]
        });
    },
    _createShowBoard: function () {
        var self = this;
        return BI.createWidget({
            type : 'bi.vertical',
            width : self._constants.REGION_WIDTH,
            height : self._constants.REGION_HEIGHT,
            cls :'bi-etl-filter-group-pop-detail',
            scrolly : false,
            items : [
                {
                    el : BI.createWidget({
                        type : 'bi.label',
                        cls : 'detail-label',
                        text : BI.i18nText('BI-Group_Detail_Short'),
                        textAlign : 'left',
                        height : self._constants.LABEL_HEIGHT
                    })
                },
                {
                    el : self.labels
                }
            ]
        });
    },
    rebuildCenter: function (center) {
        var self = this;
        BI.createWidget({
            type : 'bi.absolute',
            element : center,
            items : [
                {
                    el : self._createList(),
                    left : self._constants.WEST_LEFT
                },
                {
                    el : self._createShowBoard(),
                    left : self._constants.CENTER_LEFT
                }
            ]
        })
    },

    getValue: function () {
        return this.storedValue;
    },

    end: function(){
        this.fireEvent(BI.ETLFilterGroupPopup.EVENT_CHANGE);
    },

    populate: function () {
        var self = this;
        var items = [];
        BI.each(self.storedValue, function (i, item) {
            items.push({
                text : item,
                value : item,
                selected : true
            });
        })
        BI.each(self.fields, function (i, item) {
            if (BI.indexOf(self.storedValue, item.field_name) === -1 && item.field_type === BICst.COLUMN.STRING){
                items.push({text : item.field_name, value : item.field_name});
            }
        })
        self.list.populate(items)
        self._afterListChanged();
    }

});
BI.ETLFilterGroupPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_filter_group_popup", BI.ETLFilterGroupPopup);