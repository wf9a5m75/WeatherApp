import { FirestoreManager } from './firestore-manager';
import { StorageManager } from './storage-manager';

export class CacheManager {
  cacheInfo: any = undefined;

  constructor(
    private cacheFileName: string,
    private db: FirestoreManager,
    private storage: StorageManager,
    private calcHash: (contents: string) => string,
  ) {}

  async validateETag(
    requestETag: string,
  ): Promise<boolean> {
    await this.loadMetaFile();
    return Promise.resolve(
      this.cacheInfo &&
      this.cacheInfo.etag === requestETag
    );
  }

  async validateLastUpdate(
    last_update_time: number,
  ): Promise<boolean> {
    await this.loadMetaFile();
    return Promise.resolve(
      this.cacheInfo &&
      this.cacheInfo.timestamp === last_update_time
    );
  }

  async getContents(): Promise<any> {
    const filePath = `cache/${this.cacheFileName}.data.txt`;
    return await this.storage.getContents(filePath);
  }

  async saveCache(
    timestamp: number,
    contents: string,
  ): Promise<string> {
    const metaFilePath = `cache/${this.cacheFileName}.meta.json`;
    const dataFilePath = `cache/${this.cacheFileName}.data.txt`;

    const etag = this.calcHash(contents);

    const tasks = [
      // Create the cache files
      this.storage.saveContents(metaFilePath, JSON.stringify({
        etag,
        timestamp,
      })),

      this.storage.saveContents(dataFilePath, contents),
    ];

    await Promise.all(tasks);

    return Promise.resolve(etag);
  }

  async getETag(): Promise<string | null> {
    await this.loadMetaFile();
    if (!this.cacheInfo) {
      return Promise.resolve(null);
    }
    return Promise.resolve(this.cacheInfo.etag);
  }

  private async loadMetaFile() {
    if (this.cacheInfo) {
      return;
    }
    const filePath = `cache/${this.cacheFileName}.meta.json`;
    const cacheInfoTxt = await this.storage.getContents(filePath);
    try {
      if (cacheInfoTxt) {
        this.cacheInfo = JSON.parse(cacheInfoTxt);
      }
    } catch (exception) {
      // do nothing
    }
  }
}
