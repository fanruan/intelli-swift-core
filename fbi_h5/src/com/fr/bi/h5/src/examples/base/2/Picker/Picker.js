var React = require('lib');
var FakeObjectDataListStore = require('../../helpers/FakeObjectDataListStore');
var {
    Component,
    StyleSheet,
    PanResponder,
    Picker,
    Text,
    View
} = React;

class PickerDemo extends Component {

    constructor(props, context) {
        super(props, context);

    }

    render() {
        const data = new FakeObjectDataListStore(100);
        var rows = [];

        for (var i = 0; i < 100; i++) {
            rows.push(<Picker.Item value={data.getObjectAt(i)['firstName']} label={data.getObjectAt(i)['firstName']}>

            </Picker.Item>)
        }

        return (
            <Picker style ={{height: '100%'}}
                selectedValue={data.getObjectAt(10)['firstName']}
                onValueChange={(val)=>{console.log(val)}}
            >{rows}</Picker>
        )
    }
}

var styles = StyleSheet.create({
    view: {
        overflow: 'scroll',
        height: 300
    }
});

export default PickerDemo
