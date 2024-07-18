import axios from 'axios';

const setupAxiosInterceptors = (redirectOnAuthFail) => {
    const onResponseError = (error) => {
        if (error.response && [401, 403].includes(error.response.status)) {
            redirectOnAuthFail();
        }
        return Promise.reject(error);
    };

    axios.interceptors.response.use(response => response, onResponseError);
};

export default setupAxiosInterceptors;