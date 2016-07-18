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

    SRC_PREFIX: "src_", //数据源(字段)增删事件 + srcId

    PACKAGE_PREFIX: "package_", //业务包增删事件 + wId || ""

    WIDGET_SELECTED_PREFIX: "widget_selected_", //组件选中广播事件
};