import * as React from "react"
import * as ReactDOM from "react-dom"

import { chuckNorrisStore, Store } from "../stores"
import { ChuckNorris } from "./chucknorris"

beforeEach(() => Store.resetAllStores())

it("renders without crashing", () => {
  const div = document.createElement("div")
  chuckNorrisStore.quote = "Bla"
  ReactDOM.render(<ChuckNorris />, div)
})
