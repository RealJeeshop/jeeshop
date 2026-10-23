export function normalizeDataTableHeaders(headers) {
  return headers.map(({ text, value, ...header }) => ({
    ...header,
    title: header.title || text,
    key: header.key || value
  }))
}
