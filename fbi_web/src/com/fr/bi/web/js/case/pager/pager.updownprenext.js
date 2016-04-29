/**
 * @class BI.UpDownPreNextPager
 * @extend BI.Widget
 * 汇总表的上下左右四个方向翻页
 */
BI.UpDownPreNextPager = BI.inherit(BI.Widget, {

    constants: {
        ICON_WIDTH: 25,
        ICON_HEIGHT: 25
    },

    _defaultConfig: function(){
        return BI.extend(BI.UpDownPreNextPager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-up-down-next-pre-pager",
            width: 100,
            height: 40
        })
    },

    _init: function(){
        BI.UpDownPreNextPager.superclass._init.apply(this, arguments);
        var self = this;
        this.operatorGroup = BI.createWidget({
            type: "bi.button_group",
            height: this.constants.ICON_HEIGHT
        });
        this.operatorGroup.on(BI.ButtonGroup.EVENT_CHANGE, function(value, obj){
            self.fireEvent(BI.UpDownPreNextPager.EVENT_CHANGE, value, obj);
        });
        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [this.operatorGroup]
        });
    },

    populate: function(page){//翻页按钮
        var groupData = [{
            cls: "column-pre-page-h-font", value: BICst.SUMMARY_TABLE_PAGE_OPERATOR.ROW_PRE, disabled: page[0] === 0
        }, {
            cls: "column-next-page-h-font", value: BICst.SUMMARY_TABLE_PAGE_OPERATOR.ROW_NEXT, disabled: page[1] === 0
        }, {
            cls: "row-pre-page-h-font", value: BICst.SUMMARY_TABLE_PAGE_OPERATOR.COLUMN_PRE, disabled: page[2] === 0
        }, {
            cls: "row-next-page-h-font", value: BICst.SUMMARY_TABLE_PAGE_OPERATOR.COLUMN_NEXT, disabled: page[3] === 0
        }];
        this.operatorGroup.populate(BI.createItems(groupData, {
            type: "bi.icon_button",
            width: this.constants.ICON_WIDTH,
            height: this.constants.ICON_HEIGHT
        }));
    }
});
BI.UpDownPreNextPager.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.up_down_pre_next_pager", BI.UpDownPreNextPager);