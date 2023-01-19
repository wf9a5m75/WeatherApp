import {
  dateISO8601,
  getHash,
  streamToString,
} from './utils';
import { Readable } from 'node:stream';

describe("di/utils/dateISO8601", () => {
  it ("should return correct ISO8601 datetime in UTC", () => {
    const datetime = new Date("2023/1/18 11:25:34Z-7");
    const result = dateISO8601(datetime);
    expect(result).toBe("2023-01-18T18:25");
  });
});

describe("di/utils/streamToString", () => {
  it ("should return correct string", async () => {
    const stream = new Readable();
    stream.push("hello ");
    stream.push("world\n");
    stream.push("This is ");
    stream.push("a ");
    stream.push("test ");
    stream.push("message.");
    stream.push(null);

    const result = await streamToString(stream);
    expect(result).toBe("hello world\nThis is a test message.");
  });
});

describe("di/utils/getHash", () => {
  it ("should return correct hash string", () => {
    const result = getHash("hello world");
    expect(result)
      .toBe("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9");
  });
});
