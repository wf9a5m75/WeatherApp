import { App } from './app.js';
import { Factory } from './factory.js';
import { getPrefecture, PREFECTURE } from './prefectures.js';
import { dateISO8601 } from './utils.js';
import { firebaseConfig } from './firebase.config.js';
import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.15.0/firebase-app.js';
import { Authentication } from './authentication.js';

const initMap = async () => {
  if (document.readyState === 'loading' || !window.google) {
    return;
  }

  // Initialize Firebase
  const firebaseApp = initializeApp(firebaseConfig);

  // Initialize Google Maps
  const mapDiv = document.getElementById('map');
  const map = Factory.createMap(mapDiv, {
    center: {
      lat: 35,
      lng: 137,
    },
    zoom: 5,
    noClear: true,
    mapTypeControl: false,
  });

  const db = Factory.createDB(firebaseApp, 'cities');
  const createMarker = Factory.createMarker;
  const infoWnd = Factory.createInfoWnd();
  const geocoder = Factory.createGeocoder();
  const auth = Factory.createAuthentication(
    firebaseApp,
    Factory.createUser,
  );


  // Create the application view model
  const app = new App(
    auth,
    map,
    db,
    geocoder,
    createMarker,
    infoWnd,
    getPrefecture,
    dateISO8601,
    PREFECTURE,
  );

  // Restore all markers
  app.restoreMarkers();


  const loginBtn = document.getElementById('loginBtn');
  loginBtn.addEventListener('click', async () => {
    await app.signIn();
  });
};
window.initMap = initMap;
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initMap);
} else {
  initMap();
}
