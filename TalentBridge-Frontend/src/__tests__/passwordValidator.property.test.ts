import { describe, it, expect } from "vitest";
import * as fc from "fast-check";
import {
  validatePassword,
  validatePasswordMatch,
  isValidEmail,
} from "@/utils/passwordValidator";

/**
 * Property-based tests for password validation
 * Feature: forgot-password, Property 3: Password validation completeness (frontend)
 * Validates: Requirements 3.1, 3.2, 3.3, 3.5
 */
describe("Password Validation Properties", () => {
  // Helper to generate valid passwords (at least 8 chars, has uppercase, has number)
  const validPasswordArb = fc
    .tuple(
      fc.array(fc.constantFrom(...'abcdefghijklmnopqrstuvwxyz'.split('')), { minLength: 6, maxLength: 20 }),
      fc.constantFrom(...'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('')),
      fc.constantFrom(...'0123456789'.split(''))
    )
    .map(([base, upper, num]) => base.join('') + upper + num);

  /**
   * Feature: forgot-password, Property 3: Password validation completeness
   * Valid passwords (meeting all requirements) should pass validation
   */
  it("should pass validation for passwords meeting all requirements", () => {
    fc.assert(
      fc.property(validPasswordArb, (password) => {
        const result = validatePassword(password);
        expect(result.valid).toBe(true);
        expect(result.errors).toHaveLength(0);
        expect(result.checks.minLength).toBe(true);
        expect(result.checks.hasUppercase).toBe(true);
        expect(result.checks.hasNumber).toBe(true);
      }),
      { numRuns: 100 }
    );
  });

  /**
   * Feature: forgot-password, Property 3: Password validation completeness
   * Passwords shorter than 8 characters should fail validation
   */
  it("should fail validation for passwords shorter than 8 characters", () => {
    const shortPasswordArb = fc.string({ minLength: 1, maxLength: 7 });

    fc.assert(
      fc.property(shortPasswordArb, (password) => {
        const result = validatePassword(password);
        expect(result.checks.minLength).toBe(false);
        expect(result.valid).toBe(false);
      }),
      { numRuns: 100 }
    );
  });

  /**
   * Feature: forgot-password, Property 3: Password validation completeness
   * Passwords without uppercase should fail validation
   */
  it("should fail validation for passwords without uppercase letters", () => {
    // Generate lowercase + numbers only
    const noUppercaseArb = fc
      .array(fc.constantFrom(...'abcdefghijklmnopqrstuvwxyz0123456789'.split('')), { minLength: 8, maxLength: 20 })
      .map(arr => arr.join(''));

    fc.assert(
      fc.property(noUppercaseArb, (password) => {
        const result = validatePassword(password);
        expect(result.checks.hasUppercase).toBe(false);
        expect(result.valid).toBe(false);
      }),
      { numRuns: 100 }
    );
  });

  /**
   * Feature: forgot-password, Property 3: Password validation completeness
   * Passwords without numbers should fail validation
   */
  it("should fail validation for passwords without numbers", () => {
    // Generate letters only with at least one uppercase
    const noNumberArb = fc
      .tuple(
        fc.array(fc.constantFrom(...'abcdefghijklmnopqrstuvwxyz'.split('')), { minLength: 7, maxLength: 19 }),
        fc.constantFrom(...'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split(''))
      )
      .map(([arr, upper]) => arr.join('') + upper);

    fc.assert(
      fc.property(noNumberArb, (password) => {
        const result = validatePassword(password);
        expect(result.checks.hasNumber).toBe(false);
        expect(result.valid).toBe(false);
      }),
      { numRuns: 100 }
    );
  });

  /**
   * Feature: forgot-password, Property 3: Password validation completeness
   * Validation checks should be consistent with overall validity
   */
  it("should have consistent validation results", () => {
    const anyPasswordArb = fc.string({ minLength: 0, maxLength: 50 });

    fc.assert(
      fc.property(anyPasswordArb, (password) => {
        const result = validatePassword(password);
        const allChecksPassed =
          result.checks.minLength &&
          result.checks.hasUppercase &&
          result.checks.hasNumber;

        expect(result.valid).toBe(allChecksPassed);
      }),
      { numRuns: 100 }
    );
  });
});

/**
 * Property-based tests for password confirmation matching
 * Feature: forgot-password, Property 4: Password confirmation matching
 * Validates: Requirements 3.4
 */
describe("Password Confirmation Matching Properties", () => {
  /**
   * Feature: forgot-password, Property 4: Password confirmation matching
   * Identical passwords should match
   */
  it("should return true for identical passwords", () => {
    const passwordArb = fc.string({ minLength: 1, maxLength: 100 });

    fc.assert(
      fc.property(passwordArb, (password) => {
        expect(validatePasswordMatch(password, password)).toBe(true);
      }),
      { numRuns: 100 }
    );
  });

  /**
   * Feature: forgot-password, Property 4: Password confirmation matching
   * Different passwords should not match
   */
  it("should return false for different passwords", () => {
    const differentPasswordsArb = fc
      .tuple(
        fc.string({ minLength: 1, maxLength: 50 }),
        fc.string({ minLength: 1, maxLength: 50 })
      )
      .filter(([a, b]) => a !== b);

    fc.assert(
      fc.property(differentPasswordsArb, ([password1, password2]) => {
        expect(validatePasswordMatch(password1, password2)).toBe(false);
      }),
      { numRuns: 100 }
    );
  });
});

/**
 * Property-based tests for email format validation
 * Feature: forgot-password, Property 2: Invalid email format rejection
 * Validates: Requirements 1.4
 */
describe("Email Format Validation Properties", () => {
  /**
   * Feature: forgot-password, Property 2: Invalid email format rejection
   * Valid email formats should pass validation
   */
  it("should accept valid email formats", () => {
    const validEmailArb = fc
      .tuple(
        fc.array(fc.constantFrom(...'abcdefghijklmnopqrstuvwxyz0123456789'.split('')), { minLength: 1, maxLength: 10 }),
        fc.array(fc.constantFrom(...'abcdefghijklmnopqrstuvwxyz'.split('')), { minLength: 1, maxLength: 10 }),
        fc.array(fc.constantFrom(...'abcdefghijklmnopqrstuvwxyz'.split('')), { minLength: 2, maxLength: 4 })
      )
      .map(([local, domain, tld]) => `${local.join('')}@${domain.join('')}.${tld.join('')}`);

    fc.assert(
      fc.property(validEmailArb, (email) => {
        expect(isValidEmail(email)).toBe(true);
      }),
      { numRuns: 100 }
    );
  });

  /**
   * Feature: forgot-password, Property 2: Invalid email format rejection
   * Strings without @ should be rejected
   */
  it("should reject strings without @ symbol", () => {
    const noAtArb = fc.string({ minLength: 1, maxLength: 50 }).filter(s => !s.includes('@'));

    fc.assert(
      fc.property(noAtArb, (notEmail) => {
        expect(isValidEmail(notEmail)).toBe(false);
      }),
      { numRuns: 100 }
    );
  });

  /**
   * Feature: forgot-password, Property 2: Invalid email format rejection
   * Empty strings should be rejected
   */
  it("should reject empty strings", () => {
    expect(isValidEmail("")).toBe(false);
  });

  /**
   * Feature: forgot-password, Property 2: Invalid email format rejection
   * Strings with multiple @ should be rejected
   */
  it("should reject strings with multiple @ symbols", () => {
    const multipleAtArb = fc
      .tuple(
        fc.string({ minLength: 1, maxLength: 10 }),
        fc.string({ minLength: 1, maxLength: 10 }),
        fc.string({ minLength: 1, maxLength: 10 })
      )
      .map(([a, b, c]) => `${a}@${b}@${c}`);

    fc.assert(
      fc.property(multipleAtArb, (invalidEmail) => {
        expect(isValidEmail(invalidEmail)).toBe(false);
      }),
      { numRuns: 100 }
    );
  });
});
