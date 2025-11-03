import type { IColorItem } from "./types/common";

export const ACTIVE_ITEMS: IColorItem[] = [
  {
    title: "Active",
    value: true,
    color: "green",
  },
  {
    title: "Inactive",
    value: false,
    color: "#333",
  },
];

export const DEFAULT_PAGER = {
  page: 0,
  size: 10,
};
