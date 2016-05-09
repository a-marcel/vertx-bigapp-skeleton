import React, { PropTypes } from 'react'
import { Link } from 'react-router'

function RootLayout ({ children }) {
	return (
		<div>
			huhu
			{children}
		</div>
	)
}

RootLayout.propTypes = {
	children: PropTypes.element
}

export default RootLayout
