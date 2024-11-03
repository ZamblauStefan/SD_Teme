import React, { useEffect, useState } from 'react';
import { getUsers, updateUser, deleteUser, getDevices, updateDevice, deleteDevice, insertUserWithSync, insertDevice,insertDeviceNoUser } from '../user/api/user-api';
import BackgroundImg from '../commons/images/Dalle3.webp';
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



function AdminPage() {
    const [users, setUsers] = useState([]);
    const [devices, setDevices] = useState([]);
    const [activeSection, setActiveSection] = useState('users'); // State pentru sectiunea activa: "users" sau "devices"
    const [userMap, setUserMap] = useState({});

    // Pentru schimbarea parolei
    const [editingPasswordUserId, setEditingPasswordUserId] = useState(null);
    const [selectedUserId, setSelectedUserId] = useState(null);
    const [newPassword, setNewPassword] = useState("");

    // Fetch users or devices based on active section
    useEffect(() => {
        if (activeSection === 'users') {
            getUsers((response) => {
                console.log("Users response:", response);
                if (Array.isArray(response)) {
                    setUsers(response);
                } else {
                    console.error("Unexpected response format for users:", response);
                }
            });
        } else if (activeSection === 'devices') {
            // Get users first to create the mapping
            getUsers((userResponse) => {
                if (Array.isArray(userResponse)) {
                    const userMapTemp = {};
                    userResponse.forEach(user => {
                        userMapTemp[user.id] = user.name;
                    });
                    setUserMap(userMapTemp);

                    // Then get devices
                    getDevices((deviceResponse) => {
                        if (Array.isArray(deviceResponse)) {
                            setDevices(deviceResponse);
                        }
                    });
                }
            });
        }
    }, [activeSection]);

    // Handle changes in the table inputs
    const handleInputChange = (e, itemId, field, isUser = true) => {
        const value = e.target.value;
        if (isUser) {
            setUsers(prevUsers =>
                prevUsers.map(user =>
                    user.id === itemId ? { ...user, [field]: value } : user
                )
            );
        } else {
            setDevices(prevDevices =>
                prevDevices.map(device =>
                    device.id === itemId ? { ...device, [field]: value } : device
                )
            );
        }
    };

    // Update in the backend
    const handleBlur = (item, isUser = true) => {
        if (isUser) {
            updateUser(item, (response, status) => {
                if (status === 200 || status === 201) {
                    console.log("User updated successfully");
                } else {
                    console.error("Failed to update user");
                }
            });
        } else {
            updateDevice(item, (response, status) => {
                if (status === 200 || status === 201) {
                    console.log("Device updated successfully");
                } else {
                    console.error("Failed to update device");
                }
            });
        }
    };

    // Handle insertion of users or devices with default values
    const handleInsert = (isUser = true) => {
        if (isUser) {
            const newUser = {
                name: "NewUser",
                role: "CLIENT",
                password: "parola" // Parola implicita
            };
            insertUserWithSync(newUser, (response, status) => {
                if (status === 201) {
                    getUsers((response) => {
                        if (Array.isArray(response)) {
                            setUsers(response);
                        }
                    });
                    console.log("User inserted successfully");
                } else {
                    console.error("Failed to insert user");
                }
            });
        } else {
            const newDevice = {
                description: "DeviceNew",
                address: "NewAddress",
                maxHourlyEnergyConsumption: 10,
                userID: null // Default unassigned
            };
            insertDeviceNoUser(newDevice, (response, status) => {
                if (status === 201) {
                    getDevices((response) => {
                        if (Array.isArray(response)) {
                            setDevices(response);
                        }
                    });
                    console.log("Device inserted successfully");
                } else {
                    console.error("Failed to insert device");
                }
            });
        }
    };

// Handle deletion of users or devices
    const handleDelete = (itemId, isUser = true) => {
        if (isUser) {
            deleteUser(itemId, (response, status) => {
                if (status === 200) {
                    getUsers((response) => {
                        if (Array.isArray(response)) {
                            setUsers(response);
                        }
                    });
                    console.log("User deleted successfully");
                } else {
                    console.error("Failed to delete user");
                }
            });
        } else {
            deleteDevice(itemId, (response, status) => {
                if (status === 204) {
                    setDevices(prevDevices => prevDevices.filter(device => device.id !== itemId));
                    console.log("Device deleted successfully");

                    // Re-fetch devices list to confirm the latest data
                    getDevices((response) => {
                        if (Array.isArray(response)) {
                            setDevices(response);
                        }
                    });
                } else {
                    console.error("Failed to delete device");
                }
            });
        }
    };


    // Handle password change button click
    const handleChangePasswordClick = (userId) => {
        setEditingPasswordUserId(userId);
        setNewPassword('');
    };


    // Handle password change submission
    const handleSubmitPasswordChange = () => {
        const userToUpdate = users.find(user => user.id === editingPasswordUserId);
        if (userToUpdate) {
            const updatedUser = {
                ...userToUpdate,
                password: newPassword  // Setăm noua parolă
            };

            updateUser(updatedUser, (response, status) => {
                if (status === 200) {
                    console.log("Password updated successfully");
                    setEditingPasswordUserId(null);  // Ascundem caseta după succesul schimbării parolei
                } else {
                    console.error("Failed to update password");
                }
            });
        }
    };

// Functia pentru a ascunde casuta de schimbare a parolei
    const handleCancelPasswordChange = () => {
        setEditingPasswordUserId(null);
        setNewPassword('');
    };


    return (
        <div style={backgroundStyle}>
            <div style={contentStyle}>
                <h1>Admin Dashboard</h1>
                <p> </p>

                <div>
                    <button onClick={() => setActiveSection('users')}>Users</button>
                    <button onClick={() => setActiveSection('devices')}>Devices</button>
                </div>
            </div>
                {activeSection === 'users' && (
                    <div>
                        <button onClick={() => handleInsert(true)}>Insert User</button>
                        <table>
                            <thead>
                            <tr>
                                <th className="table-header">Name</th>
                                <th className="table-header">Role</th>
                                <th className="table-header">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {users.map((user) => (
                                <tr key={user.id}>
                                    <td>
                                        <input
                                            type="text"
                                            value={user.name}
                                            onChange={(e) => handleInputChange(e, user.id, 'name')}
                                            onBlur={() => handleBlur(user)}
                                        />
                                    </td>
                                    <td>
                                        <select
                                            value={user.role}
                                            onChange={(e) => handleInputChange(e, user.id, 'role')}
                                            onBlur={() => handleBlur(user)}
                                        >
                                            <option value="ADMIN">ADMIN</option>
                                            <option value="CLIENT">CLIENT</option>
                                        </select>
                                    </td>
                                    <td>
                                        <button onClick={() => handleDelete(user.id, true)}>Delete</button>
                                        <button onClick={() => handleChangePasswordClick(user.id)}>Change Password</button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}

            {editingPasswordUserId && (
                <div>
                    <h3>Change Password</h3>
                    <input
                        type="password"
                        placeholder="New Password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                    />
                    <button onClick={handleSubmitPasswordChange}>Submit</button>
                    <button onClick={handleCancelPasswordChange}>Cancel</button>
                </div>
            )}

            {activeSection === 'devices' && (
                <div>
                    <button onClick={() => handleInsert(false)}>Insert Device</button>
                        <table>
                            <thead>
                            <tr>
                                <th className="table-header">Description</th>
                                <th className="table-header">Address</th>
                                <th className="table-header">Max Hourly Energy Consumption</th>
                                <th className="table-header">User</th>
                                <th className="table-header">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {devices.map((device) => (
                                <tr key={device.id}>
                                    <td>
                                        <input
                                            type="text"
                                            value={device.description}
                                            onChange={(e) => handleInputChange(e, device.id, 'description', false)}
                                            onBlur={() => handleBlur(device, false)}
                                        />
                                    </td>
                                    <td>
                                        <input
                                            type="text"
                                            value={device.address}
                                            onChange={(e) => handleInputChange(e, device.id, 'address', false)}
                                            onBlur={() => handleBlur(device, false)}
                                        />
                                    </td>
                                    <td>
                                        <input
                                            type="number"
                                            value={device.maxHourlyEnergyConsumption}
                                            onChange={(e) => handleInputChange(e, device.id, 'maxHourlyEnergyConsumption', false)}
                                            onBlur={() => handleBlur(device, false)}
                                        />
                                    </td>
                                    <td>
                                        <select
                                            value={device.userID || ''}
                                            onChange={(e) => handleInputChange(e, device.id, 'userID', false)}
                                            onBlur={() => handleBlur(device, false)}
                                        >
                                            <option value="">Unassigned</option>
                                            {Object.keys(userMap).map((userId) => (
                                                <option key={userId} value={userId}>
                                                    {userMap[userId]}
                                                </option>
                                            ))}
                                        </select>
                                    </td>
                                    <td>
                                        <button onClick={() => handleDelete(device.id, false)}>Delete</button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
            );
            }

            export default AdminPage;
