import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Button, Table, Modal, Form, Input, Select, message } from 'antd';
import useAuthenticatedRequest from '../hooks/useAuthenticatedRequest';
import session from '../utils/session';
import '../App.css';

const { Option } = Select;

const ManagerPage = () => {
    const userSession = session.getFromSession('usrss');
    const [assets, setAssets] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [currentAsset, setCurrentAsset] = useState(null);
    const { sendAuthenticatedRequest } = useAuthenticatedRequest();
    const [form] = Form.useForm();

    useEffect(() => {
        fetchAssets();
    }, []);

    const fetchAssets = async () => {
        try {
            const response = await sendAuthenticatedRequest({
                url: 'http://localhost:8088/api/assets',
                userObject: userSession
            });
            setAssets(response.data);
        } catch (error) {
            console.error('Failed to fetch assets:', error);
        }
    };

    const handleAddAsset = () => {
        setCurrentAsset(null);
        form.resetFields();
        setIsModalVisible(true);
    };

    const handleEditAsset = (asset) => {
        setCurrentAsset(asset);
        form.setFieldsValue(asset);
        setIsModalVisible(true);
    };

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();
            if (currentAsset) {
                await sendAuthenticatedRequest({
                    url: `http://localhost:8088/api/assets/${currentAsset.id}`,
                    method: 'PUT',
                    data: values,
                    userObject: userSession
                });
                message.success('Asset updated successfully');
            } else {
                await sendAuthenticatedRequest({
                    url: 'http://localhost:8088/api/assets',
                    method: 'POST',
                    data: values,
                    userObject: userSession
                });
                message.success('Asset created successfully');
            }
            setIsModalVisible(false);
            fetchAssets();
        } catch (error) {
            console.error('Failed to submit asset:', error);
            message.error('Failed to submit asset');
        }
    };

    const columns = [
        {
            title: 'Name',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: 'Symbol',
            dataIndex: 'symbol',
            key: 'symbol',
        },
        {
            title: 'Current Price',
            dataIndex: 'currentPrice',
            key: 'currentPrice',
        },
        {
            title: 'Asset Type',
            dataIndex: 'assetType',
            key: 'assetType',
        },
        {
            title: 'Actions',
            key: 'actions',
            render: (text, record) => (
                <span>
                    <Button type="link" onClick={() => handleEditAsset(record)}>Edit</Button>
                </span>
            ),
        },
    ];

    return (
        <Row gutter={[16, 16]}>
            <Col span={24}>
                <Card
                    title="Assets Management"
                    bordered={false}
                    extra={<Button type="primary" onClick={handleAddAsset}>Add New Asset</Button>}
                >
                    <Table dataSource={assets} columns={columns} rowKey="id" />
                </Card>
            </Col>
            <Modal
                title={currentAsset ? 'Edit Asset' : 'Add New Asset'}
                open={isModalVisible}
                onCancel={() => setIsModalVisible(false)}
                onOk={handleSubmit}
            >
                <Form form={form} layout="vertical">
                    <Form.Item name="name" label="Name" rules={[{ required: true, message: 'Please input the name!' }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="symbol" label="Symbol" rules={[{ required: true, message: 'Please input the symbol!' }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="currentPrice" label="Current Price" rules={[{ required: true, message: 'Please input the current price!' }]}>
                        <Input type="number" step="0.01" />
                    </Form.Item>
                    <Form.Item name="assetType" label="Asset Type" rules={[{ required: true, message: 'Please select the asset type!' }]}>
                        <Select>
                            <Option value="COMMODITY">COMMODITY</Option>
                            <Option value="STOCK">STOCK</Option>
                            <Option value="CRYPTO">CRYPTO</Option>
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>
        </Row>
    );
};

export default ManagerPage;
