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

import {Colors, Size, Template, Widget} from 'data'

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
                    <Text>{new Widget(props.$$widget).getName()}</Text>
                </VerticalCenterLayout>
            </View>
        </TouchableHighlight>
    }

}
mixin.onClass(Item, PureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        height: Size.ITEM_HEIGHT,
        paddingLeft: 20,
        paddingRight: 20,
        borderBottom: '1px solid ' + Colors.SPLIT
    }
});
export default Item
