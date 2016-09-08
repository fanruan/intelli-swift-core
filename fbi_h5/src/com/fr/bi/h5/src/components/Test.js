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

import {Grid} from 'base'
import {TableWidget} from 'widgets'

import ChartComponent from './Chart/ChartComponent.js'
import TableComponent from './Table/TableComponent.js'
import MultiSelectorComponent from './MultiSelector/MultiSelectorComponent'
const {width, height} = Dimensions.get('window');

class Test extends Component {
    static propTypes = {}

    constructor(props, context) {
        super(props, context);
    }

    render() {
        return <MultiSelectorComponent>

        </MultiSelectorComponent>
    }
}
mixin.onClass(Test, PureRenderMixin);
export default Test
