// React router
import { createBrowserRouter, Navigate } from "react-router-dom";

// App shells and pages that compose several features
import ClientLayout from "@/app/layouts/client/ClientLayout";
import UserLayout from "@/app/layouts/UserLayout";
import AdminLayout from "@/app/layouts/admin/AdminLayout";
import RecruitmentLayout from "@/app/layouts/admin/RecruitmentLayout";
import RecruiterLayout from "@/app/layouts/recruiter/RecruiterLayout";
import HomePage from "@/app/pages/home/HomePage";
import DashboardPage from "@/app/pages/DashboardPage";

// Features
import { AuthPage, ProtectedRoute, SessionsPage } from "@/features/auth";
import {
  CompanyBrowsePage,
  CompanyDetailsPage,
  CompanyManagerPage,
  MembersPage,
  MyCompanyPage,
} from "@/features/company";
import {
  AdminJobManagerPage,
  AdminJobUpsertPage,
  JobBrowsePage,
  JobDetailsPage,
  RecruiterJobManagerPage,
  RecruiterJobUpsertPage,
} from "@/features/job";
import { ProfilePage, UserManagerPage, UserUpsertPage } from "@/features/user";
import {
  AdminResumeManagerPage,
  MyResumesPage,
  RecruiterResumeManagerPage,
} from "@/features/resume";
import { SkillManagerPage } from "@/features/skill";
import { AccessControlPage, PermissionManagerPage, RoleManagerPage } from "@/features/access";
import { SubscriberPage } from "@/features/subscriber";

import ErrorPage from "@/shared/presentation/components/ErrorPage";

const router = createBrowserRouter([
  // =========================
  // CLIENT ROUTE GROUP
  // =========================
  {
    path: "",
    element: <ClientLayout />,
    children: [
      { index: true, element: <Navigate to={"/home"} /> },
      { path: "home", element: <HomePage /> },
      { path: "auth", element: <AuthPage /> },
      { path: "companies", element: <CompanyBrowsePage /> },
      { path: "companies/:id", element: <CompanyDetailsPage /> },
      { path: "jobs", element: <JobBrowsePage /> },
      { path: "jobs/:id", element: <JobDetailsPage /> },
      {
        path: "user",
        element: <UserLayout />,
        children: [
          {
            index: true,
            element: <Navigate to={"info"} />,
          },
          { path: "info", element: <ProfilePage /> },
          {
            path: "subscriber",
            element: (
              <ProtectedRoute
                to="/user"
                requiredPermission="GET /subscribers/me"
              >
                <SubscriberPage />
              </ProtectedRoute>
            ),
          },
          {
            path: "resumes",
            element: (
              <ProtectedRoute to="/user" requiredPermission="POST /resumes">
                <MyResumesPage />
              </ProtectedRoute>
            ),
          },
          { path: "sessions", element: <SessionsPage /> },
        ],
      },
    ],
  },

  // =========================
  // ADMIN ROUTE GROUP
  // =========================
  {
    path: "admin",
    element: (
      <ProtectedRoute to="/" requiredPermission={"GET /admin"}>
        <AdminLayout />
      </ProtectedRoute>
    ),
    errorElement: <ErrorPage />,
    children: [
      // AUTO NAVIGATION
      { index: true, element: <Navigate to={"/admin/dashboard"} /> },

      // DASHBOARD
      { path: "dashboard", element: <DashboardPage /> },

      // COMPANY MANAGER
      {
        path: "company",
        element: (
          <ProtectedRoute to="/admin" requiredPermission="GET /companies">
            <CompanyManagerPage />
          </ProtectedRoute>
        ),
      },

      // RECRUITMENT MANAGER
      {
        path: "recruitment",
        element: <RecruitmentLayout />,
        children: [
          {
            index: true,
            element: <Navigate to={"/admin/recruitment/job-manager"} />,
          },
          {
            path: "skill-manager",
            element: (
              <ProtectedRoute to="/admin" requiredPermission="GET /skills">
                <SkillManagerPage />
              </ProtectedRoute>
            ),
          },
          {
            path: "job-manager",
            element: (
              <ProtectedRoute to="/admin" requiredPermission="GET /jobs">
                <AdminJobManagerPage />
              </ProtectedRoute>
            ),
          },
          {
            path: "job-manager/upsert",
            element: <AdminJobUpsertPage />,
          },
        ],
      },

      // RESUME MANAGER
      {
        path: "resume",
        element: (
          <ProtectedRoute to="/admin" requiredPermission="GET /resumes">
            <AdminResumeManagerPage />
          </ProtectedRoute>
        ),
      },

      // USER MANAGER
      {
        path: "user-manager",
        element: (
          <ProtectedRoute to="/admin" requiredPermission="GET /users">
            <UserManagerPage />
          </ProtectedRoute>
        ),
      },
      { path: "user-manager/upsert", element: <UserUpsertPage /> },

      // ACCESS CONTROL MANAGER
      {
        path: "access-control",
        element: <AccessControlPage />,
        children: [
          {
            index: true,
            element: <Navigate to={"/admin/access-control/permission"} />,
          },
          {
            path: "permission",
            element: (
              <ProtectedRoute
                to="/admin"
                requiredPermission="GET /permissions"
              >
                <PermissionManagerPage />
              </ProtectedRoute>
            ),
          },
          {
            path: "role",
            element: (
              <ProtectedRoute to="/admin" requiredPermission="GET /roles">
                <RoleManagerPage />
              </ProtectedRoute>
            ),
          },
        ],
      },

      // NOT FOUND
      { path: "*", element: <ErrorPage /> },
    ],
  },

  // =========================
  // RECRUITER ROUTE GROUP
  // =========================
  {
    path: "recruiter",
    element: (
      <ProtectedRoute to="/" requiredPermission="GET /recruiter">
        <RecruiterLayout />
      </ProtectedRoute>
    ),
    children: [
      // AUTO NAVIGATION
      { index: true, element: <Navigate to={"/recruiter/company"} /> },

      // SKILL MANAGER
      {
        path: "skills",
        element: (
          <ProtectedRoute to="/recruiter" requiredPermission="GET /skills">
            <SkillManagerPage theme="purple" />
          </ProtectedRoute>
        ),
      },

      // RESUME RECRUITER MANAGER
      {
        path: "resumes",
        element: (
          <ProtectedRoute
            to="/recruiter"
            requiredPermission="GET /resumes/company"
          >
            <RecruiterResumeManagerPage />
          </ProtectedRoute>
        ),
      },

      // RESUME RECRUITER MANAGER
      {
        path: "jobs",
        element: (
          <ProtectedRoute
            to="/recruiter"
            requiredPermission="GET /jobs/company"
          >
            <RecruiterJobManagerPage />
          </ProtectedRoute>
        ),
      },
      {
        path: "jobs/upsert",
        element: <RecruiterJobUpsertPage />,
      },

      // COMPANY RECRUITER MANAGER
      {
        path: "company",
        element: (
          <ProtectedRoute
            to="/recruiter"
            requiredPermission="GET /companies/me"
          >
            <MyCompanyPage />
          </ProtectedRoute>
        ),
      },

      // MEMBER MANAGER
      {
        path: "members",
        element: (
          <ProtectedRoute
            to="/recruiter"
            requiredPermission="GET /companies/me/users"
          >
            <MembersPage />
          </ProtectedRoute>
        ),
      },

      { path: "*", element: <ErrorPage /> },
    ],
  },
]);

export default router;
