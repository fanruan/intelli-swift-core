package com.fr.bi.resource;

import com.fr.stable.ArrayUtils;

/**
 * Created by Wang on 2017/1/18.
 */
public class FsResourceHelper {
    public static String[] getFsCss() {
        String[] base = BaseResourceHelper.getBaseCss();
        String[] fs = new String[]{
                //BI风格中用到的全局样式
                "com/fr/bi/web/css/modules/globalstyle/indexitem/uploadimage/widget.uploadimage.preview.css",
                "com/fr/bi/web/css/modules/globalstyle/indexitem/widget.globalstyle.indexalignchooser.css",
                "com/fr/bi/web/css/modules/globalstyle/indexitem/widget.globalstyle.indexcombo.css",
                "com/fr/bi/web/css/modules/globalstyle/indexitem/widget.globalstyle.indextitletoolbar.css",

                //加载超时
                "com/fr/bi/web/css/base/timeouttoast/widget.timeouttoast.css"
        };
        return (String[]) ArrayUtils.addAll(base, fs);
    }

    private static String[] getBaseJs() {
        return new String[]{
                //core
                "com/fr/bi/web/js/core/foundation.js",
                "com/fr/bi/web/js/core/underscore.js",
                "com/fr/bi/web/js/core/mvc/fbi.js",
                "com/fr/bi/web/js/core/mvc/factory/factory.js",
                "com/fr/bi/web/js/core/mvc/router/rooter.js",

                //const
                "com/fr/bi/web/js/data/data.js",
                "com/fr/bi/web/js/data/constant/biconst.js",
                "com/fr/bi/web/js/data/constant/constant.js",
                "com/fr/bi/web/js/data/constant/strings.js",
                "com/fr/bi/web/js/data/constant/enums.js",
                "com/fr/bi/web/js/data/constant/colors.js",
                "com/fr/bi/web/js/data/constant/attrs.js",
                "com/fr/bi/web/js/data/pool/pool.js",
                "com/fr/bi/web/js/data/pool/pool.buffer.js",
                "com/fr/bi/web/js/data/pool/pool.sharing.js",
                "com/fr/bi/web/js/data/req/req.js",
                "com/fr/bi/web/js/data/source/source.js",


                //base
                "com/fr/bi/web/js/base/var.js",
                "com/fr/bi/web/js/base/status.js",
                "com/fr/bi/web/js/base/events.js",
                "com/fr/bi/web/js/base/base.js",
                "com/fr/bi/web/js/base/plugin.js",
                "com/fr/bi/web/js/base/cache.js",
                "com/fr/bi/web/js/base/controller.js",
                "com/fr/bi/web/js/base/dom.js",
                "com/fr/bi/web/js/base/function.js",
                "com/fr/bi/web/js/base/model.js",
                "com/fr/bi/web/js/base/special.js",
                "com/fr/bi/web/js/base/view.js",
                "com/fr/bi/web/js/base/widget.js",

                "com/fr/bi/web/js/base/proto/array.js",
                "com/fr/bi/web/js/base/proto/number.js",
                "com/fr/bi/web/js/base/proto/date.js",
                "com/fr/bi/web/js/base/proto/function.js",
                "com/fr/bi/web/js/base/utils/base64.js",
                "com/fr/bi/web/js/base/utils/md5.js",
                "com/fr/bi/web/js/base/utils/xml.js",
                "com/fr/bi/web/js/base/utils/chinesePY.js",
                "com/fr/bi/web/js/base/utils/queue.js",
                "com/fr/bi/web/js/base/utils/linkedHashMap.js",
                "com/fr/bi/web/js/base/utils/tree.js",
                "com/fr/bi/web/js/base/utils/vector.js",
                "com/fr/bi/web/js/base/utils/set.js",
                "com/fr/bi/web/js/base/utils/lru.js",
                "com/fr/bi/web/js/base/utils/heap.js",
                "com/fr/bi/web/js/base/utils/aspect.js",
                "com/fr/bi/web/js/base/utils/load.js",
                "com/fr/bi/web/js/base/utils/detectElementResize.js",


                "com/fr/bi/web/js/base/parsers/expression.js",
                "com/fr/bi/web/js/base/parsers/path.js",
                "com/fr/bi/web/js/base/parsers/watcher.js",

                "com/fr/bi/web/js/base/action/action.js",
                "com/fr/bi/web/js/base/action/action.show.js",
                "com/fr/bi/web/js/base/action/action.show.effect.js",
                "com/fr/bi/web/js/base/action/action.show.scale.js",
                "com/fr/bi/web/js/base/behavior/behavior.js",
                "com/fr/bi/web/js/base/behavior/behavior.redmark.js",
                "com/fr/bi/web/js/base/behavior/behavior.highlight.js",
                "com/fr/bi/web/js/base/controller/controller.broadcast.js",
                "com/fr/bi/web/js/base/controller/controller.floatbox.js",
                "com/fr/bi/web/js/base/controller/controller.layer.js",
                "com/fr/bi/web/js/base/controller/controller.masker.js",
                "com/fr/bi/web/js/base/controller/controller.resizer.js",
                "com/fr/bi/web/js/base/controller/router.floatbox.js",
                "com/fr/bi/web/js/base/controller/controller.bubbles.js",
                "com/fr/bi/web/js/base/controller/controller.tooltips.js",
                "com/fr/bi/web/js/base/loader/loader.style.js",
                "com/fr/bi/web/js/base/event/event.list.js",
                "com/fr/bi/web/js/base/event/off.list.js",
                "com/fr/bi/web/js/base/event/listener.list.js",
                "com/fr/bi/web/js/base/logic/logic.js",
                "com/fr/bi/web/js/base/logic/logic.layout.js",
                "com/fr/bi/web/js/base/listener/listener.show.js",

                "com/fr/bi/web/js/base/module/el.js",
                "com/fr/bi/web/js/base/module/pane.js",
                "com/fr/bi/web/js/base/module/single/single.js",
                "com/fr/bi/web/js/base/module/single/text.js",
                "com/fr/bi/web/js/base/module/single/a/a.js",
                "com/fr/bi/web/js/base/module/single/img/img.js",
                "com/fr/bi/web/js/base/module/single/icon/icon.js",
                "com/fr/bi/web/js/base/module/single/button/button.basic.js",
                "com/fr/bi/web/js/base/module/single/button/button.node.js",
                "com/fr/bi/web/js/base/module/single/button/buttons/button.js",
                "com/fr/bi/web/js/base/module/single/button/buttons/button.icon.js",
                "com/fr/bi/web/js/base/module/single/button/buttons/button.image.js",
                "com/fr/bi/web/js/base/module/single/button/buttons/button.text.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/textitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/texticonitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/icontextitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/blankicontextitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/blankicontexticonitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/icontexticonitem.js",
                "com/fr/bi/web/js/base/module/single/button/node/textnode.js",
                "com/fr/bi/web/js/base/module/single/button/node/texticonnode.js",
                "com/fr/bi/web/js/base/module/single/button/node/icontextnode.js",
                "com/fr/bi/web/js/base/module/single/button/node/icontexticonnode.js",
                "com/fr/bi/web/js/base/module/single/input/input.js",
                "com/fr/bi/web/js/base/module/single/input/file.js",
                "com/fr/bi/web/js/base/module/single/input/checkbox.js",
                "com/fr/bi/web/js/base/module/single/input/radio.js",
                "com/fr/bi/web/js/base/module/single/bar/bar.loading.js",
                "com/fr/bi/web/js/base/module/single/editor/editor.js",
                "com/fr/bi/web/js/base/module/single/editor/editor.code.js",
                "com/fr/bi/web/js/base/module/single/editor/editor.textarea.js",
                "com/fr/bi/web/js/base/module/single/editor/editor.multifile.js",
                "com/fr/bi/web/js/base/module/single/label/label.js",
                "com/fr/bi/web/js/base/module/single/link/link.js",
                "com/fr/bi/web/js/base/module/single/tip/tip.js",
                "com/fr/bi/web/js/base/module/single/trigger/trigger.js",
                "com/fr/bi/web/js/base/module/single/iframe/iframe.js",

                "com/fr/bi/web/js/base/module/combination/combo.js",
                "com/fr/bi/web/js/base/module/combination/switcher.js",
                "com/fr/bi/web/js/base/module/combination/expander.js",
                "com/fr/bi/web/js/base/module/combination/group.combo.js",
                "com/fr/bi/web/js/base/module/combination/group.button.js",
                "com/fr/bi/web/js/base/module/combination/tree.button.js",
                "com/fr/bi/web/js/base/module/combination/map.button.js",
                "com/fr/bi/web/js/base/module/combination/tab.js",
                "com/fr/bi/web/js/base/module/combination/navigation.js",
                "com/fr/bi/web/js/base/module/combination/loader.js",
                "com/fr/bi/web/js/base/module/combination/searcher.js",

                "com/fr/bi/web/js/base/module/farbtastic/farbtastic.js",

//                "com/fr/bi/web/js/base/module/canvas/canvas.js",

                "com/fr/bi/web/js/base/module/svg/svg.js",

                "com/fr/bi/web/js/base/module/pager/pager.js",

                "com/fr/bi/web/js/base/module/tree/treeview.js",
                "com/fr/bi/web/js/base/module/tree/customtree.js",
                "com/fr/bi/web/js/base/module/tree/synctree.js",
                "com/fr/bi/web/js/base/module/tree/parttree.js",

                "com/fr/bi/web/js/base/module/table/table.cell.js",
                "com/fr/bi/web/js/base/module/table/table.header.cell.js",
                "com/fr/bi/web/js/base/module/table/table.footer.cell.js",
                "com/fr/bi/web/js/base/module/table/table.header.js",
                "com/fr/bi/web/js/base/module/table/table.footer.js",
                "com/fr/bi/web/js/base/module/table/table.js",

                "com/fr/bi/web/js/base/module/layer/layer.floatbox.js",
                "com/fr/bi/web/js/base/module/layer/layer.popup.js",
                "com/fr/bi/web/js/base/module/layer/layer.scroll.js",
                "com/fr/bi/web/js/base/module/layer/layer.searcher.js",

                "com/fr/bi/web/js/base/module/reqloading/loading.request.js",
                //加载超时
                "com/fr/bi/web/js/base/module/timeouttoast/widget.timeouttoast.js",

                /**公式编辑器*/
                "com/fr/bi/web/js/base/module/formula/formulaeditor.js",
                "com/fr/bi/web/js/base/module/formula/codemirror/codemirror.js",
                "com/fr/bi/web/js/base/module/formula/codemirror/addon/show-hint.js",
                "com/fr/bi/web/js/base/module/formula/codemirror/addon/formula-hint.js",
                "com/fr/bi/web/js/base/module/formula/codemirror/addon/formula-mode.js",

                "com/fr/bi/web/js/base/module/foundation/bi.message.js",

                "com/fr/bi/web/js/base/adapter/adapter.floatsection.js",

                "com/fr/bi/web/js/base/wrapper/layout.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.absolute.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.flexible.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.adaptive.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.border.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.card.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.default.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.flow.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.lattice.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.inline.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.tape.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.grid.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.vertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.division.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.window.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.table.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.td.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/float.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/absolute.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/inline.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/flexbox.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.leftrightvertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.vertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/auto.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/float.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/absolute.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/absolute.vertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/inline.vertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.float.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.vertical.js",

                "com/fr/bi/web/js/case/case.js",
                "com/fr/bi/web/js/case/layer/panel.js",
                "com/fr/bi/web/js/case/layer/pane.list.js",
                "com/fr/bi/web/js/case/layer/layer.multiselect.js",
                "com/fr/bi/web/js/case/layer/layer.panel.js",
                "com/fr/bi/web/js/case/list/list.select.js",
                "com/fr/bi/web/js/case/tip/tip.bubble.js",
                "com/fr/bi/web/js/case/tip/tip.toast.js",
                "com/fr/bi/web/js/case/tip/tip.tooltip.js",
                "com/fr/bi/web/js/case/toolbar/toolbar.multiselect.js",

                "com/fr/bi/web/js/case/button/icon/icon.trigger.js",
                "com/fr/bi/web/js/case/button/icon/icon.half.js",
                "com/fr/bi/web/js/case/button/icon/icon.change.js",

                "com/fr/bi/web/js/case/button/item.multiselect.js",
                "com/fr/bi/web/js/case/button/item.singleselect.js",
                "com/fr/bi/web/js/case/button/item.singleselect.icontext.js",
                "com/fr/bi/web/js/case/button/item.singleselect.radio.js",
                "com/fr/bi/web/js/case/button/treeitem/item.icon.treeleaf.js",
                "com/fr/bi/web/js/case/button/treeitem/item.multilayer.icon.treeleaf.js",
                "com/fr/bi/web/js/case/button/treeitem/item.first.treeleaf.js",
                "com/fr/bi/web/js/case/button/treeitem/item.last.treeleaf.js",
                "com/fr/bi/web/js/case/button/treeitem/item.mid.treeleaf.js",
                "com/fr/bi/web/js/case/button/treeitem/item.treetextleaf.js",

                "com/fr/bi/web/js/case/button/node/node.icon.arrow.js",
                "com/fr/bi/web/js/case/button/node/node.multilayer.icon.arrow.js",
                "com/fr/bi/web/js/case/button/node/node.plus.js",
                "com/fr/bi/web/js/case/button/node/node.first.plus.js",
                "com/fr/bi/web/js/case/button/node/node.last.plus.js",
                "com/fr/bi/web/js/case/button/node/node.mid.plus.js",
                "com/fr/bi/web/js/case/button/node/node.arrow.js",
                "com/fr/bi/web/js/case/button/node/node.triangle.js",

                "com/fr/bi/web/js/case/editor/editor.state.js",
                "com/fr/bi/web/js/case/editor/editor.state.simple.js",
                "com/fr/bi/web/js/case/editor/editor.sign.js",
                "com/fr/bi/web/js/case/editor/editor.shelter.js",
                "com/fr/bi/web/js/case/editor/editor.record.js",
                "com/fr/bi/web/js/case/segment/button.segment.js",
                "com/fr/bi/web/js/case/segment/segment.js",
                "com/fr/bi/web/js/case/checkbox/check.treenode.js",
                "com/fr/bi/web/js/case/checkbox/check.first.treenode.js",
                "com/fr/bi/web/js/case/checkbox/check.last.treenode.js",
                "com/fr/bi/web/js/case/checkbox/check.mid.treenode.js",
                "com/fr/bi/web/js/case/checkbox/check.treegroupnode.js",
                "com/fr/bi/web/js/case/checkbox/check.arrowtreegroupnode.js",
                "com/fr/bi/web/js/case/checkbox/check.checkingmarknode.js",

                "com/fr/bi/web/js/case/trigger/trigger.icon.js",
                "com/fr/bi/web/js/case/trigger/trigger.text.js",
                "com/fr/bi/web/js/case/trigger/trigger.text.small.js",
                "com/fr/bi/web/js/case/trigger/trigger.editor.js",
                "com/fr/bi/web/js/case/trigger/trigger.text.select.js",
                "com/fr/bi/web/js/case/trigger/trigger.text.select.small.js",

                "com/fr/bi/web/js/case/calendar/calendar.js",
                "com/fr/bi/web/js/case/calendar/calendar.year.js",

                "com/fr/bi/web/js/case/colorpicker/button/button.colorpicker.js",
                "com/fr/bi/web/js/case/colorpicker/colorpicker.js",
                "com/fr/bi/web/js/case/colorpicker/editor.colorpicker.js",

//                "com/fr/bi/web/js/case/canvas/canvas.complex.js",

                "com/fr/bi/web/js/case/floatbox/floatboxsection.bar.js",

                "com/fr/bi/web/js/case/loader/loader.lazy.js",
                "com/fr/bi/web/js/case/loader/loader.list.js",
                "com/fr/bi/web/js/case/loader/sort.list.js",

                "com/fr/bi/web/js/case/expander/expander.branch.js",
                "com/fr/bi/web/js/case/expander/expander.branch.handstand.js",

                "com/fr/bi/web/js/case/tree/tree.simple.js",
                "com/fr/bi/web/js/case/tree/tree.level.js",
                "com/fr/bi/web/js/case/tree/tree.display.js",
                "com/fr/bi/web/js/case/tree/tree.branch.js",
                "com/fr/bi/web/js/case/tree/tree.branch.handstand.js",

                "com/fr/bi/web/js/case/pager/pager.number.js",
                "com/fr/bi/web/js/case/pager/pager.direction.js",
                "com/fr/bi/web/js/case/pager/pager.skip.js",
                "com/fr/bi/web/js/case/pager/pager.all.js",

                "com/fr/bi/web/js/case/table/table.layertree.cell.js",
                "com/fr/bi/web/js/case/table/table.layertree.js",
                "com/fr/bi/web/js/case/table/table.dynamicsummarytree.js",
                "com/fr/bi/web/js/case/table/table.dynamicsummarylayertree.js",
                "com/fr/bi/web/js/case/table/tabler.js",

                //chart
                "com/fr/bi/web/js/case/chart/chart.combine.js",

                "com/fr/bi/web/js/case/logintimeout/login.timeout.js",
        };
    }

