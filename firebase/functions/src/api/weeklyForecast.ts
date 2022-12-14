import { onRequest } from 'firebase-functions/v2/https';
import axios from 'axios';
import {
  IApiResult,
} from '../openmetro';
import {
  IDailyForecast,
  IForecast,
  IWeeklyForecastsResult,
} from '../api_interface';

export const weeklyForcast = onRequest(async (req, res) => {

  const rawData = await getRawForecastData(
    35.654451866025134,
    139.75333904900143
  );
  if (!rawData) {

    res.json({
      'error': true,
      'message': 'no data',
    });
    return;
  }
  const forecasts = convertToIForecasts(rawData);
  const dailyForecasts = reduceToDailyForecasts(forecasts);

  const now = new Date();
  const last_update = getISO8601(now);

  const result: IWeeklyForecastsResult = {
    last_update,
    dailyForecasts: dailyForecasts,
  };

  res.json(result);
});

const reduceToDailyForecasts = (
  forecasts: IForecast[]
): IDailyForecast[] => {
  const results: IDailyForecast[] = [];
  const N = forecasts.length;
  if (N === 0) {
    return results;
  }

  let i = 0;
  while (i < N) {
    const datePrefix = forecasts[i].time.replace(/T.*$/, '');
    const oneDay: IDailyForecast = {
      date: datePrefix,
      forecasts: [],
    };
    while ((i < N) && (forecasts[i].time.startsWith(datePrefix))) {
      oneDay.forecasts.push(forecasts[i]);
      i += 1;
    }
    results.push(oneDay);
  }
  return results;
};

const getRawForecastData = async (
  latitude: number,
  longitude: number,
): Promise<IApiResult | null> => {
  const url = `
  https://api.open-meteo.com/v1/forecast?
  latitude=${latitude}&
  longitude=${longitude}&
  hourly=temperature_2m,cloudcover,rain,snowfall&
  timezone=Asia/Tokyo
  `.replace(/[\s\n\r]/g, '');

  try {
    const response = await axios.get(url);
    return response.data as Promise<IApiResult>;
  } catch (exception) {
    return Promise.resolve(null);
  }
}

const convertToIForecasts = (
  data: IApiResult,
): IForecast[] => {
  const results: IForecast[] = [];

  data.hourly!!.time.forEach((
    time: string,
    idx: number,
  ): void => {
    let status = 'sunny';
    const cloudcover = data.hourly!!.cloudcover!![idx];
    const rain = data.hourly!!.rain!![idx];
    const snowfall = data.hourly!!.snowfall!![idx];

    if (cloudcover >= 30) {
      if ((rain < 5) && (snowfall === 0)) {
        status = 'cloudy';
      } else if (rain > 4) {
        status = 'rain';
      } else {
        status = 'snow';
      }
    }

    results.push({
      time,
      'temperature': data.hourly!!.temperature_2m!![idx],
      status,
    });
  });

  return results;
};

const getISO8601 = (date: Date) => {
  const YYYY = date.getFullYear();
  const MM = (date.getMonth() + 1).toString().padStart(2, '0');
  const DD = date.getDate().toString().padStart(2, '0');
  const hh = date.getHours().toString().padStart(2, '0');
  const mm = date.getMinutes().toString().padStart(2, '0');
  return `${YYYY}-${MM}-${DD}T${hh}:${mm}`;
}
