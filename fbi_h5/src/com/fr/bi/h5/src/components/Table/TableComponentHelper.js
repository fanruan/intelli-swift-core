import {forEach} from 'core';
class TableComponentHelper {
    constructor(widget) {
        this.widget = widget;
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
            const dim = this.widget.getDimensionOrTargetById(id);
            result.push({
                text: dim.getName()
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
                })
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
                })
            }
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
        };
        track(this.data.data, -1);
        return result;
    }

    isFreeze(){
        return this.widget.isFreeze();
    }
}

export default TableComponentHelper;
