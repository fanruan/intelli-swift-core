import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import Immutable from 'immutable'
import {immutableShallowEqual, ReactComponentWithImmutableRenderMixin, requestAnimationFrame} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
} from 'lib'

import {Size, Template, Widget} from 'data'

import {Table, IconButton, HtapeLayout, VtapeLayout} from 'base'
import {TableWidget} from 'widgets';

import TableComponentHelper from './TableComponentHelper';
import TableComponentWidthHelper from './TableComponentWidthHelper'
import TableCell from './TableCell'
import TableHeader from './TableHeader'


class TableComponent extends Component {

    static contextTypes = {
        $template: React.PropTypes.object
    };

    constructor(props, context) {
        super(props, context);
        this._tableHelper = new TableComponentHelper(props, context);
        this._widthHelper = new TableComponentWidthHelper(this._tableHelper, props.width);

    }

    state = {
        data: []
    };

    componentWillMount() {

    }

    componentDidMount() {
        this._fetchData();
    }

    componentWillReceiveProps(nexProps) {
        if (!immutableShallowEqual(nexProps, this.props)) {
            this._fetchData();
        }
    }

    _fetchData() {
        const {$widget, wId} = this.props;
        const widget = new Widget($widget, this.context.$template, wId);
        widget.getData().then((data)=> {
            this._tableHelper.setData(data);
            this.setState({
                data: Immutable.fromJS(data)
            })
        });
    }

    _renderHeader() {
        const {$widget} = this.props;
        const widget = new Widget($widget);
        return <HtapeLayout height={Size.HEADER_HEIGHT} style={styles.header}>
            <Text style={styles.name}>{widget.getName()}</Text>
            <IconButton width={Size.HEADER_HEIGHT} className='delete'/>
        </HtapeLayout>
    }

    render() {
        const {width, height} = this.props;
        const items = this._tableHelper.getItems();
        this._widthHelper.setItems(items);
        return <VtapeLayout>
            {this._renderHeader()}
            <TableWidget
                width={width}
                height={height - Size.HEADER_HEIGHT}
                freezeCols={this._tableHelper.isFreeze() ? [0] : []}
                columnSize={this._widthHelper.getWidth()}
                header={this._tableHelper.getHeader()}
                items={items}
                groupHeader={this._tableHelper.getGroupHeader()}
                groupItems={this._tableHelper.getGroupItems()}
                /**groupHeader={[{text: 1}, {text: 2}]}
                 groupItems={[{children:[{text: 'A', children: [{text: 'A1'}, {text: 'A2'}]}, {text: 'B'}]}]}**/
                groupHeaderCellRenderer={({colIndex, ...cell})=> {
                    return <TableHeader {...cell}/>
                }}
                groupItemsCellRenderer={({...cell})=> {
                    return <TableHeader {...cell}/>
                }}
                headerCellRenderer={({colIndex, ...cell})=> {
                    return <TableHeader {...cell}/>
                }}
                itemsCellRenderer={({colIndex, rowIndex, ...cell}) => {
                    return <TableCell {...cell}/>
                }}
            >
            </TableWidget>
        </VtapeLayout>
    }
}
mixin.onClass(TableComponent, ReactComponentWithImmutableRenderMixin);

const styles = StyleSheet.create({
    wrapper: {
        position: 'relative'
    },
    name: {
        lineHeight: Size.HEADER_HEIGHT,
        paddingLeft: 4,
        paddingRight: 4,
        justifyContent: 'center'
    }
});
export default TableComponent
