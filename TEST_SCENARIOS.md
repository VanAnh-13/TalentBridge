# TALENTBRIDGE TEST SCENARIOS

## I. BACKEND TESTING (60 Test Cases)

### 1. Authentication & Authorization (12 cases)
1. **AUTH_01**: Register a new user with valid details
2. **AUTH_02**: Register a user with an email that already exists (expect fail)
3. **AUTH_03**: Log in with correct credentials
4. **AUTH_04**: Log in with a wrong password (expect fail)
5. **AUTH_05**: Log in with an email that does not exist (expect fail)
6. **AUTH_06**: Valid refresh token
7. **AUTH_07**: Expired refresh token (expect fail)
8. **AUTH_08**: Log out and invalidate the token
9. **AUTH_09**: Call the API with a valid JWT
10. **AUTH_10**: Call the API with an invalid JWT (expect 401)
11. **AUTH_11**: Call the API without the permission (expect 403)
12. **AUTH_12**: Session management

### 2. User Management (10 cases)
13. **USER_01**: Get the user list with pagination
14. **USER_02**: Search users by email
15. **USER_03**: Get a user by ID
16. **USER_04**: Update the profile
17. **USER_05**: Change the password with the correct current password
18. **USER_06**: Change the password with a wrong current password (expect fail)
19. **USER_07**: Upload an avatar
20. **USER_08**: Upload an avatar that is too large (expect fail)
21. **USER_09**: Delete a user (soft delete)
22. **USER_10**: Restore a deleted user

### 3. Company Management (10 cases)
23. **COMPANY_01**: Create a company with all its details
24. **COMPANY_02**: Create a company with a duplicate name (expect fail)
25. **COMPANY_03**: Get the company list with a filter
26. **COMPANY_04**: Get a company's details by ID
27. **COMPANY_05**: Update a company
28. **COMPANY_06**: Upload a company logo
29. **COMPANY_07**: Add a recruiter to a company
30. **COMPANY_08**: Remove a recruiter from a company
31. **COMPANY_09**: Delete a company that still has open jobs (expect fail)
32. **COMPANY_10**: Get the company list with job counts

### 4. Job Management (10 cases)
33. **JOB_01**: Create a job with valid details
34. **JOB_02**: Create a job with salary min > max (expect fail)
35. **JOB_03**: Get the job list filtered by level
36. **JOB_04**: Get the jobs of a company
37. **JOB_05**: Update a job
38. **JOB_06**: Close / reopen a job
39. **JOB_07**: Delete a job without applications
40. **JOB_08**: Delete a job with applications (expect fail)
41. **JOB_09**: Search jobs by skills
42. **JOB_10**: Get jobs with pagination and sorting

### 5. Resume Management (8 cases)
43. **RESUME_01**: Upload a valid PDF CV
44. **RESUME_02**: Upload a CV that is not a PDF (expect fail)
45. **RESUME_03**: Apply for a job with a CV
46. **RESUME_04**: Apply for the same job twice (expect fail)
47. **RESUME_05**: Update an application's status (PENDING -> REVIEWING)
48. **RESUME_06**: Get the applications of a job
49. **RESUME_07**: Get a user's applications
50. **RESUME_08**: Delete an application

### 6. Role & Permission (6 cases)
51. **ROLE_01**: Create a role with permissions
52. **ROLE_02**: Update a role's permissions
53. **ROLE_03**: Assign a role to a user
54. **ROLE_04**: Check a user's permissions
55. **ROLE_05**: Delete a role no user has
56. **ROLE_06**: Delete a role in use (expect fail)

### 7. Skill Management (4 cases)
57. **SKILL_01**: Create a skill
58. **SKILL_02**: List skills
59. **SKILL_03**: Update a skill
60. **SKILL_04**: Delete an unused skill

## II. FRONTEND TESTING (40+ Test Cases)

### 8. Authentication UI (8 cases)
61. **FE_AUTH_01**: Login form is displayed
62. **FE_AUTH_02**: Login form validation (email format, required fields)
63. **FE_AUTH_03**: Registration form is displayed
64. **FE_AUTH_04**: Registration form validation (password match, email format)
65. **FE_AUTH_05**: Redirect after a successful login
66. **FE_AUTH_06**: Error message when the login fails
67. **FE_AUTH_07**: Remember me
68. **FE_AUTH_08**: Logout clears local storage

