import React, { useEffect, useState } from "react";

import Menu from "../menu/Menu";
import Footer from "../menu/Footer";
import axios from "axios";
import { Container, Table, Button } from "react-bootstrap";
import { Link } from "react-router-dom";

const url = "http://localhost:8080/appUsers/";

export const AppUserPage = () => {

    const [data, setData] = useState([]);
    useEffect(async () => {

        await axios.get(url).then(response => {
            setData(response.data._embedded.appUserList);
        }).catch(error => {
            console.log(error);
        });

    }, []);

    return (
        <div>
            <Menu />
            <Container text style={{ marginTop: '7em' }}>
                <h1>App Users</h1>
                <Table className="my-gradient2" responsive striped borderless hover style={{ borderRadius: '10px' }}>
                    <thead className="my-gradient">
                        <tr>
                            <th>First Name</th>
                            <th>Last Name</th>
                            <th>Role</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            data.map((user) => {
                                return <tr>
                                    <th><Link to={"/appUsers/" + user.id}>{user?.firstName}</Link></th>
                                    <th>{user?.lastName}</th>
                                    <th>{user?.appUserRole}</th>
                                </tr>
                            })
                        }
                    </tbody>
                </Table>
            </Container>
            <Footer />
        </div >
    )
}

export default AppUserPage;