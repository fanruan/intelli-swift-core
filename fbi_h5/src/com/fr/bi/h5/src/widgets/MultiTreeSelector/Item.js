import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {cn, sc, isNil, requestAnimationFrame, emptyFunction} from 'core'
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
    TouchableHighlight,
    TouchableWithoutFeedback
} from 'lib'

import {Colors, Size} from 'data'

import {Icon, Checkbox, Table, AutoSizer} from 'base'


class Item extends Component {
    constructor(props, context) {
        super(props, context);
        const {text, value, selected, expanded} = props;
        this.state = {text, value, selected, expanded};
    }

    static propTypes = {};

    static defaultProps = {
        text: '',
        value: '',
        selected: 0,
        expanded: false,
        layer: 0,
        onExpand: emptyFunction,
        onSelected: emptyFunction
    };

    state = {};

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps(props) {
        const {text, value, selected, expanded} = props;
        this.setState({text, value, selected, expanded});
    }

    componentWillUpdate() {

    }

    _onExpand() {
        this.setState({
            expanded: !this.state.expanded
        }, ()=> {
            this.props.onExpand(this.state.expanded);
        })
    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        let row;
        if (!props.isLeaf) {
            row = <View className={cn({
                'active': state.expanded,
            }, 'node-fold', 'react-view')} style={[styles.icon, {
                width: 44,
                marginLeft: props.layer * 44
            }]}>
                <Icon width={16} height={16}/>
            </View>
        }
        return <TouchableHighlight onPress={this._onExpand.bind(this)} underlayColor={Colors.PRESS}>
            <View style={[styles.row]}>
                {row}
                <View style={[styles.text, {
                    marginLeft: isNil(row) ? ((props.layer + 1) * 44) : 0
                }]}>
                    <Text>
                        {isNil(state.value) ? state.text : state.value}
                    </Text>
                </View>
                <View style={[styles.icon, {width: Size.ITEM_HEIGHT}]}>
                    <Checkbox selected={state.selected === 2} half={state.selected === 1}
                              onSelected={props.onSelected}/>
                </View>
            </View>
        </TouchableHighlight>
    }
}
mixin.onClass(Item, PureRenderMixin);
const styles = StyleSheet.create({
    row: {
        flexDirection: 'row',
        borderBottomColor: Colors.SPLIT,
        borderBottomStyle: 'solid',
        borderBottomWidth: 1 / PixelRatio.get(),
        height: Size.ITEM_HEIGHT
    },

    text: {
        justifyContent: 'center',
        flexGrow: 1
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
