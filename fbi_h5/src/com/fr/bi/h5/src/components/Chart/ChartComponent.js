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
import {Widget} from 'data'


class Main extends Component {
    //static propTypes = {
    //    height: React.PropTypes.number.required,
    //    id: React.PropTypes.string.required,
    //    template: React.PropTypes.object.required
    //};

    constructor(props, context) {
        super(props, context);
    }

    componentWillMount() {
        const $$widget = this.props.$$widget;
        const wi = new Widget($$widget).createJson();
        var w = {...wi, page: -1};
        Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=chart_setting', {
            method: "POST",

            body: JSON.stringify({widget: w, sessionID: BH.sessionID})
        }).then(function (response) {
            return response.json();// 转换为JSON
        }).then((data)=> {
            let vanCharts = VanCharts.init(ReactDOM.findDOMNode(this.refs.chart));
            console.log(data);
            vanCharts.setOptions(data);
        });
    }

    render() {

        return <View ref='chart' style={{height: this.props.height, ...style.wrapper}}></View>
    }
}
mixin.onClass(Main, PureRenderMixin);

const style = StyleSheet.create({
    wrapper: {
        position: 'relative'
    }
});
export default Main