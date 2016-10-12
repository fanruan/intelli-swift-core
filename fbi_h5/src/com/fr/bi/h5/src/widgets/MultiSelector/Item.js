import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {cn, sc, requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Image,
    Dimensions,
    ListView,
    View,
    PixelRatio,
    Fetch,
    TouchableHighlight
} from 'lib'

import {Colors, Size} from 'data'
import {Layout, CenterLayout, VerticalCenterLayout} from 'layout'
import {Icon, Checkbox, Table, AutoSizer} from 'base'


class Item extends Component {
    constructor(props, context) {
        super(props, context);
        const {text, value, selected} = props;
        this.state = {text, value, selected};
    }

    static propTypes = {};

    static defaultProps = {
        text: '',
        value: '',
        selected: 0,
        onSelected: emptyFunction
    };

    state = {};

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps(props) {
        const {text, value, selected} = props;
        this.state = {text, value, selected};
    }

    componentWillUpdate() {

    }

    _onPress() {
        this.setState({
            selected: !this.state.selected
        }, ()=> {
            this.props.onSelected(this.state.selected);
        })
    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <TouchableHighlight onPress={this._onPress.bind(this)} underlayColor={Colors.PRESS}>
            <Layout box='last' style={[styles.row]}>
                <VerticalCenterLayout style={styles.text}>
                    <Text>
                        {state.value == null ? state.text : state.value}
                    </Text>
                </VerticalCenterLayout>
                <CenterLayout style={[{width: Size.ITEM_HEIGHT}]}>
                    <Checkbox checked={this.state.selected} onChecked={this._onPress.bind(this)}/>
                </CenterLayout>
            </Layout>
        </TouchableHighlight>;
    }

}
mixin.onClass(Item, PureRenderMixin);
const styles = StyleSheet.create({
    row: {
        height: Size.ITEM_HEIGHT,
        borderBottomColor: Colors.SPLIT,
        borderBottomWidth: 1 / PixelRatio.get(),
    },

    text: {
        paddingLeft: 20,
        paddingRight: 20
    }
});
export default Item
