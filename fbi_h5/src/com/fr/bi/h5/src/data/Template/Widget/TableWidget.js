/**
 * TableWidget
 * Created by Young's on 2016/10/12.
 */
import {Fetch} from 'lib'
import AbstractWidget from './AbstractWidget'
class TableWidget extends AbstractWidget {
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    getData(options) {
        this.calculationWidget();
        const wi = this.createJson();
        return Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
            method: "POST",
            body: JSON.stringify({
                widget: {
                    expander: {
                        x: {
                            type: true,
                            value: [[]]
                        },
                        y: {
                            type: true,
                            value: [[]]
                        }
                    }, ...wi
                }, sessionID: BH.sessionID
            })
        }).then(function (response) {
            return response.json();
        });

    }
}
export default TableWidget;