import { dateISO8601 } from './utils';

describe("dateISO8601 test", () => {
  it ("should return correct ISO8601 datetime in UTC", () => {
    const datetime = new Date("2023/1/18 11:25:34Z-7");
    const result = dateISO8601(datetime);
    expect(result).toBe("2023-01-18T18:25");
  });
});
