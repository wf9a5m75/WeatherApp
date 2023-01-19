import { StorageManager } from './storage-manager';
import {
  Bucket,
  File,
  GetFilesOptions,
  GetFilesResponse,
} from '@google-cloud/storage';
import { streamToString } from './utils';
import { Readable } from 'node:stream';

const createMockFile = (
  fileName: string,
  contents: string
) => {
  const dummyFile = jasmine.createSpyObj('File', [
    'createReadStream',
    'makePublic',
    'save',
  ], {
    'name': fileName
  });

  dummyFile.createReadStream
    .and
    .callFake(() => {
      const stream = new Readable();
      stream.push(contents);
      stream.push(null);
      return stream;
    });
  return dummyFile;
}
describe("di/storage-manager", () => {
  let bucketSpy;

  beforeEach (() => {
    bucketSpy = jasmine.createSpyObj(
      'Bucket',
      ['getFiles', 'file'],
    );
  });

  it ("should return all files in the directory", async () => {

    // Create 200 dummy files
    const files1 = [];
    const files2 = [];
    for (let i = 0; i < 100; i++) {
      const dummyFile = createMockFile(
        `cache/file${i}.txt`,
        `The contents of file${i}.txt`
      );
      files1.push(dummyFile);
    }
    for (let i = 100; i < 200; i++) {
      const dummyFile = createMockFile(
        `cache/file${i}.txt`,
        `The contents of file${i}.txt`
      );
      files2.push(dummyFile);
    }

    // getFiles() returns the dummy files.
    bucketSpy.getFiles
      .and
      .returnValues(
        [files1, {
          pageToken: "nextPage",
        }],
        [files2, null],
      );

    // Test the StorageManager.getFiles()
    const instance = new StorageManager(
      bucketSpy,
      (stream: Readable): Promise<string> => Promise.resolve("OK"),
    )

    const results = await instance.getFiles({
      prefix: "cache/"
    });
    expect(bucketSpy.getFiles).toHaveBeenCalledTimes(2);
    expect(results.length).toBe(200);
  })

  it ("should return the file contents", async () => {
    // Create a dummy file
    const dummyFile = createMockFile(
      "cache/dummyFile.txt",
      "Hello World"
    );
    const files = [dummyFile];

    // Mock: this.bucket.getFiles() returns the dummy file list
    bucketSpy.getFiles
      .and
      .returnValue([files, null]);

    // Create an instance
    const instance = new StorageManager(
      bucketSpy,
      streamToString,
    )

    // Try to obtain the dummyFile.txt
    const result = await instance.getContents("cache/dummyFile.txt")

    // The contents of the file should be the same
    expect(result).toBe("Hello World");
  });

  it ("should save the contents properly", async () => {

    // Create a dummy file
    const dummyFile = jasmine.createSpyObj('File', [
      'makePublic',
      'save',
    ]);

    // Mock: this.bucket.file() returns the dummy file
    bucketSpy.file.and.returnValue(dummyFile);

    // Create an instance
    const instance = new StorageManager(
      bucketSpy,
      (stream: Readable) => Promise.resolve(""),
    )

    // Try to save the contents
    await instance.saveContents("cache/dummyFile.txt", "Hello World");

    // Should be saved as expected.
    expect(bucketSpy.file).toHaveBeenCalledWith("cache/dummyFile.txt");
    expect(dummyFile.save).toHaveBeenCalledWith("Hello World");
    expect(dummyFile.makePublic).toHaveBeenCalled();
  });
});
