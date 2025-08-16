
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/Layouts/Header";
import Footer from "./components/Layouts/Footer";
import Home from "./components/Home";
import WorkoutLog from "./components/WorkoutLog";
import Login from "./components/Login";
import Register from "./components/Register";
import { UserProvider } from "./configs/UserProvider";

const App = () => {
  return (
    <UserProvider>
      <BrowserRouter>
        <Header />
        <main className="min-vh-100">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/workout" element={<WorkoutLog />} />
          </Routes>
        </main>
        <Footer />
      </BrowserRouter>
    </UserProvider>
  );
};

export default App;