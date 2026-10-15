describe('Job Page Tests', () => {
  beforeEach(() => {
    cy.visit('/jobs')
  })

  describe('FE_JOB_01: Hiển thị danh sách jobs', () => {
    it('should display job list correctly', () => {
      cy.get('[data-testid="job-grid"]').should('be.visible')
      cy.get('[data-testid="job-card"]').should('have.length.greaterThan', 0)
    })
  })

  describe('FE_JOB_02: Filter jobs theo level', () => {
    it('should filter jobs by level', () => {
      cy.get('[data-testid="level-filter"]').select('FRESHER')
      cy.wait(500)
      cy.get('[data-testid="job-card"]').each(($el) => {
        cy.wrap($el).should('contain', 'FRESHER')
      })
    })

    it('should filter jobs by INTERN level', () => {
      cy.get('[data-testid="level-filter"]').select('INTERN')
      cy.wait(500)
      cy.get('[data-testid="job-card"]').each(($el) => {
        cy.wrap($el).should('contain', 'INTERN')
      })
    })
  })

  describe('FE_JOB_03: Search jobs theo tên', () => {
    it('should search jobs by name', () => {
      cy.get('[data-testid="search-input"]').type('Software Engineer')
      cy.get('[data-testid="search-button"]').click()
      cy.wait(500)
      cy.get('[data-testid="job-card"]').each(($el) => {
        cy.wrap($el).should('contain.text', 'Software')
      })
    })
  })

  describe('FE_JOB_04: Search jobs theo địa điểm', () => {
    it('should search jobs by location', () => {
      cy.get('[data-testid="location-input"]').type('Ho Chi Minh')
      cy.get('[data-testid="search-button"]').click()
      cy.wait(500)
      cy.get('[data-testid="job-card"]').each(($el) => {
        cy.wrap($el).find('[data-testid="job-location"]').should('contain', 'Ho Chi Minh')
      })
    })
  })

  describe('FE_JOB_05: Pagination hoạt động đúng', () => {
    it('should navigate between pages', () => {
      // Check first page
      cy.get('[data-testid="page-1"]').should('have.class', 'active')
      
      // Go to second page
      cy.get('[data-testid="page-2"]').click()
      cy.wait(500)
      cy.get('[data-testid="page-2"]').should('have.class', 'active')
      
      // Check that job cards are different
      cy.get('[data-testid="job-card"]').first().invoke('text').then((firstPageJob) => {
        cy.get('[data-testid="page-1"]').click()
        cy.wait(500)
        cy.get('[data-testid="job-card"]').first().invoke('text').should('not.equal', firstPageJob)
      })
    })
  })

  describe('FE_JOB_06: Job card hiển thị đầy đủ thông tin', () => {
    it('should display complete job information on card', () => {
      cy.get('[data-testid="job-card"]').first().within(() => {
        cy.get('[data-testid="job-title"]').should('be.visible')
        cy.get('[data-testid="company-name"]').should('be.visible')
        cy.get('[data-testid="job-location"]').should('be.visible')
        cy.get('[data-testid="job-salary"]').should('be.visible')
        cy.get('[data-testid="job-level"]').should('be.visible')
      })
    })
  })

  describe('FE_JOB_07: Click job card mở job details', () => {
    it('should navigate to job details on card click', () => {
      cy.get('[data-testid="job-card"]').first().click()
      cy.url().should('match', /\/jobs\/\d+/)
      cy.get('[data-testid="job-details"]').should('be.visible')
    })
  })

  describe('FE_JOB_08: Responsive layout trên mobile/tablet', () => {
    it('should display correctly on mobile viewport', () => {
      cy.viewport('iphone-x')
      cy.get('[data-testid="job-grid"]').should('have.css', 'grid-template-columns', '1fr')
      cy.get('[data-testid="job-card"]').should('be.visible')
    })

    it('should display correctly on tablet viewport', () => {
      cy.viewport('ipad-2')
      cy.get('[data-testid="job-grid"]').should('be.visible')
      cy.get('[data-testid="job-card"]').should('be.visible')
    })
  })
})

describe('Job Details Tests', () => {
  beforeEach(() => {
    cy.visit('/jobs/1') // Assuming job with ID 1 exists
  })

  describe('FE_APPLY_01: Hiển thị chi tiết job', () => {
    it('should display job details correctly', () => {
      cy.get('[data-testid="job-title"]').should('be.visible')
      cy.get('[data-testid="job-description"]').should('be.visible')
      cy.get('[data-testid="job-requirements"]').should('be.visible')
      cy.get('[data-testid="company-info"]').should('be.visible')
    })
  })

  describe('FE_APPLY_02: Nút "Ứng tuyển" chỉ hiện khi đã login', () => {
    it('should not show apply button when not logged in', () => {
      // Clear any existing auth
      cy.window().then((win) => {
        win.localStorage.clear()
      })
      cy.reload()
      cy.get('[data-testid="apply-button"]').should('not.exist')
      cy.get('[data-testid="login-prompt"]').should('be.visible')
    })

    it('should show apply button when logged in', () => {
      // Mock logged in state
      cy.window().then((win) => {
        win.localStorage.setItem('access_token', 'mock-token')
        win.localStorage.setItem('user', JSON.stringify({ 
          email: 'test@example.com',
          permissions: ['POST /resumes']
        }))
      })
      cy.reload()
      cy.get('[data-testid="apply-button"]').should('be.visible')
    })
  })

  describe('FE_APPLY_03: Form upload CV validation', () => {
    beforeEach(() => {
      // Mock logged in state
      cy.window().then((win) => {
        win.localStorage.setItem('access_token', 'mock-token')
        win.localStorage.setItem('user', JSON.stringify({ 
          email: 'test@example.com',
          permissions: ['POST /resumes']
        }))
      })
      cy.reload()
      cy.get('[data-testid="apply-button"]').click()
    })

    it('should validate PDF file upload', () => {
      const fileName = 'test.txt'
      cy.fixture(fileName).then(fileContent => {
        cy.get('[data-testid="cv-upload"]').attachFile({
          fileContent: fileContent.toString(),
          fileName: fileName,
          mimeType: 'text/plain'
        })
      })
      cy.get('[data-testid="submit-application"]').click()
      cy.get('[data-testid="error-message"]').should('contain', 'Chỉ chấp nhận file PDF')
    })

    it('should accept valid PDF file', () => {
      const fileName = 'test-cv.pdf'
      cy.fixture(fileName).then(fileContent => {
        cy.get('[data-testid="cv-upload"]').attachFile({
          fileContent: fileContent.toString(),
          fileName: fileName,
          mimeType: 'application/pdf'
        })
      })
      cy.get('[data-testid="file-preview"]').should('contain', 'test-cv.pdf')
    })
  })

  describe('FE_APPLY_05: Không cho phép ứng tuyển lại job đã apply', () => {
    it('should disable apply button for already applied jobs', () => {
      // Mock already applied state
      cy.intercept('GET', '/resumes/me*', {
        statusCode: 200,
        body: {
          data: {
            content: [
              { jobId: 1, status: 'PENDING' }
            ]
          }
        }
      })
      
      cy.window().then((win) => {
        win.localStorage.setItem('access_token', 'mock-token')
      })
      cy.reload()
      
      cy.get('[data-testid="apply-button"]').should('be.disabled')
      cy.get('[data-testid="already-applied-message"]').should('be.visible')
    })
  })
})
