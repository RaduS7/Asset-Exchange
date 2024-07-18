import { useState } from 'react';
import axios from 'axios';

export default function useRequest() {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const sendRequest = async ({ method, url, data, headers, responseType }) => {
        setLoading(true);
        setError(null);
        try {
            const response = await axios({
                method,
                url,
                data,
                headers,
                responseType
            });
            return response;
        } catch (err) {
            console.log(err);
            setError(err);
            throw err;
        } finally {
            setLoading(false);
        }
    };

    return { sendRequest, loading, error };
}