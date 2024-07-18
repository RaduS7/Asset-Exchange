import React from 'react';
import { Form, Input, Button, Divider, Card, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import session from '../utils/session';
import axios from 'axios';
import useRequest from '../hooks/useRequest';

const LoginPage = ({ setUser, setRfr }) => {
    const navigate = useNavigate();
    const { sendRequest } = useRequest();

    const onFinish = async (values) => {
        try {
            const response = await sendRequest({
                url: 'http://localhost:8088/api/auth/sign-in',
                method: 'POST',
                data: values
            });

            message.success('Signin successful!');
            session.saveInSession('usrss', response.data);
            setUser(response.data);

            setRfr(prev => prev + 1);

            if (response.data.roles.some(role => role === 'ROLE_TRADER')) {
                navigate('/trader');
            }
            else {
                navigate('/manager');
            }
        } catch (error) {
            message.error('Failed to signin.');
        }
    };

    const onFinishFailed = (errorInfo) => {
        console.log('Failed:', errorInfo);
    };

    return (

        <Card style={{ width: '30%', padding: '20px', boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)' }}>
            <Form
                name="basic"
                initialValues={{ remember: true }}
                onFinish={onFinish}
                onFinishFailed={onFinishFailed}
                autoComplete="off"
            >
                <Form.Item
                    label="Username"
                    name="username"
                    rules={[{ required: true, message: 'Please input your username!' }]}
                >
                    <Input />
                </Form.Item>

                <Form.Item
                    label="Password"
                    name="password"
                    rules={[{ required: true, message: 'Please input your password!' }]}
                >
                    <Input.Password />
                </Form.Item>

                <Divider style={{ backgroundColor: '#d9d9d9' }} />

                <Form.Item>
                    <Button type="primary" htmlType="submit" style={{ width: '100%' }}>
                        Sign In
                    </Button>

                    <Button type="link" style={{ width: '100%' }} onClick={() => { navigate('/create-account') }}>
                        Sign Up
                    </Button>
                </Form.Item>
            </Form>
        </Card>

    );
};

export default LoginPage;
