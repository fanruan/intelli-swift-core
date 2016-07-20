/**
 * Created by richie on 15/7/13.
 */

FS.THEME.config4navigation.onAfterInit = function () {
    $('#fs-frame-search').css({
        right: 350
    });
    $('#fs-frame-reg').css({
        right: 400
    });
    var $header = $('#fs-frame-header');
    var header = BI.createWidget({
        type: "bi.absolute",
        element: $header
    });
    var newAnalysis = BI.createWidget({
        type: "bi.icon_text_item",
        cls: "new-analysis-font bi-new-analysis-button",
        text: BI.i18nText('BI-Add_Analysis'),
        height: 60,
        width: 120,
        iconWidth: 20,
        iconHeight: 20
    });
    newAnalysis.on(BI.IconTextItem.EVENT_CHANGE, function () {
        var id = BI.UUID();
        var newAnalysisBox = BI.createWidget({
            type: "bi.new_analysis_float_box"
        });
        newAnalysisBox.on(BI.NewAnalysisFloatBox.EVENT_CHANGE, function (data) {
            BI.requestAsync("fr_bi", "add_report", {
                reportName: data.reportName,
                reportLocation: data.reportLocation,
                realTime: data.realTime
            }, function (res, model) {
                if (BI.isNotNull(res) && BI.isNotNull(res.reportId)) {
                    FS.tabPane.addItem({
                        title: data.reportName,
                        src: FR.servletURL + "?op=fr_bi&cmd=init_dezi_pane&reportId=" + res.reportId + "&edit=_bi_edit_"
                    });
                }
            });
        });
        BI.Popovers.create(id, newAnalysisBox, {width: 400, height: 320}).open(id);
        newAnalysisBox.setTemplateNameFocus();
    });
    header.addItem({
        el: newAnalysis,
        right: 220,
        top: 0,
        bottom: 0
    });
    if (FS.config.isAdmin) {
        var dataConfig = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "data-config-font bi-data-config-button",
            text: BI.i18nText("BI-Data_Setting"),
            height: 60,
            width: 120,
            iconWidth: 20,
            iconHeight: 20
        });
        dataConfig.on(BI.IconTextItem.EVENT_CHANGE, function () {
            FS.tabPane.addItem({
                title: BI.i18nText('BI-Data_Setting'),
                src: FR.servletURL + '?op=fr_bi_configure&cmd=init_configure_pane'
            });
        });
        header.addItem({
            el: dataConfig,
            right: 220,
            top: 0,
            bottom: 0
        });
        header.attr("items")[0].right = 340;
        header.resize();
    }
};

/**
 * 在平台外观中添加一个用于设置BI样式的tab
 */
