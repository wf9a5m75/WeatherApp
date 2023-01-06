import { onRequest } from 'firebase-functions/v2/https';
import axios from 'axios';
import {
  IApiResult,
} from '../openmetro';
import {
  IForecast,
  IWeeklyForecastsResult,
} from '../api_interface';

export const weeklyForcasts = onRequest(async (req, res) => {

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
  const forecasts = generateWeeklyForecasts(rawData);


  const now = new Date();
  const last_update = getISO8601(now);

  const result: IWeeklyForecastsResult = {
    last_update,
    forecasts,
  };

  res.json(result);
});

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

const generateWeeklyForecasts = (
  data: IApiResult,
): IForecast[] => {
  const forecasts: IForecast[] = [];

  data.hourly!!.time.forEach((
    time: string,
    idx: number,
  ): void => {
    const dTime = new Date(time);
    const hour = dTime.getHours();
    if (hour % 3 !== 0) {
      return;
    }

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

    forecasts.push({
      time,
      'temperature': data.hourly!!.temperature_2m!![idx],
      status,
    });
  });

  return forecasts;
};

const getISO8601 = (date: Date) => {
  const YYYY = date.getFullYear();
  const MM = (date.getMonth() + 1).toString().padStart(2, '0');
  const DD = date.getDate().toString().padStart(2, '0');
  const hh = date.getHours().toString().padStart(2, '0');
  const mm = date.getMinutes().toString().padStart(2, '0');
  return `${YYYY}-${MM}-${DD}T${hh}:${mm}`;
}
