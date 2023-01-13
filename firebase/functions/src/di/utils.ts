import { Readable } from 'node:stream';
import * as crypto from 'crypto';

export const streamToString = async (stream: Readable): Promise<string> => {
  const chunks: Buffer[] = [];
  return new Promise((resolve, reject) => {
    stream.on('data', (chunk) => chunks.push(Buffer.from(chunk)));
    stream.on('error', (err) => reject(err));
    stream.on('end', () => resolve(Buffer.concat(chunks).toString('utf8')));
  })
};

export const dateISO8601 = (date: Date): string => {
  if (!(date instanceof Date)) {
    throw 'date parameter is not Date class';
  }
  const YYYY = date.getFullYear();
  const MM = (date.getMonth() + 1).toString().padStart(2, '0');
  const DD = date.getDate().toString().padStart(2, '0');
  const hh = date.getHours().toString().padStart(2, '0');
  const mm = date.getMinutes().toString().padStart(2, '0');
  return `${YYYY}-${MM}-${DD}T${hh}:${mm}`;
}

export const getHash = (contents: string) : string => {
  const hash: string = crypto.createHash('sha256').update(contents).digest('base64');
  return `"${hash}"`;
}

export const asyncWait = async (milli_seconds: number) => {
  await new Promise((resolve) => setTimeout(resolve, milli_seconds));
};
