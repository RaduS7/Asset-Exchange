function saveInSession(key, obj) {
    sessionStorage.setItem(key, JSON.stringify(obj));
}

function getFromSession(key) {
    return JSON.parse(sessionStorage.getItem(key));
}

function removeFromSession(key) {
    sessionStorage.removeItem(key);
}

export default { saveInSession, getFromSession, removeFromSession };