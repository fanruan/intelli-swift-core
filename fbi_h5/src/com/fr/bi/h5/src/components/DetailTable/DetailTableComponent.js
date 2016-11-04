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

import {Sizes, TemplateFactory, WidgetFactory} from 'data'
import {IconLink} from 'base'
import {Layout} from 'layout'
import {TableWidget} from 'widgets';

import DetailTableComponentHelper from './DetailTableComponentHelper';
import TableComponentWidthHelper from '../Table/TableComponentWidthHelper'
import TableCell from '../Table/TableCell'
import TableHeader from '../Table/TableHeader'

import {Table} from 'base'

const {ColumnGroup, Column, Cell} = Table;


class DetailTableComponent extends Component {
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
        this._fetchData(this.props, this.context);
    }

    componentWillReceiveProps(nextProps, nextContext) {
        if (!immutableShallowEqual(nextProps, this.props) || !immutableShallowEqual(nextContext, this.context)) {
            this._fetchData(nextProps, nextContext);
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

    _fetchData(props, context) {
        const {$widget, wId} = props;
        const widget = WidgetFactory.createWidget($widget, wId, TemplateFactory.createTemplate(context.$template));
        return widget.getData().then((data)=> {
            this.setState({data: data});
        });
    }

    render() {
        const {width, height} = this.props, {data} = this.state;
        this._tableHelper = new DetailTableComponentHelper(this.props, this.context);
        this._widthHelper = new TableComponentWidthHelper(this._tableHelper, this.props.width);
        this._tableHelper.setData(data);
        const items = this._tableHelper.getItems();
        this._widthHelper.setItems(items);

        return <TableWidget
            width={width}
            height={height - Sizes.HEADER_HEIGHT}
            freezeCols={this._tableHelper.isFreeze() ? [0] : []}
            columnSize={this._widthHelper.getWidth()}
            header={this._tableHelper.getHeader()}
            items={items}
            headerCellRenderer={({colIndex, ...cell})=> {
                return <TableHeader
                    wId={this.props.wId}
                    $widget={this.props.$widget}
                    {...cell}/>
            }}
            itemsCellRenderer={({colIndex, rowIndex, ...cell}) => {
                return <TableCell
                    wId={this.props.wId}
                    $widget={this.props.$widget}
                    {...cell}/>
            }}
        >
        </TableWidget>
    }
}
// mixin.onClass(DetailTableComponent, ReactComponentWithImmutableRenderMixin);

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
export default DetailTableComponent
