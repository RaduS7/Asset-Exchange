import React, { useEffect, useState } from 'react';
import { Table, Card, Button, Modal, Form, Input, Select, Typography } from 'antd';
import useAuthenticatedRequest from '../hooks/useAuthenticatedRequest';
import session from '../utils/session';

const { Title } = Typography;

const FundsPage = ({ setRfr }) => {
    const userSession = session.getFromSession('usrss');
    const [funds, setFunds] = useState([]);
    const [isAddModalVisible, setIsAddModalVisible] = useState(false);
    const [isWithdrawModalVisible, setIsWithdrawModalVisible] = useState(false);
    const { sendAuthenticatedRequest } = useAuthenticatedRequest();
    const [form] = Form.useForm();

    useEffect(() => {
        const fetchFunds = async () => {
            try {
                const response = await sendAuthenticatedRequest({
                    url: 'http://localhost:8088/api/user-funds',
                    userObject: userSession
                });
                setFunds(response.data);
            } catch (error) {
                console.error('Failed to fetch user funds:', error);
            }
        };

        fetchFunds();
    }, []);

    const handleAddFunds = async (values) => {
        try {
            await sendAuthenticatedRequest({
                url: 'http://localhost:8088/api/user-funds/add',
                method: 'POST',
                data: values,
                userObject: userSession
            });
            setIsAddModalVisible(false);
            form.resetFields();

            const response = await sendAuthenticatedRequest({
                url: 'http://localhost:8088/api/user-funds',
                userObject: userSession
            });
            setFunds(response.data);

            setRfr(prev => prev + 1);
        } catch (error) {
            console.error('Failed to add funds:', error);
        }
    };

    const handleWithdrawFunds = async (values) => {
        try {
            await sendAuthenticatedRequest({
                url: 'http://localhost:8088/api/user-funds/withdraw',
                method: 'POST',
                data: values,
                userObject: userSession
            });
            setIsWithdrawModalVisible(false);
            form.resetFields();

            const response = await sendAuthenticatedRequest({
                url: 'http://localhost:8088/api/user-funds',
                userObject: userSession
            });
            setFunds(response.data);
        } catch (error) {
            console.error('Failed to withdraw funds:', error);
        }
    };

    const columns = [
        {
            title: 'Currency',
            dataIndex: 'currency',
            key: 'currency',
        },
        {
            title: 'Total Amount',
            dataIndex: 'totalAmount',
            key: 'totalAmount',
            render: (amount) => amount.toFixed(2),
        },
        {
            title: 'Available Amount',
            dataIndex: 'availableAmount',
            key: 'availableAmount',
            render: (amount) => amount.toFixed(2),
        },
        {
            title: 'Pending Amount',
            dataIndex: 'pendingAmount',
            key: 'pendingAmount',
            render: (amount) => amount.toFixed(2),
        },
    ];

    return (
        <Card style={{ padding: '20px', boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)' }}>
            <Title level={3}>Your Funds</Title>

            <Button type="primary" onClick={() => setIsAddModalVisible(true)} style={{ marginRight: '10px' }}>
                Add Funds
            </Button>
            <Button type="primary" onClick={() => setIsWithdrawModalVisible(true)}>
                Withdraw Funds
            </Button>
            <Table dataSource={funds} columns={columns} rowKey="id" style={{ marginTop: '20px' }} />

            <Modal
                title="Add Funds"
                open={isAddModalVisible}
                onCancel={() => setIsAddModalVisible(false)}
                footer={null}
            >
                <Form form={form} onFinish={handleAddFunds}>
                    <Form.Item
                        name="currency"
                        label="Currency"
                        rules={[{ required: true, message: 'Please select a currency' }]}
                    >
                        <Select>
                            {/* {funds.map(fund => (
                                <Select.Option key={fund.currency} value={fund.currency}>
                                    {fund.currency}
                                </Select.Option>
                            ))} */}
                            <Select.Option key="USD" value="USD">
                                USD
                            </Select.Option>
                            <Select.Option key="EUR" value="EUR">
                                EUR
                            </Select.Option>
                        </Select>
                    </Form.Item>
                    <Form.Item
                        name="amount"
                        label="Amount"
                        rules={[{ required: true, message: 'Please input an amount' }]}
                    >
                        <Input type="number" min={0} step={0.01} />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit">
                            Add
                        </Button>
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title="Withdraw Funds"
                open={isWithdrawModalVisible}
                onCancel={() => setIsWithdrawModalVisible(false)}
                footer={null}
            >
                <Form form={form} onFinish={handleWithdrawFunds}>
                    <Form.Item
                        name="currency"
                        label="Currency"
                        rules={[{ required: true, message: 'Please select a currency' }]}
                    >
                        <Select>
                            {funds.map(fund => (
                                <Select.Option key={fund.currency} value={fund.currency}>
                                    {fund.currency}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item
                        name="amount"
                        label="Amount"
                        rules={[{ required: true, message: 'Please input an amount' }]}
                    >
                        <Input type="number" min={0} step={0.01} />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit">
                            Withdraw
                        </Button>
                    </Form.Item>
                </Form>
            </Modal>
        </Card>
    );
};

export default FundsPage;
