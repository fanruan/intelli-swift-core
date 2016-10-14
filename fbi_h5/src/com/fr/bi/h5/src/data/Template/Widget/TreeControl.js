/**
 * 树控件
 * Created by Young's on 2016/10/13.
 */
import {Fetch} from 'lib'
import AbstractWidget from './AbstractWidget'

class TreeControl extends AbstractWidget {
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    isControl() {
        return true;
    }

    getTreeFloors() {
        return this.getAllDimensionIds().length;
    }

    getSelectedTreeValue() {
        return this.$widget.get('value').toJS();
    }

    getData(options) {
        const wi = this.createJson();
        return Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
            method: "POST",
            body: JSON.stringify({widget: {...wi, tree_options: options}, sessionID: BH.sessionID})
        }).then(function (response) {
            return response.json();
        });

    }
}
export default TreeControl;