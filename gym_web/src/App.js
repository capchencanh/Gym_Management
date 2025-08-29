import React, { useEffect, useReducer, useContext, useState } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/Layouts/Header";
import Footer from "./components/Layouts/Footer";
import Home from "./components/Home";
import WorkoutLog from "./components/WorkoutLog";
import Login from "./components/Login";
import Register from "./components/Register";
import PTManagement from "./components/PTManagement";
import ClassList from "./components/ClassList";
import { MyUserContext, MyDispatchContext } from "./configs/Contexts";
import MyUserReducer from "./reducer/MyUserReducer"; 
import Apis, { endpoints } from "./configs/Apis";
import MySpinner from "./components/Layouts/MySpinner";

const App = () => {
    const [user, dispatch] = useReducer(MyUserReducer, null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkLogin = async () => {
            try {
                let res = await Apis.get(endpoints['profile']);
                
               
                dispatch({
                    type: "login",
                    payload: {
                        id: res.data.user_id,
                        email: res.data.email,
                        name: res.data.name,
                        role: res.data.role,
                        phone_number: res.data.phone_number,
                        gender: res.data.gender,
                        birthdate: res.data.birthdate,
                        height: res.data.height,
                        weight: res.data.weight,
                        fitness_goal: res.data.fitness_goal,
                        avatar_url: res.data.avatar_url
                    }
                });
            } catch (ex) {
                console.error("Phiên đăng nhập không hợp lệ hoặc đã hết hạn.");
            } finally {
                setLoading(false);
            }
        };

        checkLogin();
    }, []); 

    return (
        <MyUserContext.Provider value={user}>
            <MyDispatchContext.Provider value={dispatch}>
                <BrowserRouter>
                    <Header />
                    <main className="min-vh-100">
                        {loading ? <MySpinner /> : (
                            <Routes>
                                <Route path="/" element={<Home />} />
                                <Route path="/login" element={<Login />} />
                                <Route path="/register" element={<Register />} />
                                <Route path="/workout" element={<WorkoutLog />} />
                                <Route path="/pt-management" element={<PTManagement userId={user?.id} />} />
                                <Route path="/classes" element={<ClassList />} />
                                <Route path="/home" element={<Home />} />
                                <Route path="*" element={<Home />} />
                            </Routes>
                        )}
                    </main>
                    <Footer />
                </BrowserRouter>
            </MyDispatchContext.Provider>
        </MyUserContext.Provider>
    );
};

export default App;
