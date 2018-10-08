import { observer } from "mobx-react"
import * as React from "react"
import { chuckNorrisStore } from "../stores"

@observer
export class ChuckNorris extends React.Component {
  render() {
    return (
      <div>
        <code dangerouslySetInnerHTML={{ __html: chuckNorrisStore.quote }} />
        <br /><button onClick={chuckNorrisStore.nextQuote}>Fetch</button>
      </div>
    )
  }
}
