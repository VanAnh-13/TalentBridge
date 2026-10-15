import { RouterProvider } from "react-router-dom";
import router from "./router";
import { Provider } from "react-redux";
import { persistor, store } from "@/app/store";
import { wireApplication } from "./wiring";
import { Toaster } from "sonner";
import { Worker } from "@react-pdf-viewer/core";
import * as pdfjs from "pdfjs-dist";
import { ScrollArea } from "@/shared/presentation/ui/scroll-area";
import { PersistGate } from "redux-persist/integration/react";

wireApplication();

const App = () => {
  return (
    <Provider store={store}>
      <PersistGate persistor={persistor}>
        <Worker
          workerUrl={`https://unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.js`}
        >
          <ScrollArea className="h-screen">
            <RouterProvider router={router} />
          </ScrollArea>
          <Toaster richColors />
        </Worker>
      </PersistGate>
    </Provider>
  );
};

export default App;
