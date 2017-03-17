import React, {
  Component,
  Text,
  View
} from 'lib';
import {
    AutoSizer
} from 'base';

class AutoSizerDemo extends Component {

  render() {
    return (
      <AutoSizer>
        {({width, height})=> {
          return (
            <View style={{width:width, height: height, backgroundColor: '#acc'}}>
              <Text>Demo</Text>
            </View>
          )
        }}
      </AutoSizer>
    )
  }
}

export default AutoSizerDemo
