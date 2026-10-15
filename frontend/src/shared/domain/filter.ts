/*
 * The list endpoints accept a `filter` expression (spring-filter syntax). These helpers build the common ones so
 * that screens never assemble the syntax by hand.
 */

/** `field ~ '*text*'` (contains); null when there is no text to search for. */
export const contains = (field: string, text: string): string | null =>
  text ? `${field} ~ '*${text}*'` : null;

/** All the given conditions at once; null when none of them is set. */
export const allOf = (conditions: Array<string | null>): string | null => {
  const set = conditions.filter((condition): condition is string => !!condition);
  return set.length > 0 ? set.join(" and ") : null;
};
