describe('Authentication Tests', () => {
  beforeEach(() => {
    cy.visit('/auth?mode=login')
  })

  describe('FE_AUTH_01: Hiển thị form đăng nhập', () => {
    it('should display login form correctly', () => {
      cy.get('form').should('be.visible')
      cy.get('input[type="email"]').should('be.visible')
      cy.get('input[type="password"]').should('be.visible')
      cy.get('button[type="submit"]').should('contain', 'Đăng nhập')
    })
  })

  describe('FE_AUTH_02: Validation form đăng nhập', () => {
    it('should show error for invalid email format', () => {
      cy.get('input[type="email"]').type('invalid-email')
      cy.get('input[type="password"]').type('Password123')
      cy.get('button[type="submit"]').click()
      cy.get('.error-message').should('contain', 'Email không hợp lệ')
    })

    it('should show error for empty fields', () => {
      cy.get('button[type="submit"]').click()
      cy.get('.error-message').should('be.visible')
    })
  })

  describe('FE_AUTH_03: Hiển thị form đăng ký', () => {
    it('should display register form when switching to register mode', () => {
      cy.visit('/auth?mode=register')
      cy.get('form').should('be.visible')
      cy.get('input[name="name"]').should('be.visible')
      cy.get('input[type="email"]').should('be.visible')
      cy.get('input[type="password"]').should('be.visible')
      cy.get('input[name="confirmPassword"]').should('be.visible')
      cy.get('button[type="submit"]').should('contain', 'Đăng ký')
    })
  })

  describe('FE_AUTH_04: Validation form đăng ký', () => {
    beforeEach(() => {
      cy.visit('/auth?mode=register')
    })

    it('should show error when passwords do not match', () => {
      cy.get('input[name="name"]').type('Test User')
      cy.get('input[type="email"]').type('test@example.com')
      cy.get('input[type="password"]').first().type('Password123!')
      cy.get('input[name="confirmPassword"]').type('DifferentPassword123!')
      cy.get('button[type="submit"]').click()
      cy.get('.error-message').should('contain', 'Mật khẩu không khớp')
    })
  })

  describe('FE_AUTH_05: Chuyển hướng sau đăng nhập thành công', () => {
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

  describe('FE_AUTH_06: Hiển thị error message khi đăng nhập thất bại', () => {
    it('should display error message on login failure', () => {
      cy.intercept('POST', '/auth/login', {
        statusCode: 401,
        body: {
          message: 'Email hoặc mật khẩu không đúng'
        }
      })

      cy.get('input[type="email"]').type('wrong@example.com')
      cy.get('input[type="password"]').type('WrongPassword')
      cy.get('button[type="submit"]').click()
      
      cy.get('.error-message').should('contain', 'Email hoặc mật khẩu không đúng')
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

  describe('FE_AUTH_08: Logout và clear local storage', () => {
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
