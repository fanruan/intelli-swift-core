/**
 * Created by wuk on 16/4/22.
 * 角色列表面板
 */
BI.AuthorityTabs = BI.inherit(BI.LoadingPane, {


    _defaultConfig: function () {
        return BI.extend(BI.AuthorityTabs.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-link-tables-pane"
        })
    },

    _init: function () {
        BI.AuthorityTabs.superclass._init.apply(this, arguments);
        this.allPageTables = [];
        this.selectedTables = [];
        this.wrapper = BI.createWidget({
            type: "bi.default",
            element: this.element
        })
    },

    populate: function (items) {
        this.selectedTables = [];
        this.wrapper.empty();
        //没有表的
        if (BI.isEmptyArray(items)) {
            return;
        }

        this.allTables = items;
        this.tab = BI.createWidget({
            type: "bi.tab",
            direction: "custom",
            defaultShowIndex: 1,
            cardCreator: BI.bind(this._createPageCard, this)
        });
        this.wrapper.addItem({
            el: this.tab,
            top: 0,
            left: 0,
            right: 0,
            bottom: 0
        });
    },


    _formatGroup: function (groups) {
        var formatGroup = [];
        BI.each(groups, function (i, group) {
            if (group!=undefined) {
                var item = {
                    text: group.text,
                    value: group
                };
                formatGroup.push(item)
            }
        });
        return formatGroup;
    },

    _createPageCard: function () {

        var self = this;
        var group = this.allTables;
        var pageTables = BI.createWidget({
            type: "bi.button_group",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_MULTI,
            items: BI.createItems(this._formatGroup(group), {
                type: "bi.authority_button",
                cls: "bi-table-ha-button"
            }),
            layouts: [{
                type: "bi.left",
                scrollable: true,
                hgap: 10,
                vgap: 10
            }]
        });
        pageTables.on(BI.ButtonGroup.EVENT_CHANGE, function (value, ob) {
            if (ob.isSelected()) {
                self.selectedTables.push(value)
            } else {
                BI.some(self.selectedTables, function (i, v) {
                    if (BI.isEqual(v, value)) {
                        self.selectedTables.splice(i, 1);
                        return true;
                    }
                })
            }
            self.fireEvent(BI.AuthorityTabs.EVENT_CHANGE);
        });
        pageTables.setValue(this.selectedTables);
        this.allPageTables.push(pageTables);
        return BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: pageTables,
                height: "fill"
            }, {
                el: {
                    type: "bi.default"
                },
                height: 50
            }]
        });
    },


    getValue: function () {
        var allSelectedTablesDetail = [];
        BI.each(this.selectedTables, function (i, name) {
            allSelectedTablesDetail.push(name)
        });
        return allSelectedTablesDetail;
    },

    setValue: function (v) {
        BI.each(this.allPageTables, function (i, pageTables) {
            pageTables.setValue(v);
        });
    }
});

BI.AuthorityTabs.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.authority_tabs", BI.AuthorityTabs);
