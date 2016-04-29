/**
 * Created by GUY on 2015/6/26.
 * @class BI.HistoryButtonGroup
 * @extends BI.ButtonGroup
 */

BI.HistoryButtonGroup = BI.inherit(BI.ButtonGroup, {
    _defaultConfig: function () {
        return BI.extend(BI.HistoryButtonGroup.superclass._defaultConfig.apply(this, arguments), {
            allHistory : false
        })
    },

    _init: function () {
        BI.HistoryButtonGroup.superclass._init.apply(this, arguments);
        var o = this.options;
        if(o.allHistory === true) {
            this.allHistoryId = BI.UUID();
            this.addItems([{
                type:"bi.all_history_button",
                value:this.allHistoryId
            }]);
        }
    },

    getAllHistoryId: function () {
        return this.allHistoryId;
    },

    addItemFromIndex: function (item, index) {
        var o = this.options;
        var btns = this._btnsCreator([item]);
         // 有全部历史的时候需要加上全部按钮的index
        if(o.allHistory === true) {
            index++;
        }
        var fn = "insertAfter";
        var el = this.element;
        if(this.buttons.length === 0) {
            fn = "appendTo";
        } else if(index < 0){
            fn = "insertBefore";
            el = this.buttons[0].element;
        } else {
            index = Math.max(0, Math.min(this.buttons.length - 1, index))
            el = this.buttons[index].element;
        }
        var pos = index + 1;
        var end =  this.buttons.slice(pos);
        this.buttons = BI.concat(BI.concat( this.buttons.slice(0, pos), btns), end);
        var items = this._packageItems([item], this._packageBtns(btns));
        var button = BI.createWidget(BI.extend(this._packageLayout(items))).element.children();
        button[fn](el);
        this._refreshIndexFromPos(index)
    },

    _refreshIndexFromPos: function(pos) {
        var o = this.options;
        var end =  this.buttons.slice(pos);
        var buttonIndex = pos;
        if(o.allHistory !== true){
            buttonIndex ++;
        }
        BI.each(end, function(idx, item){
            if(!BI.isUndefined(item.setIndex)){
                item.setIndex(buttonIndex + idx)
            }
        })
    },

    setValue : function (v) {
        var currentValue  = this.getValue();
        if(BI.isEqual([v], currentValue)) {
            return;
        }
        this.oldValue = currentValue;
        BI.HistoryButtonGroup.superclass.setValue.apply(this, arguments);
    },

    selectLastValue : function () {
        this.setValue(this.oldValue);
    },


    getButton: function (v) {
        return BI.find(this.buttons, function (idx, item) {
            return  item.getValue() === v;
        })
    },

    deletePosition : function (pos) {
        if(this.options.allHistory === true){
            pos++;
        }
        this.removeItemAt(pos)
        this._refreshIndexFromPos(pos)
    },


    deleteFromPosition : function (pos) {
        if(this.options.allHistory === true){
            pos++;
        }
        this.removeItemAt(BI.range(pos, this.buttons.length))
    }

});

$.shortcut("bi.history_button_group", BI.HistoryButtonGroup);