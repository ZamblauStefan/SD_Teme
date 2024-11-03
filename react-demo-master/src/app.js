import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import LoginPage from './pages/LoginPage';
import AdminPage from './pages/AdminPage';
import ClientPage from './pages/ClientPage';
import PrivateRoute from './user/components/PrivateRoute';
import UnauthorizedPage from './pages/UnauthorizedPage';


import NavigationBar from './navigation-bar'
import Home from './home/home';
import styles from './commons/styles/project-style.css';
import ErrorPage from './commons/errorhandling/error-page';

/*
import UserContainer from './user/user-container'
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom'
 */

function App(){

        const isAuthenticated = localStorage.getItem('role') !== null;

        return (
            <div className={styles.back}>
            <Router>
                <div>
                    <NavigationBar />
                    <Routes>
                        {/* Ruta pentru Login */}
                        <Route path="/login" element={<LoginPage/>} />

                        {/* Rute protejate pentru admin si client */}
                        <Route
                            path="/admin"
                            element={
                                <PrivateRoute roles={['ADMIN']}>
                                    <AdminPage />
                                </PrivateRoute>
                            }
                        />
                        <Route
                            path="/client"
                            element={
                                <PrivateRoute roles={['CLIENT']}>
                                    <ClientPage />
                                </PrivateRoute>
                            }
                        />

                        {/* ruta Home */}
                        <Route path="/" element={isAuthenticated ? <Home /> : <Navigate to="/login" />} />
                        <Route path="/error" element={<ErrorPage />} />

                        {/* Ruta pentru pagini inexistente/acces neautorizat */}
                        <Route path="/unauthorized" element={<UnauthorizedPage />} />
                        <Route path="*" element={<UnauthorizedPage />} /> {/* Redirectioneaza la Unauthorized daca ruta nu este gasita */}

                    </Routes>
                </div>
            </Router>
            </div>
        )
}

export default App
