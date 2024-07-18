import React, { useState, useEffect } from 'react';
import '../App.css';
import { BrowserRouter as Router, Route, Routes, Navigate, useNavigate } from 'react-router-dom';
import LoginPage from '../pages/LoginPage';
import TraderPage from '../pages/TraderPage';
import ManagerPage from '../pages/ManagerPage';
import session from '../utils/session';
import setupAxiosInterceptors from '../utils/axiosInterceptors';
import SignupPage from '../pages/SignupPage';
import FundsPage from '../pages/FundsPage';
import AssetsPage from '../pages/AssetsPage';

function RouterComponent({ setRfr }) {
    const [user, setUser] = useState(null);
    const userSession = session.getFromSession('usrss');
    const navigate = useNavigate();

    useEffect(() => {
        setupAxiosInterceptors(() => navigate('/login'));
    }, [navigate]);

    return (
        <Routes>
            <Route path="/login" element={<LoginPage setRfr={setRfr} user={user} setUser={setUser} />} />
            <Route path="/create-account" element={<SignupPage user={user} setUser={setUser} />} />
            <Route path="/trader" element={<TraderPage setRfr={setRfr} user={user} setUser={setUser} />} />
            <Route path="/manager" element={<ManagerPage user={user} setUser={setUser} />} />
            <Route path="/funds" element={<FundsPage setRfr={setRfr} user={user} setUser={setUser} />} />
            <Route path="/assets" element={<AssetsPage user={user} setUser={setUser} />} />

            <Route path="/" element={
                (!userSession) ?
                    <Navigate replace to="/login" /> :
                    (userSession.roles.some(role => role === 'ROLE_TRADER')) ?
                        <Navigate replace to="/trader" /> : <Navigate replace to="/manager" />
            } />

            {/* <Route path="*" element={<Navigate replace to="/" />} /> */}
        </Routes>
    );
}

export default RouterComponent;