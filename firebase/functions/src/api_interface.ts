export interface IForecast {
  time: string;
  temperature: number;
  status: string;
};

export interface IDailyForecast {
  date: string;
  forecasts: IForecast[];
}

export interface IWeeklyForecastsResult {
  last_update: string;
  dailyForecasts: IDailyForecast[];
};
