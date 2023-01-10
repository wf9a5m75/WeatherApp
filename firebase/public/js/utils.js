export const dateISO8601 = (date) => {
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
