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

import {Table, Dialog, IconLink, HtapeLayout, VtapeLayout} from 'base'
import {TableWidget} from 'widgets';

import TableComponent from './TableComponent';

import SettingsComponent from '../Settings/SettingsComponent'


class TablePaneComponent extends Component {

    constructor(props, context) {
        super(props, context);
    }

    state = {
        open: false
    };

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillUpdate() {

    }

    _renderHeader() {
        const {$widget} = this.props;
        const widget = new Widget($widget);
        return <View height={Size.HEADER_HEIGHT} style={styles.header}>
            <Text>{widget.getName()}</Text>
            <IconLink className='setting-font' onPress={()=> {
                this.setState({
                    open: true
                })
            }}/>
        </View>
    }

    _renderDialog() {
        const {$widget, wId} = this.props;
        if (this.state.open) {
            return <SettingsComponent
                $widget={$widget}
                wId={wId}
                height={0}
                onReturn={()=> {
                    this.setState({
                        open: false
                    })
                }}
            />
        }
        return null;
    }

    render() {
        const {width, height, $widget, wId} = this.props;
        return <VtapeLayout>
            {this._renderDialog()}
            {this._renderHeader()}
            <TableComponent
                width={width}
                height={height - Size.HEADER_HEIGHT}
                $widget={$widget}
                wId={wId}
            >
            </TableComponent>
        </VtapeLayout>
    }
}
mixin.onClass(TablePaneComponent, ReactComponentWithImmutableRenderMixin);

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
export default TablePaneComponent
