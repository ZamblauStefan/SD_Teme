import {HOST} from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";


const endpoint = {
    user: '/user',
    userWithSync: '/userWithSync',  // Endpoint pentru sincronizare
    device: '/device' //Endpoint pentru dispozitive
};

// Inserare utilizator cu sincronizare
function insertUserWithSync(user, callback) {
    let request = new Request(HOST.users_api + endpoint.userWithSync, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });
    RestApiClient.performRequest(request, callback);
}

function getUsers(callback) {
    let request = new Request(HOST.users_api + endpoint.user, {
        method: 'GET',
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

// Functia pentru stergerea utilizatorului
function deleteUser(userId, callback) {
    let request = new Request(HOST.users_api + endpoint.user + '/' + userId, {
        method: 'DELETE'
    });
    RestApiClient.performRequest(request, callback);
}


function getUserById(params, callback){
    let request = new Request(HOST.users_api + endpoint.user + params.id, {
       method: 'GET'
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function postUser(user, callback){
    let request = new Request(HOST.users_api + endpoint.user , {
        method: 'POST',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}


function updateUser(user, callback) {
    let request = new Request(HOST.users_api + endpoint.user + '/' + user.id, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}


// Functie pentru a prelua dispozitivele asociate unui utilizator
function getDevicesByUserId(userId, callback) {
    let request = new Request(HOST.devices_api + endpoint.device + '/user/' + userId, {
        method: 'GET',
    });

    console.log("Request URL:", request.url);

    RestApiClient.performRequest(request, callback);
}


export {
    insertUserWithSync,
    deleteUser,
    getUsers,
    getUserById,
    postUser,
    updateUser,
    getDevicesByUserId
};
