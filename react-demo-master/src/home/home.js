import React from 'react';
import BackgroundImg from '../commons/images/Dalle1.webp';
import { Container, Jumbotron } from 'reactstrap';

const backgroundStyle = {
    backgroundPosition: 'center',
    backgroundSize: 'cover',
    backgroundRepeat: 'no-repeat',
    width: "100%",
    height: "100vh", // ajustat pentru a se potrivi mai bine pe ecran
    backgroundImage: `url(${BackgroundImg})`,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    textAlign: 'center'
};

const textStyle = {
    color: 'white',
    textShadow: '2px 2px 4px #000000' // umbra pentru contur negru
};

class Home extends React.Component {
    render() {
        return (
            <div>
                <Jumbotron fluid style={backgroundStyle}>
                    <Container fluid>
                        <h1 className="display-3" style={textStyle}>
                            Energy Management System
                        </h1>
                        <p className="lead" style={textStyle}>
                            <b>Enabling smart energy consumption and control for an efficient and sustainable future.</b>
                        </p>
                    </Container>
                </Jumbotron>
            </div>
        );
    }
}

export default Home;
