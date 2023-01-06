export interface IForecast {
  time: string;
  temperature: number;
  status: string;
};

export interface IWeeklyForecastsResult {
  last_update: string;
  forecasts: IForecast[];
};
