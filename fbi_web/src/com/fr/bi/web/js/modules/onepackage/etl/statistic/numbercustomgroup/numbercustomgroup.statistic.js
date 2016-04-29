/**
 * @class BI.NumberCustomGroupPopup
 * @extend BI.BarPopoverSection
 * 数值自定义分组
 */
BI.NumberCustomGroupPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend( BI.NumberCustomGroupPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-number-custom-group-view",
            table: {},
            id: "",
            group: {}
        })
    },

    end: function(){
        this.fireEvent(BI.NumberCustomGroupPopup.EVENT_CHANGE, this.group.getValue());
        this.fireEvent(BI.Controller.EVENT_CHANGE);
    },

    _init: function () {
        BI.NumberCustomGroupPopup.superclass._init.apply(this, arguments);
        var o = this.options;
        this.loadingMsker = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.element,
            text: BI.i18nText("BI-Loading")
        });
    },

    rebuildNorth : function(north) {
        var self = this, o = this.options;
        if(BI.isNull(this.loadingMsker)){
            this.loadingMsker = BI.createWidget({
                type: "bi.loading_mask",
                masker: this.element,
                text: BI.i18nText("BI-Loading")
            });
        }
        BI.Utils.getConfNumberFieldMaxMinValue(this.options.table, o.fieldName, function(res){
            BI.createWidget({
                type: "bi.label",
                element: north,
                text: BI.i18nText("BI-Grouping_Setting_Detail", res.min, res.max),
                height: 50,
                textAlign: "left",
                lgap: 10
            });
            self.loadingMsker.destroy();
            self.loadingMsker = null;
        });
        return true;
    },

    rebuildCenter : function(center) {
        var self = this;
        this.group = BI.createWidget({
            type: "bi.conf_number_custom_group",
            fieldName: this.options.fieldName
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_ERROR,function(){
            self.saveButton.setValue(BI.i18nText("BI-Correct_The_Errors_Red"));
            self.saveButton.setEnable(false);
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_VALID,function(){
            self.saveButton.setValue(BI.i18nText("BI-Save"));
            self.saveButton.setEnable(true);
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_EMPTY_GROUP,function(){
            self.saveButton.setEnable(false);
        });

        BI.createWidget({
            type : 'bi.vertical',
            element : center,
            items:[this.group]
        });
        return true;
    },

    rebuildSouth : function(south) {
        var self = this;

        this.saveButton = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Save"),
            height:30,
            level: 'common',
            readonly:false,
            handler:function(){
                self.end();
                self.close();
            }
        });

        this.cancelButton = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Cancel"),
            height:30,
            level: 'ignore',
            readonly:false,
            handler:function(){
                self.close();
            }
        });

        BI.createWidget({
            type: "bi.right",
            element: south,
            items: [this.saveButton,
                {
                    el: this.cancelButton,
                    rgap: 10
                }
            ]
        });
        return true;
    },

    populate: function(group){
        this.group.populate(BI.extend(group, {
            table: this.options.table
        }));
    }
});

BI.NumberCustomGroupPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.number_custom_group_popup", BI.NumberCustomGroupPopup);