//debugger;
FS.Plugin.LookAndFeelSettings.push({
    item: function () {
        return {
            title: BI.i18nText("BI-BI_Style"),
            content: {
                type: "fs.style_setting",
                //type: 'tablepane',
                //colSize: [135, 260, 'fill'],
                //rowSize: [30, 30, 30, 'auto'],
                //width: 920,
                //vgap: 30,
                //items: [
                //    [
                //        {type: 'llabel', value: BI.i18nText('BI-Total_Style')},
                //        {
                //            type: 'bi.combo',
                //            widgetName: 'bi.theme',
                //            width: 260,
                //            height: 30,
                //            textField: 'text',
                //            valueField: 'value',
                //            items: [{
                //                text: "--", value: 0
                //            }, {
                //                text: BI.i18nText('BI-Top_Down_Shade'), value: 4
                //            }, {
                //                text: BI.i18nText('BI-Trans'), value: 5
                //            }, {
                //                text: BI.i18nText('BI-Plane_3D'), value: 1
                //            }, {
                //                text: BI.i18nText('BI-Gradient_Hl'), value: 2
                //            }],
                //            handler: function () {
                //
                //                var view = FSCS.View;
                //                var pane = view.configPane;
                //                var jo = {};
                //                jo.chartStyle = pane.getWidgetByName('bi.chart.style').getValue();//图表形态
                //                jo.defaultStyle = pane.getWidgetByName("bi.theme").getValue();//整体风格
                //                jo.defaultColor = pane.getWidgetByName("bi.theme.chart").getValue();//图表配色
                //                console.log(jo);
                //                BI.requestAsync('fr_bi_base', 'set_config_setting', jo,
                //                    function (res) {
                //                        FR.Msg.toast(BI.i18nText("FS-Generic-Simple_Successfully"));
                //                    }
                //                );
                //            }
                //
                //        }
                //        //{type:'fs.segment', items:[{text:'默认'},{text:'炫舞红'},{text:'青草绿'},{text:'深绿'}]}
                //    ],
                //    [
                //        {type: 'llabel', value: BI.i18nText('BI-Chart_Color')},
                //        {
                //            type: 'fs.combo',
                //            widgetName: 'bi.theme.chart',
                //            width: 260,
                //            height: 30,
                //            textField: 'text',
                //            valueField: 'value',
                //            items: [],
                //            handler: function () {
                //                var view = FSCS.View;
                //                var pane = view.configPane;
                //                var jo = {};
                //                jo.chartStyle = pane.getWidgetByName('bi.chart.style').getValue();//图表形态
                //                jo.defaultStyle = pane.getWidgetByName("bi.theme").getValue();//整体风格
                //                jo.defaultColor = pane.getWidgetByName("bi.theme.chart").getValue();//图表配色
                //                console.log(jo);
                //                BI.requestAsync('fr_bi_base', 'set_config_setting', jo,
                //                    function (res) {
                //                        FR.Msg.toast(BI.i18nText("FS-Generic-Simple_Successfully"));
                //                    }
                //                );
                //            }
                //        }
                //        //{type:'fs.segment', items:[{text:'清新'},{text:'狂野'},{text:'淡雅'}]}
                //    ],
                //    [
                //        {type: 'llabel', value: BI.i18nText('BI-Chart_Style')},
                //        {
                //            type: 'fs.segment', widgetName: 'bi.chart.style', items: [
                //            {text: '2D', value: 0},
                //            {text: '3D', value: 1}
                //        ], handler: function () {
                //            var view = FSCS.View;
                //            var pane = view.configPane;
                //            var jo = {};
                //            jo.chartStyle = pane.getWidgetByName('bi.chart.style').getValue();//图表形态
                //            jo.defaultStyle = pane.getWidgetByName("bi.theme").getValue();//整体风格
                //            jo.defaultColor = pane.getWidgetByName("bi.theme.chart").getValue();//图表配色
                //            BI.requestAsync('fr_bi_base', 'set_config_setting', jo,
                //                function (res) {
                //                    FR.Msg.toast(BI.i18nText("FS-Generic-Simple_Successfully"));
                //                }
                //            );
                //        }
                //        }
                //    ],
                //    [
                //        {type: 'llabel', value: BI.i18nText('BI-Pre_View')},
                //        {
                //            type: 'grid',
                //            columns: 2,
                //            rows: 2,
                //            width: 610,
                //            widths: ['300', '310'],
                //            heights: ['180', '190'],
                //            items: [
                //                {
                //                    column: 0,
                //                    row: 0,
                //                    el: $('<div style="width: 298px;height: 178px;float: left;border-radius: 2px;border: 1px solid">')
                //                },
                //                {
                //                    column: 1,
                //                    row: 0,
                //                    el: $('<div style="width: 298px;height: 178px;float: left;border-radius: 2px;border: 1px solid;margin-left: 10px">')
                //                },
                //                {
                //                    column: 0,
                //                    row: 1,
                //                    el: $('<div style="width: 298px;height: 178px;float: left;border-radius: 2px;border: 1px solid;margin-top: 10px">')
                //                },
                //                {
                //                    column: 1,
                //                    row: 1,
                //                    el: $('<div style="width: 298px;height: 178px;float: left;border-radius: 2px;border: 1px solid;margin: 10px 0 0 10px">')
                //                }
                //            ]
                //
                //        }
                //        //$('<div>').css({background:'green', width:600, height : 400})
                //    ]
                //]

            }
        }
    },


    action: function () {
        //this.data = BI.requestSync('fr_bi_base', 'get_config_setting', null);
        this.container.populate();
        //this.getWidgetByName('bi.theme').setValue(this.data.defaultStyle);
        //this.getWidgetByName('bi.chart.style').setValue(this.data.chartStyle);
        //this.getWidgetByName('bi.theme.chart').setValue(this.data.defaultColor);
    }
});