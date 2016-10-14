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

import {Table, Dialog, IconLink} from 'base'
import {TableWidget} from 'widgets';

import TableComponentHelper from './TableComponentHelper';
import TableComponentWidthHelper from './TableComponentWidthHelper'
import TableCell from './TableCell'
import TableHeader from './TableHeader'
import WidgetFactory from '../../data/Template/Widget/WidgetFactory'


class TableComponent extends Component {

    static contextTypes = {
        $template: React.PropTypes.object
    };

    constructor(props, context) {
        super(props, context);
    }

    state = {
        data: []
    };

    componentWillMount() {

    }

    componentDidMount() {
        this._fetchData(this.props);
    }

    componentWillReceiveProps(nextProps) {
        if (!immutableShallowEqual(nextProps, this.props)) {
            this._fetchData(nextProps);
            this._changed = true;
        }
    }

    shouldComponentUpdate() {
        if (this._changed) {
            this._changed = false;
            return false;
        }
        return true;
    }

    componentWillUpdate(nextProps) {

    }

    _fetchData(props) {
        const {$widget, wId} = props;
        const widget = WidgetFactory.createWidget($widget, wId, new Template(this.context.$template));
        return widget.getData().then((data)=> {
            this.setState({data: data});
        });
    }

    render() {
        const {width, height} = this.props, {data} = this.state;
        this._tableHelper = new TableComponentHelper(this.props, this.context);
        this._widthHelper = new TableComponentWidthHelper(this._tableHelper, this.props.width);
        this._tableHelper.setData(data);
        const items = this._tableHelper.getItems();
        this._widthHelper.setItems(items);
        return <TableWidget
            width={width}
            height={height}
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
    }
}
// mixin.onClass(TableComponent, ReactComponentWithImmutableRenderMixin);

const styles = StyleSheet.create({
    wrapper: {
        position: 'relative'
    },
    header: {
        paddingLeft: 4,
        paddingRight: 4,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between'
    }
});
export default TableComponent
