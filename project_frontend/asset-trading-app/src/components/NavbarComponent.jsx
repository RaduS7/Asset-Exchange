import React, { useEffect, useState } from 'react';
import { Menu, Button, Typography } from 'antd';
import { DollarOutlined, LogoutOutlined, PieChartOutlined } from '@ant-design/icons';
import useAuthenticatedRequest from '../hooks/useAuthenticatedRequest';
import session from '../utils/session';
import { useNavigate, useLocation } from 'react-router-dom';

const { Text } = Typography;

const NavbarComponent = ({ rfr }) => {
    const userSession = session.getFromSession('usrss');
    const [totalUsdValue, setTotalUsdValue] = useState(0);
    const { sendAuthenticatedRequest } = useAuthenticatedRequest();
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const fetchTotalUsdValue = async () => {
            try {
                const response = await sendAuthenticatedRequest({
                    url: 'http://localhost:8088/api/user-funds/total-usd-value',
                    userObject: userSession
                });

                setTotalUsdValue(response.data);
            } catch (error) {
                console.error('Failed to fetch total USD value:', error);
            }
        };

        fetchTotalUsdValue();
    }, [rfr]);

    const getActiveMenuItem = () => {
        if (location.pathname.includes('/trader')) {
            return 'trade';
        }
        if (location.pathname.includes('/manager')) {
            return 'manage';
        }
        return '';
    };

    const menuItems = [
        {
            key: 'trade',
            label: <Text style={{ color: 'white', fontWeight: 'bold' }}>Trade</Text>,
            onClick: () => navigate('/trader')
        },
        ...(userSession?.roles?.some(role => role === 'ROLE_ADMIN')
            ? [{
                key: 'manage',
                label: <Text style={{ color: 'white', fontWeight: 'bold' }}>Manage</Text>,
                onClick: () => navigate('/manager')
            }]
            : [])
    ];

    return (
        <>
            {userSession &&
                < div style={{ display: 'flex', alignItems: 'center', flexWrap: 'nowrap' }}>
                    <Menu
                        mode="horizontal"
                        style={{ flexGrow: 1, backgroundColor: 'black' }}
                        selectedKeys={[getActiveMenuItem()]}
                        items={menuItems}

                    />
                    <Button
                        type="primary"
                        icon={<PieChartOutlined />}
                        style={{ marginLeft: '16px' }}
                        onClick={() => {
                            navigate('/assets');
                        }}
                    >
                        Assets
                    </Button>
                    <Button
                        type="primary"
                        icon={<DollarOutlined />}
                        style={{ marginLeft: '16px' }}
                        onClick={() => {
                            navigate('/funds');
                        }}
                    >
                        Total USD: ${totalUsdValue.toFixed(2)}
                    </Button>
                    <Button
                        type="link"
                        icon={<LogoutOutlined />}
                        style={{ marginLeft: '16px' }}
                        onClick={() => {
                            session.removeFromSession('usrss');
                            navigate('/login');
                        }}
                    >
                        Logout
                    </Button>
                </div >
            }
        </>
    );
};

export default NavbarComponent;
