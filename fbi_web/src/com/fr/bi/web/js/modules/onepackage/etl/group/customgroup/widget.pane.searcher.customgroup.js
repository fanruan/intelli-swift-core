/**
 * Created by roy on 15/10/29.
 */
BI.CustomGroupSearcherPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomGroupSearcherPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group-field-pane",
            nodeType: "bi.arrow_group_node_delete",
            title: "",
            enableCheckGroup: false
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.CustomGroupSearcherPane.superclass._init.apply(this, arguments);

        this.fieldPane = BI.createWidget({
            type: "bi.custom_group_field_pane",
            nodeType: "bi.arrow_group_node",
            enableCheckGroup: true
        });



        this.toolbar = BI.createWidget({
            type: "bi.multi_select_bar",
            cls: "bi-custom-group-searcher-toolbar",
            text: BI.i18nText("BI-Select_All_Search_Results"),
            height: 30,
            width: 110
        });

        this.toolbar.on(BI.MultiSelectBar.EVENT_CHANGE, function () {
            var fieldMap = self.fieldPane.getFieldMap();
            var isSelected = self.toolbar.isSelected();
            if (isSelected === true) {
                BI.each(fieldMap, function (id, value) {
                    self.fieldPane.setFieldSelectedTrue(id);
                })
            } else {
                BI.each(fieldMap, function (id, value) {
                    self.fieldPane.setFieldSelectedFalse(id);
                })
            }
            self.fireEvent(BI.CustomGroupSearcherPane.EVENT_TOOLBAR_VALUE_CHANGE, isSelected, fieldMap);
        });

        this.fieldPane.on(BI.CustomGroupFieldPane.EVENT_CHANGE, function (obj) {
            switch (self.fieldPane.checkSelectedAll()) {
                case "none":
                    self.toolbar.setSelected(false);
                    break;
                case "half":
                    self.toolbar.setHalfSelected(true);
                    break;
                case "all":
                    self.toolbar.setSelected(true);
            }
            self.fireEvent(BI.CustomGroupSearcherPane.EVENT_CHANGE, obj)
        });



        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: self.toolbar,
                height: 30,
                width: 110,
                top: -40,
                right: 5
            }, {
                el:self.fieldPane,
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            }]
        });

    },

    populate: function (items, selectedIDs, keyword) {
        var self = this;
        self.fieldPane.populate(items);
        var fieldMap = self.fieldPane.getFieldMap();
        BI.each(selectedIDs, function (i, id) {
            if (BI.isNotNull(fieldMap[id])) {
                self.fieldPane.setFieldSelectedTrue(id)
            }
        });

        BI.each(fieldMap, function (id, fieldName) {
            self.fieldPane.doRedMark(keyword, id);
        });

        if (BI.size(fieldMap) > 0) {
            self.toolbar.setEnable(true);
        } else {
            self.toolbar.setEnable(false);
        }


        switch (self.fieldPane.checkSelectedAll()) {
            case "none":
                self.toolbar.setSelected(false);
                break;
            case "half":
                self.toolbar.setHalfSelected(true);
                break;
            case "all":
                self.toolbar.setSelected(true);
        }
    },

    empty: function () {
        this.fieldPane.empty();
    }

});
BI.CustomGroupSearcherPane.EVENT_CHANGE = "EVENT_CHANGE";
BI.CustomGroupSearcherPane.EVENT_TOOLBAR_VALUE_CHANGE = "EVENT_TOOLBAR_VALUE_CHANGE";
$.shortcut("bi.etl_group_custom_group_searcher_pane", BI.CustomGroupSearcherPane);