import { RouterProvider } from "react-router-dom";
import router from "./router";
import { Provider } from "react-redux";
import { persistor, store } from "@/app/store";
import { wireApplication } from "./wiring";
import { Toaster } from "sonner";
import { ScrollArea } from "@/shared/presentation/ui/scroll-area";
import { PersistGate } from "redux-persist/integration/react";

wireApplication();

const App = () => {
  return (
    <Provider store={store}>
      <PersistGate persistor={persistor}>
        <ScrollArea className="h-screen">
          <RouterProvider router={router} />
        </ScrollArea>
        <Toaster richColors />
      </PersistGate>
    </Provider>
  );
};

export default App;
