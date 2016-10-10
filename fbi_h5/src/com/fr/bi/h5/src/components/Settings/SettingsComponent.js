import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'
import Immutable from 'immutable'

import {
    ReactComponentWithPureRenderMixin, ReactComponentWithImmutableRenderMixin,
    cn, sc, math, isNil, emptyFunction, shallowEqual, immutableShallowEqual, isEqual, isEmpty, each,
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
    TouchableHighlight
} from 'lib'

import {Colors, Size, Template, Widget, Dimension, Target} from 'data'

import {CenterLayout, VtapeLayout, HtapeLayout, IconButton, Icon, TextLink, Table, Overlay, Sortable} from 'base'

import {MultiSelectorWidget} from 'widgets'

import SettingsComponentHelper from './SettingsComponentHelper'


const {SortableContainer, SortableElement, SortableHandle, arrayMove} = Sortable;

const DragHandle = SortableHandle(() => {
    return <IconButton style={styles.dragHandler} className={'drag-handler-icon'} iconWidth={18} iconHeight={18}/>
});

const SortableItem = SortableElement(({value}) => {
    return <View style={styles.sortableItems}>
        <Text>{value.text}</Text>
        <DragHandle/>
    </View>
});

const SortableList = SortableContainer(({items}) => {
    return (
        <ScrollView>
            {items.map((value, index) =>
                <SortableItem key={`item-${value.dId}`} index={index} value={value}/>
            )}
        </ScrollView>
    );
});

class SettingsComponent extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        onReturn: emptyFunction,
        onComplete: emptyFunction
    };

    state = {};

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    _renderHeader() {
        const {$widget} = this.props;
        const widget = new Widget($widget);
        return <View height={Size.HEADER_HEIGHT} style={styles.header}>
            <TextLink onPress={()=> {
                this.refs['overlay'].close();
            }} style={styles.back}>{'返回'}</TextLink>
            <Text style={styles.name}>{widget.getName()}</Text>
            <TextLink style={styles.complete}>{'完成'}</TextLink>
        </View>
    }

    _onSortEnd = ({oldIndex, newIndex}) => {
        // this.setState({
        //     items: arrayMove(this.state.items, oldIndex, newIndex)
        // });
    };

    _renderDialog() {
        return <SortableList items={this._helper.getDimensionsItems()}
                             onSortEnd={this._onSortEnd}
                             useDragHandle={true}
                             lockAxis='y'
                             helperClass='sortable-helper'
        />;
    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        this._helper = new SettingsComponentHelper(props, this.context);
        return <Overlay ref='overlay' onClose={()=> {
            this.props.onReturn();
        }}>
            <VtapeLayout style={styles.wrapper}>
                {this._renderHeader()}
                {this._renderDialog()}
            </VtapeLayout>
        </Overlay>
    }

    componentWillReceiveProps(nextProps) {

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
        flexDirection: 'row',
        paddingLeft: 20,
        paddingRight: 20,
        alignItems: 'center',
        justifyContent: 'space-between',
        color: Colors.TEXT,
        backgroundColor: Colors.HIGHLIGHT
    },
    sortableItems: {
        flexDirection: 'row',
        paddingLeft: 20,
        paddingRight: 20,
        alignItems: 'center',
        justifyContent: 'space-between',
        height: Size.ITEM_HEIGHT,
        borderBottomWidth: 1 / PixelRatio.get(),
        borderBottomColor: Colors.BORDER
    },
    dragHandler: {
        opacity: 0.25
    }
});
export default SettingsComponent
