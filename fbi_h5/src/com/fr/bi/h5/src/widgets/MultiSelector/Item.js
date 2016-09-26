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

import {Colors} from 'data'

import {Icon, Table, AutoSizer} from 'base'


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
            <View style={[styles.row]}>
                <View style={styles.text}>
                    <Text>
                        {state.value == null ? state.text : state.value}
                    </Text>
                </View>
                <View className={cn('check-box-icon', 'react-view', cn({
                    'active': this.state.selected
                }))} style={[styles.icon, {width: 30}]}>
                    <Icon width={16} height={16}></Icon>
                </View>
            </View>
        </TouchableHighlight>
    }

}
mixin.onClass(Item, PureRenderMixin);
const styles = StyleSheet.create({
    row: {
        flexDirection: 'row',
        height: 44,
        borderBottomColor: Colors.SPLIT,
        borderBottomWidth: 1 / PixelRatio.get(),
    },

    text: {
        justifyContent: 'center',
        flexGrow: 1,
    },

    icon: {
        justifyContent: 'center',
        alignItems: 'center'
    },

    selected: {
        backgroundColor: Colors.HIGHLIGHT
    }
});
export default Item
