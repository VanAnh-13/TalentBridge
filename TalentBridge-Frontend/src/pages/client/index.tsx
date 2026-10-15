import { useAuthDispatch, useAuthState, getAccount } from "@/features/auth";
import { useEffect } from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";

const RootPage = () => {
  const dispatch = useAuthDispatch();
  const { isLogin } = useAuthState();

  useEffect(() => {
    if (!isLogin) dispatch(getAccount());
  }, [isLogin, dispatch]);

  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <div className="flex-grow">
        <Outlet />
      </div>
      <Footer />
    </div>
  );
};

export default RootPage;
