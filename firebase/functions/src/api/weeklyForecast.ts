import * as express from 'express';
import { onRequest, Request } from 'firebase-functions/v2/https';
import { StatusCodes } from 'http-status-codes';
import {
  IApiResult,
} from '../openmetro';
import {
  IDailyForecast,
  IForecast,
  IWeeklyForecastsResult,
} from '../api_interface';
import {
  awaitTimeout,
  dateISO8601,
  getHash
} from '../di/utils';

import {
  getFirestoreManager,
  FirestoreManager
} from '../di/factory';
import { FirestoreUtils } from '../di/firestore-utils';

export class ViewModel {
  constructor(
    private db: FirestoreManager,
    private getUTCDateTime: (date: Date) => string
  ) {}

  async weeklyForcast(req: Request, res: express.Response): Promise<void> {
    const cityId: string = (req.query.city_id || '').toString();
    if (cityId === '') {
      res.status(StatusCodes.BAD_REQUEST).json({
        "status": "error",
        "message": "city_id parameter is required."
      });
      return;
    }


    const docPath = `cities/${cityId}`;

    const data = await this.db.getDocData(docPath);
    if (!data || !data.weather) {
      res.status(StatusCodes.NOT_FOUND).end();
      return;
    }

    const etag = data.weather.etag;

    // Compare the `If-None-Match` value and the cache ETag values.
    // If they are the same, the cache of the client side is valid.
    if (etag === req.get('If-None-Match')) {
      // HTTP 304: Not Modified
      res.status(StatusCodes.NOT_MODIFIED).end();
      return;
    }

    res.setHeader('ETag', etag);
    res.setHeader('Content-Type', 'Application/json');
    res.status(StatusCodes.OK);
    res.send({
      last_update: this.getUTCDateTime(data.weather.last_update.toDate()),
      overall: "sunny", // TODO:
      forecasts: data.weather.forecast,
    }).end();
  }
}
export const weeklyForcast = onRequest(async (req, res) => {
  const db = getFirestoreManager();

  const app = new ViewModel(
    db,
    dateISO8601
  );

  await app.weeklyForcast(req, res);
});
