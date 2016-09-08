import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {requestAnimationFrame} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
    } from 'lib'


class Main extends Component {
    //static propTypes = {
    //    height: React.PropTypes.number.required,
    //    id: React.PropTypes.string.required,
    //    template: React.PropTypes.object.required
    //};

    constructor(props, context) {
        super(props, context);
    }

    render() {
        const widgetObj = this.props.widget;
        const wi = widgetObj.createJson();
        return <View ref={function (ob) {
            var w = {...wi, page: -1};
            Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
                method: "POST",

                body: JSON.stringify({widget: w, sessionID: BH.sessionID})
            }).then(function (response) {
                return response.json();// 转换为JSON
            }).then((data)=> {
                let vanCharts = VanCharts.init(ReactDOM.findDOMNode(ob));
                vanCharts.setOptions(Data.Utils.convertDataToChartData(data.data, wi, {
                    click: (d)=> {
                        console.log(d)
                    }
                }))
            });

        }} style={{height: this.props.height, ...style.wrapper}}></View>
    }
}
mixin.onClass(Main, PureRenderMixin);

const style = StyleSheet.create({
    wrapper: {
        position: 'relative'
    }
});
export default Main
