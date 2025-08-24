
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { useContext } from "react";
import Header from "./components/Layouts/Header";
import Footer from "./components/Layouts/Footer";
import Home from "./components/Home";
import WorkoutLog from "./components/WorkoutLog";
import Login from "./components/Login";
import Register from "./components/Register";
import PTManagement from "./components/PTManagement";
import { UserProvider } from "./configs/UserProvider";
import { MyUserContext } from "./configs/Contexts";
import ClassList from "./components/ClassList";


const AppContent = () => {
  const user = useContext(MyUserContext);
  
  
  
  return (
    <BrowserRouter>
      <Header />
      <main className="min-vh-100">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/workout" element={<WorkoutLog />} />
          <Route path="/classes" element={<ClassList />} />
          <Route path="/pt-management" element={<PTManagement userId={user?.id} />} />
        </Routes>
      </main>
      <Footer />
    </BrowserRouter>
  );
};

const App = () => {
  return (
    <UserProvider>
      <AppContent />
    </UserProvider>
  );
};

export default App;