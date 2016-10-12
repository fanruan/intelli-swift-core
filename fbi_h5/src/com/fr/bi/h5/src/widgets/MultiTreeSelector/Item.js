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
import {Layout, CenterLayout, VerticalCenterLayout} from 'layout'

import {Icon, Button, IconButton, Checkbox, Table, AutoSizer} from 'base'


class Item extends Component {
    constructor(props, context) {
        super(props, context);
        const {text, value, checked, halfCheck, expanded} = props;
        this.state = {text, value, checked, halfCheck, expanded};
    }

    static propTypes = {};

    static defaultProps = {
        text: '',
        value: null,
        checked: false,
        halfCheck: false,
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
        const {text, value, checked, halfCheck, expanded} = props;
        this.setState({text, value, checked, halfCheck, expanded});
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
            row = <IconButton className={'node-fold-font'} selected = {state.expanded} style={[styles.icon, {
                width: 44,
                marginLeft: props.layer * 44
            }]}>
                <Icon width={16} height={16}/>
            </IconButton>
        }
        return <Button onPress={this._onExpand.bind(this)}>
            <Layout box='justify' style={[styles.row]}>
                {row}
                <VerticalCenterLayout style={[{
                    paddingLeft: isNil(row) ? ((props.layer + 1) * 44) : 0
                }]}>
                    <Text>
                        {isNil(state.value) ? state.text : state.value}
                    </Text>
                </VerticalCenterLayout>
                <CenterLayout style={[{width: Size.ITEM_HEIGHT}]}>
                    <Checkbox checked={state.checked} halfCheck={state.halfCheck}
                              onChecked={props.onSelected}/>
                </CenterLayout>
            </Layout>
        </Button>
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

    selected: {
        backgroundColor: Colors.HIGHLIGHT
    }
});
export default Item
