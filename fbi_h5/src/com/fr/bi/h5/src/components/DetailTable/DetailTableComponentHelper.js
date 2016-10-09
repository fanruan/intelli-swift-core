import {each, isEmpty} from 'core';
import {Template, Widget, Dimension} from 'data';
class DetailTableComponentHelper {
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
        const ids = this.widget.getAllUsedDimensionAndTargetIds();
        const result = [];
        ids.forEach((id)=> {
            const $$dim = this.widget.get$$DimensionOrTargetById(id);
            result.push({
                text: new Dimension($$dim).getName()
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
