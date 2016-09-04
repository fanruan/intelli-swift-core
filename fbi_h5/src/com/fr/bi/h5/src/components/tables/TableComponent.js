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
import {FixedDataTable} from 'base';
const {Table, Column, ColumnGroup, Cell} = FixedDataTable;


class Main extends Component {

    constructor(props, context) {
        super(props, context);

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
        const wi = this.props.template.popConfig.widgets[this.props.id];
        Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
            method: "POST",
            body: JSON.stringify({widget: wi, sessionID: BH.sessionID})
        }).then(function (response) {
            return response.json();
        }).then((data)=> {
            this.setState({
                data: data
            })
        });
    }

    render() {
        const {data} = this.state;
        return <Table
            rowHeight={30}
            groupHeaderHeight={30}
            headerHeight={30}
            rowsCount={dataList.getSize()}
            width={this.props.width}
            height={this.props.height}>
            <ColumnGroup
                fixed={true}
                header={<Cell>Name</Cell>}>
                <Column
                    fixed={true}
                    header={<Cell>First Name</Cell>}
                    cell={<TextCell data={dataList} col="firstName" />}
                    width={150}
                    />
                <Column
                    fixed={true}
                    header={<Cell>Last Name</Cell>}
                    cell={<TextCell data={dataList} col="lastName" />}
                    width={150}
                    />
            </ColumnGroup>
            <ColumnGroup
                header={<Cell>About</Cell>}>
                <Column
                    header={<Cell>Company</Cell>}
                    cell={<TextCell data={dataList} col="companyName" />}
                    flexGrow={1}
                    width={150}
                    />
                <Column
                    header={<Cell>Sentence</Cell>}
                    cell={<TextCell data={dataList} col="sentence" />}
                    flexGrow={1}
                    width={150}
                    />
            </ColumnGroup>
        </Table>
    }
}
mixin.onClass(Main, PureRenderMixin);

const style = StyleSheet.create({
    wrapper: {
        position: 'relative'
    }
});
export default Main
