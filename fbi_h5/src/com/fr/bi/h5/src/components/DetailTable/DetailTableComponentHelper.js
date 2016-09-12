import {each, isEmpty} from 'core';
class DetailTableComponentHelper {
    constructor(widget) {
        this.widget = widget;
        this.data = [];
    }

    setData(data) {
        this.data = data;
    }


    getHeader() {
        const ids = this.widget.getAllUsedDimensionAndTargetIds();
        const result = [];
        ids.forEach((id)=> {
            const dim = this.widget.getDimensionOrTargetById(id);
            result.push({
                text: dim.getName()
            })
        });
        return result;
    }

    getItems() {
        const result = [];
        if (isEmpty(this.data)) {
            return [];
        }
        each(this.data.data.value, (rows, i)=> {
            each(rows, (v, j)=> {
                if (!result[j]) {
                    result[j] = [];
                }
                result[j][i] = {
                    text: v,
                    value: v
                };
            })
        });
        return result;
    }

    isFreeze() {
        return this.widget.isFreeze();
    }
}

export default DetailTableComponentHelper;
