// src/components/PrivateRoute.js

import React from 'react';
import {Navigate, Outlet } from 'react-router-dom';

const PrivateRoute = ({roles  }) => {
    const currentRole = localStorage.getItem('role'); // Obtinem rolul din localStorage

            if (!currentRole) {
                // Daca utilizatorul nu este autentificat, se face redirectionarea la login
                return <Navigate to="/login" replace />;
            }

            if (roles && roles.indexOf(currentRole) === -1) {
                // Daca rolul nu este autorizat pentru această pagina, redirectioneaza la o pagina de eroare sau la home
                return <Navigate to="/unauthorized" replace />;
            }

            // Daca totul este in regula, randam componenta
    return <Outlet />;
};

export default PrivateRoute;
