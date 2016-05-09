import React, { Component, PropTypes } from 'react'
import { connect } from 'react-redux'

import classes from './IndexView.scss'


class IndexView extends Component {
	render () {
		return (
			<div className={classes['test']}>drin2</div>
		)
	}
}

IndexView.propTypes = {
}

const mapStateToProps = (state) => ({
})

export default connect((mapStateToProps), {
})(IndexView)
