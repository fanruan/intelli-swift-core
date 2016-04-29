/**
 * @class BI.ReportSearchResultPane
 * @extends BI.Widget
 */
BI.ReportSearchResultPane = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ReportSearchResultPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-report-search-result-pane",
            items: []
        })
    },

    _init: function () {
        BI.ReportSearchResultPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        //全选、半选
        this.halfCheck = BI.createWidget({
            type: "bi.multi_select_bar",
            width: 36,
            text: ""
        });
        this.halfCheck.on(BI.MultiSelectBar.EVENT_CHANGE, function(){
            if(this.isSelected()){
                self.resultList.setNotSelectedValue([]);
            }  else {
                self.resultList.setValue([]);
            }
        });

        //list
        this.resultList = BI.createWidget({
            type: "bi.button_group",
            chooseType: BI.Selection.Multi,
            behaviors: {
                redmark: function(){
                    return true;
                }
            },
            layouts: [{
                type: "bi.vertical"
            }]
        });
        this.list = BI.createWidget({
            type: "bi.list_pane",
            cls: "report-folder-list",
            el: this.resultList,
            items: []
        });
        this.resultList.on(BI.Controller.EVENT_CHANGE, function () {
            if(this.getNotSelectedValue().length === 0){
                self.halfCheck.setSelected(true);
            } else if(this.getValue().length === 0) {
                self.halfCheck.setSelected(false);
            } else {
                self.halfCheck.setHalfSelected(true);
            }
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.center_adapt",
                    items: [this.halfCheck],
                    width: 50,
                    height: 40
                },
                top: 0,
                left: 0
            }, {
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Search_Result"),
                    cls: "search-result-comment",
                    height: 40,
                    textAlign: "left",
                    lgap: 5
                },
                top: 0,
                left: 50,
                right: 0
            }, {
                el: this.list,
                top: 40,
                left: 0,
                bottom: 0,
                right: 0
            }]
        })
    },

    _formatItems: function (items, viewType) {
        var self = this, o = this.options;
        BI.each(items, function (i, item) {
            BI.extend(item, {
                type: viewType === BI.TemplateManager.LIST_VIEW ? "bi.report_list_view_item" : "bi.report_card_view_item",
                onClickReport: function(){
                    FS.tabPane.addItem({
                        title: item.text,
                        src: FR.servletURL + item.buildUrl + "&edit=_bi_edit_"
                    });
                },
                onRenameReport: function(name){
                    self.fireEvent(BI.ReportSearchResultPane.EVENT_REPORT_RENAME, item.id, name);
                },
                onDeleteReport: function(){
                    self.fireEvent(BI.ReportSearchResultPane.EVENT_DELETE, item.id);
                },
                validationChecker: function(name){
                    return o.reportChecker(name, item.id);
                }
            });
        });
        return items;
    },

    getSelectedItems: function(){
        return this.resultList.getValue();
    },

    empty: function(){
        this.resultList.populate([]);
    },

    populate: function (items, keyword, viewType) {
        this.halfCheck.setSelected(false);
        switch (viewType){
            case BI.TemplateManager.LIST_VIEW:
                this.resultList.attr("layouts", [{
                    type: "bi.vertical"
                }]);
                break;
            case BI.TemplateManager.CARD_VIEW:
                this.resultList.attr("layouts", [{
                    type: "bi.left",
                    hgap: 10,
                    vgap: 10
                }]);
                break;
        }
        //this.resultList.populate(this._formatItems(items, viewType), keyword);
        this.list.populate(this._formatItems(items, viewType), keyword);
    }
});
BI.ReportSearchResultPane.EVENT_REPORT_RENAME = "ReportSearchResultPane.EVENT_REPORT_RENAME";
BI.ReportSearchResultPane.EVENT_DELETE = "ReportSearchResultPane.EVENT_DELETE";
$.shortcut("bi.report_search_result_pane", BI.ReportSearchResultPane);