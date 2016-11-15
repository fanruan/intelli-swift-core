/**
 * 第一个值轴y——leftY***;第二个值轴y--rightY***;第三个值轴y--rightY2***;分类轴x——cat***(category)
 * 气泡图 散点图 的x轴作为值轴使用第二个值轴的命名模板
 * 图表样式设置
 * Created by AstronautOO7 on 2016/8/1.
 */
BI.AbstractChartSetting = BI.inherit(BI.Widget, {

});

BI.extend(BI.AbstractChartSetting, {
    SINGLE_LINE_HEIGHT: 58,
    SIMPLE_H_GAP: 10,
    SIMPLE_L_GAP: 2,
    SIMPLE_H_LGAP: 5,
    CHECKBOX_WIDTH: 16,
    EDITOR_WIDTH: 80,
    EDITOR_HEIGHT: 26,
    BUTTON_WIDTH: 40,
    BUTTON_HEIGHT: 30,
    ICON_WIDTH: 24,
    ICON_HEIGHT: 24,
    NUMBER_LEVEL_SEGMENT_WIDTH: 300,
    FORMAT_SEGMENT_WIDTH: 240,
    LEGEND_SEGMENT_WIDTH: 180,
    COMBO_WIDTH: 200
});