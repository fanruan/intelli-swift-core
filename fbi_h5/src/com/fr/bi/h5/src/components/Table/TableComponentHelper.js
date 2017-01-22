import {each, deepClone} from 'core';
import {TemplateFactory, WidgetFactory, DimensionFactory} from 'data'
class TableComponentHelper {
    constructor(props, context) {
        const {$widget, wId} = props;
        this.widget = WidgetFactory.createWidget($widget, wId, TemplateFactory.createTemplate(context.$template));
        this.data = [];
    }

    setData(data) {
        this.data = data;
    }


    getHeader() {
        const ids = this.widget.getAllUsedTargetDimensionIds();
        const result = [{
            text: '行表头'
        }];
        ids.forEach((id)=> {
            const $$dim = this.widget.get$DimensionByDimensionId(id);
            result.push({
                text: DimensionFactory.createDimension($$dim, id, this.widget).getName()
            })
        });
        return result;
    }

    getItems() {
        const dimensionIds = this.widget.getAllUsedDimDimensionIds();
        const targetIds = this.widget.getAllUsedTargetDimensionIds();
        var result = [];
        const track = (node, layer, pValues)=> {
            if (!node) {
                return;
            }
            if (node.n) {
                if (!result[0]) {
                    result[0] = [];
                }
                result[0].push({
                    dId: dimensionIds[layer],
                    layer: layer,
                    text: node.n
                });
                if (node.s) {
                    pValues = deepClone(pValues || []);
                    pValues.push({
                        value: [node.n],
                        dId: dimensionIds[layer]
                    });
                    node.s.forEach((v, idx)=> {
                        if (!result[idx + 1]) {
                            result[idx + 1] = [];
                        }
                        result[idx + 1].push({
                            dId: targetIds[idx],
                            text: v,
                            clicked: pValues
                        })
                    })
                }
            }
            if (node.c) {
                node.c.forEach((child)=> {
                    track(child, layer + 1, pValues);
                })
            }
            // if (!node.n) {
            //     if (!result[0]) {
            //         result[0] = [];
            //     }
            //     // result[0].push({
            //     //     text: '汇总'
            //     // });
            //     if (node.s) {
            //         node.s.forEach((v, idx)=> {
            //             if (!result[idx + 1]) {
            //                 result[idx + 1] = [];
            //             }
            //             result[idx + 1].push({
            //                 dId: targetIds[idx],
            //                 text: v
            //             })
            //         })
            //     }
            // }
        };
        track(this.data.data, -1);
        return result;
    }

    getGroupHeader() {
        const result = [];
        const ids = this.widget.getRowDimensionIds();
        each(ids, (id)=> {
            const dimension = DimensionFactory.createDimension(this.widget.get$DimensionByDimensionId(id));
            result.push({
                text: dimension.getName()
            });
        });
        return result;
    }

    //交叉表表头
    getGroupItems() {
        return [];
    }

    isFreeze() {
        return this.widget.isFreeze();
    }
}

export default TableComponentHelper;
