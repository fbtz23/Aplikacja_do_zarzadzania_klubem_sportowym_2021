import React, { Component } from 'react'
import { Route, Redirect } from 'react-router-dom'

import AuthService from '../services/AuthService';

class AuthRoute extends Component {
    render() {

        if (AuthService.isUserLoggedIn()) {
            return <Route {...this.props} />
        } else {
            return <Redirect to="/login" />
        }

    }
}

export default AuthRoute