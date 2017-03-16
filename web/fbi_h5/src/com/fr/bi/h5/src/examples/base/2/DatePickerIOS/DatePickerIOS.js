var React = require('lib');
var {
    Component,
    StyleSheet,
    DatePickerIOS
} = React;

class DatePickerIOSDemo extends Component {

    constructor(props, context) {
        super(props, context);

    }

    render() {

        return (
            <DatePickerIOS
                onDateChange={(val)=>{console.log(val)}}
            ></DatePickerIOS>
        )
    }
}

var styles = StyleSheet.create({
});

export default DatePickerIOSDemo