### 9. Navigation & Routing (6 cases)
69. **FE_NAV_01**: The navigation menu matches the role
70. **FE_NAV_02**: Protected routes redirect when not logged in
71. **FE_NAV_03**: Admin routes are only accessible to admins
72. **FE_NAV_04**: Recruiter routes are only accessible to recruiters
73. **FE_NAV_05**: Breadcrumb navigation works
74. **FE_NAV_06**: A 404 page is shown for invalid routes

### 10. Job Listing Page (8 cases)
75. **FE_JOB_01**: Job list is displayed
76. **FE_JOB_02**: Filter jobs by level (INTERN, FRESHER, MIDDLE, SENIOR)
77. **FE_JOB_03**: Search jobs by name
78. **FE_JOB_04**: Search jobs by location
79. **FE_JOB_05**: Pagination works
80. **FE_JOB_06**: Job card shows all its details
81. **FE_JOB_07**: Clicking a job card opens its details
82. **FE_JOB_08**: Responsive layout on mobile and tablet

### 11. Job Details & Application (6 cases)
83. **FE_APPLY_01**: Job details are displayed
84. **FE_APPLY_02**: The "Apply" button only shows when logged in
85. **FE_APPLY_03**: CV upload form validation
86. **FE_APPLY_04**: Success message after applying
87. **FE_APPLY_05**: A job cannot be applied for twice
88. **FE_APPLY_06**: Company information in the job details

### 12. Admin Dashboard (6 cases)
89. **FE_ADMIN_01**: Dashboard shows statistics cards
90. **FE_ADMIN_02**: Sidebar menu works
91. **FE_ADMIN_03**: User management table with CRUD actions
92. **FE_ADMIN_04**: Company management with filter/search
93. **FE_ADMIN_05**: Role & permission management UI
94. **FE_ADMIN_06**: Data export

### 13. Recruiter Dashboard (6 cases)
95. **FE_RECRUIT_01**: Create a company when the recruiter has none
96. **FE_RECRUIT_02**: Job management for the company
97. **FE_RECRUIT_03**: CV/application management
98. **FE_RECRUIT_04**: Add/remove company members
99. **FE_RECRUIT_05**: Update the company profile
100. **FE_RECRUIT_06**: View the applicants of each job

### 14. User Profile & Settings (6 cases)
101. **FE_USER_01**: View and edit profile information
102. **FE_USER_02**: Change password with validation
103. **FE_USER_03**: Upload an avatar with a preview
104. **FE_USER_04**: View the list of submitted CVs
105. **FE_USER_05**: Subscribe/unsubscribe from job notifications
106. **FE_USER_06**: Manage login sessions

## III. INTEGRATION TESTING (10+ cases)

107. **INT_01**: End-to-end flow: Register -> Login -> Apply Job
108. **INT_02**: Company creates a job -> User applies -> Recruiter reviews the CV
109. **INT_03**: Admin creates a role -> Assigns it to a user -> Checks permissions
110. **INT_04**: Upload a file -> Store it in the cloud -> Retrieve its URL
111. **INT_05**: Send an e-mail notification when a new job is posted
112. **INT_06**: Search with Vietnamese text (with diacritics)
113. **INT_07**: Concurrent user sessions
114. **INT_08**: Rate limiting for API calls
115. **INT_09**: Database transaction rollback on error
116. **INT_10**: Cache invalidation after a data update

## IV. PERFORMANCE TESTING (5 cases)

117. **PERF_01**: Load test with 100 concurrent users
118. **PERF_02**: API response time < 500ms for GET requests
119. **PERF_03**: Upload a 10MB file
120. **PERF_04**: Database query optimisation (no N+1)
121. **PERF_05**: Frontend bundle size < 500KB

## V. SECURITY TESTING (5 cases)

122. **SEC_01**: SQL injection prevention
123. **SEC_02**: XSS attack prevention
124. **SEC_03**: CSRF protection
125. **SEC_04**: Rate limiting to prevent brute force
126. **SEC_05**: Sensitive data is not exposed in API responses
