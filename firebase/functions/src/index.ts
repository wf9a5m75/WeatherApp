import { onRequest } from 'firebase-functions/v2/https';

exports.forecast = onRequest((req, res) => {
  console.log("Yes!");
  res.send("Hello World!");
});
