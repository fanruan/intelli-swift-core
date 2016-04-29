/**
 * Created by roy on 16/1/20.
 */
BI.BusinessPackageGroupButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        var conf = BI.BusinessPackageGroupButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-business-package",
            height: 140,
            width: 150
        })
    },

    _init: function () {
        var o = this.options, self = this;
        BI.BusinessPackageGroupButton.superclass._init.apply(this, arguments);


        var packageButton = BI.createWidget({
            type: "bi.center_adapt",
            cls: "business-package-icon",
            items: [{
                el: {
                    type: "bi.icon",
                    width: 90,
                    height: 75
                }
            }]
        });

        packageButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.BusinessPackageGroupButton.EVENT_CHANGE, self)
        });

        this.packageNameEditor = BI.createWidget({
            type: "bi.label_covered_editor",
            value: o.text
        });

        this.packageNameEditor.on(BI.LabelCoveredEditor.EVENT_CONFIRM, function () {
            var value = self.packageNameEditor.getValue();
            var id = self.attr("value");
            self.fireEvent(BI.BusinessPackageGroupButton.EVENT_EDITOR_CONFIRM, value, id);
        });


        this.tableNumLabel = BI.createWidget({
            type: "bi.label",
            cls: "business-package-table-num-label",
            value: o.table_count
        });

        this.checkboxIcon = BI.createWidget({
            type: "bi.center_adapt",
            cls: "business-package-selected-icon",
            items: [{
                el: {
                    type: "bi.icon",
                    height: 16,
                    width: 16
                }
            }],
            invisible: true,
            height: 16,
            width: 16
        });

        BI.createWidget({
            type: "bi.absolute",
            cls: "bi-business-package-button",
            element: this.element,
            items: [{
                el: packageButton,
                left: 30,
                right: 30,
                top: 30,
                bottom: 30
            }, {
                el: this.packageNameEditor,
                left: 30,
                right: 30,
                top: 110,
                bottom: 0
            }, {
                el: this.tableNumLabel,
                right: 40,
                bottom: 40
            }, {
                el: this.checkboxIcon,
                left: 0,
                top: 0
            }]
        });

    },

    doClick: function () {
        BI.BusinessPackageGroupButton.superclass.doClick.apply(this, arguments);
        if (this.isSelected()) {
            this.checkboxIcon.setVisible(true);
        } else {
            this.checkboxIcon.setVisible(false);
        }
    },

    setFocus: function () {
        this.packageNameEditor.focus();
    },


    doHighLight: function () {
        this.packageNameEditor.doHighLight();
    },

    unHighLight: function () {
        this.packageNameEditor.unHighLight();
    },

    getValue: function () {
        return this.packageNameEditor.getValue();
    }
});
BI.BusinessPackageGroupButton.EVENT_CLICK_DELETE = "EVENT_CLICK_DELETE";
BI.BusinessPackageGroupButton.EVENT_CHANGE = "EVENT_CHANGE";
BI.BusinessPackageGroupButton.EVENT_EDITOR_CONFIRM = "EVENT_EDITOR_CONFIRM";
$.shortcut("bi.business_package_group_button", BI.BusinessPackageGroupButton);