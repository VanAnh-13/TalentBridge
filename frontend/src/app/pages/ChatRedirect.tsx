import { Navigate, useSearchParams } from "react-router-dom";

import { useHasPermission } from "@/features/auth";

/** Notification links point at /chat; recruiters and candidates have their chat inside their own workspace. */
const ChatRedirect = () => {
  const [params] = useSearchParams();
  const isRecruiter = useHasPermission("GET /recruiter");
  return <Navigate to={`${isRecruiter ? "/recruiter" : "/user"}/chat?${params.toString()}`} replace />;
};

export default ChatRedirect;
