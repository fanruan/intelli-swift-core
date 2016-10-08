import {each} from 'core';
import {Template, Widget, Dimension} from 'data'
class TableComponentHelper {
    constructor(props) {
        const template = new Template(props.$template);
        const wId = props.wId;
        this.widget = template.getWidgetById(wId);
        this.data = [];
    }

    setData(data) {
        this.data = data;
    }


    getHeader() {
        const ids = this.widget.getAllUsedTargetIds();
        const result = [{
            text: '行表头'
        }];
        ids.forEach((id)=> {
            const $$dim = this.widget.get$$DimensionOrTargetById(id);
            result.push({
                text: new Dimension($$dim).getName()
            })
        });
        return result;
    }

    getItems() {
        const dimensionIds = this.widget.getAllUsedDimensionIds();
        const targetIds = this.widget.getAllUsedTargetIds();
        var result = [];
        const track = (node, layer)=> {
            if (!node) {
                return;
            }
            if (node.n) {
                if (!result[0]) {
                    result[0] = [];
                }
                result[0].push({
                    dId: dimensionIds[layer],
                    text: node.n
                });
                if (node.s) {
                    node.s.forEach((v, idx)=> {
                        if (!result[idx + 1]) {
                            result[idx + 1] = [];
                        }
                        result[idx + 1].push({
                            dId: targetIds[idx],
                            text: v
                        })
                    })
                }
            }
            if (node.c) {
                node.c.forEach((child)=> {
                    track(child, layer + 1);
                })
            }
            if (!node.n) {
                if (!result[0]) {
                    result[0] = [];
                }
                result[0].push({
                    text: '汇总'
                });
                if (node.s) {
                    node.s.forEach((v, idx)=> {
                        if (!result[idx + 1]) {
                            result[idx + 1] = [];
                        }
                        result[idx + 1].push({
                            dId: targetIds[idx],
                            text: v
                        })
                    })
                }
            }
        };
        track(this.data.data, -1);
        return result;
    }

    getGroupHeader() {
        const result = [];
        const ids = this.widget.getRowDimensionIds();
        each(ids, (id)=> {
            const dimension = new Dimension(this.widget.get$$DimensionById(id));
            result.push({
                text: dimension.getName()
            });
        });
        return result;
    }

    getGroupItems() {
        return [];
    }

    isFreeze() {
        return this.widget.isFreeze();
    }
}

export default TableComponentHelper;
