// The interface starts in English in a fresh browser, so the assertions use the English texts.
describe('Authentication Tests', () => {
  beforeEach(() => {
    cy.visit('/auth?mode=login')
  })

  describe('FE_AUTH_01: Login form is displayed', () => {
    it('should display login form correctly', () => {
      cy.get('form').should('be.visible')
      cy.get('input[type="email"]').should('be.visible')
      cy.get('input[type="password"]').should('be.visible')
      cy.get('button[type="submit"]').should('contain', 'Log in')
    })
  })

  describe('FE_AUTH_02: Login form validation', () => {
    it('should show error for invalid email format', () => {
      cy.get('input[type="email"]').type('invalid-email')
      cy.get('input[type="password"]').type('Password123')
      cy.get('button[type="submit"]').click()
      cy.get('.error-message').should('contain', 'Invalid email address')
    })

    it('should show error for empty fields', () => {
      cy.get('button[type="submit"]').click()
      cy.get('.error-message').should('be.visible')
    })
  })

  describe('FE_AUTH_03: Registration form is displayed', () => {
    it('should display register form when switching to register mode', () => {
      cy.visit('/auth?mode=register')
      cy.get('form').should('be.visible')
      cy.get('input[name="name"]').should('be.visible')
      cy.get('input[type="email"]').should('be.visible')
      cy.get('input[type="password"]').should('be.visible')
      cy.get('input[name="confirmPassword"]').should('be.visible')
      cy.get('button[type="submit"]').should('contain', 'Sign up')
    })
  })

  describe('FE_AUTH_04: Registration form validation', () => {
    beforeEach(() => {
      cy.visit('/auth?mode=register')
    })

    it('should show error when passwords do not match', () => {
      cy.get('input[name="name"]').type('Test User')
      cy.get('input[type="email"]').type('test@example.com')
      cy.get('input[type="password"]').first().type('Password123!')
      cy.get('input[name="confirmPassword"]').type('DifferentPassword123!')
      cy.get('button[type="submit"]').click()
      cy.get('.error-message').should('contain', 'Passwords do not match')
    })
  })

  describe('FE_AUTH_05: Redirect after a successful login', () => {
    it('should redirect to home page after successful login', () => {
      // Mock successful login
      cy.intercept('POST', '/auth/login', {
        statusCode: 200,
        body: {
          data: {
            access_token: 'mock-token',
            user: {
              email: 'test@example.com',
              name: 'Test User'
            }
          }
        }
      })

      cy.get('input[type="email"]').type('test@example.com')
      cy.get('input[type="password"]').type('Password123!')
      cy.get('button[type="submit"]').click()
      
      cy.url().should('include', '/home')
    })
  })

  describe('FE_AUTH_06: Error message when the login fails', () => {
    it('should display error message on login failure', () => {
      cy.intercept('POST', '/auth/login', {
        statusCode: 401,
        body: {
          message: 'Incorrect email or password'
        }
      })

      cy.get('input[type="email"]').type('wrong@example.com')
      cy.get('input[type="password"]').type('WrongPassword')
      cy.get('button[type="submit"]').click()
      
      cy.get('.error-message').should('contain', 'Incorrect email or password')
    })
  })

  describe('FE_AUTH_07: Remember me functionality', () => {
    it('should save credentials when remember me is checked', () => {
      cy.get('input[type="checkbox"][name="rememberMe"]').check()
      cy.get('input[type="email"]').type('test@example.com')
      cy.get('input[type="password"]').type('Password123!')
      
      // Check that localStorage or cookies are set
      cy.window().then((win) => {
        expect(win.localStorage.getItem('rememberMe')).to.exist
      })
    })
  })

  describe('FE_AUTH_08: Logout clears local storage', () => {
    it('should clear local storage and redirect on logout', () => {
      // First login
      localStorage.setItem('access_token', 'mock-token')
      localStorage.setItem('user', JSON.stringify({ email: 'test@example.com' }))
      
      cy.visit('/home')
      cy.get('[data-testid="logout-btn"]').click()
      
      cy.window().then((win) => {
        expect(win.localStorage.getItem('access_token')).to.be.null
        expect(win.localStorage.getItem('user')).to.be.null
      })
      
      cy.url().should('include', '/auth')
    })
  })
})
