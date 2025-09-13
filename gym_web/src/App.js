import React, { useEffect, useReducer, useContext, useState, Suspense, useMemo, useCallback } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/Layouts/Header";
import Footer from "./components/Layouts/Footer";
import ErrorBoundary from "./components/ErrorBoundary";
import LoadingSpinner from "./components/LoadingSpinner";
import { MyUserContext, MyDispatchContext } from "./configs/Contexts";
import MyUserReducer from "./reducer/MyUserReducer"; 
import Apis, { endpoints } from "./configs/Apis";
import "./App.css";

const Home = React.lazy(() => import("./components/Home"));
const WorkoutLog = React.lazy(() => import("./components/WorkoutLog"));
const Login = React.lazy(() => import("./components/Login"));
const Register = React.lazy(() => import("./components/Register"));
const PTManagement = React.lazy(() => import("./components/PTManagement"));
const ClassList = React.lazy(() => import("./components/ClassList"));
const Profile = React.lazy(() => import("./components/Profile"));
const Package = React.lazy(() => import("./components/Package"));
const AIChatBox = React.lazy(() => import("./components/AIChatBox"));

const App = () => {
    const [user, dispatch] = useReducer(MyUserReducer, null);
    const [loading, setLoading] = useState(true);

    const checkLogin = useCallback(async () => {
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
            dispatch({ type: "logout" });
            localStorage.clear();
            sessionStorage.clear();
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        checkLogin();
    }, [checkLogin]); 

    const contextValue = useMemo(() => ({ user, dispatch }), [user, dispatch]);

    return (
        <ErrorBoundary>
            <MyUserContext.Provider value={user}>
                <MyDispatchContext.Provider value={dispatch}>
                    <BrowserRouter>
                        <div className="App">
                            <Header />
                            <div className="App-main">
                                {loading ? (
                                    <LoadingSpinner 
                                        size="large" 
                                        text="Đang tải ứng dụng..." 
                                    />
                                ) : (
                                    <Suspense fallback={
                                        <LoadingSpinner 
                                            size="medium" 
                                            text="Đang tải trang..." 
                                        />
                                    }>
                                        <Routes>
                                            <Route path="/" element={<Home />} />
                                            <Route path="/login" element={<Login />} />
                                            <Route path="/register" element={<Register />} />
                                            <Route path="/workout" element={<WorkoutLog />} />
                                            <Route path="/pt-management" element={<PTManagement userId={user?.id} />} />
                                            <Route path="/classes" element={<ClassList />} />
                                            <Route path="/profile" element={<Profile />} />
                                            <Route path="/package" element={<Package />} />
                                            <Route path="/home" element={<Home />} />
                                            <Route path="*" element={<Home />} />
                                        </Routes>
                                    </Suspense>
                                )}
                            </div>
                            <Footer />
                            <Suspense fallback={null}>
                                <AIChatBox />
                            </Suspense>
                        </div>
                    </BrowserRouter>
                </MyDispatchContext.Provider>
            </MyUserContext.Provider>
        </ErrorBoundary>
    );
};

export default App;
