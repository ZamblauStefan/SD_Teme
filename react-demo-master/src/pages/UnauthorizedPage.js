// UnauthorizedPage.js
import React from 'react';
import '../commons/styles/project-style.css';

function UnauthorizedPage() {
    return (
        <div className="unauthorized-container">
            <div className="unauthorized-message">
                <h1>Acces neautorizat</h1>
                <p>Nu aveti permisiunea de a accesa aceasta pagina.</p>
                <p>Va rugam sa va autentificati cu un cont valid.</p>
            </div>
        </div>
    );
}

export default UnauthorizedPage;
