import {
  Bucket,
  File,
  GetFilesOptions,
  GetFilesResponse,
} from '@google-cloud/storage';
import { Readable } from 'node:stream';

export class StorageManager {
  constructor(
    private bucket: Bucket,
    private streamToStr: (stream: Readable) => Promise<string>
  ) { }

  async getContents(filePath: string): Promise<string | null> {
    const file: File | null = await this.getFile(filePath);

    if (!file) {
      return Promise.resolve(null);
    }

    return await this.streamToStr(file.createReadStream({
      decompress: true,
    }));
  }

  async getFile(filePath: string): Promise<File | null> {
    const files: File[] = await this.getFiles({
      prefix: filePath,
    });
    if ((files.length !== 1) || (files[0].name !== filePath)) {
      return Promise.resolve(null);
    }
    return files[0];
  }

  async has(filePath: string): Promise<boolean> {
    const files: File[] = await this.getFiles({
      prefix: filePath,
    });

    return (files.length === 1) && (files[0].name === filePath);
  }

  async getFiles(query: GetFilesOptions = {}): Promise<File[]> {
    query.maxResults = 'maxResults' in query ? query.maxResults : 100;
    const results: File[] = [];

    const { ...res }: GetFilesResponse = await this.bucket.getFiles(query);
    res[0].forEach((file) => results.push(file));
    let nextQuery: any = res[1];

    while (nextQuery) {
      query.pageToken = nextQuery.pageToken;
      const { ...res }: GetFilesResponse = await this.bucket.getFiles(query);
      res[0].forEach((file) => results.push(file));
      nextQuery = res[1];
    }
    return results;
  }

  async saveContents(fileName: string, contents: string) {
    const file: File = this.bucket.file(fileName)
    await file.save(contents);
    await file.makePublic();
  }
}
