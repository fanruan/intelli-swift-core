BI.HistoryButton =  FR.extend(BI.BasicButton, {

    _constant : {
        selectTagWidth:5,
        gap:15,
        height:25,
        iconWidth:15
    },

    _defaultConfig: function () {
        return BI.extend(BI.HistoryButton.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-history-tab-button bi-list-item",
            text : "",
            index : 1,
            value : 1,
            width:  180,
            forceSelected:true,
            canDelete : true
        })
    },

    _init: function () {
        BI.HistoryButton.superclass._init.apply(this, arguments);
        this.hasError = true;
        var o = this.options;
        this.text = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            whiteSpace: "nowrap",
            textHeight: this._constant.height,
            height: this._constant.height,
            hgap: 0,
            text: this._createTextString(),
            title : o.text,
            value : o.value,
            width : o.width - this._constant.selectTagWidth*2 - this._constant.gap - this._constant.iconWidth
        });

        this.deleteButton = BI.createWidget({
            type:"bi.icon_button",
            title:BI.i18nText("Delete"),
            cls:"delete-field-font delete bi-shake-icon"
        })

        var self = this;
        this.deleteButton.on(BI.IconButton.EVENT_CHANGE, function(){
            self.fireEvent(BI.HistoryButton.EVENT_DELETE, self.getValue())
        })
        BI.createWidget({
            element:this.element,
            type:"bi.htape",
            height:this._constant.height,
            items : [{
                        type:"bi.layout",
                        width:this._constant.gap,
                        height:this._constant.height
                    },{
                        el:  this.text
                     }, {
                        type:"bi.center_adapt" ,
                        items:[this.deleteButton],
                        height:this._constant.height,
                        width:this._constant.iconWidth
                    }, {
                        type:"bi.layout",
                        width:this._constant.selectTagWidth,
                        height:this._constant.height
                     },{
                        type:"bi.layout",
                        cls:"select_tag",
                        width:this._constant.selectTagWidth,
                        height:this._constant.height
                    }]
        })
        //setVisible会调用缓存的样式如果不设置一下display block会导致show的时候会使用diplay inline导致效果不正确
        this.deleteButton.element.css('display',"block");
        this.deleteButton.setVisible(false);
    },

    _setDeleteButtonVisible : function (b) {

        this.deleteButton.setVisible(b);
    },

    setSelected : function (b) {
        BI.HistoryButton.superclass.setSelected.apply(this, arguments);
        var o = this.options;
        this._setDeleteButtonVisible(o.canDelete && b === true);
    },

    setShowDelete : function (b) {
        this.options.canDelete = b;
        this.setSelected(this.isSelected())
    },

    setIndex : function (v) {
        this.options.index = v;
        this._refreshText();
    },

    setText : function(v) {
        BI.HistoryButton.superclass.setText.apply(this, arguments)
        this._refreshText();
    },

    _refreshText : function () {
        this.text.setText(this._createTextString());
        this._refreshRedMark();
    },

    _createTextString : function () {
        var o = this.options;
        return o.index + "." + o.text;
    },

    setValid : function (hasError) {
        this.hasError =   hasError;
        this._refreshRedMark();
    },

    _refreshRedMark : function () {
        this.hasError === true ? this._unRedMark() : this._doRedMark();
    },

    _doRedMark: function(){
        this.text.doRedMark(this.options.text);
    },

    _unRedMark: function(){
        this.text.unRedMark("");
    }

})
BI.HistoryButton.EVENT_DELETE = "HistoryButton.event_delete"

$.shortcut("bi.history_button",BI.HistoryButton)