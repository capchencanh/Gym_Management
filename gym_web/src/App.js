
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/Layouts/Header";
import Footer from "./components/Layouts/Footer";
import Home from "./components/Home";

const App = () => {
  return (
    <BrowserRouter>
      <Header />
      <main className="min-vh-100">
        <Routes>
          <Route path="/" element={<Home />} />
        </Routes>
      </main>
      <Footer />
    </BrowserRouter>
  );
};

export default App;