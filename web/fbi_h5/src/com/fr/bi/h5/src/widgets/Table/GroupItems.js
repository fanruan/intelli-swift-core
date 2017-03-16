import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'

import {
    ReactComponentWithPureRenderMixin, ReactComponentWithImmutableRenderMixin,
    cn, sc, math, isNil, emptyFunction, shallowEqual, isEqual, isEmpty, each,
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
    TouchableHighlight
} from 'lib'

import {CenterLayout, Icon, Table} from 'base'


class GroupItems extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        headerRowHeight: 30,
        columnSize: [],
        groupHeader: [],
        groupItems: [],
        groupItemsCellRenderer: emptyFunction,
        width: 200,
        height: 30
    };

    state = {};

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    _renderCells() {
        const {groupHeader, groupItems, columnSize, headerRowHeight, groupItemsCellRenderer, ...props} = this.props;
        const cells = [];
        let index = 0;
        const track = (node, layer)=> {
            if (!isEmpty(node.children)) {
                let width = 0;
                each(node.children, (child)=> {
                    width += track(child, layer + 1);
                });
                node.width = width;
                return width;
            }
            let values = node.values;
            if (isEmpty(values)) {
                values = [''];
            }
            node.width = math.sum(columnSize.slice(index, index + values.length));
            index += values.length;
            return node.width;

        };
        each(groupItems, (item)=> {
            track(item, 0);
        });

        if (groupItems.length > 0) {
            let children = groupItems[0].children;
            let offsets = [];
            let offset = 0;
            each(children, (child)=> {
                offsets.push(offset);
                offset += child.width;
            });
            let layer = 0;
            while (children.length > 0 && layer < groupHeader.length) {
                let childs = [];
                let childOffsets = [];
                each(children, (child, i)=> {
                    const style = {
                        position: 'absolute',
                        width: child.width,
                        height: headerRowHeight,
                        borderBottomWidth: 1 / PixelRatio.get(),
                        borderRightWidth: 1 / PixelRatio.get(),
                        borderBottomColor: '#d3d3d3',
                        borderRightColor: '#d3d3d3'
                    };
                    if (isEmpty(child.children)) {
                        style.height = (groupHeader.length - layer) * headerRowHeight;
                    }
                    translateDOMPositionXY(style, offsets[i], layer * headerRowHeight);
                    cells.push(<View style={style}>
                        {groupItemsCellRenderer({layer, height: style.height, ...child})}
                    </View>);
                    let offset = 0;
                    each(child.children, (c)=> {
                        childs.push(c);
                        childOffsets.push(offsets[i] + offset);
                        offset += c.width;
                    });
                });
                children = childs;
                offsets = childOffsets;
                layer++;
            }
        }
        return cells;
    }

    render() {
        const {width, height, groupItems, headerRowHeight, groupItemsCellRenderer, ...props} = this.props, {...state} = this.state;
        const items = this._renderCells();
        return <View style={[styles.wrapper, {width: width, height: height}]}>
            {items}
        </View>
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
mixin.onClass(GroupItems, ReactComponentWithPureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        position: 'relative'
    }
});
export default GroupItems
