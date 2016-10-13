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

import {Colors, Size, Template, Widget, Dimension, Target} from 'data'

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


const {SortableContainer, SortableElement, SortableHandle, arrayMove} = Sortable;

const DragHandle = SortableHandle(() => {
    return <IconButton effect={false} style={styles.dragHandler} className={'drag-handler-icon'} iconWidth={18}
                       iconHeight={18}/>
});

const SortableItem = SortableElement(({value}) => {
    return <Button>
        <Layout main='justify' cross='center' style={styles.sortableItems}>
            <Text>{value.text}</Text>
            <DragHandle/>
        </Layout>
    </Button>
});

const SortableList = SortableContainer(({items}) => {
    return (
        <ScrollView style={{height: Size.ITEM_HEIGHT * items.length}}>
            {items.map((value, index) =>
                <SortableItem key={`item-${value.dId}`} index={index} value={value}/>
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
        collapsed: {}
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
        const {$widget} = this.props;
        const widget = new Widget($widget);
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
            const items = this._helper.getDimensionsItems(viewItem.viewId);
            array.push(<Collapsible collapsed={this.state.collapsed[viewItem.viewId] || false}>
                <SortableList items={items}
                              onSortEnd={({oldIndex, newIndex})=> {
                                  this._onSortEnd({
                                      oldIndex, newIndex, viewId: viewItem.viewId
                                  });
                              }}
                              useDragHandle={true}
                              lockAxis='y'
                              helperClass='sortable-helper'
                />
            </Collapsible>)
        });

        return <ScrollView>
            {array}
        </ScrollView>;
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
        height: Size.HEADER_HEIGHT
    },
    sortableItems: {
        paddingLeft: 20,
        paddingRight: 20,
        height: Size.ITEM_HEIGHT,
        borderBottomWidth: 1 / PixelRatio.get(),
        borderBottomColor: Colors.BORDER
    },
    dragHandler: {
        opacity: 0.25
    },
    collapseHeader: {
        paddingLeft: 20,
        backgroundColor: '#d8f2fd',
        height: Size.ITEM_HEIGHT
    }
});
export default SettingsComponent
