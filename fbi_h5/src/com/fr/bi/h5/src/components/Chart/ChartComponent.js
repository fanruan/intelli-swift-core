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
import {IconButton, HtapeLayout, VtapeLayout} from 'base'
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

    _renderHeader() {
        const {$widget} = this.props;
        const widget = new Widget($widget);
        return <HtapeLayout height={Size.HEADER_HEIGHT} style={styles.header}>
            <Text style={styles.name}>{widget.getName()}</Text>
            <IconButton width={Size.HEADER_HEIGHT} className='delete'/>
        </HtapeLayout>
    }

    render() {

        return <VtapeLayout>
            {this._renderHeader()}
            <View ref='chart' style={{height: this.props.height, ...styles.wrapper}}/>
        </VtapeLayout>
    }
}
mixin.onClass(ChartComponent, ReactComponentWithImmutableRenderMixin);

const styles = StyleSheet.create({
    wrapper: {
        position: 'relative'
    },
    name: {
        lineHeight: Size.HEADER_HEIGHT,
        paddingLeft: 4,
        paddingRight: 4,
        justifyContent: 'center'
    }
});
export default ChartComponent