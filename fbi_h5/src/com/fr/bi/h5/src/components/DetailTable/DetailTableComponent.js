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
import {IconLink, HtapeLayout, VtapeLayout} from 'base'
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
        this._tableHelper = new DetailTableComponentHelper(props, context);
        this._widthHelper = new TableComponentWidthHelper(this._tableHelper, props.width);
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
            this._tableHelper = new DetailTableComponentHelper(nextProps, this.context);
            this._widthHelper = new TableComponentWidthHelper(this._tableHelper, nextProps.width);
            this._fetchData(nextProps);
        }
    }

    componentWillUpdate(nextProps) {

    }

    _fetchData(props) {
        const {$widget, wId} = props;
        const widget = new Widget($widget, this.context.$template, wId);
        return widget.getData().then((data)=> {
            this.setState({data: data});
        });
    }

    _renderHeader() {
        const {$widget} = this.props;
        const widget = new Widget($widget);
        return <View height={Size.HEADER_HEIGHT} style={styles.header}>
            <Text>{widget.getName()}</Text>
            <IconLink className='setting-font'/>
        </View>
    }

    render() {
        const {width, height} = this.props, {data} = this.state;
        this._tableHelper.setData(data);
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
mixin.onClass(DetailTableComponent, ReactComponentWithImmutableRenderMixin);

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
