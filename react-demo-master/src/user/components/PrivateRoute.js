// PrivateRoute.js
import React from 'react';
import { Navigate } from 'react-router-dom';

const PrivateRoute = ({ children, roles }) => {
    const role = localStorage.getItem('role');
    const isAuthenticated = role !== null;

    // Verificam daca utilizatorul este autentificat si daca are rolul potrivit
    if (!isAuthenticated) {
        return <Navigate to="/login" />;
    }

    if (roles && !roles.includes(role)) {
        return <Navigate to="/unauthorized" />;
    }

    return children;
};

export default PrivateRoute;
