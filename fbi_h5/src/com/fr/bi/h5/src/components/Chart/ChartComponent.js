import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {ReactComponentWithImmutableRenderMixin, requestAnimationFrame, immutableShallowEqual} from 'core'
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
import {Size, TemplateFactory, WidgetFactory} from 'data'


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

    _fetchData(props) {
        const {$widget, wId} = props;
        const widget = WidgetFactory.createWidget($widget, wId, TemplateFactory.createTemplate(this.context.$template));
        widget.getData().then((data)=> {
            this.chart.setOptions(data);
        });
    }

    componentDidMount() {
        this.chart = VanCharts.init(ReactDOM.findDOMNode(this.refs.chart));
        this._fetchData(this.props);
    }

    componentWillReceiveProps(nextProps) {
        if (!immutableShallowEqual(nextProps, this.props)) {
            this._fetchData(nextProps);
            this._changed = true;
        }
    }

    shouldComponentUpdate() {
        if (this._changed) {
            this._changed = false;
            return false;
        }
        return true;
    }

    render() {

        return <View ref='chart' style={{height: this.props.height, width: this.props.width, ...styles.wrapper}}/>
    }
}
// mixin.onClass(ChartComponent, ReactComponentWithImmutableRenderMixin);

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