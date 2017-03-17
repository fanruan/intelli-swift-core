/**
 * ChartWidget
 * Created by Young's on 2016/10/12.
 */
import {Fetch} from 'lib'
import AbstractWidget from './AbstractWidget'

class ChartWidget extends AbstractWidget {
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    getData(options) {
        this.calculationWidget();
        const wi = this.createJson();
        return Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=chart_setting', {
            method: "POST",
            body: JSON.stringify({widget: {...wi, page: -1}, sessionID: BH.sessionID})
        }).then(function (response) {
            return response.json();
        });

    }
}

export default ChartWidget;
