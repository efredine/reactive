import {observer} from 'mobx-react'
import * as React from 'react'
import {ViewStore} from '../stores/ViewStore'

export interface Props {
  store: ViewStore,
}

@observer
export class Connect extends React.Component<Props, object> {
  render() {
    const {store} = this.props;
    return (
      <div>
        <br /><button onClick={store.connect}>Connect</button>
      </div>
    )
  }
}
