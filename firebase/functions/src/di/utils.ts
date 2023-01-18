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
  const YYYY = date.getUTCFullYear();
  const MM = (date.getUTCMonth() + 1).toString().padStart(2, '0');
  const DD = date.getUTCDate().toString().padStart(2, '0');
  const hh = date.getUTCHours().toString().padStart(2, '0');
  const mm = date.getUTCMinutes().toString().padStart(2, '0');
  return `${YYYY}-${MM}-${DD}T${hh}:${mm}`;
}

export const getHash = (contents: string) : string => {
  const hash: string = crypto.createHash('sha256').update(contents).digest('base64');
  return `"${hash}"`;
}
