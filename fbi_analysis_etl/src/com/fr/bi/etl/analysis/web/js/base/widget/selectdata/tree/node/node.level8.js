/**
 * @class BI.SelectDataLevel8Node
 * @extends BI.NodeButton
 */
BI.SelectDataLevel8Node = FR.extend(BI.NodeButton, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectDataLevel8Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-data-level8-node bi-list-item",
            id: "",
            pId: "",
            open: false,
            height: 25,
            iconWidth:13,
            iconHeight:13
        })
    },

    _initControl : function () {
        this.controller = new BI.SelectDataLevel8NodeController();
        this.controller.setWidget(this);
    },

    _init: function () {
        BI.SelectDataLevel8Node.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        self._initControl();
        this.checkbox = BI.createWidget({
            type: "bi.tree_group_node_checkbox",
            iconWidth: o.iconWidth,
            iconHeight: o.iconHeight
        })
        var icon = BI.createWidget({
            type:"bi.center_adapt",
            cls : "icon-analysis-table",
            height: o.height,
            items : [BI.createWidget ({
                type : "bi.icon",
                width: o.iconWidth,
                height: o.iconHeight
            })]
        })
        this.text = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            whiteSpace: "nowrap",
            textHeight: o.height,
            height: o.height,
            hgap: o.hgap,
            text: o.text,
            value: o.value,
            py: o.py
        });
        this.settingIcon = BI.createWidget({
                type: "bi.down_list_combo",
                height: o.height,
                iconCls: "icon-analysis-table-set",
                items: this._createItemList()
        })

        this.settingIcon.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.controller.afterClickList(v, o);
        });

        this.settingIcon.element.click(function(e){
            e.stopPropagation();
        })

        this.tip = BI.createWidget({
            type: "bi.label",
            cls: "select-data-selected-count-label",
            whiteSpace: "nowrap",
            width: 25,
            height: o.height
        });
        this.checkbox.on(BI.Controller.EVENT_CHANGE, function (type) {
            if(type ===  BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        })
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                width: 23,
                el: this.checkbox
            }, {
                width : 23,
               el: icon
            },{
                el: this.text
            }, {
                el : this.settingIcon,
                width : 23
            },{
                width: 25,
                el: this.tip
            }]
        })
    },

    _createItemList : function (){
        return [[{
            text: BI.i18nText("BI-Edit"),
            value:ETLCst.ANALYSIS_TABLE_SET.EDIT
        }], [{
            text: BI.i18nText("BI-Rename"),
            value:ETLCst.ANALYSIS_TABLE_SET.RENAME
        }], [{
            text: BI.i18nText("BI-Copy"),
            value:ETLCst.ANALYSIS_TABLE_SET.COPY
        }], [{
            text: BI.i18nText("BI-Remove"),
            value:ETLCst.ANALYSIS_TABLE_SET.DELETE
        }]];
    },

    doRedMark: function () {
        this.text.doRedMark.apply(this.text, arguments);
    },

    unRedMark: function () {
        this.text.unRedMark.apply(this.text, arguments);
    },

    doClick: function () {
        BI.SelectDataLevel8Node.superclass.doClick.apply(this, arguments);
        this.checkbox.setSelected(this.isOpened());
    },

    setOpened: function (v) {
        BI.SelectDataLevel8Node.superclass.setOpened.apply(this, arguments);
        this.checkbox.setSelected(v);
    },

    setValue: function (items) {
        BI.SelectDataLevel8Node.superclass.setValue.apply(this, arguments);
        if(BI.isEmpty(items)){
            this.tip.setText("");
        } else {
            this.tip.setText("(" + items.length + ")");
        }
        this.tip.setTitle(items.toString());
    }
});
$.shortcut("bi.select_data_level" + ETLCst.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE + "_node", BI.SelectDataLevel8Node);