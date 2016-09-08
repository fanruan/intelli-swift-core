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

import {Colors} from 'data'

import {Icon, Table, AutoSizer} from 'base'


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

    _onSelect(e) {
        let selected = 0;
        if (this.state.selected < 2) {
            selected = 2;
        }
        this.setState({
            selected: selected
        }, ()=> {
            this.props.onSelected(this.state.selected);
        });
        e.stopPropagation();
    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        let row;
        if (!props.isLeaf) {
            row = <View className={cn({
                    'right-font': !state.expanded,
                    'down-font': state.expanded,
                    'react-view': true
                    })} style={[styles.icon, {
                            width: 30,
                            marginLeft: props.layer * 23
                    }]}>
                <Icon width={16} height={16}/>
            </View>
        }
        return <TouchableHighlight onPress={this._onExpand.bind(this)} underlayColor={Colors.PRESS}>
            <View style={[styles.row]}>
                {row}
                <View style={[styles.text, {
                    marginLeft: isNil(row)?(props.layer*23+34):4
                }]}>
                    <Text>
                        {isNil(state.value) ? state.text : state.value}
                    </Text>
                </View>
                <TouchableWithoutFeedback onPress={this._onSelect.bind(this)}>
                    <View className={[cn({
                        'check-half-select-icon': state.selected == 1,
                        'check-box-icon': state.selected !== 1,
                        'active': state.selected === 2,
                        'react-view': true
                    })]} style={[styles.icon, {width: 30}]}>
                        <Icon width={16} height={16}/>
                    </View>
                </TouchableWithoutFeedback>
            </View>
        </TouchableHighlight>
    }

}
mixin.onClass(Item, PureRenderMixin);
const styles = StyleSheet.create({
    row: {
        flexDirection: 'row',
        borderBottomColor: Colors.BORDER,
        borderBottomStyle: 'solid',
        borderBottomWidth: 1 / PixelRatio.get(),
        height: 35
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
        backgroundColor: Colors.SELECTED
    }
});
export default Item
