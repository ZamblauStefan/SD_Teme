// src/components/PrivateRoute.js

import React from 'react';
import {Navigate, Outlet } from 'react-router-dom';

const PrivateRoute = ({roles  }) => {
    const currentRole = localStorage.getItem('role'); // Obtinem rolul din localStorage

    console.log("Current role:", currentRole);  // Verificam valoarea

            if (!currentRole) {
                // Daca utilizatorul nu este autentificat, se face redirectionarea la login
                console.log("User not authenticated. Redirecting to login...");
                return <Navigate to="/login" replace />;
            }

            if (roles && roles.indexOf(currentRole) === -1) {
                // Daca rolul nu este autorizat pentru această pagina, redirectioneaza la o pagina de eroare sau la home
                console.log("User does not have the required role. Redirecting to unauthorized...");
                return <Navigate to="/unauthorized" replace />;
            }

            // Daca totul este in regula, randam componenta
    console.log("User is authenticated and authorized. Rendering the requested component.");
    return <Outlet />;
};

export default PrivateRoute;
