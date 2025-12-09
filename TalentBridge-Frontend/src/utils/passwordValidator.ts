export interface PasswordValidationResult {
  valid: boolean;
  errors: string[];
  checks: {
    minLength: boolean;
    hasUppercase: boolean;
    hasNumber: boolean;
  };
}

const MIN_LENGTH = 8;
const UPPERCASE_PATTERN = /[A-Z]/;
const NUMBER_PATTERN = /[0-9]/;

export function validatePassword(password: string): PasswordValidationResult {
  const errors: string[] = [];
  const checks = {
    minLength: password.length >= MIN_LENGTH,
    hasUppercase: UPPERCASE_PATTERN.test(password),
    hasNumber: NUMBER_PATTERN.test(password),
  };

  if (!checks.minLength) {
    errors.push("Mật khẩu phải có ít nhất 8 ký tự");
  }

  if (!checks.hasUppercase) {
    errors.push("Mật khẩu phải có ít nhất 1 chữ hoa");
  }

  if (!checks.hasNumber) {
    errors.push("Mật khẩu phải có ít nhất 1 số");
  }

  return {
    valid: errors.length === 0,
    errors,
    checks,
  };
}

export function validatePasswordMatch(
  password: string,
  confirmPassword: string
): boolean {
  return password === confirmPassword;
}

export function isValidEmail(email: string): boolean {
  const emailPattern = /^[\w.-]+@([\w-]+\.)+[\w-]{2,4}$/;
  return emailPattern.test(email);
}
