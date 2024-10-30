import React, { useEffect, useState } from 'react';
import { getDevicesByUserId } from '../user/api/user-api'; // Aici am importat functia

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
                console.log("Response:", response); // Verifica raspunsul primit
                if (response && response.data) {
                    console.log("Setting devices:", response.data);
                    setDevices(response.data);
                }
            });
        }else {
            console.error("User ID not found in localStorage");
        }
    }, []);

    return (
        <div>
            <h1>Client Dashboard</h1>
            <p>Welcome, {userName} ! Here are your devices:</p>
            <table>
                <thead>
                <tr>
                    <th>Description</th>
                    <th>Address</th>
                    <th>Max Hourly Energy Consumption</th>
                </tr>
                </thead>
                <tbody>
                {devices.map((device) => (
                    <tr key={device.id}>
                        <td>{device.description}</td>
                        <td>{device.address}</td>
                        <td>{device.maxHourlyEnergyConsumption || device.max_hourly_energy_consumption}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default ClientPage;
