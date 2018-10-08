import { action, observable } from "mobx"

import { fetchJSON } from "../api"
import { Store } from "./base"

/**
 * {
 *   "type": "success",
 *   "value": {
 *     "id": 267,
 *     "joke": "Ozzy Osbourne bites the heads off of bats. Chuck Norris bites the heads off of Siberian Tigers.",
 *     "categories": []
 *   }
 * }
 */
export interface ChuckNorrisContent {
  type: string
  value: {
    id: number
    joke: string
    categories: string[]
  }
}

export class ChuckNorrisStore extends Store {
  @observable quote: string

  init() {
    this.quote = "&lt;not loaded&gt;"
  }

  @action.bound setQuoteFromContent(content: ChuckNorrisContent) {
    this.quote = content.value.joke
  }

  @action.bound setQuoteFromError(error: any) {
    this.quote = "Fehler! " + error
  }

  @action.bound async nextQuote() {
    this.quote = "&lt;loading...&gt;"
    try {
      const content = await fetchJSON<ChuckNorrisContent>("http://api.icndb.com/jokes/random")
      this.setQuoteFromContent(content)
    } catch (reason) {
      this.setQuoteFromError(reason)
    }
  }
}
