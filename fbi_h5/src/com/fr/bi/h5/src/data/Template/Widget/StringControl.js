/**
 * 文本控件
 * Created by Young's on 2016/10/13.
 */
import {Fetch} from 'lib'
import AbstractControl from './AbstractControl'

class StringControl extends AbstractControl {
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    getSelectType() {
        return this.$widget.getIn(['value', 'type']);
    }

    getSelectValue() {
        const value = this.$widget.getIn(['value', 'value']);
        return value ? value.toArray() : [];
    }

    getData(options) {
        const wi = this.createJson();
        return Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
            method: "POST",
            body: JSON.stringify({widget: {...wi, text_options: options}, sessionID: BH.sessionID})
        }).then(function (response) {
            return response.json();
        });

    }
}
export default StringControl;