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
import {IconLink} from 'base'
import {Size, Template, Widget} from 'data'


class ChartComponent extends Component {
    //static propTypes = {
    //    height: React.PropTypes.number.required,
    //    id: React.PropTypes.string.required,
    //    template: React.PropTypes.object.required
    //};
    static contextTypes = {
        $template: React.PropTypes.object
    };

    constructor(props, context) {
        super(props, context);
    }

    componentWillMount() {

    }

    componentDidMount() {
        this.chart = VanCharts.init(ReactDOM.findDOMNode(this.refs.chart));
        const {$widget, wId} = this.props;
        const widget = new Widget($widget, this.context.$template, wId);
        widget.getData().then((data)=> {
            this.chart.setOptions(data);
        });
    }

    componentWillUpdate() {
        const {$widget, wId} = this.props;
        const widget = new Widget($widget, this.context.$template, wId);
        widget.getData().then((data)=> {
            this.chart.setData(data);
        });
    }

    render() {

        return <View ref='chart' style={{height: this.props.height, width: this.props.width, ...styles.wrapper}}/>
    }
}
mixin.onClass(ChartComponent, ReactComponentWithImmutableRenderMixin);

const styles = StyleSheet.create({
    wrapper: {
        position: 'relative'
    },
    header: {
        paddingLeft: 4,
        paddingRight: 4,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between'
    }
});
export default ChartComponent