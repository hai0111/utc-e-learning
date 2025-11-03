export const toSlug = (text: string): string => {
  return text
    .normalize("NFD") // split accent from letters
    .replace(/[\u0300-\u036f]/g, "") // remove accents
    .replace(/[^a-zA-Z0-9\s-]/g, "") // remove special chars
    .trim()
    .replace(/\s+/g, "-") // replace spaces with dash
    .toLowerCase();
};

export function toLookup<T extends Record<string, any>, K extends keyof T>(
  arr: T[],
  key: K
): Map<T[K], T> {
  return arr.reduce((acc, item) => {
    acc.set(item[key], item);
    return acc;
  }, new Map<T[K], T>());
}

export const clearAllCookies = () => {
  const cookies = document.cookie.split(";");

  for (let i = 0; i < cookies.length; i++) {
    const cookie = cookies[i]!.trim();
    const eqPos = cookie.indexOf("=");
    const name = eqPos > -1 ? cookie.substring(0, eqPos) : cookie;

    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
  }
};
