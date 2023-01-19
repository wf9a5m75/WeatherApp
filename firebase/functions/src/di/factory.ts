// import { FirebaseApp } from '@firebase/app-types';
import { applicationDefault, App, Credential } from 'firebase-admin/app';
import * as firebase from 'firebase-admin';
import { FirestoreManager } from './firestore-manager';
import { StorageManager } from './storage-manager';
import { CacheManager } from './cache-manager';
import { Firestore } from 'firebase-admin/firestore';
import { Storage } from 'firebase-admin/storage';
import { Bucket } from '@google-cloud/storage';
import {
  getHash,
  streamToString,
} from './utils';
export { FirestoreManager } from './firestore-manager';
export { CacheManager } from './cache-manager';

// Needs to set environment
// <Windows powert shell>
//   $env:GOOGLE_APPLICATION_CREDENTIALS="C:\Users\username\Downloads\service-account-file.json"
// <Linux or macOS>
//   export GOOGLE_APPLICATION_CREDENTIALS="/home/user/Downloads/service-account-file.json"
// const firebaseApp = initializeApp({
//   credential: applicationDefault()
// });
const projectId: string = 'weather-app-8a034';
const credential: Credential = applicationDefault();

// Firebase App
const admin: App = firebase.initializeApp({
  credential,
  storageBucket: `${projectId}.appspot.com`,
});

// Storage
const storage: Storage = (admin as any).storage();
const bucket: Bucket = storage.bucket();
const storageMgr: StorageManager = new StorageManager(
  bucket,
  streamToString,
);

// Firestore
const db: Firestore = (admin as any).firestore();
const dbMgr: FirestoreManager = new FirestoreManager(db);


// Exports
export const getFirestoreManager = () => dbMgr;
export const getStorageManager = () => storageMgr;

export const getCacheManager = (contentsFileName: string): CacheManager => {
  return new CacheManager(
    contentsFileName,
    getFirestoreManager(),
    getStorageManager(),
    getHash,
  );
};
