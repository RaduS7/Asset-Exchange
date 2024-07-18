import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Select, Typography, InputNumber, Button, Form, message, Modal, Divider } from 'antd';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import moment from 'moment';
import useAuthenticatedRequest from '../hooks/useAuthenticatedRequest';
import session from '../utils/session';
import '../App.css';

const { Option } = Select;
const { Text } = Typography;

const TraderPage = ({ setUser, setRfr }) => {
    const userSession = session.getFromSession('usrss');
    const [update, setUpdate] = useState(false);
    const [assets, setAssets] = useState([]);
    const [selectedAsset, setSelectedAsset] = useState('AAPL');
    const [priceData, setPriceData] = useState([]);
    const [tradeOrders, setTradeOrders] = useState([]);
    const [userOrders, setUserOrders] = useState([]);
    const [statusFilter, setStatusFilter] = useState('all');
    const [orderTypeFilter, setOrderTypeFilter] = useState('all');
    const { sendAuthenticatedRequest } = useAuthenticatedRequest();
    const [form] = Form.useForm();
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [orderToCancel, setOrderToCancel] = useState(null);

    useEffect(() => {
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


        const fetchTradeOrders = async () => {
            try {
                const response = await sendAuthenticatedRequest({
                    url: 'http://localhost:8088/api/tradeOrders',
                    userObject: userSession
                });
                setTradeOrders(response.data);
            } catch (error) {
                console.error('Failed to fetch trade orders:', error);
            }
        };

        const fetchUserOrders = async () => {
            try {
                const response = await sendAuthenticatedRequest({
                    url: 'http://localhost:8088/api/tradeOrders/my-orders',
                    userObject: userSession
                });
                setUserOrders(response.data);
            } catch (error) {
                console.error('Failed to fetch user orders:', error);
            }
        };

        fetchAssets();
        fetchTradeOrders();
        fetchUserOrders();

        (async () => {
            try {
                const response = await sendAuthenticatedRequest({
                    url: 'http://localhost:8088/api/asset-history/by-symbol',
                    method: 'POST',
                    data: { symbol: selectedAsset || 'AAPL' },
                    userObject: userSession
                });
                setPriceData(response.data.map(entry => ({
                    time: moment(entry.time).format('YYYY-MM-DD HH:mm'),
                    price: entry.price
                })));
            } catch (error) {
                console.error('Failed to fetch asset price data:', error);
            }
        })();

        setUser(userSession)
    }, [update]);

    const handleAssetChange = async (symbol) => {
        setSelectedAsset(symbol);
        try {
            const response = await sendAuthenticatedRequest({
                url: 'http://localhost:8088/api/asset-history/by-symbol',
                method: 'POST',
                data: { symbol },
                userObject: userSession
            });
            setPriceData(response.data.map(entry => ({
                time: moment(entry.time).format('YYYY-MM-DD HH:mm'),
                price: entry.price
            })));
        } catch (error) {
            console.error('Failed to fetch asset price data:', error);
        }
    };

    const getYAxisDomain = () => {
        if (priceData.length === 0) return [0, 'auto'];
        const prices = priceData.map(entry => entry.price);
        const minPrice = Math.min(...prices);
        const maxPrice = Math.max(...prices);
        const buffer = (maxPrice - minPrice) * 0.1; // 10% buffer
        return [minPrice - buffer, maxPrice + buffer];
    };

    const handleOrder = async (orderType) => {
        try {
            const values = await form.validateFields();
            const response = await sendAuthenticatedRequest({
                url: 'http://localhost:8088/api/tradeOrders',
                method: 'POST',
                data: {
                    ...values,
                    symbol: selectedAsset,
                    orderType: orderType
                },
                userObject: userSession
            });
            message.success(`${orderType} order placed successfully`);
            setUpdate(!update);
            setRfr(prev => prev + 1);
        } catch (error) {
            console.error(`Failed to place ${orderType} order:`, error);
            message.error(`Failed to place ${orderType} order`);
        }
    };

    const showCancelModal = (order) => {
        setOrderToCancel(order);
        setIsModalVisible(true);
    };

    const handleCancelOrder = async () => {
        try {
            await sendAuthenticatedRequest({
                url: `http://localhost:8088/api/tradeOrders/${orderToCancel.id}`,
                method: 'DELETE',
                userObject: userSession
            });
            message.success('Order canceled successfully');
            setUpdate(!update);
        } catch (error) {
            console.error('Failed to cancel order:', error);
            message.error('Failed to cancel order');
        } finally {
            setIsModalVisible(false);
            setOrderToCancel(null);
        }
    };

    const handleCancelModal = () => {
        setIsModalVisible(false);
        setOrderToCancel(null);
    };

    const pendingOrders = tradeOrders.filter(order =>
        order.symbol === selectedAsset && order.status === 'PENDING'
    );

    const buyOrders = pendingOrders.filter(order => order.orderType === 'BUY').sort((a, b) => b.price - a.price);
    const sellOrders = pendingOrders.filter(order => order.orderType === 'SELL').sort((a, b) => a.price - b.price);

    const filteredUserOrders = userOrders.filter(order => {
        return (statusFilter === 'all' || order.status === statusFilter) &&
            (orderTypeFilter === 'all' || order.orderType === orderTypeFilter);
    });


    return (
        <Row gutter={[24, 16]} className="full-width-row">
            <Col span={16}>
                <Card title="Big Card" bordered={false} style={{ height: '100%' }}>
                    <Select
                        defaultValue="AAPL"
                        placeholder="Select an asset"
                        style={{ width: '100%', marginBottom: '16px' }}
                        onChange={handleAssetChange}
                    >
                        {assets.map(asset => (
                            <Option key={asset.symbol} value={asset.symbol}>
                                {asset.symbol}
                            </Option>
                        ))}
                    </Select>
                    {selectedAsset && priceData.length > 0 && (
                        <>
                            <Text>Price Data for {selectedAsset}</Text>
                            <ResponsiveContainer width="100%" height={400}>
                                <LineChart
                                    data={priceData}
                                    margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                                >
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="time" />
                                    <YAxis domain={getYAxisDomain()} />
                                    <Tooltip />
                                    <Legend />
                                    <Line type="monotone" dataKey="price" stroke="#8884d8" />
                                </LineChart>
                            </ResponsiveContainer>
                        </>
                    )}
                </Card>
            </Col>
            <Col span={8}>
                <Card title="Order Book" bordered={false} style={{ marginBottom: '16px' }}>
                    {selectedAsset && pendingOrders.length > 0 ? (
                        <>
                            {
                                buyOrders.map(order => (
                                    <div key={order.id}>
                                        <Text style={{ color: 'green', fontWeight: 'bold' }}>{order.quantity} @ ${order.price.toFixed(2)}</Text>
                                    </div>
                                ))
                            }
                            <Divider style={{ borderColor: '#c9c9c9' }} />
                            {
                                sellOrders.map(order => (
                                    <div key={order.id}>
                                        <Text style={{ color: 'red', fontWeight: 'bold' }}>{order.quantity} @ ${order.price.toFixed(2)}</Text>
                                    </div>
                                ))
                            }
                        </>
                    ) : (
                        <Text>No pending orders for the selected asset</Text>
                    )}
                </Card>
                <Card title="Buy/Sell" bordered={false}>
                    <Form form={form} layout="vertical">
                        <Form.Item
                            name="quantity"
                            label="Quantity"
                            rules={[{ required: true, message: 'Please input the quantity' }]}
                        >
                            <InputNumber min={1} style={{ width: '100%' }} />
                        </Form.Item>
                        <Form.Item
                            name="price"
                            label="Price"
                            rules={[{ required: true, message: 'Please input the price' }]}
                        >
                            <InputNumber min={0} step={0.01} style={{ width: '100%' }} />
                        </Form.Item>
                        <Form.Item>
                            <Button type="primary" onClick={() => handleOrder('BUY')} style={{ backgroundColor: "green", marginRight: '10px' }}>
                                Buy
                            </Button>
                            <Button type="primary" onClick={() => handleOrder('SELL')} style={{ backgroundColor: "red" }}>
                                Sell
                            </Button>
                        </Form.Item>
                    </Form>
                </Card>
            </Col>
            <Col span={16}>
                <Card title="Your Orders" bordered={false} style={{ height: '300px', overflowY: 'auto' }}>
                    <Row gutter={[16, 16]}>
                        <Col span={12}>
                            <Select
                                placeholder="Filter by Status"
                                style={{ width: '100%' }}
                                onChange={value => setStatusFilter(value)}
                                defaultValue="all"
                            >
                                <Option value="all">All</Option>
                                <Option value="PENDING">Pending</Option>
                                <Option value="COMPLETE">Complete</Option>
                                <Option value="CANCELLED">Cancelled</Option>
                            </Select>
                        </Col>
                        <Col span={12}>
                            <Select
                                placeholder="Filter by Order Type"
                                style={{ width: '100%' }}
                                onChange={value => setOrderTypeFilter(value)}
                                defaultValue="all"
                            >
                                <Option value="all">All</Option>
                                <Option value="BUY">Buy</Option>
                                <Option value="SELL">Sell</Option>
                            </Select>
                        </Col>
                    </Row>

                    <Divider />
                    <div>
                        {filteredUserOrders.length > 0 ? (
                            filteredUserOrders.map(order => (
                                <div key={order.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <Text><b>{order.symbol}</b> - <span style={{ color: (order.orderType === 'BUY') ? 'green' : 'red' }}>{order.orderType} </span> - <b style={{ color: 'blue' }}>{order.quantity}</b> @ <b>${order.price.toFixed(2)}</b>
                                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  {moment(order.orderTime).format('YYYY-MM-DD HH:mm')}
                                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <span style={{ color: (order.status === 'PENDING') ? 'orange' : (order.status === 'COMPLETED') ? 'green' : 'red' }}>{order.status}</span></Text>
                                    {order.status === 'PENDING' && (
                                        <Button type="link" onClick={() => showCancelModal(order)}>
                                            Cancel
                                        </Button>
                                    )}
                                </div>
                            ))
                        ) : (
                            <Text>No orders found</Text>
                        )}
                    </div>
                </Card>
            </Col>

            <Modal
                title="Cancel Order"
                open={isModalVisible}
                onOk={handleCancelOrder}
                onCancel={handleCancelModal}
            >
                <Text>Are you sure you want to cancel the order for {orderToCancel?.quantity} @ ${orderToCancel?.price.toFixed(2)}?</Text>
            </Modal>
        </Row >
    );
};

export default TraderPage;
