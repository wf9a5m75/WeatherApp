import { App } from './app.js';
import { Factory } from './factory.js';
import { getPrefecture, PREFECTURE } from './prefectures.js';
import { dateISO8601 } from './utils.js';
import { firebaseConfig } from './firebase.config.js';
import { initializeApp } from "https://www.gstatic.com/firebasejs/9.15.0/firebase-app.js";

const initMap = async () => {
  if (document.readyState === 'loading' || !window.google) {
    return;
  }

  // Initialize Firebase
  const firebaseApp = initializeApp(firebaseConfig);
  console.log(firebaseConfig);

  // Initialize Google Maps
  const mapDiv = document.getElementById('map');
  const map = Factory.createMap(mapDiv, {
    center: {
      lat: 35,
      lng: 137,
    },
    zoom: 5,
    noClear: true,
  });

  const db = Factory.createDB(firebaseApp, 'cities');
  const createMarker = Factory.createMarker;
  const geocoder = Factory.createGeocoder();

  // Create the application view model
  const app = new App(
    map,
    db,
    geocoder,
    createMarker,
    getPrefecture,
    dateISO8601,
    PREFECTURE,
  );

  // Restore all markers
  app.restoreMarkers();
};
window.initMap = initMap;
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initMap);
} else {
  initMap();
}
