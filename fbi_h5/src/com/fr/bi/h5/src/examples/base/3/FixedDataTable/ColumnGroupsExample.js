/**
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only. Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

"use strict";

var FakeObjectDataListStore = require('../../helpers/FakeObjectDataListStore');
var {FixedDataTable} = require('base');
var React = require('react');
var {Dimensions} = require('lib')

const {width, height} = Dimensions.get('window');

const {Table, Column, ColumnGroup, Cell} = FixedDataTable;

const TextCell = ({rowIndex, data, col, ...props}) => (
  <Cell {...props}>
    {data.getObjectAt(rowIndex)[col]}
  </Cell>
);

class ColumnGroupsExample extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      dataList: new FakeObjectDataListStore(100),
    };
  }

  render() {
    var {dataList} = this.state;

    return (
      <Table
        rowHeight={30}
        groupHeaderHeight={30}
        headerHeight={30}
        rowsCount={dataList.getSize()}
        width={width}
        height={height}
        {...this.props}>
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
    );
  }
}

module.exports = ColumnGroupsExample;
