import React from 'react'
import { Route, IndexRoute } from 'react-router'
import RootLayout from 'layouts/RootLayout/RootLayout'

import IndexView from 'views/IndexView/IndexView'


export default (store) => {
    return (
        <Route path='/' component={RootLayout}>
		<IndexRoute component={IndexView}/>
	</Route>
    )
}
