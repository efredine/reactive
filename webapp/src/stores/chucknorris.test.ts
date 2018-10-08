import { ChuckNorrisContent, ChuckNorrisStore } from "./chucknorris"

it("sets quote on ok", async function () {
  const reply: ChuckNorrisContent = {
    "type": "success",
    "value": {
      "categories": [],
      "id": 267,
      "joke": "Ozzy Osbourne bites the heads off of bats. Chuck Norris bites the heads off of Siberian Tigers."
    }
  }
  window.fetch = jest.fn().mockImplementation(() => Promise.resolve({ json: () => reply, ok: true }))
  const store = new ChuckNorrisStore()
  await store.nextQuote()
  expect(store.quote).toEqual(reply.value.joke)
})

it("sets an error message on error", async function () {
  expect.assertions(1)
  window.fetch = jest.fn().mockImplementation(() => { throw new Error("Jörgjörgjörgjörgjörgjörg...ULF!") })
  const store = new ChuckNorrisStore()
  await store.nextQuote()
  expect(store.quote).toEqual("Fehler! Error: Jörgjörgjörgjörgjörgjörg...ULF!")
})
