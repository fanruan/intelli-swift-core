FontView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FontView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-font"
        })
    },

    _init: function () {
        FontView.superclass._init.apply(this, arguments);
    },

    _createItems: function () {
        return BI.map(this.get("items"), function (i, item) {
            return {
                type: "bi.center",
                width: 120,
                height: 150,
                vgap: 10,
                hgap: 10,
                items: [{
                    el: {
                        type: "bi.border",
                        items: {
                            south: {
                                el: {
                                    type: "bi.label",
                                    cls: "font-description",
                                    text: item,
                                    title: item,
                                    height: 30
                                },
                                height: 30
                            },
                            center: {
                                type: "bi.icon_button",
                                cls: item
                            }
                        }
                    }
                }]
            }
        })
    },

    _createCenter: function () {
        return {
            type: "bi.vertical",
            items: [{
                type: "bi.left",
                items: this._createItems()
            }]
        }
    },

    _render: function (vessel) {
        var self = this;
        BI.createWidget({
            type: "bi.border",
            element: vessel,
            items: {
                north: {
                    el: {
                        type: "bi.left_right_vertical_adapt",
                        items: {
                            right: [{
                                el: {
                                    type: "bi.button",
                                    text: BI.i18nText("BI-Exit"),
                                    height: 30,
                                    handler: function () {
                                        self.notifyParentEnd();
                                    }
                                }
                            }]
                        }
                    },
                    height: 50
                },
                center: {
                    el: this._createCenter()
                }
            }
        })
    }
})

FontModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(FontModel.superclass._defaultConfig.apply(this, arguments), {
            items: [


                "close-font",
                "close-red-font",
                "close-h-font",
                "close-e-font",
                "close-ha-font",

                "search-close-h-font",

                "trigger-font",
                "trigger-h-font",
                "trigger-ha-font",

                "pre-page-font",
                "pre-page-h-font",
                "pre-page-ha-font",

                "next-page-font",
                "next-page-h-font",
                "next-page-ha-font",

                "search-font",
                "search-h-font",
                "search-ha-font",
                "share-font",
                "share-h-font",
                "share-ha-font",

                "delete-font",
                "delete-h-font",
                "delete-ha-font",
                "delete-e-font",
//子菜单选中
                "dot-font",
                "dot-h-font",
                "dot-ha-font",
                "dot-e-font",

//向右展开子菜单
                "pull-right-font",
                "pull-right-h-font",
                "pull-right-ha-font",
                "pull-right-e-font",

//复制
                "copy-font",
                "copy-h-font",
                "copy-ha-font",
                "copy-e-font",

//选中的字段
                "check-mark-font",
                "check-mark-h-font",
                "check-mark-ha-font",
                "check-mark-e-font",

//指标来自
                "dimension-from-font",
                "dimension-from-h-font",
                "dimension-from-ha-font",
                "dimension-from-e-font",

//图表类型选择
                "chart-type-font",
                "chart-type-h-font",
                "chart-type-ha-font",
                "chart-type-e-font",

//样式设置
                "style-set-font",
                "style-set-h-font",
                "style-set-ha-font",
                "style-set-e-font",

//过滤
                "filter-font",
                "filter-h-font",
                "filter-ha-font",
                "filter-e-font",

            /**维度/指标 下拉列表图标字体 ~end~**/

            /** dashboard组件/控件 下拉列表图标字体 ~begin~**/
                "link-to-widget-h-font",
                "link-to-detail-h-font",
                "detail-setting-h-font",
                "export-to-excel-h-font",
                "widget-copy-h-font",
                "widget-delete-h-font",

            /** dashboard组件/控件 下拉列表图标字体 ~end~**/

//树控件图标
                "tree-node-triangle-expand-font",
                "tree-node-triangle-collapse-font",

//翻页按钮字体图标
                "row-pre-page-h-font",
                "row-next-page-h-font",
                "column-pre-page-h-font",
                "column-next-page-h-font",

//日期控件字体图标
                "widget-date-next-h-font",
                "widget-date-pre-h-font",
                "widget-date-h-change-font",

//单选下拉框
//向下展开子菜单
                "pull-down-font",
                "pull-down-h-font",
                "pull-down-ha-font",

//字段区域
//TODO 向下展开的图表暂时没有(不同于普通的向下箭头，角度应该更小一点)
                "delete-field-font",
                "delete-field-h-font",
                "delete-field-ha-font",

//移动到分组
//TODO 新建分组图标(一个蓝色的“+”)

//dashboard
                "toolbar-save-font",
                "toolbar-undo-font",
                "toolbar-redo-font",

                "chart-table-font",
                "chart-axis-font",
                "chart-bar-font",
                "chart-accumulate-bar-font",
                "chart-pie-font",
                "chart-map-font",
                "chart-dashboard-font",
                "chart-doughnut-font",
                "chart-detail-font",
                "chart-more-font",
                "chart-bubble-font",
                "chart-scatter-font",
                "chart-radar-font",

                "chart-string-font",
                "chart-number-font",
                "chart-tree-font",
                "chart-date-font",
                "chart-year-font",
                "chart-month-font",
                "chart-quarter-font",
                "chart-ymd-font",
                "chart-date-range-font",
                "chart-query-font",
                "chart-reset-font",
                "chart-textarea-font",
                "chart-reuse-font",

                "detail-chart-summary-table-font",
                "detail-chart-axis-font",
                "detail-chart-bar-font",
                "detail-char-accumulate-bar-font",
                "detail-chart-pie-font",
                "detail-chart-map-font",
                "detail-chart-dashboard-font",
                "detail-chart-doughnut-font",
                "detail-chart-bubble-font",
                "detail-chart-scatter-font",
                "detail-chart-radar-font",

//数值区间
                "less-font",
                "less-equal-font",
                "less-arrow-font",
                "less-equal-arrow-font",

                "check-font",

//移动到分组
                "move2group-add-font",

//选择字段
                "select-data-field-string-font",
                "select-data-field-number-font",
                "select-data-field-date-font",
                "select-data-field-date-group-font",
                "select-data-preview-font",

                "detail-dimension-set-font",
                "detail-real-data-warning-font",

//数据配置分组统计中的选择字段
                "select-group-field-string-font",
                "select-group-field-number-font",
                "select-group-field-date-font",

//联动
                "dashboard-widget-combo-linkage-font",

//详细设置
                "dashboard-widget-combo-detail-set-font",

//导出为excel
                "dashboard-widget-combo-export-excel-font",

                "group-add-font",

//自定义排序
                "sortable-font",

//清空
                "dashboard-widget-combo-clear-font",

//文本控件
                "text-bold-font",
                "text-italic-font",
                "text-underline-font",
                "text-color-font",
                "text-background-font",
                "text-align-left-font",
                "text-align-center-font",
                "text-align-right-font",

//平台-我创建的
                "move-font",
                "share-font",
                "new-file-font",
                "file-font",
                "folder-font",
                "letter-font",
                "time-font",
                "rename-font",

//选择表
                "data-source-table-font",
                "etl-table-font",
                "excel-table-font",
                "sql-table-font",

                "refresh-table-font",

                "tables-tile-view-font",
                "tables-relation-view-font",

                "add-new-table-pull-down-font",

                "data-link-set-font",
                "data-link-test-font",
                "data-link-delete-font",
                "data-link-check-font",

                "classify-font",
                "series-font"

            ]
        })
    },

    _init: function () {
        FontModel.superclass._init.apply(this, arguments);
    }
})