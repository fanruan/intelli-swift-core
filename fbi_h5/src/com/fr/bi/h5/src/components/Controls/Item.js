import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {cn, sc, isNil, requestAnimationFrame, emptyFunction, shallowEqual, isEqual, each} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise,
    TouchableHighlight
} from 'lib'

import {Colors, Template} from 'data'

import {VerticalCenterLayout, Icon, Table, AutoSizer} from 'base'

import {MultiSelectorWidget} from 'widgets'


class Item extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {};

    state = {};

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps() {

    }

    componentWillUpdate() {

    }

    componentWillUnmount() {

    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <TouchableHighlight underlayColor={Colors.PRESS} onPress={this.props.onPress}>
            <View>
                <VerticalCenterLayout style={styles.wrapper}>
                    <Text>{props.widget.getName()}</Text>
                </VerticalCenterLayout>
            </View>
        </TouchableHighlight>
    }

}
mixin.onClass(Item, PureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        height: 44,
        padding: '0 4px',
        borderBottom: '1px solid ' + Colors.SPLIT
    }
});
export default Item
