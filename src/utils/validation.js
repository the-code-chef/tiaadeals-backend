const validateEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
};

const validatePassword = (password) => {
  // At least 8 characters, 1 uppercase, 1 lowercase, 1 number
  const re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
  return re.test(password);
};

const validateSignupInput = (firstName, lastName, email, password) => {
  const errors = [];

  if (!firstName || firstName.trim().length === 0) {
    errors.push('First name is required');
  }

  if (!lastName || lastName.trim().length === 0) {
    errors.push('Last name is required');
  }

  if (!email || !validateEmail(email)) {
    errors.push('Valid email is required');
  }

  if (!password || !validatePassword(password)) {
    errors.push('Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number');
  }

  return errors;
};

const validateLoginInput = (email, password) => {
  const errors = [];

  if (!email || !validateEmail(email)) {
    errors.push('Valid email is required');
  }

  if (!password) {
    errors.push('Password is required');
  }

  return errors;
};

module.exports = {
  validateEmail,
  validatePassword,
  validateSignupInput,
  validateLoginInput
}; 
 