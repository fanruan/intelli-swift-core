/**
 * Created by Young's on 2016/6/22.
 */
BI.TableChartErrorPane = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.TableChartErrorPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-chart-error-pane"
        })
    },

    _init: function(){
        BI.TableChartErrorPane.superclass._init.apply(this, arguments);
        var self = this;
        var detailInfoButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Detail_Info"),
            width: 90,
            height: 30
        });
        detailInfoButton.on(BI.Button.EVENT_CHANGE, function(){
            self.errorInfo.setVisible(!self.errorInfo.isVisible());
        });

        this.errorInfo = BI.createWidget({
            type: "bi.label",
            textHeight: 25,
            cls: "error-info",
            whiteSpace: "normal",
            textAlign: "left",
            invisible: true
        });
        BI.createWidget({
            type: "bi.horizontal_auto",
            element: this.element,
            scrolly: true,
            items: [{
                type: "bi.center_adapt",
                cls: "error-face-icon",
                items: [{
                    type: "bi.icon",
                    width: 100,
                    height: 100
                }]
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Error_Comment_Page_Not_Found"),
                cls: "error-comment-page-not-found",
                height: 30
            }, {
                type: "bi.flexbox_center_adapt",
                items: [{
                    type: "bi.left",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Error_Problem_Is"),
                        cls: "error-comment-problem-is",
                        height: 30
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Error_Concat_Us"),
                        cls: "error-comment-concat-us",
                        height: 30
                    }]
                }]
            }, detailInfoButton, self.errorInfo],
            vgap: 10
        })
    },

    setErrorInfo: function(error) {
        this.errorInfo.setText(error);
    }
});
$.shortcut("bi.table_chart_error_pane", BI.TableChartErrorPane);