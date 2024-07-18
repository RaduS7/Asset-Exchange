import React, { useState } from 'react';
import './App.css';
import RouterComponent from './components/RouterComponent';
import Navbar from './components/NavbarComponent';
import { BrowserRouter as Router } from 'react-router-dom';
import { Layout, Row, Col } from 'antd';
import session from './utils/session';

const { Header, Content, Footer } = Layout;

function App() {

  const userSession = session.getFromSession('usrss');
  const [rfr, setRfr] = useState(0);

  return (
    <div className="App">
      <Router>
        <Layout className="layout">
          <Header className="header">
            <Row justify="space-between" align="middle" style={{ width: '100%' }}>
              {userSession &&
                <>
                  <Col>
                    <b style={{ color: 'white' }}>ASSET EXCHANGE</b>
                  </Col>
                  <Col>
                    <Navbar rfr={rfr} />
                  </Col>
                </>
              }
              {!userSession &&
                <Col>
                  <b style={{ color: 'white' }}>ASSET EXCHANGE</b>
                </Col>
              }
            </Row>
          </Header>
          <Content className="content">
            <RouterComponent setRfr={setRfr} />
          </Content>
          <Footer className="footer">
            ©2024 ASSET EXCHANGE
          </Footer>
        </Layout>
      </Router>
    </div>
  );
}

export default App;
