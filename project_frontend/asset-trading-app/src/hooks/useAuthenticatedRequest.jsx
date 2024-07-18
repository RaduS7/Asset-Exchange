import { useCallback } from 'react';
import useRequest from './useRequest';

export default function useAuthenticatedRequest() {
    const { sendRequest: originalSendRequest, loading, error } = useRequest();

    const sendAuthenticatedRequest = useCallback(async ({ method, url, userObject, data, headers, responseType }) => {
        const authHeaders = {
            ...headers,
            'Authorization': `Bearer ${userObject.token}`
        };

        return originalSendRequest({
            method,
            url,
            data,
            headers: authHeaders,
            responseType
        });
    }, [originalSendRequest]);

    return {
        sendAuthenticatedRequest,
        loading,
        error,
    };
}