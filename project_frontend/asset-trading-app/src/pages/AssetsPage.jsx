import React, { useEffect, useState } from 'react';
import { Table, Card, Typography } from 'antd';
import useAuthenticatedRequest from '../hooks/useAuthenticatedRequest';
import session from '../utils/session';

const { Title } = Typography;

const AssetsPage = () => {
    const userSession = session.getFromSession('usrss');
    const [userAssets, setUserAssets] = useState([]);
    const { sendAuthenticatedRequest } = useAuthenticatedRequest();

    useEffect(() => {
        const fetchUserAssets = async () => {
            try {
                const response = await sendAuthenticatedRequest({
                    url: `http://localhost:8088/api/user-assets`,
                    userObject: userSession
                });
                setUserAssets(response.data);
            } catch (error) {
                console.error('Failed to fetch user assets:', error);
            }
        };

        fetchUserAssets();
    }, []);

    const columns = [
        {
            title: 'Asset Symbol',
            dataIndex: ['asset', 'symbol'],  // Access nested property
            key: 'symbol',
        },
        {
            title: 'Current Price',
            dataIndex: ['asset', 'currentPrice'],  // Access nested property
            key: 'currentPrice',
            render: currentPrice => currentPrice.toFixed(2),
        },
        {
            title: 'Quantity',
            dataIndex: 'quantity',
            key: 'quantity',
            render: quantity => quantity.toFixed(2),
        }
    ];

    return (
        <Card style={{ padding: '20px', boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)', marginTop: '20px' }}>
            <Title level={3}>Your Assets</Title>
            <Table dataSource={userAssets} columns={columns} rowKey={record => record.asset.symbol} style={{ marginTop: '20px' }} />
        </Card>
    );
};

export default AssetsPage;
