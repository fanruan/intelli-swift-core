/**
 * 广播
 * Created by Root on 2015/12/30.
 */
BICst.BROADCAST = {
    TEST: "test_broadcast",

    WIDGETS_PREFIX: "widgets_",//组件增删事件 + wId || ""
    DIMENSIONS_PREFIX: "dimensions_",//维度增删事件 + wId || ""
    REFRESH_PREFIX: "refresh_",//刷新事件 + wId
    LINKAGE_PREFIX: "linkage_",//联动事件 + wId
    RESET_PREFIX: "reset_",//重置事件 + wId
    GLOBAL_STYLE_PREFIX: "global_style_",//样式改变事件 + wId

    SRC_PREFIX: "src_", //数据源(字段)增删事件 + srcId
    FIELD_DROP_PREFIX: "field_drop_",   //字段的drop事件，只要drop了，所有的字段都取消选中

    PACKAGE_PREFIX: "package_", //业务包增删事件 + wId || ""

    WIDGET_SELECTED_PREFIX: "widget_selected_", //组件选中广播事件

    DETAIL_EDIT_PREFIX: "detail_edit_", //进入编辑界面 + wId
    
    FIELD_DRAG_START: "__filed_drag_start__",   //字段拖动开始，用于通知region状态改变
    FIELD_DRAG_STOP: "__field_drag_stop__",     //字段拖动结束，用于通知region状态改变
    
    FILTER_LIST_PREFIX: "filter_list_", //过滤条件变化 + wId

    IMAGE_LIST_PREFIX: "image_list_", //图片列表变化 + wId

    CHART_CLICK_PREFIX: "chart_click_" //图表钻取框事件
};
