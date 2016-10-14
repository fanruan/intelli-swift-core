import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'
import Immutable from 'immutable'

import {
    ReactComponentWithPureRenderMixin, ReactComponentWithImmutableRenderMixin,
    cn, sc, math, isNil, emptyFunction, shallowEqual, immutableShallowEqual, isEqual, isEmpty, each, clone,
    translateDOMPositionXY, requestAnimationFrame
} from 'core'
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
    ScrollView,
    TouchableHighlight,
    TouchableWithoutFeedback
} from 'lib'

import {Colors, Sizes, TemplateFactory, WidgetFactory} from 'data'

import {Layout, CenterLayout} from 'layout'
import {
    Button,
    IconButton,
    TextButton,
    Icon,
    TextLink,
    Table,
    Overlay,
    Sortable,
    Collapsible
} from 'base'

import {MultiSelectorWidget} from 'widgets'

import SettingsComponentHelper from './SettingsComponentHelper'
import DimensionComponent from './Dimensions/DimensionComponent'
import DimensionSortableComponent from './Dimensions/DimensionSortableComponent'


const {SortableContainer} = Sortable;

const SortableList = SortableContainer(({items, wId, $widget}) => {
    return (
        <ScrollView style={{height: Sizes.ITEM_HEIGHT * items.length}}>
            {items.map((value, index) =>
                <DimensionSortableComponent key={`item-${value.dId}`} index={index} value={value} wId={wId}
                                            $widget={$widget}
                                            dId={value.dId}/>
            )}
        </ScrollView>
    );
});

class SettingsComponent extends Component {
    static contextTypes = {
        actions: React.PropTypes.object
    };

    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        onReturn: emptyFunction,
        onComplete: emptyFunction
    };

    state = {
        $widget: this.props.$widget,
        collapsed: {},
        sortable: false
    };

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            $widget: nextProps.$widget
        })
    }

    _renderHeader() {
        const {$widget, wId} = this.props;
        const widget = WidgetFactory.createWidget($widget, wId, TemplateFactory.createTemplate(this.context.$template));
        return <Layout main='justify' cross='center' style={styles.header}>
            <TextLink onPress={()=> {
                this.refs['overlay'].close();
            }} style={styles.back}>{'返回'}</TextLink>
            <Text style={styles.name}>{widget.getName()}</Text>
            <TextLink onPress={()=> {
                this.refs['overlay'].close(true);
            }} style={styles.complete}>{'完成'}</TextLink>
        </Layout>
    }

    _onSortEnd = ({oldIndex, newIndex, viewId}) => {
        const $widget = this._helper.doMove(viewId, oldIndex, newIndex);
        this.setState({
            $widget: $widget
        });
    };

    _renderSortableList(viewItem) {
        const items = this._helper.getDimensionsItems(viewItem.viewId);
        return <SortableList items={items}
                             $widget={this.state.$widget}
                             wId={this.props.wId}
                             onSortEnd={({oldIndex, newIndex})=> {
                                 this._onSortEnd({
                                     oldIndex, newIndex, viewId: viewItem.viewId
                                 });
                             }}
                             useDragHandle={true}
                             lockAxis='y'
                             helperClass='sortable-helper'
        />
    }

    _renderUnSortableList(viewItem) {
        const items = this._helper.getDimensionsItems(viewItem.viewId);
        return <ScrollView style={{height: Sizes.ITEM_HEIGHT * items.length}}>
            {items.map((value, index) =>
                <DimensionComponent key={index} value={value} wId={this.props.wId} $widget={this.state.$widget}
                                    dId={value.dId} onValueChange={($widget)=> {
                    this.setState({
                        $widget: $widget
                    });
                }}/>
            )}
        </ScrollView>
    }

    _renderDialog() {
        const array = [];
        each(this._helper.getViewItems(), (viewItem)=> {
            array.push(<TextButton textAlign='left' style={styles.collapseHeader} onPress={()=> {
                const collapsed = clone(this.state.collapsed);
                collapsed[viewItem.viewId] = !collapsed[viewItem.viewId];
                this.setState({
                    collapsed
                })
            }}>{viewItem.text}</TextButton>);
            array.push(<Collapsible collapsed={this.state.collapsed[viewItem.viewId] || false}>
                {this.state.sortable ? this._renderSortableList(viewItem) : this._renderUnSortableList(viewItem)}
            </Collapsible>)
        });

        return <Layout dir='top' box='first'>
            <View style={{height: 100}}>
                <TextButton onPress={()=> {
                    this.setState({
                        sortable: !this.state.sortable
                    })
                }} style={styles.sortChangeButton}>{this.state.sortable ? '退出排序' : '排序'}</TextButton>
            </View>
            <ScrollView>
                {array}
            </ScrollView>
        </Layout>;
    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        this._helper = new SettingsComponentHelper(state, this.context);
        return <Overlay ref='overlay' onClose={(tag)=> {
            if (tag === true) {
                const {$widget} = this.state, {wId} = this.props;
                this.props.onComplete({$widget, wId});
            } else {
                this.props.onReturn();
            }
        }}>
            <Layout dir='top' box='first' style={styles.wrapper}>
                {this._renderHeader()}
                {this._renderDialog()}
            </Layout>
        </Overlay>
    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }

}
mixin.onClass(SettingsComponent, ReactComponentWithPureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        position: 'absolute',
        backgroundColor: '#ffffff',
        left: 10,
        right: 10,
        top: 30,
        bottom: 10
    },
    header: {
        paddingLeft: 20,
        paddingRight: 20,
        color: Colors.TEXT,
        backgroundColor: Colors.HIGHLIGHT,
        height: Sizes.HEADER_HEIGHT
    },
    sortableItems: {
        paddingLeft: 20,
        paddingRight: 20,
        height: Sizes.ITEM_HEIGHT,
        borderBottomWidth: 1 / PixelRatio.get(),
        borderBottomColor: Colors.BORDER
    },
    dragHandler: {
        opacity: 0.25
    },
    collapseHeader: {
        paddingLeft: 20,
        backgroundColor: '#d8f2fd',
        height: Sizes.ITEM_HEIGHT
    },

    sortChangeButton: {
        position: 'absolute',
        right: 20,
        bottom: 20,
        height: 20
    }
});
export default SettingsComponent
