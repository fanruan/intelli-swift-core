import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {ReactComponentWithImmutableRenderMixin, requestAnimationFrame} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
} from 'lib'
import {Template, Widget} from 'data'


class ChartComponent extends Component {
    //static propTypes = {
    //    height: React.PropTypes.number.required,
    //    id: React.PropTypes.string.required,
    //    template: React.PropTypes.object.required
    //};

    constructor(props, context) {
        super(props, context);
    }

    componentWillMount() {
        const template = new Template(this.props.$template);
        const wId = this.props.wId;
        const widget = template.getWidgetById(wId);
        widget.getData().then((data)=> {
            let vanCharts = VanCharts.init(ReactDOM.findDOMNode(this.refs.chart));
            vanCharts.setOptions(data);
        });
    }

    render() {

        return <View ref='chart' style={{height: this.props.height, ...style.wrapper}}></View>
    }
}
mixin.onClass(ChartComponent, ReactComponentWithImmutableRenderMixin);

const style = StyleSheet.create({
    wrapper: {
        position: 'relative'
    }
});
export default ChartComponent