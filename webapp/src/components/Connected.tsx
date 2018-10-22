import {observer} from 'mobx-react'
import * as React from 'react'
import {View, ViewStore} from '../stores/ViewStore'

const Error = ({ error }) => <h1>{'Error: ' + error}</h1>

export interface Props<T> {
  view: View<T>,
  store: ViewStore,
}

@observer
export class Connected<T> extends React.Component<Props<T>, object> {
  render() {
    const {view} = this.props;

    switch (view.document.state) {
      case 'pending':
        return <h1>Loading ...</h1>
      case 'rejected':
        return <Error error={view.document.value.message}/>
      case 'fulfilled':
        return (
          <div>
            Connected! Woot!<br/>
            {JSON.stringify(view.document.value)}
          </div>
        )
    }
  }
}
