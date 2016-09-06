import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {requestAnimationFrame} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
    } from 'lib'

import {TableWidget} from 'widgets';

import TableComponentHelper from './TableComponentHelper';

import {Table} from 'base'

const {ColumnGroup, Column, Cell} = Table;


class TableComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this._tableHelper = new TableComponentHelper(props.widget);
    }

    state = {
        data: []
    };

    componentWillMount() {
        this._fetchData();
    }

    componentDidMount() {

    }

    _fetchData() {
        const wi = this.props.widget.createJson();
        const w = {
            expander: {
                x: {
                    type: true,
                    value: [[]]
                },
                y: {
                    type: true,
                    value: [[]]
                }
            }, ...wi
        };
        Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
            method: "POST",
            body: JSON.stringify({widget: w, sessionID: BH.sessionID})
        }).then(function (response) {
            return response.json();
        }).then((data)=> {
            console.log(data);
            this._tableHelper.setData(data);
            this.forceUpdate();
        });
    }

    render() {
        const {width, height} = this.props;
        return <View
            style={{width: width, height: height}}
            >
            <View
                style={{position: 'absolute', left: 10, right: 10, top: 10, bottom: 10}}
                >
                <TableWidget
                    width={width - 20}
                    height={height - 20}
                    isNeedFreeze={this._tableHelper.isFreeze()}
                    freezeCols={[0]}
                    columnSize={[300, 300]}
                    header={this._tableHelper.getHeader()}
                    items={this._tableHelper.getItems()}
                    headerCellRenderer={(colIndex, cell)=> {
                        return <Cell>{cell.text}</Cell>
                    }}
                    itemsCellRenderer={({colIndex, rowIndex, items, ...props}) => {
                        return <Cell {...props}>
                            {items[colIndex][rowIndex].text}
                        </Cell>
                    }}
                    >
                </TableWidget>
            </View>
        </View>
    }
}
mixin.onClass(TableComponent, PureRenderMixin);

const style = StyleSheet.create({
    wrapper: {
        position: 'relative'
    }
});
export default TableComponent
