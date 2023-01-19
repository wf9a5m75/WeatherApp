import * as express from 'express';
import { onRequest, Request } from 'firebase-functions/v2/https';
import {
  getCacheManager,
  getFirestoreManager,
  FirestoreManager,
  CacheManager
} from '../di/factory';
import { PREFECTURE } from '../const/';
import { dateISO8601 } from '../di/utils';
import { StatusCodes } from 'http-status-codes';
import { FirestoreUtils } from '../di/firestore-utils';
import { CollectionReference } from 'firebase-admin/firestore';

type ICity = {
  id: string;
  name: string;
}
type IPrefecture = {
  id: string;
  name: string;
  cities: ICity[];
}


export class ViewModel {
  constructor(
    private db: FirestoreManager,
    private cacheMgr: CacheManager,
    private getUTCDateTime: (date: Date) => string,
    private collectionToData: (collectionRef: CollectionReference) => Promise<any[]>,
    private PREF_NAMES: { [key: string]: string},
  ) { }

  async locations(req: Request, res: express.Response): Promise<void> {

    // Compare the `If-None-Match` value and the cache ETag values.
    // If they are the same, the cache of the client side is valid.
    const isValidETag = await this.cacheMgr.validateETag(
      "cities",
      req.get('If-None-Match')
    );
    if (isValidETag) {
      // HTTP 304: Not Modified
      res.status(StatusCodes.NOT_MODIFIED).end();
      return;
    }

    // Compare the last update of the `last_updates/cities` and
    // the value of `cache/cities.meta`.
    // If they are the same, the contents are not changed.
    // So, returns the cached data
    const lastUpdate  = await this.db.getDocData('last_updates/cities');
    const hasDataNotChanged = await this.cacheMgr.validateLastUpdate(
      "cities",
      lastUpdate.timestamp
    );

    if (hasDataNotChanged) {
      const contents = await this.cacheMgr.getContents("cities");
      const etag = await this.cacheMgr.getETag("cities");
      if (contents && etag) {
        res.setHeader('ETag', etag);
        res.setHeader('Content-Type', 'Application/json');
        res.status(StatusCodes.OK);
        res.send(contents).end();
        return;
      }
    }

    // If there is no cache data, we need to create it.
    const citiesRef = this.db.getCollectionRef('cities');
    const cities = await this.collectionToData(citiesRef);

    const pref_cities: {
      [key: string]: IPrefecture;
    } = {};

    const prefIDs = Object.keys(this.PREF_NAMES);
    prefIDs.forEach((prefId: string) => {
      pref_cities[prefId] = {
        id: prefId,
        name: this.PREF_NAMES[prefId],
        cities: [],
      }
    });

    try {
      cities.forEach((cityData: any) => {
        if (!(cityData.pref in pref_cities)) {
          throw `unknow pref value: "${cityData.pref}"`;
        }
        pref_cities[cityData.pref].cities.push({
          name: cityData.city,
          id: cityData.id,
        });
      });
    } catch (e) {
      console.error(e);
      res.status(StatusCodes.INTERNAL_SERVER_ERROR).end();
      return;
    }

    const resultPrefectures = [];
    prefIDs.forEach((prefId: string) => {
      resultPrefectures.push(pref_cities[prefId]);
    });

    const lastUpdateISO8601 = this.getUTCDateTime(new Date(lastUpdate.timestamp));
    const result = {
      last_update: lastUpdateISO8601,
      prefectures: resultPrefectures,
    };

    // Save cache data, then obtain the ETag
    const txt = JSON.stringify(result);
    const etag = await this.cacheMgr.saveCache(
      "cities",
      lastUpdate.timestamp,
      txt,
    );

    // Return the API response
    res.setHeader('ETag', etag);
    res.status(StatusCodes.OK);
    res.json(result).end();
  }
}

exports.locations = onRequest(async (req, res) => {
  const db = getFirestoreManager();
  const cacheMgr = getCacheManager();

  const app = new ViewModel(
    db,
    cacheMgr,
    dateISO8601,
    FirestoreUtils.toData,
    PREFECTURE,
  );
  await app.locations(req, res);
});
