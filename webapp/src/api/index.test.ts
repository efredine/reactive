import { fetchJSON } from "."

it("returns response on ok", async function () {
  interface TestType { name: string, count: number }
  window.fetch = jest.fn().mockImplementation(() => Promise.resolve({ json: () => ({ name: "Lurch", count: 4711 } as TestType) }))
  const response = await fetchJSON("/url")
  expect(response).toBeDefined()
})

it("throws an error on error", async function () {
  expect.assertions(1)
  window.fetch = jest.fn().mockImplementation(() => { throw new Error("Jörgjörgjörgjörgjörgjörg...ULF!") })
  try {
    await fetchJSON("/url")
  } catch (reason) {
    expect(reason).toEqual(new Error("Jörgjörgjörgjörgjörgjörg...ULF!"))
  }
})
