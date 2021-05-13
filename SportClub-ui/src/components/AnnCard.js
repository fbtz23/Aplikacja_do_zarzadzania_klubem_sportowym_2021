import React, {useState} from "react";
import { Button,Modal,Form} from "react-bootstrap"
import axios from "axios";
class AnnCard extends React.Component{

    constructor(props) {
        super(props);
        this.state={
            firstName:this.props.announcement.user.firstName,
            lastName:this.props.announcement.user.lastName,
            date:this.props.announcement.date,
            text:this.props.announcement.text,
            update:
                {
                  text:this.props.announcement.text,
                },
            show:false,
        }
    }

    handleClose=()=>{
        this.setState({show:false})
    };
    handleShow=()=> {
        this.setState({show:true})
    };

    edit=async event => {
        event.preventDefault();
        try {

            const url = "http://localhost:8080/announcements/" + this.props.announcement.id;
            const response = await axios.patch(url, this.state.update);

        } catch (e) {
            console.log(`😱 Axios request failed: ${e}`);
        }

        this.setState({show:false});

    }

    dataChange = event =>
    {
        this.setState({update:{text:event.target.value}});
        this.setState({text:event.target.value});
       // this.state.update.text=event.target.value;
    }

render() {
    return (
        <div id="logform">
            <div className="row">
                <div><h5>{this.state.firstName} {this.state.lastName}</h5></div>
                <div className="date">{this.state.date.substring(0, 10) + " " + this.state.date.substring(11, 16)}</div>
            </div>
            <div>
                <span className="text">{this.state.text}</span>
            </div>
            <Button variant="primary" size="sm" onClick={this.handleShow}>
                Edit
            </Button>{" "}
            <Button variant="primary" size="sm">
                Delete
            </Button>
            <Modal show={this.state.show} onHide={this.handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit announcement</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group controlId="modalEdit">
                            <Form.Label>Edit text</Form.Label>
                            <Form.Control as="textarea" rows={3} readOnly={false} defaultValue={this.state.text}
                                          onChange={this.dataChange}/>
                        </Form.Group>
                        <Button onClick={this.edit}>Save Changes</Button>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={this.handleClose}>
                        Close
                    </Button>

                </Modal.Footer>
            </Modal>
        </div>

    );
}
}export default AnnCard;
