import React, { useEffect, useState } from 'react';
import { getDevicesByUserId } from '../user/api/user-api'; // Aici am importat functia
import BackgroundImg from '../commons/images/Dalle2.webp';
import '../commons/styles/project-style.css';

const backgroundStyle = {
    backgroundPosition: 'center',
    backgroundSize: 'cover',
    backgroundRepeat: 'no-repeat',
    width: "100%",
    minHeight: "100vh",
    backgroundImage: `url(${BackgroundImg})`,
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    flexDirection: 'column'
};

const contentStyle = {
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    color: 'white',
    padding: '20px',
    borderRadius: '10px',
    textAlign: 'center'
};


const ClientPage = () => {
    const [devices, setDevices] = useState([]);
    const userName = localStorage.getItem('userName'); // Obtinem numele utilizatorului din localStorage

    //verificam ca pagina ClientPage se randeaza corect
    console.log("ClientPage rendered");



    useEffect(() => {
        const userId = localStorage.getItem('userId'); // Se obtine userId din localStorage
        console.log("User ID:", userId); // Verifica daca userId este corect

        if(userId) {
            getDevicesByUserId(userId, (response) => {
                console.log("Data Response:", response.data);// Verifica raspunsul primit
                response.forEach((device) => {
                    console.log("Device:", device);
                });

                if (Array.isArray(response)) {
                    console.log("Setting devices:", response);
                    setDevices(response);
                } else {
                    console.error("Unexpected response format:", response);
                }
            });
        } else {
            console.error("User ID not found in localStorage");
        }
    }, []);


    return (
        <div style={backgroundStyle}>
            <div style={contentStyle}>
                <h1>Client Dashboard</h1>
                <p>Welcome, {userName} ! Here are your devices:</p>
                {devices.length > 0 ? (
                    <table className="table table-striped table-bordered">
                        <thead>
                        <tr>
                            <th className="table-header">Description</th>
                            <th className="table-header">Address</th>
                            <th className="table-header">Max Hourly Energy Consumption</th>
                        </tr>
                        </thead>
                        <tbody>
                        {devices.map((device) => (
                            <tr key={device.id}>
                                <td>{device.description}</td>
                                <td>{device.address}</td>
                                <td>{device.maxHourlyEnergyConsumption}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                ) : (
                    <p>No devices available.</p>
                )}
            </div>
        </div>
            );
            };

            export default ClientPage;
