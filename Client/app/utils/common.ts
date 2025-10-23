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