    private static String[] getWidgetJs() {
        return new String[]{
                "com/fr/bi/web/js/widget/base/tip/tip.helper.js",

                //text combo
                "com/fr/bi/web/js/widget/base/combo/textvaluecombo/popup.textvalue.js",
                "com/fr/bi/web/js/widget/base/combo/textvaluecombo/combo.textvalue.js",
                "com/fr/bi/web/js/widget/base/combo/textvaluecombo/combo.textvaluesmall.js",

                // text icon check combo
                "com/fr/bi/web/js/widget/base/combo/textvaluecheckcombo/popup.textvaluecheck.js",
                "com/fr/bi/web/js/widget/base/combo/textvaluecheckcombo/combo.textvaluecheck.js",
                "com/fr/bi/web/js/widget/base/combo/textvaluecheckcombo/combo.textvaluechecksmall.js",

                //editor icon check combo
                "com/fr/bi/web/js/widget/base/combo/editoriconcheckcombo/combo.editoriconcheck.js",

                //text icon pane combo
                "com/fr/bi/web/js/widget/base/combo/textvaluedownlistcombo/combo.textvaluedownlist.js",
                "com/fr/bi/web/js/widget/base/combo/textvaluedownlistcombo/trigger.textvaluedownlist.js",

                //单选combo
                "com/fr/bi/web/js/widget/base/combo/staticcombo/combo.static.js",

                //iconcombo
                "com/fr/bi/web/js/widget/base/combo/iconcombo/trigger.iconcombo.js",
                "com/fr/bi/web/js/widget/base/combo/iconcombo/popup.iconcombo.js",
                "com/fr/bi/web/js/widget/base/combo/iconcombo/combo.icon.js",

                //文本框控件
                "com/fr/bi/web/js/widget/base/editor/editor.text.js",
                "com/fr/bi/web/js/widget/base/editor/editor.text.small.js",
                "com/fr/bi/web/js/widget/base/editor/editor.search.js",
                "com/fr/bi/web/js/widget/base/editor/editor.search.small.js",
                "com/fr/bi/web/js/widget/base/editor/editor.adapt.js",
                "com/fr/bi/web/js/widget/base/editor/editor.sign.initial.js",
                "com/fr/bi/web/js/widget/base/editor/editor.sign.style.js",
                "com/fr/bi/web/js/widget/base/editor/editor.clear.js",

                //segment控件
                "com/fr/bi/web/js/widget/base/segment/button.line.segment.js",
                "com/fr/bi/web/js/widget/base/segment/segment.line.js",

                //文本工具条
                "com/fr/bi/web/js/widget/texttoolbar/sizechooser/texttoolbar.sizechooser.js",
                "com/fr/bi/web/js/widget/texttoolbar/alignchooser/texttoolbar.alignchooser.js",
                "com/fr/bi/web/js/widget/texttoolbar/colorchooser/texttoolbar.colorchooser.trigger.js",
                "com/fr/bi/web/js/widget/texttoolbar/backgroundchooser/texttoolbar.backgroundchooser.trigger.js",
                "com/fr/bi/web/js/widget/texttoolbar/texttoolbar.js",

                //选色控件
                "com/fr/bi/web/js/widget/colorchooser/colorchooser.trigger.js",
                "com/fr/bi/web/js/widget/colorchooser/colorchooser.popup.js",
                "com/fr/bi/web/js/widget/colorchooser/colorchooser.custom.js",
                "com/fr/bi/web/js/widget/colorchooser/colorchooser.js",

                //单选下拉树
                "com/fr/bi/web/js/widget/singletree/singletree.combo.js",
                "com/fr/bi/web/js/widget/singletree/singletree.popup.js",
                "com/fr/bi/web/js/widget/singletree/singletree.trigger.js",

                //可选下拉树
                "com/fr/bi/web/js/widget/selecttree/nodes/node.first.plus.js",
                "com/fr/bi/web/js/widget/selecttree/nodes/node.mid.plus.js",
                "com/fr/bi/web/js/widget/selecttree/nodes/node.last.plus.js",
                "com/fr/bi/web/js/widget/selecttree/selecttree.combo.js",
                "com/fr/bi/web/js/widget/selecttree/selecttree.expander.js",
                "com/fr/bi/web/js/widget/selecttree/selecttree.popup.js",

                //多层单选下拉树
                "com/fr/bi/web/js/widget/multilayersingletree/treeitem/item.first.treeleaf.js",
                "com/fr/bi/web/js/widget/multilayersingletree/treeitem/item.mid.treeleaf.js",
                "com/fr/bi/web/js/widget/multilayersingletree/treeitem/item.last.treeleaf.js",
                "com/fr/bi/web/js/widget/multilayersingletree/node/node.first.plus.js",
                "com/fr/bi/web/js/widget/multilayersingletree/node/node.mid.plus.js",
                "com/fr/bi/web/js/widget/multilayersingletree/node/node.last.plus.js",
                "com/fr/bi/web/js/widget/multilayersingletree/multilayersingletree.leveltree.js",
                "com/fr/bi/web/js/widget/multilayersingletree/multilayersingletree.popup.js",
                "com/fr/bi/web/js/widget/multilayersingletree/multilayersingletree.combo.js",

                //多层可选下拉树
                "com/fr/bi/web/js/widget/multilayerselecttree/node/node.first.plus.js",
                "com/fr/bi/web/js/widget/multilayerselecttree/node/node.mid.plus.js",
                "com/fr/bi/web/js/widget/multilayerselecttree/node/node.last.plus.js",
                "com/fr/bi/web/js/widget/multilayerselecttree/multilayerselecttree.leveltree.js",
                "com/fr/bi/web/js/widget/multilayerselecttree/multilayerselecttree.popup.js",
                "com/fr/bi/web/js/widget/multilayerselecttree/multilayerselecttree.combo.js",

                //复选下拉树
                "com/fr/bi/web/js/widget/multitree/trigger/multi.tree.button.checkselected.js",
                "com/fr/bi/web/js/widget/multitree/trigger/searcher.multi.tree.js",
                "com/fr/bi/web/js/widget/multitree/multi.tree.popup.js",
                "com/fr/bi/web/js/widget/multitree/multi.tree.search.pane.js",
                "com/fr/bi/web/js/widget/multitree/check/multi.tree.check.pane.js",
                "com/fr/bi/web/js/widget/multitree/multi.tree.combo.js",

                //可切换单选复选的树
                "com/fr/bi/web/js/widget/switchtree/switchtree.js",

                //图控件
                "com/fr/bi/web/js/widget/detailchart/chart.abstract.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulatearea.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulateaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulatebar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulateradar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.area.js",
                "com/fr/bi/web/js/widget/detailchart/chart.axis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.multiaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.multiaxiscombine.js",
                "com/fr/bi/web/js/widget/detailchart/chart.bar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.bubble.js",
                "com/fr/bi/web/js/widget/detailchart/chart.dashboard.js",
                "com/fr/bi/web/js/widget/detailchart/chart.donut.js",
                "com/fr/bi/web/js/widget/detailchart/chart.forcebubble.js",
                "com/fr/bi/web/js/widget/detailchart/chart.line.js",
                "com/fr/bi/web/js/widget/detailchart/chart.percentaccumulateaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.percentaccumulatearea.js",
                "com/fr/bi/web/js/widget/detailchart/chart.compareaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.comparebar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.comparearea.js",
                "com/fr/bi/web/js/widget/detailchart/chart.pie.js",
                "com/fr/bi/web/js/widget/detailchart/chart.radar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.scatter.js",
                "com/fr/bi/web/js/widget/detailchart/chart.fallaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.rangearea.js",
                "com/fr/bi/web/js/widget/detailchart/chart.map.js",
                "com/fr/bi/web/js/widget/detailchart/chart.gismap.js",


                //mask
                "com/fr/bi/web/js/widget/base/mask/loading.mask.js",
                "com/fr/bi/web/js/widget/base/mask/loading.background.js",
                "com/fr/bi/web/js/widget/base/mask/cancel.loading.mask.js",

                //toolbar
                "com/fr/bi/web/js/widget/base/toolbar/toolbar.progress.processor.js",
                "com/fr/bi/web/js/widget/base/toolbar/toolbar.progress.bar.js",
                "com/fr/bi/web/js/widget/base/toolbar/toolbar.progress.js",
        };
    }

    public static String[] getFsJs() {
        String[] base = getBaseJs();
        String[] widget = getWidgetJs();
        return (String[]) ArrayUtils.addAll(ArrayUtils.addAll(base, widget), new String[]{

                //BI风格中用到的全局样式
                "com/fr/bi/web/js/modules/globalstyle/indexitem/uploadimage/widget.uploadimage.preview.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/widget.globalstyle.indexalignchooser.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/widget.globalstyle.indexcombo.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/widget.globalstyle.indexcharttoolbar.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/widget.globalstyle.indextitletoolbar.js"
        });
    }
}
