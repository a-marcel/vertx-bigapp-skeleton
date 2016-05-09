import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import { Router } from 'react-router'
import { useRouterHistory } from 'react-router'
import configureStore from 'redux/configureStore'
import createBrowserHistory from 'history/lib/createBrowserHistory'
import { syncHistoryWithStore } from 'react-router-redux'
import makeRoutes from 'routes'




const initialState = window.__INITIAL_STATE__

const rootElement = document.getElementById('app')
const browserHistory = useRouterHistory(createBrowserHistory)({
})

const store = configureStore(initialState, browserHistory)

const history = syncHistoryWithStore(browserHistory, store, {
    selectLocationState: (state) => state.router
})


const routes = makeRoutes(store)

render(
    <Provider store={store}>
        <Router history={history}>
            {routes}
        </Router>
    </Provider>,
    rootElement
)
