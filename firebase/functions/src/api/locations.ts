import { onRequest } from 'firebase-functions/v2/https';


exports.locations = onRequest((req, res) => {
  res.send("OK from locations");
});
